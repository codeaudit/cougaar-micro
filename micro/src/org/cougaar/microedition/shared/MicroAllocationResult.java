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

public class MicroAllocationResult implements Encodable {

  public MicroAllocationResult() {
  }

  protected static String tag = "MicroAllocationResult";
  public void encode(StringBuffer str) {
    str.append("<");
    str.append(tag);
    str.append(">");
    str.append("</");
    str.append(tag);
    str.append(">");
  }
}