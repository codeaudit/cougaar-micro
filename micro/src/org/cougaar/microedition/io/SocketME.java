
package cougaar.microedition.io;

import java.io.*;

public interface SocketME {

  public OutputStream getOutputStream ( String server, int port ) throws IllegalArgumentException, IllegalAccessException, IOException;

}
