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

/**
 * Asset for turret control.
 */

public class TiniTurretController extends TurretControllerResource {

  public static final double TURRETRESOLUTION = 15.0;  // 1 full motor rotation = 15° turret rotation
  public static final int TURRET_RESOLUTION = 15;
  private double bearing = -360.0;  // relative to robot

  private TiniMotorController motor;
  private static final int COAST = 0;
  private static final int BACKWARD = 1;
  private static final int FORWARD = 2;
  private static final int BRAKE = 3;
  private int MotorDirection = BRAKE;

  private TiniRotationAndLimitSensor sensors;

  private SweepStateMachine ssm;
  private boolean SweepSMRun = false;
  public static final int LEFT = 0;
  public static final int MIDDLE = 1;
  public static final int RIGHT = 2;
  private int Hemisphere = MIDDLE;
  public static final int SWEEP_WAIT = 0;
  public static final int SWEEP_CALIBRATION_REWIND = 1;
  public static final int SWEEP_CALIBRATION = 2;
  public static final int SWEEP_SETUP = 3;
  public static final int SWEEP = 4;
  public static final int SWEEP_REWIND = 5;

  private BearingStateMachine bsm;
  private boolean BearingSMRun = false;
  public static final int BEARING_WAIT = 0;
  public static final int BEARING_CALIBRATION_REWIND = 1;
  public static final int BEARING_FORWARD_SEEK = 2;
  public static final int BEARING_BACKWARD_SEEK = 3;

  // thread control
  private Thread ST;
  private int SweepThreadState = STOP;
  private Thread BT;
  private int BearingThreadState = STOP;
  static final int RUN = 0;
  static final int SUSPEND = 1;
  static final int STOP = 2;

  private boolean debugging = true;
  private boolean debuggingpauses = false;


  // test code
  public void main(String args[]) {
    int BearingToAcquire = 45;
    TiniTurretController TTC = new TiniTurretController();
    TTC.setHemisphere(LEFT);
    TTC.startScan();
    longpause("MAIN: main thread paused...");
    TTC.stopScan();

    TTC.setHemisphere(RIGHT);
    TTC.startScan();
    longpause("MAIN: main thread paused...");
    TTC.stopScan();


    TTC.startSeek(BearingToAcquire);
    longpause("MAIN: main thread paused...");
    TTC.stopSeek();


    if (debugging) {System.out.println(Thread.currentThread().getName() + " is finished.");}

  }


  public TiniTurretController() {
    try
    {
      motor = new TiniMotorController();
      motor.setDirection(MotorDirection);
      if (debugging) {motor.readHWStatus();}
      sensors = new TiniRotationAndLimitSensor();
      if (debugging) {sensors.readHWStatus();}
      if (debugging) {System.out.println("\nMotor and Sensors Initialized.");}
    }
    catch (Throwable t)
    {
      if (debugging) {System.out.println(t);}
    }
  }

