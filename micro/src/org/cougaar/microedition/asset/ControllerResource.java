/*
 * <copyright>
 *  Copyright 1999-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.asset;

import java.util.*;

/**
 * Base class for all ControllerResources.
 */
public abstract class ControllerResource extends ResourceAdapter
{
  protected int chan = 0;
  protected String units = "";
  protected double scalingFactor = 1000.0;

  public abstract void getValues( double [] values); //more of a sensor resource item, keep for now
  public abstract void getValueAspects(int [] values); //more of a sensor resource item, keep for now
  public abstract int getNumberAspects();
  public boolean getSuccess() { return true; }
  public abstract void setChan(int c);
  public abstract void setUnits(String u);
  public abstract boolean conditionChanged(); //indicates state has changed since last read.

  public abstract void startControl();
  public abstract void stopControl();
  public abstract boolean isUnderControl(); //by anything
  public abstract void modifyControl(String controlparameter, String controlparametervalue);

  public int getChan() {
    return chan;
  }

  public String getUnits() {
    return units;
  }

  public double getScalingFactor() {
    return scalingFactor;
  }

 public void setScalingFactor(double sf) {
    scalingFactor = sf;
  }

  //this can be used to associated the resource to one item, task, object, etc.
  private String associated_id = "";
  public void setAssociation(String id)
  {
    associated_id = id;
  }

  public String getAssociation()
  {
    return associated_id;
  }

  public ControllerResource() {}

}
