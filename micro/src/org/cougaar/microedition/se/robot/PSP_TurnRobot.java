/*
 * <copyright>
 *  Copyright 1997-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.se.robot;

import org.cougaar.domain.planning.ldm.plan.PlanElement;

import org.cougaar.util.UnaryPredicate;
import java.io.*;
import java.util.*;
import org.cougaar.lib.planserver.PSP_BaseAdapter;
import org.cougaar.lib.planserver.PlanServiceProvider;
import org.cougaar.lib.planserver.UISubscriber;
import org.cougaar.lib.planserver.HttpInput;
import org.cougaar.lib.planserver.PlanServiceContext;
import org.cougaar.lib.planserver.PlanServiceUtilities;
import org.cougaar.lib.planserver.RuntimePSPException;
import org.cougaar.core.cluster.Subscription;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.domain.planning.ldm.RootFactory;
import org.cougaar.core.cluster.IncrementalSubscription;
import org.cougaar.microedition.shared.Constants;

/**
 */
public class PSP_TurnRobot extends PSP_BaseAdapter
  implements PlanServiceProvider, UISubscriber
{

  public PSP_TurnRobot()
  {
    super();
  }

  public PSP_TurnRobot( String pkg, String id ) throws RuntimePSPException
  {
    setResourceLocation(pkg, id);
  }

  public boolean test(HttpInput query_parameters, PlanServiceContext sc)
  {
    super.initializeTest(); // IF subclass off of PSP_BaseAdapter.java
    return false;  // This PSP is only accessed by direct reference.
  }

  UnaryPredicate advancePred()
  {
    UnaryPredicate newPred = new UnaryPredicate()
    {
      public boolean execute(Object o)
      {
	boolean ret=false;
	if (o instanceof Task)
	{
	  Task mt = (Task)o;
	  ret= (mt.getVerb().equals(Constants.Robot.verbs[Constants.Robot.ADVANCE]));
	}
	return ret;
      }
    };
    return newPred;
  }

  /**
   * Called when a HTTP request is made of this PSP.
   * @param out data stream back to the caller.
   * @param query_parameters tell me what to do.
   * @param psc information about the caller.
   * @param psu unused.
   */
  public void execute( PrintStream out,
                       HttpInput query_parameters,
                       PlanServiceContext psc,
                       PlanServiceUtilities psu ) throws Exception
  {

    try
    {
      String degreesparam = "degrees";
      String mmparam = "millimeters";

      out.println("PSP_TurnRobot called from " + psc.getSessionAddress());
      System.out.println("PSP_TurnRobot called from " + psc.getSessionAddress());

      //remove other rotation/advancement tasks
      IncrementalSubscription subscription = null;

      subscription = (IncrementalSubscription)psc
	.getServerPlugInSupport().subscribe(this, advancePred());

      Iterator iter = subscription.getCollection().iterator();
      if (iter.hasNext())
      {
	Task task=null;
	while (iter.hasNext())
	{
	  task = (Task)iter.next();
	  psc.getServerPlugInSupport().publishRemoveForSubscriber(task);
	}
      }

      //now pop open a rotation task
      if( query_parameters.existsParameter(degreesparam) )
      {
         out.println();
         String degreestext = (String) query_parameters.getFirstParameterToken(degreesparam, '=');
         out.println("Rotate: ["+degreestext+"]");

	 String mmtext = (String) query_parameters.getFirstParameterToken(mmparam, '=');
         out.println("Translate: ["+mmtext+"]");

	 RootFactory theLDMF = psc.getServerPlugInSupport().getFactoryForPSP();

	 NewTask t = theLDMF.newTask();
	 t.setPlan(theLDMF.getRealityPlan());
	 t.setVerb(Verb.getVerb(Constants.Robot.verbs[Constants.Robot.ADVANCE]));

	 Vector prepositions = new Vector();

	 psc.getServerPlugInSupport().openLogPlanTransaction();

	 NewPrepositionalPhrase npp = theLDMF.newPrepositionalPhrase();
	 npp.setPreposition(Constants.Robot.prepositions[Constants.Robot.ROTATEPREP]);
	 npp.setIndirectObject(degreestext);
	 prepositions.add(npp);

	 npp = theLDMF.newPrepositionalPhrase();
	 npp.setPreposition(Constants.Robot.prepositions[Constants.Robot.TRANSLATEPREP]);
	 npp.setIndirectObject(mmtext);
	 prepositions.add(npp);

	 t.setPrepositionalPhrases(prepositions.elements());
	 psc.getServerPlugInSupport().closeLogPlanTransaction();

	 psc.getServerPlugInSupport().publishAddForSubscriber(t);

      }
    }
    catch (Exception ex)
    {
      out.println(ex.getMessage());
      ex.printStackTrace(out);
      System.out.println(ex);
      out.flush();
    }
  }

  /**
   * A PSP can output either HTML or XML (for now).  The server
   * should be able to ask and find out what type it is.
   **/
  public boolean returnsXML() {
    return false;
  }

  public boolean returnsHTML() {
    return true;
  }

  /**  Any PlanServiceProvider must be able to provide DTD of its
   *  output IFF it is an XML PSP... ie.  returnsXML() == true;
   *  or return null
   **/
  public String getDTD()  {
    return null;
  }

  /**
   * The UISubscriber interface. (not needed)
   */
  public void subscriptionChanged(Subscription subscription) {
  }
}
