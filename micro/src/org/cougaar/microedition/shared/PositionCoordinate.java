package org.cougaar.microedition.shared;

public class PositionCoordinate
{
  public double latitude; //decimal degrees, North positive
  public double longitude; //decimal degrees, East positive

  public PositionCoordinate()
  {
    latitude = 0.0;
    longitude = 0.0;
  }
  public PositionCoordinate(double lat, double lon)
  {
    latitude = lat;
    longitude = lon;
  }
}
