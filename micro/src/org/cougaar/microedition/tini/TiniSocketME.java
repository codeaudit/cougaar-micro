/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */

package org.cougaar.microedition.tini;

import org.cougaar.microedition.io.*;
import java.net.*;
import java.io.*;

public class TiniSocketME implements SocketME {

  Socket sock = null;

  public OutputStream getOutputStream( String server, int port ) throws IllegalArgumentException, IllegalAccessException, IOException {

    sock = new Socket ( server, port );

    return sock.getOutputStream();

  }

  public InputStream getInputStream( ) throws IOException {

    return sock.getInputStream();

  }

  public void close() throws IOException {

    sock.close();

  }

}
