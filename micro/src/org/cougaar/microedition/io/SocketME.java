/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */

package org.cougaar.microedition.io;

import java.io.*;

/**
 * This interface provides the template for opening Sockets in the ME domain.
 */
public interface SocketME {

  /**
   * This method opens a client Socket to another host
   *
   * @param  server       string that holds the hoatname or ip address to be connected to.
   * @param  port         server socket port the recepient is listening on.
   * @return OutputStream for writing your message to.
   */
  public OutputStream getOutputStream ( String server, int port ) throws IllegalArgumentException, IllegalAccessException, IOException;
  public InputStream getInputStream() throws IOException;
  public void close() throws IOException;

}
