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

interface Mach5Command
{
    static String [] cmdstring = { "STOP", "GO FORWARD", "GO BACKWARD","ROTATE","READSTATE" };
    static int STOP = 0;
    static int GOFORWARD = 1;
    static int GOBACKWARD = 2;
    static int ROTATE = 3;
    static int READSTATE = 4;
}

/*

This structure keeps track of where the Mach 5 is in a relative coordinate frame.
It assumes that the relative coordinate frame is defined by the robots position and
orientation in a absolute coordinate frame at start up. For example, the robotbase
will start at Xrel = Yrel = 0 and ThetaRel = 90.0

*/
class Mach5RelativePositionData
{
  public Mach5RelativePositionData()
  {
    leftwheelpos = 0;
    rightwheelpos = 0;
    Xrel = Yrel = 0.0;
    Thetarel = Math.PI/2.0;
    specifiedrotation = 0.0;
    lastcommand = Mach5Command.STOP;
  }

  public long leftwheelpos; //Mach 5 wheel position in mm
  public long rightwheelpos;
  public double Xrel; // X position (mm) as measured from start point
  public double Yrel; //Y position (mm) as measured from start point
  public double Thetarel; //orientation measure from X axis, radians
  public double specifiedrotation; //set when ordered to rotate
  public int lastcommand; //the last command issued when updated
}

/**
 *  CougaarME Resource for controlling a MACH5 robot base from a TINI board.
 *  Parameters are:
 *  port=(serial0 or serial1)
 *    If you use serial 0, you must do a downserver -s to kill the console server
 *    If you use serial 1, you must move the jumper JP4 on the STEP board to the "RS232" position
 *  debug=
 *    If you define debug, you get loads of console text output
 *
 *  The cable for the TINI <--> Mach5 should be null modem, specifically:
 *  TINI   Mach5
 *   1  ---  1
 *   2  ---  3
 *   3  ---  2
 *   7  ---  7
 */
public class TiniMach5LocomotionResource extends ControllerResource
{

  /**
   * Constructor.  Sets name default.
   */
  public TiniMach5LocomotionResource()
  {

  }

  private String portName = "serial1";
  private boolean debug = false;
  private Mach5RelativePositionData laststatechange = new Mach5RelativePositionData();
  static final double TICKSPERDEGREE = 2.25; //experimentally determined
  //static final double TICKSPERDEGREE = 2.1685716; //computed from wheel base
  public static final int CLOCKWISE = 0;
  public static final int COUNTER_CLOCKWISE = 1;

  /**
   *  The only paramater is "port" which defaults to "serial1"
   */
  public void setParameters(Hashtable params)
  {
    setName("TiniMach5LocomotionResource");

    if (params != null)
    {
      if (params.get("port") != null)
      {
        portName = (String)params.get("port");
      }
      debug = (params.get("debug") != null);
    }

    startMonitorThread();
  }

  private double speed = 0;

  public long getSpeed()
  {
    return (long)speed;
  }

  public void setSpeed(long newSpeed)
  {
    speed = newSpeed;
  }

  public void  rotate(int direction, long degrees)
  {
    UpdatePositionState(Mach5Command.ROTATE);

    String msg = "";
    int ticks = (int)((double)degrees * TICKSPERDEGREE); // experimentally determined
    switch (direction)
    {
      case CLOCKWISE :
        msg = "SPR "+ticks+" -"+ticks+"\n";
	laststatechange.specifiedrotation = -1.0*degrees;
        break;
      case COUNTER_CLOCKWISE :
        msg = "SPR -"+ticks+" "+ticks+"\n";
	laststatechange.specifiedrotation = degrees;
        break;
      default:
        throw new IllegalArgumentException("LocomotionResource.rotate must be one of CLOCKWISE or COUNTERCLOCKWISE");
    }
    sendMsg(msg);
  }

  public void stop() {
    UpdatePositionState(Mach5Command.STOP);
    sendMsg("SV 0 0\n");
  }

