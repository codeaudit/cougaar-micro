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
import org.cougaar.microedition.tini.DS2450;

/**
 * Asset for rotation and limit sensor.
 * Includes SW latches for the alarms.
 */

public class TiniRotationAndLimitSensor extends RotationAndLimitSensorResource {

  private static DS2450 SensorDevices;
  private static final int TURRETSENSORINDEX = 0;
  private boolean LimitAlarm = false;     // software latch of limit alarm
  private boolean RotationAlarm = false;  // software latch of rotation alarm
  private static final int ROTATIONALARMCHANNEL = 0;
  private static final int ROTATIONRESETCHANNEL = 1;
  private static final int LIMITALARMCHANNEL = 2;
  private static final int LIMITRESETCHANNEL = 3;
  boolean debugging = false;
  private static final double ADRANGE = 5.12;
  private static final double ADRESOLUTION = 0.01;
  private static final int ALARMTYPE = 1;
  private static final double ALARMTRIGGER = 3.0;

  public TiniRotationAndLimitSensor() {
    try
    {
      SensorDevices = new DS2450();
      this.configureAD();
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  // test code
/*
  public static void main(String args[])
  {
    boolean LimitReached = false;
    boolean FullRotationReached = false;
    TiniRotationAndLimitSensor TRALS = new TiniRotationAndLimitSensor();
    if (debugging) {TRALS.readHWStatus();}

    // perform and A/D and read the alarms
    TRALS.refreshAlarms();

    // A new alarm this time?
    LimitReached = TRALS.isLimitTriggered();
    // If alarm occured, clear it
    if (LimitReached) {TRALS.clearLimitAlarm();}

    // A new alarm this time?
    FullRotationReached = TRALS.isRotationTriggered();
    // If alarm occured, clear it
    if (FullRotationReached) {TRALS.clearRotationAlarm();}

    for (int i = 0; i < 20; i++)
    {
      System.out.println("\nPerforming A/D...");
      TRALS.refreshAlarms();

      System.out.println("Checking for new alarms:");
      LimitReached = TRALS.isLimitTriggered();
      if (LimitReached) {
        System.out.println("ALARM: Limit switch was closed.");
        TRALS.clearLimitAlarm();
      }
      FullRotationReached = TRALS.isRotationTriggered();
      if (FullRotationReached) {
        System.out.println("ALARM: Rotation switch was closed.");
        TRALS.clearRotationAlarm();
      }
      LimitReached = TRALS.isLimitTriggered();
      FullRotationReached = TRALS.isRotationTriggered();
      System.out.println("Alarms reset.\n   Limit = " + LimitReached + "\n   Rotation = " + FullRotationReached);
    }
  }
*/

  /*
   * Configure the A/D sensor device as follows:
   * a.) Channels 0 & 2 are A/D converters over 5.12 voltage range with high
   *     alarms triggered when the A/D voltage is >= 3.0 volts.
   * b.) Channels 1 & 3 are output pins initialized to output high.  Note that
   *     there is a hardware inverter on the output that converts them to low.
   */

  public void configureAD()
  {
    double adresult = 0.0;
    boolean alarmresult = false;
    boolean debugging = false;
    try
    {
      boolean alarmenable = true;
      boolean adoutputenable = true;
      boolean adoutputstate = true;  // true = not conducting to ground, logic 1
      // Rotation:  turn off output enable feature, then set up for a/d
      adoutputenable = false;
      SensorDevices.configureADOutput(TURRETSENSORINDEX, ROTATIONALARMCHANNEL, adoutputenable, adoutputstate);
      adoutputenable = true;
      SensorDevices.configureAD(TURRETSENSORINDEX, ROTATIONALARMCHANNEL, ADRANGE, ADRESOLUTION);
      SensorDevices.configureAlarm(TURRETSENSORINDEX, ROTATIONALARMCHANNEL, ALARMTYPE, ALARMTRIGGER, alarmenable);
      // Rotation:  set the output high
      SensorDevices.configureADOutput(TURRETSENSORINDEX, ROTATIONRESETCHANNEL, adoutputenable, adoutputstate);
      // Limit:  turn off output enable feature, then set up for a/d
      adoutputenable = false;
      SensorDevices.configureADOutput(TURRETSENSORINDEX, LIMITALARMCHANNEL, adoutputenable, adoutputstate);
      adoutputenable = true;
      SensorDevices.configureAD(TURRETSENSORINDEX, LIMITALARMCHANNEL, ADRANGE, ADRESOLUTION);
      SensorDevices.configureAlarm(TURRETSENSORINDEX, LIMITALARMCHANNEL, ALARMTYPE, ALARMTRIGGER, alarmenable);
      // Limit:  set the output high
      SensorDevices.configureADOutput(TURRETSENSORINDEX, LIMITRESETCHANNEL, adoutputenable, adoutputstate);
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  // read the hardware status
  public void readHWStatus() {
    try {
      SensorDevices.readStatus(TURRETSENSORINDEX);
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  // read the hardware alarms
  public void refreshAlarms() {
    try {
      boolean[] AlarmsTriggered;
      double[] VoltagesRead;
      VoltagesRead = SensorDevices.readAllVoltages(TURRETSENSORINDEX);
      AlarmsTriggered = SensorDevices.readAllAlarms(TURRETSENSORINDEX, ALARMTYPE);
      if (LimitAlarm || AlarmsTriggered[LIMITALARMCHANNEL]) {
        LimitAlarm = true;
      }
      if (RotationAlarm || AlarmsTriggered[ROTATIONALARMCHANNEL]) {
        RotationAlarm = true;
      }
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  public long getValue() {
    return 0;
  }

  public void setUnits(String newUnits) {
    return;
  }

  public void setChan(int newChannel) {
    return;
  }

  public boolean isLimitTriggered() {
    return LimitAlarm;
  }

  public boolean isRotationTriggered() {
    return RotationAlarm;
  }

  public void clearRotationAlarm() {
    // clear the hardware alarm by resetting the F/F
    boolean adoutputenable = true;
    boolean adoutputstate = false;
    SensorDevices.configureADOutput(TURRETSENSORINDEX, ROTATIONRESETCHANNEL, adoutputenable, adoutputstate);
    adoutputstate = true;
    SensorDevices.configureADOutput(TURRETSENSORINDEX, ROTATIONRESETCHANNEL, adoutputenable, adoutputstate);

    // clear the software version of the alarm
    RotationAlarm = false;
  }

  public void clearLimitAlarm() {
    // clear the hardware alarm by resetting the F/F
    boolean adoutputenable = true;
    boolean adoutputstate = false;
    SensorDevices.configureADOutput(TURRETSENSORINDEX, LIMITRESETCHANNEL, adoutputenable, adoutputstate);
    adoutputstate = true;
    SensorDevices.configureADOutput(TURRETSENSORINDEX, LIMITRESETCHANNEL, adoutputenable, adoutputstate);

    // clear the software version of the alarm
    LimitAlarm = false;
  }
}