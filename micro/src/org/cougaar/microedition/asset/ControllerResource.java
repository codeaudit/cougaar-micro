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
 * Base class for all ControllerResources.
 */
public abstract class ControllerResource extends Resource {

  public int chan = 0;
  public String units = "";

  public abstract void setChan(int c);
  public abstract void setUnits(String u);
  public abstract double getValue();

  public ControllerResource() {}

  public void init() {
    try {
      Hashtable t = getParameters();
      if (t == null)
        return;
      if (t.containsKey("units"))
        setUnits((String)t.get("units"));
      if (t.containsKey("chan"))
        setChan(Integer.parseInt((String)t.get("chan")));
    } catch (Exception e) {System.out.println("caught " + e);}
  }

  public int getChan() {
    return chan;
  }

  public String getUnits() {
    return units;
  }

}
