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
public abstract class LocomotionResource extends ResourceAdapter {

  /**
   * @return the speed setting (in mm/sec)
   */
  public abstract long getSpeed();
  /**
   * @param newSpeed the speed (in mm/sec) that the robot will move forward or backward
   */
  public abstract void setSpeed(long newSpeed);

  public static final int CLOCKWISE = 0;
  public static final int COUNTER_CLOCKWISE = 1;
  /**
   * Rotate one tick in the given direction.
   * @param direction one of CLOCKWISE or COUNTER_CLOCKWISE
   * @param degrees the number of degrees to rotate in that direction
   */
  public abstract long[] rotate(int direction, long degrees);
  /**
   * Move forward at the requested speed
   * @see setSpeed(long)
   */
  public abstract long[] forward();
  /**
   * Move in reverse at the requested speed
   * @see setSpeed(long)
   */
  public abstract long[] backward();
  /**
   * Whoa!
   */
  public abstract long[] stop();
}
