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

package org.cougaar.microedition.tini;

import java.util.*;
import java.io.*;
import java.lang.*;
import javax.comm.*;

import org.cougaar.microedition.asset.LocationResource;
import org.cougaar.microedition.asset.ControllerResource;
import org.cougaar.microedition.shared.Constants;


public class TiniBogusLocationResource extends ControllerResource implements LocationResource
{

  private double longitude = 0.0;
  private double latitude = 0.0;
  private double heading = 0.0;
  private double altitude = 0.0;
  private String myName = "";
  private Hashtable attrtable = null;

  /**
   * Constructor.  Sets name default.
   */
  public TiniBogusLocationResource() {}

  public void setName(String n) {
    myName = n;
  }

  public String getName() {
    return myName;
  }

  public Hashtable getParameters()
  {
    return attrtable;
  }

  public void setParameters(Hashtable params)
  {
    setName("TiniBogusLocationResource");
    setScalingFactor((long)Constants.Geophysical.DEGTOBILLIONTHS);
    attrtable = params;
    if (params != null)
    {
      if (params.get("Latitude") != null)
      {
	String pstr = (String)params.get("Latitude");
        Double temp = new Double(pstr);
        latitude = temp.doubleValue();
      }
      if (params.get("Longitude") != null)
      {
	String pstr = (String)params.get("Longitude");
        Double temp = new Double(pstr);
        longitude = temp.doubleValue();
      }
      if (params.get("Heading") != null)
      {
	String pstr = (String)params.get("Heading");
        Double temp = new Double(pstr);
        heading = temp.doubleValue();
      }

      if (params.get("Altitude") != null)
      {
	String pstr = (String)params.get("Altitude");
        Double temp = new Double(pstr);
        altitude = temp.doubleValue();
      }
    }
  }

  public void getValues(long [] values)
  {
    values[0] = (long)(scalingFactor*getLatitude());
    values[1] = (long)(scalingFactor*getLongitude());
    values[2] = (long)(scalingFactor*getHeading());
  }

  public void getValueAspects(int [] aspects)
  {
    aspects[0] = Constants.Aspects.LATITUDE;
    aspects[1] = Constants.Aspects.LONGITUDE;
    aspects[2] = Constants.Aspects.HEADING;
  }

  public int getNumberAspects()
  {
    return 3;
  }

  public void setChan(int c) {}
  public void setUnits(String u) {}
  public boolean conditionChanged() {return true;}

  private boolean isundercontrol = false;

  public void startControl()
  {
    isundercontrol = true;
  }

  public void stopControl()
  {
    isundercontrol = false;
  }

  public boolean isUnderControl()
  {
    return isundercontrol;
  }

  public void modifyControl(String controlparameter, String controlparametervalue)
  {

  }

  public boolean getSuccess()
  {
    return true;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public double getAltitude()
  {
    return altitude;
  }

  public double getHeading()
  {

    return heading;
  }

  public Date getDate()
  {
    return new Date();
  }
}