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
import org.cougaar.microedition.shared.Constants;

/**
 * Represents Flashlight resources controlled by TINI boards.
 */
public class TiniFlashlight extends ControllerResource {

  private boolean isOnNow=false;
  private boolean wasOnThen = false;
  private boolean changecondition = false;
  private DS2450 ds2450;
  private int ds2450Index=0, ds2450Channel=0;
  private String owAddr;
  private int debugLevel = 0;


  /**
   * Set parameters with values from my node and initialize TiniFlashlight.
   */
  public void setParameters(Hashtable t)
  {
    setName("TiniFlashlight");
    super.setParameters(t);
    if (t != null && t.containsKey("owaddr"))
      setAddr((String)t.get("owaddr"));

    /*
    if (owAddr==null) {
      System.err.println("TiniFlashlight requires a non-null owaddr from a parameter in the xml file.");
    } else {
      initDS2450(owAddr);
    }
    */
    initDS2450();


    //testing
    try
    {
      setOn(true);
      Thread.sleep(1000);
      setOn(false);
    }
    catch (Exception e) {}
  }

  /**
   * Set the address of the DS2450.
   */
  private void setAddr(String addr) { owAddr=addr; }


  /**
   * Read One Wire Bus to determine if flashlight is on.
   */
  public boolean isOn() {

    if (ds2450!=null) {
      if (debugLevel > 20) {
        System.out.println("TiniFlashlight.isOnNow calling "
          +"ds2450.readOutput(ds2450Index, ds2450Channel) with values:");
        System.out.println("ds2450.readOutput("+ds2450Index+", "+ds2450Channel+")");
      }
      isOnNow=!ds2450.readOutput(ds2450Index, ds2450Channel);  // output is false when light is on
      if (debugLevel > 20) System.out.println("TiniFlashlight.isOnNow Returned from ds2450.readOutput");
    } else {
      isOnNow=false;
	System.err.println("TiniFlashlight.isOnNow but ds2450 is null -- returning false ");
    }
    return isOnNow;
  }

  /**
    Attempts to set the Flashlight to the value indicated.
    @value true for on; false for off
    @return actual value after the method completes execution
  */
  public boolean setOn(boolean value)
  {
    System.out.println("TiniFlashlight: setOn("+value+")");
    // setPinTo returns the value of the pin after the call (should be same as value)
    isOnNow=setPinTo(value);
    System.out.println("TiniFlashlight: Leaving setOn("+value
        +") and flashlight isOn() returns "+isOn());

    if(isOnNow != wasOnThen)
      changecondition = true;

    return isOnNow;
  }

  /**
   * Constructor.
   */
  public TiniFlashlight() { }

  /**
   * Set one wire bus to the given value.
   * @return value read from the bus after attempting to set it.
   */
  private boolean  setPinTo(boolean value) {
    boolean outputenable=true;
    boolean pinValue=!value;  // set pin to false to turn light on
    if (ds2450!=null) {
      if (debugLevel > 30) {
        System.out.println("TiniFlashlight.setPinTo calling "
          +"ds2450.configureADOutput(ds2450Index, ds2450Channel, outputenable, value) with values:");
        System.out.println("ds2450.configureADOutput("+ds2450Index+", "+ds2450Channel+", "+outputenable+", "+pinValue+")");
      }

      ds2450.configureADOutput(ds2450Index, ds2450Channel, outputenable, pinValue);

      if (debugLevel > 30) System.out.println("Returned from ds2450.configureADOutput");
    } else {
      if (debugLevel > -20) System.out.println("TiniFlashlight.setPinTo("+value+") but ds2450 is null ");
    }
    return isOn();
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
      int adindex =ds2450Index;
      int adchan = ds2450Channel;
      boolean adoutputstate = true;  // true = not conducting to ground, logic 1

      if (debugLevel > 30) System.out.println("TiniFlashlight.initDS2450 Starting....");
      if (owAddr==null) {
        ds2450 = new DS2450();
      } else {
        ds2450 = new DS2450(getName(), owAddr);
      }
      if (debugLevel > 30) System.out.println("TiniFlashlight.initDS2450 A/Ds Initialized.");

      // set the output pin low
      boolean adoutputenable = false;
      ds2450.configureADOutput(adindex, adchan, adoutputenable, adoutputstate);
      if (debugLevel > 30) System.out.println("TiniFlashlight.initDS2450 "+ds2450.readDeviceName(adindex) + ", A/D " + adindex + ", channel " + adchan + " reconfigured: ");
      if (debugLevel > 30) System.out.println("TiniFlashlight.initDS2450 "+"adoutputenable: "+adoutputenable+" adoutputstate: "+adoutputstate);
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

  public void getValues(long [] values)
  {
    changecondition = false; //reset
    wasOnThen = isOnNow;

    values[0] = (long)(1.0*scalingFactor);
  }

  public void getValueAspects(int [] aspects)
  {
    aspects[0] = Constants.Aspects.FLASHLIGHT;
  }

  public int getNumberAspects()
  {
    return 1;
  }

  public void setChan(int c) {}
  public void setUnits(String u) {}
  public boolean conditionChanged() {return changecondition;}

  private boolean isundercontrol = false;

  public void startControl()
  {
    setOn(true);
    isundercontrol = true;
  }

  public void stopControl()
  {
    setOn(false);
    isundercontrol = false;
  }

  public boolean isUnderControl()
  {
    return isundercontrol;
  }

  public boolean getSuccess() { return isOnNow; }

  public void modifyControl(String controlparameter, String controlparametervalue)
  {

  }
}

