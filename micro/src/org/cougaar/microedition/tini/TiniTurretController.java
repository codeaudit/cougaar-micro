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

import java.lang.Thread;

import org.cougaar.microedition.asset.*;
import com.ibutton.adapter.*;
import com.ibutton.container.*;
import org.cougaar.microedition.tini.DS2406;
import org.cougaar.microedition.shared.Constants;

/**
 * Asset for turret control.
 */

public class TiniTurretController extends TurretControllerResource
{

  public static final double TURRETRESOLUTION = 15.0;  // 1 full motor rotation = 15° turret rotation
  public static final int TURRET_RESOLUTION = 15;
  private double bearing = -360.0;  // relative to robot
  private double oldbearing = 0; //measures change in measurement

  private TiniMotorController motor;
  private static final int COAST = 0;
  private static final int BACKWARD = 2;  // turret rotates counterclockwise (decreasing bearing)
  private static final int FORWARD = 1;  // turret rotates clockwise (increasing bearing)
  private static final int BRAKE = 3;
  private int MotorDirection = BRAKE;

  private TiniRotationAndLimitSensor sensors;

  private SweepStateMachine ssm;
  private boolean SweepSMRun = false;
  public static final int LEFT = Constants.Robot.TURRET_LEFT;
  public static final int MIDDLE = Constants.Robot.TURRET_MIDDLE;
  public static final int RIGHT = Constants.Robot.TURRET_RIGHT;
  private int Hemisphere = MIDDLE;
  public static final int SWEEP_WAIT = 0;
  public static final int SWEEP_SETUP = 1;
  public static final int SWEEP = 2;
  public static final int SWEEP_REWIND = 3;

  private BearingStateMachine bsm;
  private boolean BearingSMRun = false;
  public static final int BEARING_WAIT = 0;
  public static final int BEARING_FORWARD_SEEK = 3;
  public static final int BEARING_BACKWARD_SEEK = 2;

  // thread control
  private Thread ST;
  private int SweepThreadState = STOP;
  private Thread BT;
  private int BearingThreadState = STOP;
  static final int RUN = 0;
  static final int SUSPEND = 1;
  static final int STOP = 2;

  static private boolean debugging = true;
  private boolean debuggingpauses = false;
  private boolean valuechanged = true;


  // test code
  public static void main(String args[]) {
    System.out.println("First line in main.");

    int BearingToAcquire = 45;
    TiniTurretController TTC = new TiniTurretController();
    TTC.SweepSMRun=true;
    TTC.setHemisphere(MIDDLE);
    TTC.startScan();

    /*
    TTC.longpause("MAIN: main thread paused...");
    TTC.stopScan();

    TTC.setHemisphere(RIGHT);
    TTC.startScan();
    TTC.longpause("MAIN: main thread paused...");
    TTC.stopScan();


    TTC.startSeek(BearingToAcquire);
    TTC.longpause("MAIN: main thread paused...");
    TTC.stopSeek();

    */
    if (debugging) {System.out.println(Thread.currentThread().getName() + " is finished.");}

  }

  public TiniTurretController()
  {
    //DON'T DO ANYTHING HERE. USE SET PARAMETERS TO INITIALIZE
  }

  public void setParameters(java.util.Hashtable t)
  {

    setName("TiniTurretController");
    try
    {
      if (debugging) { System.out.println("TiniTurretController::setParameters..."); }
      motor = new TiniMotorController();
      if (debugging) { System.out.println("TiniTurretController ctor - motor controller constructed."); }
      motor.setDirection(MotorDirection);
      if (debugging) {System.out.println("set motor direction to "+MotorDirection); motor.readHWStatus();}
      sensors = new TiniRotationAndLimitSensor();
      if (debugging) {sensors.readHWStatus();}
      if (debugging) {System.out.println("\nMotor and Sensors Initialized.");}
      setFullyConstructed(true);
      calibrateTurret();
    }
    catch (Throwable thr)
    {
      if (debugging) {System.out.println("Caught throwable in TiniTurretController ctor: "+thr);}
    }
  }

