/*
 * <copyright>
 *  Copyright 1999-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.ldm;

/**
 * A Subscriber is an entity that has a single subscription to the blackboard
 */ 
public class Subscriber {

  public Subscriber() {
  }
  private org.cougaar.microedition.ldm.Distributor distributor;
  private org.cougaar.microedition.plugin.PlugIn plugin;
  private org.cougaar.microedition.ldm.Subscription subscription;

  public org.cougaar.microedition.ldm.Distributor getDistributor() {
    return distributor;
  }

  public void setDistributor(org.cougaar.microedition.ldm.Distributor newDistributor) {
    distributor = newDistributor;
  }

  public void setPlugIn(org.cougaar.microedition.plugin.PlugIn newPlugIn) {
    plugin = newPlugIn;
  }

  public org.cougaar.microedition.plugin.PlugIn getPlugIn() {
    return plugin;
  }

  public void setSubscription(org.cougaar.microedition.ldm.Subscription newSubscription) {
    subscription = newSubscription;
  }

  public org.cougaar.microedition.ldm.Subscription getSubscription() {
    return subscription;
  }

  public void execute() {
    getPlugIn().execute();
  }

 
} 
