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
package org.cougaar.microedition.shared;

import java.util.*;

/**
 * Represents tasks (things to do) in the micro cluster's blackboard.
 */
public class MicroTask implements Encodable {

  public MicroTask() {}

  public MicroTask(String source) {
    long time = System.currentTimeMillis();
    setUniqueID( source + "/" + new Long(time).toString() );
  }
  private String verb;
  private java.util.Vector prepositionalPhrases;
  private org.cougaar.microedition.shared.MicroAllocation allocation;

  public void setMe(MicroTask mt) {
    uniqueID = mt.uniqueID;
    verb = mt.verb;
    prepositionalPhrases = mt.prepositionalPhrases;
    // (don't want to null out the allocation)
    allocation = mt.allocation;
    if (allocation != null)
      allocation.setTask(this);
  }

  /**
   * Get the verb describing what to do.
   */
  public String getVerb() {
    return verb;
  }

  /**
   * Set the verb describing what to do.
   */
  public void setVerb(String newVerb) {
    verb = newVerb;
  }

  /**
   * Set prepositional phrases that qualify how the task should be done.
   */
  public void setPrepositionalPhrases(java.util.Vector newPrepositionalPhrases) {
    prepositionalPhrases = newPrepositionalPhrases;
  }

  /**
   * Add another prepositional phrase to the current set.
   */
  public void addPrepositionalPhrase(MicroPrepositionalPhrase mpp) {
    if (prepositionalPhrases == null)
      prepositionalPhrases = new Vector();
    prepositionalPhrases.addElement(mpp);
  }

  /**
   * Return the list of prepositional phrases qualifying this task.
   */
  public java.util.Vector getPrepositionalPhrases() {
    return prepositionalPhrases;
  }

  /**
   * Associate an allocation (disposition) with this task.
   */
  public void setAllocation(org.cougaar.microedition.shared.MicroAllocation newAllocation) {
    allocation = newAllocation;
  }

  /**
   * Get the allocation associated with this task.
   */
  public org.cougaar.microedition.shared.MicroAllocation getAllocation() {
    return allocation;
  }

  protected static String tag = "MicroTask";
  private String uniqueID;

  /**
   * XML encode this object and all sub-objects.
   */
  public void encode(StringBuffer str) {
    str.append("<");
    str.append(tag);
    str.append(" verb=\""+getVerb()+"\"");
    str.append(" uniqueID=\""+getUniqueID()+"\"");
    str.append(">");
    if (getAllocation() != null)
      getAllocation().encode(str);
    if (getPrepositionalPhrases() != null) {
      Enumeration preps = getPrepositionalPhrases().elements();
      while (preps.hasMoreElements()) {
        MicroPrepositionalPhrase mpp = (MicroPrepositionalPhrase)preps.nextElement();
        mpp.encode(str);
      }
    }
    str.append("</");
    str.append(tag);
    str.append(">");
  }



  public String toString() {
    StringBuffer sb=new StringBuffer();
    encode(sb);
    return sb.toString();
  }


  /**
   * Set the identifier string associated with this task.
   */
  public void setUniqueID(String newUniqueID) {
    uniqueID = newUniqueID;
  }

  /**
   * Get the identifier string associated with this task.
   */
  public String getUniqueID() {
    return uniqueID;
  }
}
