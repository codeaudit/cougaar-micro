
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
