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
 * create a Tini Lightmeter.
 */
public class TiniLightmeter extends LightmeterResource {

  static int lightChan = 3;

  public TiniLightmeter() {}

  public void setChan(int c) {
    chan = lightChan;
    if (c != lightChan)
      throw new IllegalArgumentException("bad chan for LightmeterResource: " + c + " using " + lightChan);
  }

  public void setUnits(String u) {}

  public double getValue() {
    byte[]   state;
    double range = (-999.*2.);
    TINIExternalAdapter adapter = new TINIExternalAdapter();
    double curVoltage = -999.;
    try {
      adapter.beginExclusive(true);
      adapter.targetFamily(0x20);
//      adapter.setSearchAlliButtons();
      iButtonContainer20 aD = (iButtonContainer20)adapter.getFirstiButton();

      if (aD != null) {
        state = aD.readDevice();
        range = aD.getADRange(getChan(), state);
        if (range < 5.0) {
          aD.setADRange(getChan(), 5.12, state);
          aD.writeDevice(state);
        }
        aD.doADConvert(getChan(), state);
        curVoltage = aD.getADVoltage(getChan(), state);
    adapter.endExclusive();
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    adapter.endExclusive();
    }

    System.out.println("Light Reading: Ch" + getChan() + " = " + (range-curVoltage) + " V");
    return range-curVoltage;
  }
}
