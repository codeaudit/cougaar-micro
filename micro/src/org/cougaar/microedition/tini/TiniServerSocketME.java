
package org.cougaar.microedition.tini;

import org.cougaar.microedition.io.*;
import java.io.*;
import java.net.*;

public class TiniServerSocketME implements ServerSocketME {
  private ServerSocket ss;

  public void openServerSocket( int myListenPort ) throws IllegalArgumentException, IllegalAccessException, IOException {

    ss = new ServerSocket ( myListenPort );

  }

  public InputStream acceptInputStream() throws IOException {

    return(ss.accept().getInputStream());

  }

}
