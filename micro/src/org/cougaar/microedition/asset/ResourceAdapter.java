/*
 * <copyright>
 *  Copyright 1999-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.asset;

import java.util.*;

/**
 * Base class for all Resources.
 */
public abstract class ResourceAdapter implements Resource {

  private String myName = "";
  private Hashtable attrtable = null;

  public void setName(String n) {
    myName = n;
  }

  public String getName() {
    return myName;
  }

  public void setParameters(Hashtable t) {
    attrtable = t;
    if (t != null && t.containsKey("name"))
      setName((String)t.get("name"));
  }

  public Hashtable getParameters() {
    return attrtable;
  }

}
