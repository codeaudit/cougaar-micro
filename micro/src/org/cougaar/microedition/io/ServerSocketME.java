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
 * This interface provides the template for opening and accepting ServerSockets in the ME domain.
 */
public interface ServerSocketME {

  /**
   * This method opens a ServerSocket
   *
   * @param   port    server socket port to listen on
   * @return  none
   */
  public void openServerSocket (int port) throws IllegalArgumentException, IllegalAccessException, IOException;

  /**
   * This method waits for a client to open a socket to me, and returns an InputStream
   *
   * @param   none
   * @return  InputStream for reading purposes.
   */
  public InputStream acceptInputStream () throws IOException ;
}