  boolean fullyConstructed=false;
  synchronized public boolean isFullyConstructed() { return fullyConstructed; }
  synchronized public void setFullyConstructed(boolean value) { fullyConstructed=value; notifyAll(); }
//  synchronized public boolean waitUntilFullyConstructed() {
//    while (!isFullyConstructed()) {
//      System.out.println("Waiting for TiniTurretController to become fully Constructed...");
//      try { wait(); } catch (Exception ex) {}
//      System.out.println("Notified, so check if TiniTurretController became fully Constructed.");
//    }
//    return fullyConstructed;
//  }

  public void getValues(double [] values)
  {
    oldbearing = bearing;
    values[0] = bearing;
  }

  public void getValueAspects(int [] aspects)
  {
    aspects[0] = Constants.Aspects.BEARING;
  }

  public int getNumberAspects()
  {
    return 1;
  }

  public void setChan(int c) {}
  public void setUnits(String u) {}

  public boolean conditionChanged()
  {
    return ((oldbearing != bearing));
  }

  public void startControl()
  {
    startScan();
  }

  public void stopControl()
  {
    stopScan();
  }

  public boolean isUnderControl()
  {
    return SweepSMRun;
  }

  public void modifyControl(String param, String val)
  {
    if(param.equalsIgnoreCase(Constants.Robot.prepositions[Constants.Robot.TURRETDIRECTIONPREP]))
    {
      if(val.equalsIgnoreCase(Constants.Robot.SEARCHRIGHT))
	setHemisphere(RIGHT);
      else if(val.equalsIgnoreCase(Constants.Robot.SEARCHLEFT))
	setHemisphere(LEFT);
      else
        setHemisphere(MIDDLE);

      System.out.println("TiniTurretController: hemisphere set: " +Hemisphere);
    }
  }

  synchronized public boolean startScan() {
    if (BearingThreadState != STOP || SweepThreadState != STOP) {
      if (debugging) {System.out.println("startScan returning false.");}
      return false;
    }
    ssm = new SweepStateMachine();
    ST = new Thread(ssm);
    ST.setName("Sweep State Machine Thread");
    if (debugging) {System.out.println(ST.getName() + " is running.");}
    ST.start();
    ssm.setThreadState(RUN);
    SweepSMRun = true;  // kick off the Sweep State Machine
    if (debugging) {System.out.println("startScan returning true.");}
    return true;
  }

  public boolean isRunning() {
    return SweepSMRun;
  }

  public void stopScan() {
    SweepSMRun = false;  // reset the Sweep State Machine to the wait state
    ssm.setThreadState(STOP);  // end the thread
    /*  NOTE: Tini 1.01 does not implement the join() method.  Hence, must use
     *        thread.isAlive() to determine when another thread had completed.
     */
    try
    {
      while (ST.isAlive()) {
        if (debugging) {System.out.println("Waiting for " + ST.getName() + " to die...");}
        Thread.currentThread().sleep(500);
      }
    } catch (InterruptedException e) {
      System.out.println(e);
    }
    if (debugging) {System.out.println(ST.getName() + " has expired.");}
  }

  public boolean startSeek(int SeekBearing) {
    if (SweepThreadState != STOP || BearingThreadState != STOP) {
      return false;
    }
    bsm = new BearingStateMachine();
    bsm.setBearing(SeekBearing);
    BT = new Thread(bsm);
    BT.setName("Bearing State Machine Thread");
    if (debugging) {System.out.println(BT.getName() + " is running.");}
    BT.start();
    bsm.setThreadState(RUN);
    BearingSMRun = true;  // kick off the Bearing State Machine
    return true;
  }

  public void stopSeek() {
    BearingSMRun = false;  // reset the Bearing State Machine to the wait state
    bsm.setThreadState(STOP);
    /*  NOTE: Tini 1.01 does not implement the join() method.  Hence, must use
     *        thread.isAlive() to determine when another thread had completed.
     */
    try
    {
      while (BT.isAlive()) {
        if (debugging) {System.out.println("Waiting for " + BT.getName() + " to die...");}
        Thread.currentThread().sleep(500);
      }
    } catch (InterruptedException e) {
      System.out.println(e);
    }
    if (debugging) {System.out.println(BT.getName() + " has expired.");}
  }

