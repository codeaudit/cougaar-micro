
package cougaar.microedition.tini;

import cougaar.microedition.io.*;
import java.net.*;
import java.io.*;

public class TiniSocketME implements SocketME {

  public OutputStream getOutputStream( String server, int port ) throws IllegalArgumentException, IllegalAccessException, IOException {

    Socket sock = new Socket ( server, port );

    return sock.getOutputStream();

  }
}
