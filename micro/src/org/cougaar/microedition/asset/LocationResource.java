package org.cougaar.microedition.asset;

/**
 * Title:        Your Product Name
 * Description:  Your description
 * Copyright:    Copyright (c) 1999
 * Company:      Your Company
 * @author
 * @version
 */

public interface LocationResource extends Asset
{
  public double getLatitude();
  public double getLongitude();
  public double getAltitude();
  public double getHeading();

  public java.util.Date getDate();

}