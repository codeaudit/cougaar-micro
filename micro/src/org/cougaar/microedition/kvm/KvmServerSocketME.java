/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */

package org.cougaar.microedition.kvm;

import org.cougaar.microedition.io.*;
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
