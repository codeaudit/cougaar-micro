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

import cougaar.microedition.io.*;

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

  private Semaphore sem = new Semaphore();

  String name;

  /**
   * @param name the cluster name to be accessed by plugins.
   */
  public Distributor(String name) {
    this.name = name;
  }

  /**
   * @return the name of this cluster.
   */
  public String getName() {
    return name;
  }

  private Object owner = null;

  /**
   * Begin modifications to the blackboard.
   * @param subscriber The object that will hold the "lock" on this transaction.
   */
  public synchronized void openTransaction(Object subscriber) {
    if (subscriber == owner)
      return;
    while (owner != null) {
      try {
        wait();
      } catch (InterruptedException ie) {}
    }
    owner = subscriber;
    addedList.removeAllElements();
    changedList.removeAllElements();
    removedList.removeAllElements();
  }

  /**
   * Commit (finish) modifications to the blackboard.  Delta lists are updated
   * for all subscribers.
   * @param subscriber The object that currently holds the "lock" on this transaction.
   * @exception RuntimeException if the subscriber parameter does not equal the last
   * subscriber given to openTransaction.
   */
  public synchronized void closeTransaction(Object subscriber) {

  if (subscriber != owner)
    throw new RuntimeException("Attempt to close unopen transaction");

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

  owner = null;
  notify();
  distribute();

  }

  /**
   * Add an object to the blackboard.
   * @return true
   */
  public boolean publishAdd(Object o) {
    if (!addedList.contains(o))
      addedList.addElement(o);
    return true;
  }

  /**
   * Advertise a change to an object that already exists on the blackboard.
   * @return true
   */
  public boolean publishChange(Object o) {
    if (!changedList.contains(o))
      changedList.addElement(o);
    return true;
  }

  /**
   * Remove an object from the blackboard.
   * @return true
   */
  public boolean publishRemove(Object o) {
    if (!removedList.contains(o))
      removedList.addElement(o);
    return true;
  }

  /**
   * Add a subscriber to be notified of changes to the blackboard.
   */
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

  /**
   * Remove a subscriber.  It will no longer be notified of changes to the blackboard.
   */
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

  private MessageTransport messageTransport = null;

  public MessageTransport getMessageTransport() {
    return messageTransport;
  }
  public void setMessageTransport(MessageTransport mt) {
    messageTransport = mt;
  }

  /**
   *  Check for subscribers who have something to do.
   */
  public void distribute() {
    sem.put();
  }

  /**
   * Pause until a subscriber has something to do.
   */
  public void waitForSomeWork() {
    if (runnableSubscribers.size() == 0)
      sem.take();
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
        openTransaction(runme);
        runme.execute();
        runme.getSubscription().clearLists();
        // collect changed subscriptions
        closeTransaction(runme);
//        try {Thread.sleep(5000);} catch (Exception e) {}
      }
      waitForSomeWork();
    }
  }
}

class Semaphore {

  int count = 1;
  public Semaphore() {
  }

  public synchronized void take() {
    while (count == 0) {
      try { wait(); } catch (InterruptedException ie) {
        System.out.println("interrupted");
      }
    }
    count = 0;
  }

  public synchronized void put() {
    count = 1;
    notify();
  }

}