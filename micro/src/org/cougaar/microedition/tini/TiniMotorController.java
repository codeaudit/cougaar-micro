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
import org.cougaar.microedition.tini.DS2406;


/**
 * Asset for motor control.
 */

public class TiniMotorController extends MotorControllerResource {

  private static DS2406 controlInputs;
  private int direction = 0;  // 0 = coast, 1 = backward, 2 = forward, 3 = brake
  private boolean debugging = false;
  private static final int IN1 = 0;
  private static final int IN2 = 1;
  private static final int COAST = 0;
  private static final int BACKWARD = 1;
  private static final int FORWARD = 2;
  private static final int BRAKE = 3;

  // test code
/*
  public void main(String args[]) {
    TiniMotorController TMC = new TiniMotorController();
    TMC.setDirection(COAST);
    TMC.start();
    TMC.stop();

    TMC.setDirection(BACKWARD);
    TMC.start();
    TMC.stop();

    TMC.setDirection(FORWARD);
    TMC.start();
    TMC.stop();

    TMC.setDirection(BRAKE);
    TMC.start();
    TMC.stop();

    TMC.setDirection(FORWARD);
    TMC.turnOneIncrement();

    TMC.setDirection(BACKWARD);
    TMC.turnOneIncrement();

    if (debugging) {TMC.readHWStatus();}
  }
*/

  public TiniMotorController() {
    try
    {
      controlInputs = new DS2406();
      if (debugging) {System.out.println("\nSwitches Initialized...");}
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  public void setDirection(int newDirection) {
    if (newDirection >= COAST && newDirection <= BRAKE) {
      direction = newDirection;
    } else {
      direction = COAST;
    }
  }

  public void turnOneIncrement() {
    try {
      switch (direction) {
        case 1:
          controlInputs.setSwitch(IN2, true);
          controlInputs.setSwitch(IN1, false);
          controlInputs.setSwitch(IN1, true);
          break;
        case 2:
          controlInputs.setSwitch(IN1, true);
          controlInputs.setSwitch(IN2, false);
          controlInputs.setSwitch(IN2, true);
          break;
      }
      return;
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  public void start() {
    try {
      switch (direction) {
        case 0:
          controlInputs.setSwitch(IN1, false);
          controlInputs.setSwitch(IN2, false);
          break;
        case 1:
          controlInputs.setSwitch(IN1, false);
          controlInputs.setSwitch(IN2, true);
          break;
        case 2:
          controlInputs.setSwitch(IN1, true);
          controlInputs.setSwitch(IN2, false);
          break;
        case 3:
          controlInputs.setSwitch(IN1, true);
          controlInputs.setSwitch(IN2, true);
          break;
      }
      return;
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  public void stop() {
    try {
      controlInputs.setSwitch(IN1, true);
      controlInputs.setSwitch(IN2, true);
      return;
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  public void readHWStatus() {
    try {
      controlInputs.reportSwitch(IN1);
      controlInputs.reportSwitch(IN2);
      return;
    }
    catch (Throwable t)
    {
      System.out.println(t);
    }
  }

  public double getValue() {
    return 0.0;
  }

  public void setUnits(String newUnits) {
    return;
  }

  public void setChan(int newChannel) {
    return;
  }
}