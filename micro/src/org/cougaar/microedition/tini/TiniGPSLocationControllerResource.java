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
import javax.comm.*;

import org.cougaar.microedition.asset.LocationResource;
import org.cougaar.microedition.asset.ControllerResource;
import org.cougaar.microedition.shared.Constants;

/**
 * Title:        Differential GPS Resource
 * Description:  This class polls the RT-Star differential GPS receiver
 *               over a serial interface and maintains a private database
 *               of position properties.  Accessor methods are available to
 *               provide read-only access to latitude, longitude, altitude,
 *               GPS date/timestamp, number of sattelite vehicles used in
 *               the GPS solution, groundspeed, heading, velocities (N,E,V),
 *               and overall health.  The data are extracted from DGPS
 *               message 20 (See the BAEC All-Star User's manual for the
 *               message format).
 *                    - one paramater is "port" which defaults to "serial1".
 *                    - one parameter is "debug" which defaults to false.
 * @author       ww/jdg/dav
 * @version      1.0
 */

/**
 * Special Note: One must be careful when converting change in Lat/Lon into
 *               distance!  For latitude, it is a simple scalar conversion,
 *               but for Longitude....  see below.
 *
 * Convert Change In Lat/Lon into Feet
 *
 * Latitude - 1 minute of change in latitude is equal to 1 nautical mile, or
 * 6076.115486 feet. There are 60 minutes to one degree, so therefore there
 * are 364,560 feet to one degree change of latitude, or about 69 miles.
 *
 * Longitude - 1 minute of change in longitude equals 1 nautical mile times
 * cosine of latitude.
 *
 * Example - going straight west 30 minutes from the Lowe's store in Ames, IA
 * (located exactly at 42 degrees N lat) =
 *
 *       (30 * 6076.115...) * cos(42) = 135,460 ft, or 25.65 miles
 *
 * (somewhere near Boone/Greene county line, as if you cared.)
 */

public class TiniGPSLocationControllerResource extends ControllerResource implements LocationResource {

  private String portName = "serial1";
  private boolean debug = false;

  //private Date dgpsDate = new Date();
  private boolean dgpsHealthy=false;
  private short dgpsSatellites;
  private double dgpsHeading, dgpsLatitude, dgpsLongitude, dgpsAltitude;
  private float dgpsGroundSpeed, dgpsVelocityNorth, dgpsVelocityEast,
                dgpsVelocityVertical ;

  public TiniGPSLocationControllerResource() {}

  // accessor methods for DGPS properties

  public double getHeading() { // returns true bearing, in degrees
    return dgpsHeading;        // (zero is true North)
  }

  public double getAltitude() { // returns altitude, in meters above SL
    return dgpsAltitude;
  }

  public double getLongitude() { // returns Longitude, in degrees, where
    return dgpsLongitude;        // negative is Westward from Greenwich
  }

  public double getLatitude() { // returns Latitude, in degrees, where
    return dgpsLatitude;        // negative is South of the Equator
  }

  public short getNumSVs() { // returns the number of Satellite Vehicles
    return dgpsSatellites;   // used from the last DGPS message 20
  }

  public float getGroundSpeed() { // returns ground speed, in Meters/sec
    return dgpsGroundSpeed;       //
  }

  public float getVelocityNorth() { // returns Meters/sec
    return dgpsVelocityNorth;       // (true North vector)
  }

  public float getVelocityEast() { // returns Meters/sec
    return dgpsVelocityEast;       // (wrt true North)
  }

  public float getVelocityVertical() { // returns Meters/sec
    return dgpsVelocityVertical;       //
  }

  public boolean isHealthy() { // healthy if last DGPS message was good (ie,
    return dgpsHealthy;        // the number of Satellite Vehicles >3, and the
  }                            // year was valid)

  private int dgpsHour, dgpsMinute, dgpsDay, dgpsMonth, dgpsYear;
  private double dgpsSeconds;

  public Date getDate() {
  /**
   * On-demand parse the time data from the last message into a date
   */
    Calendar dgpsCal = Calendar.getInstance();
    dgpsCal.set(dgpsYear,dgpsMonth,dgpsDay,dgpsHour,dgpsMinute,(int)dgpsSeconds);
    //Date dgpsDate = new Date(dgpsYear,dgpsMonth,dgpsDay,dgpsHour,dgpsMinute,(int)dgpsSeconds);
    return dgpsCal.getTime();
  }

  public void getValues(long [] values)
  {
    values[0] = (long)(scalingFactor*getLatitude());
    values[1] = (long)(scalingFactor*getLongitude());
    //values[2] = getAltitude();
    values[2] = (long)(scalingFactor*getHeading());
  }

