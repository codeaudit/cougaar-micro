/*
 * SocketME.java
 *
 * Copyright 2000 by BBN Technologies, LLC. All Rights Reserved
 *
 */

package cougaar.microedition.io;

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

}
