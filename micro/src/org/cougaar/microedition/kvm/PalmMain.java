/*
 * <copyright>
 * 
 * Copyright 1997-2001 BBNT Solutions, LLC.
 * under sponsorship of the Defense Advanced Research Projects
 * Agency (DARPA).
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Cougaar Open Source License as published by
 * DARPA on the Cougaar Open Source Website (www.cougaar.org).
 * 
 * THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 * PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 * IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 * ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 * HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 * TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THE COUGAAR SOFTWARE.
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