  public void setbearing(double val)
  {
    bearing = val;
  }

  public double getBearing()
  {
    return bearing;
  }

  private boolean lastlimit = false;

  public int computeRotations(double destinationdegrees)
  {
    int num = 0;

    num = (int)((destinationdegrees - bearing)/TURRETRESOLUTION);

    System.out.println("Bearing= " +bearing +" DestBrg= "+destinationdegrees +" Num= " +num);

    return num;

  }
  public void adjustbearing(boolean correctforrange, boolean autocal)
  {
    if(MotorDirection == FORWARD)
       bearing += TURRETRESOLUTION;

    if(MotorDirection == BACKWARD)
       bearing -= TURRETRESOLUTION;

    if(correctforrange)
    {
      if(bearing < 0.0) bearing += 360.0;
      if(bearing >= 360.0) bearing -= 360.0;
    }

    //dynamic calibration part
    if(autocal)
    {
      sensors.refreshAlarms();
      if (sensors.isLimitTriggered() != lastlimit)
      {
	  lastlimit = sensors.isLimitTriggered();
	  if(bearing < 30.0 || bearing > 330.0) // assume vicinity of limit switch
	  {
	    System.out.println("adjustbearing: adjusting to 0.0");
	    bearing = 0.0;
	  }
      }
    }
  }

  public void setHemisphere(int SearchZone) {
    Hemisphere = SearchZone;
  }

  public boolean goToBearing(double bearing) {
    if (SweepThreadState != STOP) {
      return false;
    }
    BearingSMRun = true;
    return true;
  }

  public void calibrateTurret() {
    // calibrate turret (rewind until limit sensor closes)
    int IncrementCounter = 0;
    int MotorRotations = 0;
    if (debugging) {System.out.println("Turret Calibration is running");}
    sensors.refreshAlarms();
    sensors.clearLimitAlarm();
    sensors.clearRotationAlarm();
    System.gc();
    if (debugging) {System.out.println("TURRET_CALIBRATION: Starting...");}
    MotorDirection = FORWARD;
    motor.setDirection(MotorDirection);
    while (!sensors.isLimitTriggered())
    {
      motor.turnOneIncrement();
      sensors.refreshAlarms();
      if (sensors.isRotationTriggered())
      {
        IncrementCounter++;
        if (debugging) {System.out.println("TURRET_CALIBRATION: Rotation Alarm.");}
        sensors.clearRotationAlarm();
        adjustbearing(false, false);
        if (debugging) {System.out.println("TURRET_CALIBRATION: Bearing = " + bearing);}
      }
      // safeguard the turret hardware
      if (IncrementCounter > 200) {
          if (debugging) {System.out.println("TURRET_CALIBRATION: Possible limit sensor failure.");}
          return;
      }
    }
    if (debugging) {System.out.println("TURRET_CALIBRATION: Limit Alarm Triggered.");}
    sensors.clearLimitAlarm();

    // move turret back across limit sensor
    System.gc();
    MotorDirection = BACKWARD;
    motor.setDirection(MotorDirection);
    for (int j = 0; j < 2; j++) {motor.turnOneIncrement();}
    sensors.clearLimitAlarm();
    sensors.clearRotationAlarm();
    setbearing(0.0);
    if (debugging) {System.out.println("TURRET_CALIBRATION: Calibration Complete. Bearing = " + bearing);}
    if (debugging) {pause("Short pause...");}
    return;
  }

  private class SweepStateMachine implements Runnable {

    public synchronized void setThreadState (int s) {
      SweepThreadState = s;
      if (s == RUN || s == STOP) {notify();}
    }

    private synchronized boolean checkThreadState() {
      while (SweepThreadState == SUSPEND) {
        try {
          if (debugging) System.out.println("checkThreadState going into wait...");
          wait();
          if (debugging) System.out.println("checkThreadState came out of wait; SweepThreadState: "+SweepThreadState);
        } catch (InterruptedException e) {
          // ignore
        }
      }
      if (SweepThreadState == STOP) {return false;}
      return true;
    }

