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
package org.cougaar.microedition.se.domain;

import java.io.OutputStream;
import java.io.InputStream;
/**
 * Interface to be implemented by classes interested in receiving messages
 * (text) from Micro clusters.
 */
public interface MessageListener {

  /**
   * Callback method for text messages from micro clusters.
   * @param message The text of the message.
   * @param source ID of the sender of the message.
   * @param srcAddress IP address of the sender
   * @param clientout output stream back to the client.
   * @param clientin input stream back to the client.
   * @return false if the listener wants to keep the socket open
   */
  public boolean deliverMessage(String message, String source, String srcAddress, OutputStream clientout, InputStream clientin);

}
