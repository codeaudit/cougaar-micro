/*
 * <copyright>
 * Copyright 1997-2000 Defense Advanced Research Projects Agency (DARPA)
 * and ALPINE (A BBN Technologies (BBN) and Raytheon Systems Company
 * (RSC) Consortium). This software to be used in accordance with the
 * COUGAAR license agreement.  The license agreement and other
 * information on the Cognitive Agent Architecture (COUGAAR) Project can
 * be found at http://www.cougaar.org or email: info@cougaar.org.
 * </copyright>
 */
package org.cougaar.microedition.se.test;

import java.util.*;

import org.cougaar.core.plugin.*;
import org.cougaar.core.util.*;
import org.cougaar.util.*;
import org.cougaar.core.cluster.*;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.microedition.se.domain.*;
/**
 * A test PlugIn to test interoperatbility with Cougaar ME.
 * It asks all known micro clusters for the temperature.
 */
public class CommsTestPlugIn extends SimplePlugIn {

  IncrementalSubscription assetSub, allocSub;
  String name = "Unset";

  /**
   * Subscribe to MicroClusters and my own allocations.
   */
  protected void setupSubscriptions() {

    Vector parameters = getParameters();
    Enumeration pnum = parameters.elements();
    while (pnum.hasMoreElements()) {
      String param = (String)pnum.nextElement();
      if (param.toLowerCase().indexOf("name") < 0)
        continue;
      int indx = param.indexOf("=");
      if (indx < 0)
        continue;
      name = (param.substring(indx+1)).trim();
      System.out.println("Name: " + name);
    }

    assetSub = (IncrementalSubscription)subscribe(new UnaryPredicate() {
      public boolean execute(Object o) {return o instanceof MicroCluster;}});


    allocSub = (IncrementalSubscription)subscribe(new UnaryPredicate() {
      public boolean execute(Object o) {
        if (o instanceof Allocation) {
          Allocation a = (Allocation)o;
          if (a.getTask().getVerb().equals("Measure")) {
            PrepositionalPhrase pp = a.getTask().getPrepositionalPhrase("Target");
            if (pp != null) {
              return pp.getIndirectObject().equals(name);
            }
          }
        }
        return false;
      }});

  }

  /**
   * Handle new micro clusters and changes to my allocations
   */
  protected void execute() {

    // System.out.println("CommsTestPlugin: execute");
    //
    // Allocate a temperature measure task to all micro clusters
    //
    Enumeration micros = assetSub.getAddedList();
    while (micros.hasMoreElements()) {
      MicroCluster micro = (MicroCluster)micros.nextElement();
      if (micro.getMicroClusterPG().getName().equals(name)) {
        Task t = makeTask();
        publishAdd(t);
        Allocation allo = makeAllocation(t, micro);
        System.out.println("Allocating to :"+micro.getMicroClusterPG().getName());
        publishAdd(allo);
      }
    }

    //
    // Look at allocation results to see what the temperature is.
    //
    Enumeration allos = allocSub.getChangedList();
    while (allos.hasMoreElements()) {
      Allocation alloc = (Allocation)allos.nextElement();
      AllocationResult ar = alloc.getReceivedResult();
      double tmp = ar.getValue(0);
      System.out.println(name+" is: "+tmp);
      Task t = alloc.getTask();
//      publishChange(t);
    }
  }

  /**
   * Gin-up an new temperature task.
   */
  private Task makeTask() {
    NewTask t = theLDMF.newTask();
    t.setPlan(theLDMF.getRealityPlan());
    t.setVerb(Verb.getVerb("Measure"));

    Vector prepositions = new Vector();

    NewPrepositionalPhrase npp = theLDMF.newPrepositionalPhrase();
    npp.setPreposition("Target");
    npp.setIndirectObject(name);
    prepositions.add(npp);

    npp = theLDMF.newPrepositionalPhrase();
    npp.setPreposition("Value");
    npp.setIndirectObject("Button");

    prepositions.add(npp);
    t.setPrepositionalPhrases(prepositions.elements());

    return t;
  }

  /**
   * Gin-up an allocation of this task to this asset
   */
  private Allocation makeAllocation(Task t, MicroCluster micro) {
    AllocationResult estAR = null;
    Allocation allocation =
      theLDMF.createAllocation(t.getPlan(), t, micro, estAR, Role.ASSIGNED);
    return allocation;
  }
}