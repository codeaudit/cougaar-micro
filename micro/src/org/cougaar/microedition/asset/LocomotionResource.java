/*
 * <copyright>
 * Copyright 1997-2000 Defense Advanced Research Projects Agency (DARPA)
 * and ALPINE (A BBN Technologies (BBN) and Raytheon Systems Company
 * (RSC) Consortium). This software to be used in accordance with the
 * COUGAAR license agreement.  The license agreement and other
 * information on the Cognitive Agent Architecture (COUGAAR) Project can
 * be found at http://www.cougaar.org or email: info@cougaar.org.
 * </copyright>
 */

package org.cougaar.microedition.asset;

/**
 * This resource controls a two-wheeled robot base.  Note that the speed
 * can only be set while the robot is stopped.
 */
public abstract class LocomotionResource extends Resource {

  /**
   * @return the speed setting (in mm/sec)
   */
  public abstract double getSpeed();
  /**
   * @param newSpeed the speed (in mm/sec) that the robot will move forward or backward
   */
  public abstract void setSpeed(double newSpeed);

  public static final int CLOCKWISE = 0;
  public static final int COUNTER_CLOCKWISE = 1;
  /**
   * Rotate one tick in the given direction.
   * @param direction one of CLOCKWISE or COUNTER_CLOCKWISE
   */
  public abstract void rotate(int direction);
  /**
   * Move forward at the requested speed
   * @see setSpeed(double)
   */
  public abstract void forward();
  /**
   * Move in reverse at the requested speed
   * @see setSpeed(double)
   */
  public abstract void backward();
  /**
   * Whoa!
   */
  public abstract void stop();
}