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

import com.dalsemi.system.*;

/**
 * Kills a TINI-OS task.
 * Run: java KillJava.tini <ps substring to kill>
 * eg: "java KillJava.tini Node.tini"
 */
public class KillJava {

  public KillJava(String token) {
  System.err.println("START");
    String [] ps = TINIOS.getTaskTable();
    for (int i=0; i<ps.length; i++) {
      System.out.println("ps: "+ps[i]);
      if (ps[i].indexOf(token) != -1) {
        String pid = ps[i].substring(0, ps[i].indexOf(":"));
        int ipid = Integer.parseInt(pid);
        System.out.println("KILL: "+ipid);
        TINIOS.killTask(ipid);
      }
    }
  }
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("USAGE: java KillJava.tini <what-to-kill>");
    } else {
      KillJava killJava1 = new KillJava("Node.tini");
    }
  }
}