/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.asset;

import java.util.Hashtable;

public interface Resource extends Asset {
  public void setName(String n);
  public String getName();

  public void setParameters(Hashtable t);
  public Hashtable getParameters();
}