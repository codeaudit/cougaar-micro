/*
 * <copyright>
 *  Copyright 1997-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */

package cougaar.microedition.demo;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;

import alp.ui.planserver.*;
import alp.plugin.*;
import alp.util.*;
import alp.cluster.*;
import alp.ldm.plan.*;

import cougaar.microedition.domain.*;

public class PSP_MicroData extends PSP_BaseAdapter implements PlanServiceProvider, KeepAlive, UseDirectSocketOutputStream, UISubscriber
{

/**
 * This predicate matches what I want
 */
  class Gimme implements UnaryPredicate {
    public boolean execute(Object o) {
      if (o instanceof Allocation) {
        Allocation a = (Allocation)o;
          if (a.getTask().getVerb().equals("Measure"))
            return (
              (a.getTask().getPrepositionalPhrase("Temperature") != null) ||
              (a.getTask().getPrepositionalPhrase("Light") != null) ||
              (a.getTask().getPrepositionalPhrase("Value") != null)
            );
      }
      return false;
    }
  }

  /**
   * A zero-argument constructor is required for dynamically loaded PSPs,
   * required by Class.newInstance()
   **/
  public PSP_MicroData()
  {
    super();
  }

  public PSP_MicroData(String pkg, String id) throws RuntimePSPException
  {
    setResourceLocation(pkg, id);
  }

  public boolean test(HttpInput query_parameters, PlanServiceContext sc)
  {
    super.initializeTest(); // IF subclass off of PSP_BaseAdapter.java
    return false;  // This PSP is only accessed by direct reference.
  }

  /**
   *
   * Periodically sends HTML update to client
   **/
  int iterationCounter =0;
  public void execute(
      PrintStream cout,
      HttpInput query_parameters,
      PlanServiceContext psc,
      PlanServiceUtilities psu) throws Exception
  {
    //
    // Look at allocation results to see what the temperature is.
    //
    out = cout;
    Subscription subscription = psc.getServerPlugInSupport().subscribe(this, new Gimme());
    while (true) {
      try {Thread.sleep(Long.MAX_VALUE);} catch (Exception e) {}
    }
  }

  PrintStream out;

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

  /**
   * Any PlanServiceProvider must be able to provide DTD of its
   * output IFF it is an XML PSP... ie.  returnsXML() == true;
   * or return null
   **/
  public String getDTD()  {
    return null;
  }

  /**
   * The UISubscriber interface. (not needed)
   */
  public void subscriptionChanged(Subscription subscription) {

    Collection container = ((IncrementalSubscription)subscription).getChangedCollection();
    if (container == null) {
      container = ((IncrementalSubscription)subscription).getAddedCollection();
      if (container == null)
        return;
    }
    else
      container.addAll(((IncrementalSubscription)subscription).getAddedCollection());

    Iterator iterate = container.iterator();
    while (iterate.hasNext()) {
      Allocation alloc = (Allocation)iterate.next();
      if (alloc == null) break;
      AllocationResult ar = alloc.getReceivedResult();
      if (ar == null) continue;
      if (alloc.getTask().getPrepositionalPhrase("Temperature") != null)
        out.println("Temperature:"+ar.getValue(0));
      else if (alloc.getTask().getPrepositionalPhrase("Light") != null)
        out.println("Light:"+ar.getValue(0));
      else if (alloc.getTask().getPrepositionalPhrase("Value") != null)
        out.println("PDA:"+ar.getValue(0));

      out.flush();
    }
  }
}
