
package cougaar.microedition.kvm;

import cougaar.microedition.io.*;
import javax.microedition.io.*;
import java.io.*;

public class KvmServerSocketME implements ServerSocketME {

  private StreamConnectionNotifier ss;

  public void openServerSocket( int myListenPort ) throws IllegalArgumentException, IllegalAccessException, IOException {

    ss = (StreamConnectionNotifier)Connector.open("serversocket://:" + myListenPort);

  }

  public InputStream acceptInputStream() throws IOException {

    return(ss.acceptAndOpen().openInputStream());

  }

}
