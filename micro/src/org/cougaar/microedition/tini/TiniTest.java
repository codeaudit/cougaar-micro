/*
 * <copyright>
 *  Copyright 1999-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.tini;

import org.cougaar.microedition.asset.*;
import com.ibutton.adapter.*;
import com.ibutton.container.*;

/**
 * create a Tini Thermometer.
 */
public class TiniTest extends ResourceAdapter {

  static String[] Units = { "1", "-1", "" };

  String units = "";

  public TiniTest() {}

  public void setChan(int c) {}

  public void setUnits(String u) {
    units = Units[0];
    boolean ok = false;
    for (int i=0; Units[i] != ""; i++)
      if (u.equals(Units[i]))
        ok = true;
    if (!ok)
      throw new IllegalArgumentException("bad units: " + u + "; using " + Units[0]);
    units = u;
  }

  public String getUnits() { return units; }

  public long getValue() {
    double dval = (double)-999.999;
    String unit = getUnits();
    try {
//      adapter.setSearchAlliButtons();
      if (unit.equals("1"))
        dval = (double)1.;
      else
        dval = (double)-1.;
      System.out.println(unit + " "+ getName() + " Reading: " + dval);
    } catch (Exception e) {
      System.out.println("caught exception: " + e);
    }
    return (long)dval;
  }

}
