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

import java.util.*;
import org.cougaar.microedition.asset.*;
import com.ibutton.adapter.*;
import com.ibutton.container.*;
import org.cougaar.microedition.util.*;
import org.cougaar.microedition.ldm.*;
import org.cougaar.microedition.plugin.*;
import org.cougaar.microedition.shared.*;
import org.cougaar.microedition.shared.Constants;

public class TiniSONARController extends ControllerResource {

  static int sonarIdx = 0 ;
  static int sonarChan = 0 ;
  static int debugLevel=10;

  private double sensorthreshold;
  private DS2450 ds2450=null;

  int position=0;
  int triggeringthreshold=3;
  int historySize=4;
  Vector historyQueue =null;
  int score=0;

  public void modifyControl(String controlparameter, String controlparametervalue)
  {
    if(controlparameter.equalsIgnoreCase("threshold"))
    {
      Double temp = new Double(controlparametervalue);
      sensorthreshold = temp.doubleValue();
      System.out.println("TiniSONARController: threshold set: " +sensorthreshold);
    }
  }

  private boolean isStarted = false;
  public boolean isUnderControl() {return isStarted;}

  public void startControl()
  {
    isStarted = true;
  }

  public void stopControl()
  {
    isStarted = false;
  }

  private long newreturnvalue = 0;
  private long oldreturnvalue = -1;

  public void getValues(double [] values)
  {
    oldreturnvalue = newreturnvalue;
    values[0] = (double)newreturnvalue;
  }

  public void getValueAspects(int [] aspects)
  {
    aspects[0] = Constants.Aspects.DETECTION;
  }

  public int getNumberAspects()
  {
    return 1;
  }
  public boolean getSuccess()
  {
    if(newreturnvalue > 0 )
      return true;
    else
      return false;
  }

  public boolean conditionChanged()
  {
      Boolean historyValue = (Boolean)historyQueue.elementAt(position);
      if (historyValue.booleanValue())
        score-- ;
      boolean thisTime = (getDS2450Value() > sensorthreshold);
      if (thisTime)
        score++ ;
      historyQueue.setElementAt((thisTime)?Boolean.TRUE:Boolean.FALSE, position);
      position=(position+1)%historySize;

      if (score >= triggeringthreshold)
	 newreturnvalue = 1;
      else
	 newreturnvalue = 0;

      return (oldreturnvalue != newreturnvalue);
  }

  /**
   * Constructor.  Sets name default.
   */
  public TiniSONARController() {}

  /**
   * Set parameters with values from my node and initialize resource.
   */
  public void setParameters(java.util.Hashtable t) {
    setName("TiniSONARController");
    historyQueue=new Vector();
    for (int idx=0; idx<historySize; idx++)
    {
        historyQueue.addElement(Boolean.FALSE);
    }
    initDS2450();

  }


/**
 * Initialize DS2450 object for reading and setting items on the one wire bus.
 * Note that DS2450.adoutputenable must be false (as it is by default
 * in the DS2450 object).
 **/
  private void initDS2450() {
    initDS2450(null);
  }

/**
 * Initialize DS2450 object for reading and setting items on the one wire bus.
 * Note that DS2450.adoutputenable must be false (as it is by default
 * in the DS2450 object).
 * @param owAddr initialize the item at the given address;  if the given address
 *               is null, then initialize each of the devices found
 **/
  private void initDS2450(String owAddr) {
    try
    {
      int adindex =sonarIdx;
      int adchan = sonarChan;
      boolean adoutputstate = true;  // true = not conducting to ground, logic 1

      if (debugLevel > 30) System.out.println("TiniSONARController.initDS2450 Starting....");
      if (owAddr==null) {
        ds2450 = new DS2450();
      } else {
        ds2450 = new DS2450(getName(), owAddr);
      }
      if (debugLevel > 30) System.out.println("TiniSONARController.initDS2450 A/Ds Initialized.");

      // set the output pin low
      boolean adoutputenable = false;
      ds2450.configureADOutput(adindex, adchan, adoutputenable, adoutputstate);
      if (debugLevel > 30) System.out.println("TiniSONARController.initDS2450 "+ds2450.readDeviceName(adindex) + ", A/D " + adindex + ", channel " + adchan + " reconfigured: ");
      if (debugLevel > 30) System.out.println("TiniSONARController.initDS2450 "+"adoutputenable: "+adoutputenable+" adoutputstate: "+adoutputstate);
    }
    catch (Exception ex)
    {
      if (debugLevel > -20) System.err.println("initDS2450 caught Exception: "+ex);
     ex.printStackTrace();
    }
    catch (Throwable t)
    {
      if (debugLevel > -20) System.err.println("initDS2450 caught Throwable: "+t);
    }
  }

  /**
   * Set the DS2450 channel to be used.  This must be consistent with the wiring
   * of the hardware.
   */
  public void setChan(int c) {
    chan = sonarChan = c;
    System.out.println("TiniSONARController: setting channel to "+sonarChan);
    if (c != sonarChan)
      throw new IllegalArgumentException(getName() + ": bad chan: " + c + "; using: " + sonarChan);
  }

  public void setUnits(String u) {}

  /**
   * A prior implementation of a method to read the voltage from the DS2450 device.
   * Probably obsolete now.
   */
  public double getLMValue() {
    byte[]   state;
    double range = (-999.*2.);
    TINIExternalAdapter adapter = new TINIExternalAdapter();
    double curVoltage = -999.;
    try {
      adapter.beginExclusive(true);
      adapter.targetFamily(0x20);
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
      }
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    } finally {
      adapter.endExclusive();
    }

    System.out.println(getName() + " Reading: Ch" + getChan() + " = " + (range-curVoltage) + " V");
    return range-curVoltage;
  }

  /**
   * Read voltage from DS2450 device using the DS2450 object.
   */
  public double getDS2450Value() {
    if (debugLevel > 30) System.out.println("TiniSONARController.getDS2450Value and ds2450 is "+ds2450);
    return (ds2450==null) ? 0.0 : ds2450.readVoltage(sonarIdx, sonarChan);
  }
}