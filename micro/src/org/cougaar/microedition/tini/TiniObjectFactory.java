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

import org.cougaar.microedition.util.ObjectFactoryAdapter;
import java.util.*;

/**
 * Defines objects that work for the TINI (and also the J2SE JVM).
 */
public class TiniObjectFactory extends ObjectFactoryAdapter {

  public void addClasses() {
    addClass("org.cougaar.microedition.tini.TiniServerSocketME");
    addClass("org.cougaar.microedition.tini.TiniSocketME");
    addClass("org.cougaar.microedition.tini.TiniFileLoader");
  }
}