  public void  forward() {
    int spd = (int)speed;
    if(spd == 0)
       stop();
    else
    {
      UpdatePositionState(Mach5Command.GOFORWARD);
      sendMsg("SV "+spd+" "+spd+"\n");
    }
  }

  public void backward() {
    int spd = 0 - (int)speed;
    if(spd == 0)
      stop();
    else
    {
      UpdatePositionState(Mach5Command.GOBACKWARD);
      sendMsg("SV "+spd+" "+spd+"\n");
    }
  }

  private Thread owner = null;

  public synchronized void UpdatePositionState(int newcommand)
  {
   if(owner != null)
   {
    System.out.println("Waiting to UpdatePositionState...");
    try {
      wait();
    }
    catch (Exception e) {
      System.out.println("UpdatePositionState: Exception on wait");
    }
    owner = Thread.currentThread();
   }

   long [] wheelpos = getWheelPositions();
   if(wheelpos == null) return;

   //compute change in wheel positioning since last command
   long diffleft = wheelpos[0] - laststatechange.leftwheelpos;
   long diffright = wheelpos[1] - laststatechange.rightwheelpos;
   double mmrange = 0.0;

   switch(laststatechange.lastcommand)
   {
    case Mach5Command.STOP:
	 break;
    case Mach5Command.GOFORWARD:
    case Mach5Command.GOBACKWARD:
	 //compute how far we have come
	 //the diff should be the same on each wheel
	 if(diffleft != diffright)
	 {
	    System.out.println("Different range indications " +diffleft +" " +diffright);
	    mmrange = (diffleft + diffright) * 0.5;
	 }
	 else
	 {
	   mmrange = diffleft;
	 }

	 laststatechange.Xrel += mmrange * TiniTrig.tinicos(laststatechange.Thetarel);
	 laststatechange.Yrel += mmrange * TiniTrig.tinisin(laststatechange.Thetarel);

         break;
    case Mach5Command.ROTATE:
	 if((diffright + diffleft) != 0)
	 {
	    System.out.println("Different rotation indications " +diffleft +" " +diffright);
	 }
	 mmrange = (diffright - diffleft)*0.5; //averages the absolute value

	 //double degreesrotated = mmrange/TICKSPERDEGREE;
	 double degreesrotated = laststatechange.specifiedrotation; //because the wheels report wrong

	 laststatechange.Thetarel += (degreesrotated*(Math.PI/180.0));
	 if(laststatechange.Thetarel >= (Math.PI*2.0)) laststatechange.Thetarel -= (Math.PI*2.0);
	 if(laststatechange.Thetarel < 0.0) laststatechange.Thetarel += (Math.PI*2.0);
    	 break;
   }

   laststatechange.lastcommand = newcommand;
   laststatechange.leftwheelpos = wheelpos[0];
   laststatechange.rightwheelpos = wheelpos[1];

   if (debug) System.out.println("Current relative state: "
			       +Mach5Command.cmdstring[laststatechange.lastcommand] +" "
			       +laststatechange.rightwheelpos +" "
			       +laststatechange.leftwheelpos +" "
			       +laststatechange.Xrel +" "
			       +laststatechange.Yrel +" "
			       +laststatechange.Thetarel*(180.0/Math.PI));

   owner = null;
   notifyAll();
  }