  public boolean startScan() {
    if (BearingThreadState != STOP || SweepThreadState != STOP) {
      return false;
    }
    ssm = new SweepStateMachine();
    ST = new Thread(ssm);
    ST.setName("Sweep State Machine Thread");
    if (debugging) {System.out.println(ST.getName() + " is running.");}
    ST.start();
    ssm.setThreadState(RUN);
    SweepSMRun = true;  // kick off the Sweep State Machine
    return true;
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

  public double getBearing() {
    return bearing;
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

  public long getValue() {
    return 0;
  }

  public void setUnits(String newUnits) {
    return;
  }

  public void setChan(int newChannel) {
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
          wait();
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
          break sweepstateloop;
        }

        switch (SweepStateIndex) {

          case SWEEP_WAIT:
            while (!SweepSMRun) {
              try {
                ST.sleep(250);
              } catch (InterruptedException e) {
                if (debugging) {System.out.println("SLEEP_WAIT: Sleep interrupted.");}
              }
            }
            SweepStateIndex = SWEEP_CALIBRATION_REWIND;
            break;

          // calibrate turret (rewind until limit sensor closes)
          case SWEEP_CALIBRATION_REWIND:
            sensors.refreshAlarms();
            sensors.clearLimitAlarm();
            sensors.clearRotationAlarm();
            System.gc();
            if (debugging) {System.out.println("SWEEP_CALIBRATION_REWIND: Starting...");}
            MotorDirection = BACKWARD;
            motor.setDirection(MotorDirection);
            IncrementCounter = 0;
            while (!sensors.isLimitTriggered())
            {
              motor.turnOneIncrement();
              sensors.refreshAlarms();
              if (sensors.isRotationTriggered())
              {
                IncrementCounter++;
                if (debugging) {System.out.println("SWEEP_CALIBRATION_REWIND: Rotation Alarm.");}
                sensors.clearRotationAlarm();
                bearing = bearing - TURRETRESOLUTION;
                if (debugging) {System.out.println("SWEEP_CALIBRATION_REWIND: Bearing = " + bearing);}
              }
              // safeguard the turret hardware
              if (IncrementCounter > 200) {
                  if (debugging) {System.out.println("SWEEP_CALIBRATION_REWIND: Possible limit sensor failure.");}
                  break sweepstateloop;
              }
              if (!SweepSMRun) {
                SweepStateIndex = SWEEP_WAIT;
                break;
              }
            }
            if (debugging) {System.out.println("SWEEP_CALIBRATION_REWIND: Limit Alarm Triggered.");}
            sensors.clearLimitAlarm();
            SweepStateIndex = SWEEP_CALIBRATION;
            break;

          // move turret back across limit sensor
          case SWEEP_CALIBRATION:
            System.gc();
            MotorDirection = FORWARD;
            motor.setDirection(MotorDirection);
            for (int j = 0; j < 2; j++) {motor.turnOneIncrement();}
            sensors.clearLimitAlarm();
            sensors.clearRotationAlarm();
            bearing = 0.0;
            if (debugging) {System.out.println("SWEEP_CALIBRATION: Calibration Complete. Bearing = " + bearing);}
            SweepStateIndex = SWEEP_SETUP;
            break;

          // rotate to sweep starting position
          case SWEEP_SETUP:
            System.gc();
            MotorRotations = 0;
            if (LEFT == Hemisphere) {MotorRotations = 12;}
            else if (MIDDLE == Hemisphere) {MotorRotations = 6;}
            else if (RIGHT == Hemisphere) {MotorRotations = 0;}
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
              bearing = bearing + TURRETRESOLUTION;
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
            MotorDirection = FORWARD;
            motor.setDirection(MotorDirection);
            if (LEFT == Hemisphere) {
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
                }
                MotorRevs += 1;
                sensors.clearRotationAlarm();
                bearing = bearing + TURRETRESOLUTION;
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
              bearing = 360.0;
              if (debugging) {System.out.println("SWEEP: Sweep complete.  Bearing = " + bearing);}
              SweepStateIndex = SWEEP_REWIND;
              if (debuggingpauses) {pause("SWEEP: Pause 10 sec. before beginning sweep rewind...");}
            } else {  // hemisphere = MIDDLE or RIGHT
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
                bearing = bearing + TURRETRESOLUTION;
                if (!SweepSMRun) {
                  SweepStateIndex = SWEEP_WAIT;
                  break;
                }
                if (debugging) {System.out.println("SWEEP: Bearing = " + bearing);}
                if (debuggingpauses) {pause("SWEEP: Pause 10 sec. before continuing...");}
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
            MotorDirection = BACKWARD;
            motor.setDirection(MotorDirection);
            // go back across limit & rotation sensors & ignore alarms
            for (int j = 0; j < 4; j++) {motor.turnOneIncrement();}
            sensors.clearLimitAlarm();
            sensors.clearRotationAlarm();

            // start rewind
            if (RIGHT == Hemisphere)  // recalibrate instead of sweeping back
            {
              SweepStateIndex = SWEEP_WAIT;
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
                bearing = bearing - TURRETRESOLUTION;
                if (!SweepSMRun) {
                  SweepStateIndex = SWEEP_WAIT;
                  break;
                }
                if (debugging) {System.out.println("SWEEP_REWIND:  Bearing = " + bearing);}
                if (debuggingpauses) {pause("SWEEP_REWIND: Pause 10 sec. then continue...");}
              }
              // avoid the erroneous "double close" at the end of a sweep rewind
              MotorDirection = FORWARD;
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
            BearingStateIndex = BEARING_CALIBRATION_REWIND;
            break;

          // calibrate turret (rewind until limit sensor closes)
          case BEARING_CALIBRATION_REWIND:
            MotorDirection = BACKWARD;
            motor.setDirection(MotorDirection);
            sensors.refreshAlarms();
            sensors.clearLimitAlarm();
            sensors.clearRotationAlarm();
            System.gc();
            if (debugging) {System.out.println("BEARING_CALIBRATION_REWIND: Starting rewind.");}
            MotorDirection = BACKWARD;
            motor.setDirection(MotorDirection);
            IncrementCounter = 0;
            while (!sensors.isLimitTriggered())
            {
              motor.turnOneIncrement();
              sensors.refreshAlarms();
              if (sensors.isRotationTriggered())
              {
                IncrementCounter++;
                if (debugging) {System.out.println("BEARING_CALIBRATION_REWIND: Rotation Alarm.");}
                sensors.clearRotationAlarm();
                bearing = bearing - TURRETRESOLUTION;
                if (debugging) {System.out.println("BEARING_CALIBRATION_REWIND: Bearing = " + bearing);}
              }
              // safeguard the turret hardware
              if (IncrementCounter > 200) {
                  if (debugging) {System.out.println("BEARING_CALIBRATION_REWIND: Possible limit sensor failure.");}
                  break bearingstateloop;
              }
              if (!BearingSMRun) {
                BearingStateIndex = BEARING_WAIT;
                break;
              }
            }
            if (debugging) {System.out.println("BEARING_CALIBRATION_REWIND: Limit Alarm Triggered.");}
            sensors.clearLimitAlarm();
            if (0 <= BearingOfInterest && 180 > BearingOfInterest) {
              // move motor forward one increment (back across limit sensor)
              System.gc();
              MotorDirection = FORWARD;
              motor.setDirection(MotorDirection);
              for (int j = 0; j < 2; j++) {motor.turnOneIncrement();}
              bearing = 0.0;
              BearingStateIndex = BEARING_FORWARD_SEEK;
            } else if (180 <= BearingOfInterest && 360 > BearingOfInterest) {
              // calibration is now complete
              bearing = 360.0;
              BearingStateIndex = BEARING_BACKWARD_SEEK;
            } else {
              if (debugging) {System.out.println("BEARING_CALIBRATION_REWIND: Invalid Bearing.");}
              bearing = 0.0;
              BearingStateIndex = BEARING_WAIT;
            }
            sensors.clearLimitAlarm();
            sensors.clearRotationAlarm();
            if (debugging) {System.out.println("BEARING_CALIBRATION_REWIND: Calibration Complete.  Bearing = " + bearing);}
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
              bearing = bearing + TURRETRESOLUTION;
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
              bearing = bearing - TURRETRESOLUTION;
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