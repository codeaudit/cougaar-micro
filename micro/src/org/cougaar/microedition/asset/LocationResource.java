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
 * This resource maintains position information like that available from
 * a GPS receiver.
 */
public interface LocationResource extends Resource
{
  /**
   * Read the latitude. Negative values are south.
   * @return the current latitude.
   */
  public double getLatitude();
  /**
   * Read the longitude. Negative values are west.
   * @return the current longitude.
   */
  public double getLongitude();
  /**
   * Read the altitude.
   * @return the current altitude.
   */
  public double getAltitude();
  /**
   * Get the current heading (orientation) of the vehicle.
   */
  public double getHeading();

  /**
   * Get the last time value.
   */
  public java.util.Date getDate();

}