    public void run() {
      int SweepStateIndex = SWEEP_WAIT;
      int IncrementCounter = 0;
      int MotorRotations = 0;
      sweepstateloop: while (true)
      {
        if (debugging) {System.out.println(Thread.currentThread().getName() + " is running");}
        if ( !checkThreadState() ) {
          if (debugging) { System.out.println("Breaking out of sweepstateloop due to !cheThreadState()"); }
          break sweepstateloop;
        }

        if (debugging) { System.out.println("SweepStateIndex: "+SweepStateIndex); }

        switch (SweepStateIndex) {

          case SWEEP_WAIT:
            while (!SweepSMRun) {
              try {
                ST.sleep(250);
                if (debugging) { System.out.println("ST.sleep"); }
              } catch (InterruptedException e) {
                if (debugging) {System.out.println("SLEEP_WAIT: Sleep interrupted.");}
              }
            }
            SweepStateIndex = SWEEP_SETUP;
            break;

          // rotate to sweep starting position
          case SWEEP_SETUP:
            System.gc();

	    MotorDirection = FORWARD;

            if (LEFT == Hemisphere) {MotorRotations = computeRotations(0.0);}
            else if (MIDDLE == Hemisphere) {MotorRotations = computeRotations(90.0);}
            else if (RIGHT == Hemisphere) {MotorRotations = computeRotations(180.0);}

	    if(MotorRotations >= 0)
	    {
	      MotorDirection = FORWARD;
	    }
	    else
	    {
	      MotorRotations = -MotorRotations;
	      MotorDirection = BACKWARD;
	    }

	    motor.setDirection(MotorDirection);

	    /*
	    MotorDirection = FORWARD;
            motor.setDirection(MotorDirection);
            MotorRotations = 0;
            if (LEFT == Hemisphere) {MotorRotations = 0;}
            else if (MIDDLE == Hemisphere) {MotorRotations = 6;}
            else if (RIGHT == Hemisphere) {MotorRotations = 12;}
            if ((MIDDLE == Hemisphere) || (RIGHT == Hemisphere)) {
              // move turret back across limit sensor
              System.gc();
              for (int j = 0; j < 3; j++) {motor.turnOneIncrement();}
              sensors.clearLimitAlarm();
              sensors.clearRotationAlarm();
              setbearing(0.0);
              if (debugging) {System.out.println("SWEEP_SETUP: Beginning. Bearing = " + bearing);}
            }
	    */
            for (int i = 0; i < MotorRotations; i++) {
              IncrementCounter = 0;
              while (!sensors.isRotationTriggered())
              {
                motor.turnOneIncrement();
                IncrementCounter++;
                // safeguard the turret hardware
                if (IncrementCounter > 20) {
                  if (debugging) {System.out.println("SWEEP_SETUP: Possible rotation sensor failure...");}
                  break sweepstateloop;
                }
                sensors.refreshAlarms();
                if (IncrementCounter < 1) {sensors.clearRotationAlarm();}
              }
              sensors.clearRotationAlarm();
	      adjustbearing(true, true);
              if (debugging) {System.out.println("SWEEP_SETUP: Bearing = " + bearing);}
              if (debuggingpauses) {pause("SWEEP_SETUP: Pause 10 sec. before continuing...");}
              if (!SweepSMRun) {
                SweepStateIndex = SWEEP_WAIT;
                break;
              }
            }
            if (debugging) {System.out.println("SWEEP_SETUP: Sweep start point reached. Bearing = " + bearing);}
            SweepStateIndex = SWEEP;
            if (debuggingpauses) {pause("SWEEP_SETUP: Pause 10 sec. before beginning sweep...");}
            break;

          // sweep 180°
          case SWEEP:
            System.gc();
            MotorRotations = 12;
            IncrementCounter = 0;
            MotorDirection = BACKWARD;
            motor.setDirection(MotorDirection);
            if (RIGHT == Hemisphere) {  // recalibrate while sweeping
              sensors.clearLimitAlarm();
              int MotorRevs = 0;
              while (!sensors.isLimitTriggered()) {
                IncrementCounter = 0;
                while (!sensors.isRotationTriggered()) {
                  motor.turnOneIncrement();
                  IncrementCounter++;
                  // safeguard the turret hardware
                  if (IncrementCounter > 20) {
                    if (debugging) {System.out.println("SWEEP: Possible rotation sensor failure...");}
                    break sweepstateloop;
                  }
                  sensors.refreshAlarms();
                  if (IncrementCounter < 2) {sensors.clearRotationAlarm();}
		  try { Thread.sleep(100); } catch (Exception e) {}
                }
                MotorRevs += 1;
                sensors.clearRotationAlarm();
                adjustbearing(true, false);
                // ignore false Limit Alarms
                if (sensors.isLimitTriggered()) {
                  if (MotorRevs < (MotorRotations-1)) {
                    sensors.clearLimitAlarm();
                  }
                }
                if (!SweepSMRun) {
                  SweepStateIndex = SWEEP_WAIT;
                  break;
                }
                if (debugging) {System.out.println("SWEEP: Bearing = " + bearing);}
              }
              sensors.clearLimitAlarm();
              setbearing(360.0);
              if (debugging) {System.out.println("SWEEP: Sweep complete.  Bearing = " + bearing);}
              SweepStateIndex = SWEEP_REWIND;
              if (debuggingpauses) {pause("SWEEP: Pause 10 sec. before beginning sweep rewind...");}
            } else {  // hemisphere = MIDDLE or LEFT
              for (int i = 0; i < MotorRotations; i++) {
                IncrementCounter = 0;
                while (!sensors.isRotationTriggered()) {
                  motor.turnOneIncrement();
                  IncrementCounter++;
                  // safeguard the turret hardware
                  if (IncrementCounter > 20) {
                    if (debugging) {System.out.println("SWEEP: Possible rotation sensor failure...");}
                    break sweepstateloop;
                  }
                  sensors.refreshAlarms();
                  if (IncrementCounter < 2) {sensors.clearRotationAlarm();}
                }
                sensors.clearRotationAlarm();
		adjustbearing(true, true);
                if (!SweepSMRun) {
                  SweepStateIndex = SWEEP_WAIT;
                  break;
                }
                if (debugging) {System.out.println("SWEEP: Bearing = " + bearing);}
                if (debuggingpauses) {pause("SWEEP: Pause 10 sec. before continuing...");}
		try { Thread.sleep(100); } catch (Exception e) {}
              }
              if (debugging) {System.out.println("SWEEP: Sweep complete.  Bearing = " + bearing);}
              SweepStateIndex = SWEEP_REWIND;
              if (debuggingpauses) {pause("SWEEP: Pause 10 sec. before beginning sweep rewind...");}
            }
            break;

          // rewind 180°
          case SWEEP_REWIND:
            System.gc();
            MotorRotations = 12;
            IncrementCounter = 0;
            MotorDirection = FORWARD;
            motor.setDirection(MotorDirection);
            // go back across limit & rotation sensors & ignore alarms
            for (int j = 0; j < 3; j++) {motor.turnOneIncrement();}
            sensors.clearLimitAlarm();
            sensors.clearRotationAlarm();

            // start rewind
            if (LEFT == Hemisphere)  // recalibrate while sweeping back
            {
              // calibrate turret (rewind until limit sensor closes)
              IncrementCounter = 0;
              MotorRotations = 0;
              if (debugging) {System.out.println("Turret Calibration is running");}
              sensors.refreshAlarms();
              sensors.clearLimitAlarm();
              sensors.clearRotationAlarm();
              System.gc();
              if (debugging) {System.out.println("SWEEP_REWIND: Starting...");}
              MotorDirection = FORWARD;
              motor.setDirection(MotorDirection);
              while (!sensors.isLimitTriggered())
              {
                motor.turnOneIncrement();
                sensors.refreshAlarms();
                if (sensors.isRotationTriggered())
                {
                  IncrementCounter++;
                  if (debugging) {System.out.println("SWEEP_REWIND: Rotation Alarm.");}
                  sensors.clearRotationAlarm();
                  adjustbearing(false, false);
                  if (debugging) {System.out.println("SWEEP_REWIND: Bearing = " + bearing);}
                }
                // safeguard the turret hardware
                if (IncrementCounter > 200) {
                  if (debugging) {System.out.println("SWEEP_REWIND: Possible limit sensor failure.");}
                  break;
                }
              }
              if (debugging) {System.out.println("SWEEP_REWIND: Limit Alarm Triggered.");}
              sensors.clearLimitAlarm();

              // move turret back across limit sensor
              System.gc();
              MotorDirection = BACKWARD;
              motor.setDirection(MotorDirection);
              for (int j = 0; j < 2; j++) {motor.turnOneIncrement();}
              sensors.clearLimitAlarm();
              sensors.clearRotationAlarm();
              setbearing(0.0);
              if (debugging) {System.out.println("SWEEP_REWIND: Calibration Complete. Bearing = " + bearing);}
              if (debugging) {pause("Short pause...");}
              SweepStateIndex = SWEEP;
            } else {  // hemisphere = MIDDLE or LEFT
              for (int i = 0; i < MotorRotations; i++) {
                IncrementCounter = 0;
                while (!sensors.isRotationTriggered())
                {
                  motor.turnOneIncrement();
                  IncrementCounter++;
                  // safeguard the turret hardware
                  if (IncrementCounter > 20) {
                    if (debugging) {System.out.println("SWEEP_REWIND: Possible rotation sensor failure...");}
                    break sweepstateloop;
                  }
                  // avoid erroneous "double close" of switch
                  sensors.refreshAlarms();
                  if (IncrementCounter < 2) {sensors.clearRotationAlarm();}
                }
                sensors.clearRotationAlarm();
                adjustbearing(true, true);
                if (!SweepSMRun) {
                  SweepStateIndex = SWEEP_WAIT;
                  break;
                }
                if (debugging) {System.out.println("SWEEP_REWIND:  Bearing = " + bearing);}
                if (debuggingpauses) {pause("SWEEP_REWIND: Pause 10 sec. then continue...");}
		try { Thread.sleep(100); } catch (Exception e) {}
              }
              // avoid the erroneous "double close" at the end of a sweep rewind
              MotorDirection = BACKWARD;
              motor.setDirection(MotorDirection);
              for (int j = 0; j < 2; j++) {motor.turnOneIncrement();}
              sensors.clearLimitAlarm();
              sensors.clearRotationAlarm();
              SweepStateIndex = SWEEP;
              if (debugging) {System.out.println("SWEEP_REWIND: Rewind complete.  Bearing = " + bearing);}
            }


//            break sweepstateloop;  // stops state machine after one iteration
            break;  // continuous sweep
        }  // end switch
      }  // end while
      if (debugging) {System.out.println(Thread.currentThread().getName() + " is finished.");}
    }  // end run()

  }  // end SweepStateMachine class



//  private class BearingStateMachine extends Thread {
  private class BearingStateMachine implements Runnable {

