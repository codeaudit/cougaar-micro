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