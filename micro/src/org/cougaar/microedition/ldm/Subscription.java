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

  public void clearLists() {
    addedList.clear();
    changedList.clear();
    removedList.clear();
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

  public void setAddedList(java.util.Vector newAddedList) {
    addedList = newAddedList;
  }

  public java.util.Vector getAddedList() {
    return addedList;
  }

  public void setChangedList(java.util.Vector newChangedList) {
    changedList = newChangedList;
  }

  public java.util.Vector getChangedList() {
    return changedList;
  }

  public void setRemovedList(java.util.Vector newRemovedList) {
    removedList = newRemovedList;
  }

  public java.util.Vector getRemovedList() {
    return removedList;
  }

  public void setMemberList(java.util.Vector newMemberList) {
    memberList = newMemberList;
  }

  public java.util.Vector getMemberList() {
    return memberList;
  }

}