  public synchronized Mach5RelativePositionData ReadCurrentState()
  {
   if(owner != null)
   {
    System.out.println("Waiting to ReadCurrentState...");
    try {
      wait();
    }
    catch (Exception e) {
      System.out.println("ReadCurrentState: Exception on wait");
    }
    owner = Thread.currentThread();
   }

   Mach5RelativePositionData ret = new Mach5RelativePositionData();

   long [] wheelpos = getWheelPositions();
   if(wheelpos == null)
     return laststatechange;

   //compute change in wheel positioning since last command
   long diffleft = wheelpos[0] - laststatechange.leftwheelpos;
   long diffright = wheelpos[1] - laststatechange.rightwheelpos;
   double mmrange = 0.0;

   switch(laststatechange.lastcommand)
   {
    case Mach5Command.GOFORWARD:
    case Mach5Command.GOBACKWARD:
	 //how far are we along this leg
	 mmrange = (diffleft + diffright) * 0.5;
	 ret.Xrel = laststatechange.Xrel + mmrange * TiniTrig.tinicos(laststatechange.Thetarel);
	 ret.Yrel = laststatechange.Yrel + mmrange * TiniTrig.tinisin(laststatechange.Thetarel);
	 ret.Thetarel = laststatechange.Thetarel;
         break;
    case Mach5Command.ROTATE:
	 //include last rotation
	 ret.Xrel = laststatechange.Xrel;
	 ret.Yrel = laststatechange.Yrel;

	 //mmrange = (diffright - diffleft)*0.5; //averages the absolute value
	 //double degreesrotated = mmrange/TICKSPERDEGREE; //positive = CW
	 ret.Thetarel = laststatechange.Thetarel + (laststatechange.specifiedrotation*(Math.PI/180.0));
	 if(ret.Thetarel >= (Math.PI*2.0)) ret.Thetarel -= (Math.PI*2.0);
	 if(ret.Thetarel < 0.0) ret.Thetarel += (Math.PI*2.0);
    	 break;
   }

   owner = null;
   notifyAll();

   return ret;
  }

  /**
   * Gets the positions of the wheels since startup.
   * @return the left wheel position in [0], the right in [1]
   */
  public long [] getWheelPositions(){
    long [] ret = new long[2];
    ret[0] = 0;
    ret[1] = 0;

    sendMsg("QP\n");
    String msg = getMsg();
    while (msg == null) {
      try { Thread.sleep(100);} catch (InterruptedException ie){}
      msg = getMsg();
    }

    try {
      org.cougaar.microedition.util.StringTokenizer toker = new org.cougaar.microedition.util.StringTokenizer(msg.trim(), " ");

      // format should be "> NNNNNN NNNNNN ....."
      int ntokens = toker.countTokens();
      if (ntokens >= 3) { //  enough tokens
        toker.nextToken();  // eat the ">"
        String left = toker.nextToken();
        String right = toker.nextToken();
        ret[0] = Long.parseLong(left);
        ret[1] = Long.parseLong(right);
      }
    } catch (Exception nfe) { // error parsing wheel position text
      System.out.println("getWheelPositions Exception " +nfe);
      ret = null;
    }
    return ret;
  }

  private Vector outgoingMsgs = new Vector();
  private Vector incomingMsgs = new Vector();


  private void sendMsg(String msg) {
    synchronized (outgoingMsgs) {
      outgoingMsgs.addElement(msg);
      if (debug) System.out.println("sendMsg: outQ= "+outgoingMsgs);
      outgoingMsgs.notifyAll();
    }
  }
  private String getMsg() {
    String ret = null;
    if (incomingMsgs.size() > 0) {
      synchronized (incomingMsgs) {
        ret = (String)incomingMsgs.elementAt(0);
        incomingMsgs.removeElementAt(0);
	if (debug) System.out.println("getMsg: inQ= "+incomingMsgs);
      }
    }
    return ret;
  }

  private void startMonitorThread() {
    Thread t = new Thread(new SerialManager());
    t.start();
  }

