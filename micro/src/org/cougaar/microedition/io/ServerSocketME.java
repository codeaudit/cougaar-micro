
package cougaar.microedition.io;

import java.io.*;

public interface ServerSocketME {

  public void openServerSocket (int port) throws IllegalArgumentException, IllegalAccessException, IOException;

  public InputStream acceptInputStream () throws IOException ;
}
