/*
 * ServerSocketME.java
 *
 * Copyright 2000 by BBN Technologies, LLC. All Rights Reserved
 *
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
