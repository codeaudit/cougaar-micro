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

import org.cougaar.microedition.util.*;
import org.cougaar.microedition.shared.*;
import org.cougaar.microedition.asset.*;
import java.io.*;
import java.util.*;

/**
 * This class handles sending messages to another cluster,
 */
public interface MessageTransport {

  public void addMessageListener(MessageListener ml);

  public void removeMessageListener(MessageListener ml);

  public void sendMessage(Encodable msg, MicroCluster dest, String op);

  public void takePacket(String data, String source);

}
