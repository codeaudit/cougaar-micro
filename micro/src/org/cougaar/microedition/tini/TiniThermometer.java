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
public class TiniThermometer extends ThermometerResource {

  static String[] Units = { "Celsius", "Fahrenheit", "" };

  public TiniThermometer() {}

  public void setChan(int c) {}

  public void setUnits(String u) {
    units = Units[0];
    boolean ok = false;
    for (int i=0; Units[i] != ""; i++)
      if (u.equals(Units[i]))
        ok = true;
    if (!ok)
      throw new IllegalArgumentException(getName() + ": bad units: " + u + "; using: " + Units[0]);
    units = u;
  }

  public double getValue() {
    TINIExternalAdapter adapter = new TINIExternalAdapter();
    double dval = (double)-999.999;
    String unit = getUnits();
    try {
      adapter.beginExclusive(true);
      adapter.targetFamily(0x10);
//      adapter.setSearchAlliButtons();
      iButtonContainer10 tempSensor = (iButtonContainer10)adapter.getFirstiButton();
      if (tempSensor == null)
        return dval;
      if (unit.equals("Fahrenheit"))
        dval = tempSensor.readTemperatureFahrenheit();
      else
        dval = tempSensor.readTemperature();
      System.out.println(unit + " " + getName() + " Reading: " + dval);
      adapter.endExclusive();
    } catch (Exception e) {
      System.out.println("caught exception: " + e);
      adapter.endExclusive();
    }
    return dval;
  }

}
