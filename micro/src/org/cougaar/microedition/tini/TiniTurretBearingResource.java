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

import org.cougaar.microedition.asset.*;
import org.cougaar.microedition.shared.Constants;
import java.util.*;
import java.io.*;
import java.lang.*;
import javax.comm.*;

//this class keeps track of where the robot was in terms of
//relative coordinates the last time the command state changed


public class TiniTurretBearingResource extends ControllerResource
{

  /**
   * Constructor.  Sets name default.
   */
  public TiniTurretBearingResource() {  }

  private double fixedturretbearing = 0.0;

  public void setParameters(Hashtable params)
  {
    setName("TiniTurretBearingResource");
  }

  public void getValues(double [] values)
  {
    values[0] = fixedturretbearing;
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
  public boolean conditionChanged() {return true;} //always report heading

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
    if(controlparameter.equalsIgnoreCase("bearing"))
    {
      Double temp = new Double(controlparametervalue);
      fixedturretbearing = temp.doubleValue();
      System.out.println("TiniTurretBearingResource: modifyControl bearing value: " +fixedturretbearing);
    }

    if(controlparameter.equalsIgnoreCase(Constants.Robot.prepositions[Constants.Robot.TURRETDIRECTIONPREP]))
    {
      if(controlparametervalue.equalsIgnoreCase(Constants.Robot.SEARCHRIGHT))
	System.out.println("TiniTurretBearingResource: modifyControl set RIGHT hemisphere");
      else if(controlparametervalue.equalsIgnoreCase(Constants.Robot.SEARCHLEFT))
	System.out.println("TiniTurretBearingResource: modifyControl set LEFT hemisphere");
      else
        System.out.println("TiniTurretBearingResource: modifyControl set MIDDLE hemisphere");
    }
  }
}