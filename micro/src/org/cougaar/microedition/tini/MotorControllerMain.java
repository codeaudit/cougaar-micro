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

import org.cougaar.microedition.cluster.Node;
import com.dalsemi.tininet.TININet;
import com.dalsemi.shell.TINIShell;

public class MotorControllerMain {
  public static void main(String[] args) {
//    TININet.setIPAddress("192.233.51.95");
    TININet.setIPAddress("192.168.0.2");
    TININet.setSubnetMask("255.255.255.0");


    com.dalsemi.system.Debug.setDefaultStreams("serial0", 115200);
    System.out.println("Starting Node");

    String [] nodeArgs = {"LocomotionController", "192.168.0.10", "1235"};
    Node.main(nodeArgs);
  }
}