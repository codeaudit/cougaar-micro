
package org.cougaar.microedition.kvm;

import org.cougaar.microedition.io.*;
import javax.microedition.io.*;
import java.io.*;

public class KvmSocketME implements SocketME {

  StreamConnection sock = null;

  public OutputStream getOutputStream( String server, int port ) throws IllegalArgumentException, IllegalAccessException, IOException {

    //System.out.println("Try to open:"+"socket://" + server + ":" + port);
    sock = (StreamConnection)Connector.open("socket://" + server + ":" + port);
    //System.out.println("DONE to open:"+"socket://" + server + ":" + port);

    return sock.openOutputStream();

  }

  public InputStream getInputStream( ) throws IOException {

    return sock.openInputStream();

  }

  public void close() throws IOException {

    sock.close();

  }

}
