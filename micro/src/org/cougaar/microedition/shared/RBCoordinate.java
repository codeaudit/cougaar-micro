package org.cougaar.microedition.shared;

public class RBCoordinate
{
  public double range; //meters
  public double bearing; //degrees CW from True North

  public RBCoordinate()
  {
    range = 0.0;
    bearing = 0.0;
  }

  public RBCoordinate(double r, double b)
  {
    range = r;
    bearing = b;
  }
}