  public void getValueAspects(int [] aspects)
  {
    aspects[0] = Constants.Aspects.LATITUDE;
    aspects[1] = Constants.Aspects.LONGITUDE;
    //aspects[2] = Constants.Aspects.ALTITUDE;
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

  // Parameter handling
  public void setParameters(Hashtable params)
  {
    setName("TiniGPSLocationControllerResource");
    setScalingFactor((long)Constants.Geophysical.DEGTOBILLIONTHS);

    if (params != null) {
      if (params.get("port") != null) {
        portName = (String)params.get("port");
      }
      debug = (params.get("debug") != null);
    }
    if (debug)System.out.println("TiniGPSLocationControllerResource:setParams:"+params);
    startMonitorThread();
  }

  private void startMonitorThread() {
    Thread t = new Thread(new SerialManager());
    t.start();
  }

  /**
   *  A worker thread to service the serial port
   */
  private short ThisYear=2001;
  /*
   * @todo : make this not hard-coded
   */
  private class SerialManager implements Runnable {
    public void run() {
      /*
       * Open serial port input stream
       */
      InputStream input = null;

      if (debug) System.out.println("TiniGPSLocationControllerResource started on "+portName);

      if (portName.equals("serial1")) {
        com.dalsemi.system.TINIOS.enableSerialPort1();
      }
      try {
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
        SerialPort sp = (SerialPort)portId.open("GPS", 0);
        sp.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        sp.enableReceiveThreshold(2048);

        input = sp.getInputStream();
      } catch (Exception ex) {
        System.out.println("TiniGPSLocationControllerResource Error:"+ex);
        ex.printStackTrace();
      }

      if (debug) System.out.println("TiniGPSLocationControllerResource Starting");

      double PI=3.14159265358979;

      /*
       * Search for the beginning of Message #20 in the input stream
       */
      while (true) {
       try {
         int i=0;
         while(true) { // Identify the start of message 20
           int datum = input.read();
           if (datum == 1) { // SOH
             int byte2 = input.read();
             if (byte2 == 20) { // Msg 20
               int byte3 = input.read();
               if (byte3 == 235)  // complement of 20
                 break;
             }
           }
         }

         /*
          * Input the entire message
          */
         byte [] block = new byte[77]; //already read 3 bytes
         int count = input.read(block, 3, 74);

         /*
          * Extract the number of satellites and date/time from the message,
          * and use this information to determine the health of the message.
          */
         byte numSatellites = block[71];
         dgpsSatellites=(short)numSatellites;
         dgpsYear = (int)unsigned(block[16]) + (int)unsigned(block[17])*256;
         dgpsHealthy=(numSatellites>3) & (dgpsYear==ThisYear);
         dgpsMonth=block[15];
         dgpsDay=block[14];
         dgpsHour=block[4];
         dgpsMinute=block[5];
         byte [] dbl = new byte[8];
         System.arraycopy(block, 6, dbl, 0, 8);
         dgpsSeconds = makeDouble(dbl);

         /*
          * Extract the lat, lon, alt, speed, heading, velN, velE, and velV,
          * and place in the private database for use by the accessor methods.
          */
         System.arraycopy(block, 18, dbl, 0, 8);
         dgpsLatitude = (180.0/PI)*makeDouble(dbl);
         System.arraycopy(block, 26, dbl, 0, 8);
         dgpsLongitude = (180.0/PI)*makeDouble(dbl);
         byte [] flt = new byte[4];
         System.arraycopy(block, 34, flt, 0, 4);
         dgpsAltitude = (double)makeFloat(flt);
         System.arraycopy(block, 38, flt, 0, 4);
         dgpsGroundSpeed = makeFloat(flt);
         System.arraycopy(block, 42, flt, 0, 4);
         dgpsHeading = (180.0/PI)*(double)makeFloat(flt);
         System.arraycopy(block, 46, flt, 0, 4);
         dgpsVelocityNorth = makeFloat(flt);
         System.arraycopy(block, 50, flt, 0, 4);
         dgpsVelocityEast = makeFloat(flt);
         System.arraycopy(block, 54, flt, 0, 4);
         dgpsVelocityVertical = makeFloat(flt);

         if (debug) System.out.println("GPS: nSat: "+numSatellites+ " year:"+dgpsYear+" lat:"+dgpsLatitude+" long:"+dgpsLongitude);


       } catch (IOException ioe) {
         ioe.printStackTrace();
       } // try
      } // while
    } // public void run

    private long unsigned(byte byt) {
      return (long)byt&0xff;
    }

    /*
     * Method to make a double out of an unsigned long
     */
    private double makeDouble(byte [] bits) {
      long lng = 0;
      for (int i=0; i<8; i++) {
        lng |= (unsigned(bits[i])) << (i*8);
      }
      return Double.longBitsToDouble(lng);
    }

    /*
     * Method to make a float out of an unsigned int
     */
    private float makeFloat(byte [] bits) {
      int shrt = 0;
      for (int i=0; i<4; i++) {
        shrt |= (unsigned(bits[i])) << (i*8);
      }
      return Float.intBitsToFloat(shrt);
    }
  }

  public boolean getSuccess()
  {
    if (debug)
      System.out.println("NumSats: " + getNumSVs() + "  YearGiven: " + getDate().getYear() + "  YearNow: " + (new Date()).getYear() );
    if ( (getNumSVs() >= 4) && (getDate().getYear() == (new Date()).getYear()) )
      return true;
    else
      return false;
  }

  private void debugfunc()
  {
    debug = true;
    startMonitorThread();
  }

/*
  public static void main(String args[])
  {
    TiniGPSLocationControllerResource tgpsres = new TiniGPSLocationControllerResource();
    tgpsres.debugfunc();
  }
  */
}