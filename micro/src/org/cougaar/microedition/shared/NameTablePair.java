/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.shared;

import java.util.*;

//--------
public class NameTablePair {
  public String name;
  public Hashtable table;

  public NameTablePair(String n, Hashtable t) {
    name = n;
    if (t != null)
      table = t;
    else
      table = null;
  }
}
