package org.cougaar.microedition.kvm;

import com.sun.kjava.*;
import org.cougaar.microedition.cluster.Node;

public class PalmMain {
  static String myName = "PDA1";
  static String myHost = "127.0.0.1";
  static String myHostPort = "1235";

  public static void main(String [] args) {
    System.out.println("Starting Node: "+myName+" " +myHost+" " + myHostPort);
    String [] myArgs = {myName, myHost, myHostPort};
    Node.main(myArgs);
  }
}
