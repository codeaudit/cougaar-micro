/*
 * <copyright>
 * Copyright 1997-2000 Defense Advanced Research Projects Agency (DARPA)
 * and ALPINE (A BBN Technologies (BBN) and Raytheon Systems Company
 * (RSC) Consortium). This software to be used in accordance with the
 * COUGAAR license agreement.  The license agreement and other
 * information on the Cognitive Agent Architecture (COUGAAR) Project can
 * be found at http://www.cougaar.org or email: info@cougaar.org.
 * </copyright>
 */

package org.cougaar.microedition.se.domain;

import java.io.*;

public class Logfile {

  PrintStream out;
  public Logfile(String filename) {
    try {
      out = new PrintStream(new FileOutputStream(filename));
      System.out.println("Opened logfile: "+filename);
    } catch (Exception ex) {
      System.err.println("Error opening file "+filename);
      ex.printStackTrace();
    }
  }
  public void log(String text) {
    out.println("" + System.currentTimeMillis() + ","+text);
    out.flush();
  }

  public void log(double dbl) {
    log(""+dbl);
  }

  public void log(long lng) {
    log(""+lng);
  }
}
