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

/**
 * Qualifier describing how a task should be carried out.  These are two
 * arbitrary strings.
 */
public class MicroPrepositionalPhrase implements Encodable {

  public MicroPrepositionalPhrase() {
  }

  public MicroPrepositionalPhrase(String preposition, String indirectObject) {
    setPreposition(preposition);
    setIndirectObject(indirectObject);
  }

  private String preposition;
  private String indirectObject;

  public String getPreposition() {
    return preposition;
  }

  public void setPreposition(String newPreposition) {
    preposition = newPreposition;
  }

  public void setIndirectObject(String newIndirectObject) {
    indirectObject = newIndirectObject;
  }

  public String getIndirectObject() {
    return indirectObject;
  }

  protected static String tag = "MicroPrepositionalPhrase";
  /**
   * XML encode this object and all sub-objects
   */
  public void encode(StringBuffer str) {
    str.append("<");
    str.append(tag);
    str.append(" preposition=\"" + getPreposition() + "\"");
    str.append(" indirectObject=\"" + getIndirectObject() + "\"");
    str.append("/>");
  }



}