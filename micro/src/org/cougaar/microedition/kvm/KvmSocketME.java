
package cougaar.microedition.kvm;

import cougaar.microedition.io.*;
import javax.microedition.io.*;
import java.io.*;

public class KvmSocketME implements SocketME {

  public OutputStream getOutputStream( String server, int port ) throws IllegalArgumentException, IllegalAccessException, IOException {

    StreamConnection sock = (StreamConnection)Connector.open("socket://" + server + ":" + port);

    return sock.openOutputStream();

  }

}
