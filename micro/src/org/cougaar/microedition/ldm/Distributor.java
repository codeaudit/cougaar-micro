/*
 * <copyright>
 *  Copyright 1999-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package cougaar.microedition.ldm;

import java.util.*;

/**
 * The Distributor registers PlugIn subscriptions, executing PlugIns based
 * on changes to their subscriptions.
 */
public class Distributor {

  private Vector addedList = new Vector();
  private Vector changedList = new Vector();
  private Vector removedList = new Vector();

  private Vector runnableSubscribers = new Vector();
  private Vector allSubscribers = new Vector();

  private Vector allObjects = new Vector();

  public Distributor() {
  }

  public synchronized void openTransaction() {
    addedList.removeAllElements();
    changedList.removeAllElements();
    removedList.removeAllElements();
  }

  public void closeTransaction() {

  // process added list
  for (Enumeration objects = addedList.elements();  objects.hasMoreElements();) {
    Object o = objects.nextElement();
    Vector subs = getSubscribers(o);
    // update subscribers
    for (Enumeration subsenum = subs.elements(); subsenum.hasMoreElements();) {
      Subscriber s = (Subscriber)subsenum.nextElement();
      Vector list = s.getSubscription().getAddedList();
      if (!list.contains(o))
        list.addElement(o);
      list = s.getSubscription().getMemberList();
      if (!list.contains(o))
        list.addElement(o);
      if (!runnableSubscribers.contains(s))
        runnableSubscribers.addElement(s);
    }
    // Update the master blackboard
    if (!allObjects.contains(o))
      allObjects.addElement(o);
  }

  // process changed list
  for (Enumeration objects = changedList.elements();  objects.hasMoreElements();) {
    Object o = objects.nextElement();
    Vector subs = getSubscribers(o);
    // update subscribers
    for (Enumeration subsenum = subs.elements(); subsenum.hasMoreElements();) {
      Subscriber s = (Subscriber)subsenum.nextElement();
      Vector list = s.getSubscription().getChangedList();
      if (!list.contains(o))
        list.addElement(o);
      if (!runnableSubscribers.contains(s))
        runnableSubscribers.addElement(s);
    }
  }

  // process removed list
  for (Enumeration objects = removedList.elements();  objects.hasMoreElements();) {
    Object o = objects.nextElement();
    Vector subs = getSubscribers(o);
    // update subscribers
    for (Enumeration subsenum = subs.elements(); subsenum.hasMoreElements();) {
      Subscriber s = (Subscriber)subsenum.nextElement();
      Vector list = s.getSubscription().getRemovedList();
      if (!list.contains(o))
        list.addElement(o);
      list = s.getSubscription().getMemberList();
      list.removeElement(o);
      if (!runnableSubscribers.contains(s))
        runnableSubscribers.addElement(s);
    }
    // Update the master blackboard
    allObjects.removeElement(o);
  }


  }

  public boolean publishAdd(Object o) {
    if (!addedList.contains(o))
      addedList.addElement(o);
    return true;
  }

  public boolean publishChange(Object o) {
    if (!changedList.contains(o))
      changedList.addElement(o);
    return true;
  }

  public boolean publishRemove(Object o) {
    if (!removedList.contains(o))
      removedList.addElement(o);
    return true;
  }

  public boolean addSubscriber(Subscriber s) {
    if (!allSubscribers.contains(s))
      allSubscribers.addElement(s);
    for (Enumeration objects = allObjects.elements();  objects.hasMoreElements();) {
      Object o = objects.nextElement();
      if (s.getSubscription().getPredicate().execute(o)) {
        s.getSubscription().getMemberList().addElement(o);
        s.getSubscription().getAddedList().addElement(o);
        if (!runnableSubscribers.contains(s))
          runnableSubscribers.addElement(s);
      }
    }
    return true;
  }

  public boolean removeSubscriber(Subscriber s) {
    allSubscribers.removeElement(s);
    return true;
  }

  private Vector getSubscribers(Object o) {
    Vector ret = new Vector();
    Enumeration subs = allSubscribers.elements();
    while (subs.hasMoreElements()) {
      Subscriber s = (Subscriber)subs.nextElement();
      if (s.getSubscription().getPredicate().execute(o))
        ret.addElement(s);
    }
    return ret;
  }

  /**
   * Manage PlugIn subscriptions and executions
   */
  public void cycle() {

    for (;;) {
      // execute PlugIns
      while (runnableSubscribers.size() > 0) {
        Subscriber runme = (Subscriber)runnableSubscribers.elementAt(0);
        runnableSubscribers.removeElementAt(0);
        openTransaction();
        runme.execute();
        runme.getSubscription().clearLists();
        // collect changed subscriptions
        closeTransaction();
//        try {Thread.sleep( 1000 );} catch (Exception e) {}
      }
    }
  }
}
