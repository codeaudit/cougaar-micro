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
package cougaar.microedition.shared;

import java.util.*;

public class MicroTask implements Encodable {

  public MicroTask() {
  }
  private String verb;
  private java.util.Vector prepositionalPhrases;
  private cougaar.microedition.shared.MicroAllocation allocation;

  public String getVerb() {
    return verb;
  }

  public void setVerb(String newVerb) {
    verb = newVerb;
  }

  public void setPrepositionalPhrases(java.util.Vector newPrepositionalPhrases) {
    prepositionalPhrases = newPrepositionalPhrases;
  }

  public void addPrepositionalPhrase(MicroPrepositionalPhrase mpp) {
    if (prepositionalPhrases == null)
      prepositionalPhrases = new Vector();
    prepositionalPhrases.add(mpp);
  }

  public java.util.Vector getPrepositionalPhrases() {
    return prepositionalPhrases;
  }

  public void setAllocation(cougaar.microedition.shared.MicroAllocation newAllocation) {
    allocation = newAllocation;
  }

  public cougaar.microedition.shared.MicroAllocation getAllocation() {
    return allocation;
  }

  protected static String tag = "MicroTask";
  public void encode(StringBuffer str) {
    str.append("<");
    str.append(tag);
    str.append(" verb=\""+getVerb()+"\"");
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
}