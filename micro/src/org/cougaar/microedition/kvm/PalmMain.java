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

//import com.sun.kjava.*;
import org.cougaar.microedition.cluster.Node;

//public class PalmMain extends Spotlet {
public class PalmMain {
  static String myName = "PDA1";
//  static String myHost = "192.233.51.176";
//  static String myHost = "192.233.51.250";
//  static String myHost = "127.0.0.1";
  static String myHost = "192.168.1.100";
  static String myHostPort = "1235";

  public static void main(String [] args) {
    System.out.println("Starting Node: "+myName+" " +myHost+" " + myHostPort);
    String [] myArgs = {myName, myHost, myHostPort};
    Node.main(myArgs);
  }
}