    private int BearingOfInterest = 0;

    public void setBearing (int NewBearing) {
      if (NewBearing < 0 || NewBearing > 360) {
        BearingOfInterest = 0;
        if (debugging) {System.out.println("Bearing must be between 0 and 360, inclusive.");}
        return;
      }
      if (360 == NewBearing) {NewBearing = 0;}
      if (!(0 == (NewBearing % TURRET_RESOLUTION))) {
        BearingOfInterest = 0;
        if (debugging) {System.out.println("Bearing must be a multiple of " + TURRET_RESOLUTION + ".");}
      } else {
        BearingOfInterest = NewBearing;
      }
      return;
    }

    public synchronized void setThreadState (int s) {
      BearingThreadState = s;
      if (s == RUN || s == STOP) {notify();}
    }

    private synchronized boolean checkThreadState() {
      while (BearingThreadState == SUSPEND) {
        try {
          wait();
        } catch (InterruptedException e) {
          // ignore
        }
      }
      if (BearingThreadState == STOP) {return false;}
      return true;
    }

    public void run() {
      int BearingStateIndex = BEARING_WAIT;
      int IncrementCounter = 0;
      int MotorRotations = 0;
      bearingstateloop: while (true)
      {
        if (debugging) {System.out.println(Thread.currentThread().getName() + " is running");}
        if ( !checkThreadState() ) {
          break bearingstateloop;
        }

        switch (BearingStateIndex) {
          // wait state
          case BEARING_WAIT:
            while (!BearingSMRun) {
              try {
                BT.sleep(250);
              } catch (InterruptedException e) {
                if (debugging) {System.out.println("BEARING_WAIT: Sleep interrupted on Bearing State Machine.");}
              }
            }
            BearingStateIndex = BEARING_FORWARD_SEEK;
            break;

          // rotate to bearing of interest
          case BEARING_FORWARD_SEEK:
            System.gc();
            MotorRotations = BearingOfInterest/TURRET_RESOLUTION;
            for (int i = 0; i < MotorRotations; i++) {
              IncrementCounter = 0;
              while (!sensors.isRotationTriggered())
              {
                motor.turnOneIncrement();
                IncrementCounter++;
                // safeguard the turret hardware
                if (IncrementCounter > 20) {
                  if (debugging) {System.out.println("BEARING_FORWARD_SEEK: Possible rotation sensor failure during seek...");}
                  break bearingstateloop;
                }
                sensors.refreshAlarms();
                if (IncrementCounter < 1) {sensors.clearRotationAlarm();}
              }
              sensors.clearRotationAlarm();
              adjustbearing(true, false);
              if (debugging) {System.out.println("BEARING_FORWARD_SEEK: Bearing = " + bearing);}
              if (debuggingpauses) {pause("BEARING_FORWARD_SEEK: Pause 10 sec. then continue...");}
              if (!BearingSMRun) {
                BearingStateIndex = BEARING_WAIT;
                break;
              }
            }
            if (debugging) {System.out.println("BEARING_FORWARD_SEEK: Bearing of interest reached.  Bearing = " + bearing);}
            break bearingstateloop;

          // rotate to bearing of interest
          case BEARING_BACKWARD_SEEK:
            System.gc();
            MotorRotations = (360 - BearingOfInterest)/TURRET_RESOLUTION;
            IncrementCounter = 0;
            sensors.clearLimitAlarm();
            sensors.clearRotationAlarm();

            for (int i = 0; i < MotorRotations; i++) {
              IncrementCounter = 0;
              while (!sensors.isRotationTriggered())
              {
                motor.turnOneIncrement();
                IncrementCounter++;
                // safeguard the turret hardware
                if (IncrementCounter > 20) {
                  if (debugging) {System.out.println("BEARING_BACKWARD_SEEK: Possible rotation sensor failure during seek...");}
                  break bearingstateloop;
                }
                // avoid erroneous "double close" of switch
                sensors.refreshAlarms();
                if (IncrementCounter < 2) {sensors.clearRotationAlarm();}
              }
              sensors.clearRotationAlarm();
              adjustbearing(true, false);
              if (!BearingSMRun) {
                BearingStateIndex = BEARING_WAIT;
                break;
              }
              if (debugging) {System.out.println("BEARING_BACKWARD_SEEK: Bearing = " + bearing);}
              if (debuggingpauses) {pause("BEARING_BACKWARD_SEEK: Pause 10 sec. then continue...");}
            }
            if (debugging) {System.out.println("BEARING_BACKWARD_SEEK: Bearing of interest reached.  Bearing = " + bearing);}
            break bearingstateloop;
        }  // end switch
      }  // end while
      if (debugging) {System.out.println(Thread.currentThread().getName() + " is finished.");}
    }  // end run()
  }  // end BearingStateMachine class

  public void pause(String S) {
    System.out.println(S);
    System.out.println(Thread.currentThread().getName() + " is sleeping...");
    try {
      Thread.currentThread().sleep(10000);
    } catch (InterruptedException e) {
      System.out.println(Thread.currentThread().getName() + " PAUSE: Sleep interrupted.");
    }
  }

  public void longpause(String S) {
    System.out.println(S);
    System.out.println(Thread.currentThread().getName() + " is sleeping...");
    try {
      Thread.currentThread().sleep(60000);  // one minute
    } catch (InterruptedException e) {
      System.out.println(Thread.currentThread().getName() + " PAUSE: Sleep interrupted.");
    }
  }
}