  /**
   *  A worker thread to service the serial port
   */
  private class SerialManager implements Runnable {
    public void run() {
      /*
       * Open serial port input and output streams
       */
//       InputStream input = System.in;
//       OutputStream output = System.out;

       InputStream input;
       OutputStream output;

      if (debug) System.out.println("TiniMach5LocomotionResource started on "+portName);
      if (portName.equals("serial1")) {
        com.dalsemi.system.TINIOS.enableSerialPort1();
      }
      try {
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
        SerialPort sp = (SerialPort)portId.open("MACH5", 0);
        sp.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        sp.enableReceiveTimeout(1000);

        input = sp.getInputStream();
        output = sp.getOutputStream();
      } catch (Exception exp) {
        System.err.println("TiniMach5LocomotionResource: Error initializing serial port '"+portName+"'");
        exp.printStackTrace();
        return;
      }

       byte [] data = new byte[128];

       forever:
       while (true) {
         /*
          * Write any pending output messages
          */
          String msg = null;
          synchronized (outgoingMsgs) {
            while (outgoingMsgs.isEmpty()) {
              try {
                System.gc();
                if (debug) System.out.println("Waiting on "+outgoingMsgs);
                outgoingMsgs.wait();
                if (debug) System.out.println("DONE Waiting on "+outgoingMsgs);
              } catch (InterruptedException ex) {}
            }
            msg = (String)outgoingMsgs.elementAt(0);
            outgoingMsgs.removeElementAt(0);
          }
          try {
            if (debug) System.out.println("SEND:"+msg);
            output.write(msg.getBytes());
            output.flush();
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }

          /*
           * Read until the terminal character is received
           */
           int dataptr = 0;
           try {
             byte ch = 0;
             while (ch != 10) { // line-feed
               int datum = input.read();
               if (datum < 0) { // read timeout
                 if (debug) System.out.println("#### Serial port timeout");
                 continue forever;
               }
               ch = (byte)datum;
               data[dataptr++] = ch;
             }
           } catch (IOException ioe) {
             ioe.printStackTrace();
           }
            if (debug) System.out.println("RECV:"+new String(data));
           // only ones I'm interested in are the responses to the "QP"
           if (msg.startsWith("QP"))
             incomingMsgs.addElement(new String(data, 0, dataptr));
       }

    }


  }
/* */
  public static void main (String argv[]) {
    TiniMach5LocomotionResource resource = new TiniMach5LocomotionResource();
    resource.setParameters(new Hashtable());

    resource.setSpeed(100);
    resource.forward();
    System.out.println("forward...");

    long [] wheels = resource.getWheelPositions();
    System.out.println("Wheels: "+wheels[0]+":"+wheels[1]);

    try { Thread.sleep(1000); } catch (Exception ex) {}
    System.out.println("Stop now.");
    resource.stop();
    wheels = resource.getWheelPositions();
    System.out.println("Wheels: "+wheels[0]+":"+wheels[1]);

    System.out.println("Wait a second.");
    try { Thread.sleep(1000); } catch (Exception ex) {}
    System.out.println("Exit.");
    System.exit(0);


  }

  public void getValues(long [] values)
  {

  }

  public void getValueAspects(int [] aspects)
  {

  }

  public int getNumberAspects()
  {
    return 0;
  }

  public void setChan(int c) {}
  public void setUnits(String u) {}
  public boolean conditionChanged() {return true;} //always report heading

  private boolean isundercontrol = false;

  public void startControl()
  {
    forward();
    isundercontrol = true;
  }

  public void stopControl()
  {
    stop();
    isundercontrol = false;
  }

  public boolean isUnderControl()
  {
    return isundercontrol;
  }

  public void modifyControl(String controlparameter, String controlparametervalue)
  {
    try
    {
      if(controlparameter.equalsIgnoreCase(Constants.Robot.prepositions[Constants.Robot.VELOCITYPREP]))
      {
	Double temp = new Double(controlparametervalue);
	setSpeed((long)temp.doubleValue());
	System.out.println("TiniMach5LocomotionResource: speed set: " +getSpeed());
      }

      if(controlparameter.equalsIgnoreCase(Constants.Robot.prepositions[Constants.Robot.ORIENTATIONPREP]))
      {
	Double temp = new Double(controlparametervalue);
	double degrees = temp.doubleValue();
	System.out.println("TiniMach5LocomotionResource: set orientation: " +degrees +" degrees");
	stop();
	if(degrees < 0)
	  rotate(COUNTER_CLOCKWISE, (long)(-degrees));
	else
	  rotate(CLOCKWISE, (long)(degrees));
      }
    }
    catch (Exception ex) {}
  }
}