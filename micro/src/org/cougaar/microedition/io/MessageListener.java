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
package org.cougaar.microedition.io;

/**
 * This interface is implemented by classes which need to see incoming XML messages.
 */
public interface MessageListener {

  /**
   * This method is called when a new XML message arrives
   * @param data the XML document
   * @param source the name of the cluster sending the message
   */
  public void deliverMessage(String data, String source);
}
