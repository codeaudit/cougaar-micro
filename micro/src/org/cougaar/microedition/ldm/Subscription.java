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
 * Defines the set of objects than a subscribers is interested in.
 */
public class Subscription {

  public Subscription() {
    setAddedList(new Vector());
    setChangedList(new Vector());
    setRemovedList(new Vector());
    setMemberList(new Vector());
  }
  private cougaar.microedition.ldm.Subscriber subscriber;
  private cougaar.microedition.util.UnaryPredicate predicate;
  private java.util.Vector addedList;
  private java.util.Vector changedList;
  private java.util.Vector removedList;
  private java.util.Vector memberList;

  protected void clearLists() {
    addedList.removeAllElements();
    changedList.removeAllElements();
    removedList.removeAllElements();
  }

  public cougaar.microedition.ldm.Subscriber getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(cougaar.microedition.ldm.Subscriber newSubscriber) {
    subscriber = newSubscriber;
  }

  public void setPredicate(cougaar.microedition.util.UnaryPredicate newPredicate) {
    predicate = newPredicate;
  }

  public cougaar.microedition.util.UnaryPredicate getPredicate() {
    return predicate;
  }

  protected void setAddedList(java.util.Vector newAddedList) {
    addedList = newAddedList;
  }

  /**
   * Get the list of objects matching the predicate
   * added to the blackboard since the last execute().
   */
  public java.util.Vector getAddedList() {
    return addedList;
  }

  protected void setChangedList(java.util.Vector newChangedList) {
    changedList = newChangedList;
  }

  /**
   * Get the list of objects matching the predicate
   * changed on the blackboard since the last execute().
   */
  public java.util.Vector getChangedList() {
    return changedList;
  }

  protected void setRemovedList(java.util.Vector newRemovedList) {
    removedList = newRemovedList;
  }

  /**
   * Get the list of objects matching the predicate
   * removed from the blackboard since the last execute().
   */
  public java.util.Vector getRemovedList() {
    return removedList;
  }

  protected void setMemberList(java.util.Vector newMemberList) {
    memberList = newMemberList;
  }

  /**
   * Get the list of objects that currently match the subscription predicate.
   */
  public java.util.Vector getMemberList() {
    return memberList;
  }

  /**
   * Returns true if any of the delta lists (added, changed, removed) have
   * objects on them.
   */
  public boolean hasChanged() {
    return ((addedList.size()   != 0) ||
            (changedList.size() != 0) ||
            (removedList.size() != 0));
  }

}
