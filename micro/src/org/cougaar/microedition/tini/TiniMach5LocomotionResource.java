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
import java.util.*;
import java.io.*;
import javax.comm.*;

/**
 *  CougaarME Resource for controlling a MACH5 robot base from a TINI board.
 */
public class TiniMach5LocomotionResource extends LocomotionResource {

  public TiniMach5LocomotionResource() {
  }

  private String portName = "serial1";

  /**
   *  The only paramater is "port" which defaults to "serial1"
   */
  public void setParameters(Hashtable params) {
    super.setParameters(params);
    if (params.get("port") != null) {
      portName = (String)params.get("port");
    }
    startMonitorThread();
  }

  private double speed = 0;
  public double getSpeed() {
    return speed;
  }
  public void setSpeed(double newSpeed){
    speed = newSpeed;
  }

  public void rotate(int direction){
    String msg = "";
    switch (direction) {
      case CLOCKWISE :
        msg = "SPR 1 -1\n";
        break;
      case COUNTER_CLOCKWISE :
        msg = "SPR -1 1\n";
        break;
      default:
        throw new IllegalArgumentException("LocomotionResource.rotate must be one of CLOCKWISE or COUNTERCLOCKWISE");
    }
    sendMsg(msg);
  }

  public void stop() {
    sendMsg("SV 0 0\n");
  }

  public void forward() {
    int spd = (int)speed;
    sendMsg("SV "+spd+" "+spd+"\n");
  }

  public void backward() {
    int spd = 0 - (int)speed;
    sendMsg("SV "+spd+" "+spd+"\n");
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

    org.cougaar.microedition.util.StringTokenizer toker = new org.cougaar.microedition.util.StringTokenizer(msg, " ");

    // format should be "> NNNNNN NNNNNN ....."
    int ntokens = toker.countTokens();
    if (ntokens >= 3) { //  enough tokens
      toker.nextToken();  // eat the ">"
      String left = toker.nextToken();
      String right = toker.nextToken();
      ret[0] = Long.parseLong(left);
      ret[1] = Long.parseLong(right);
    }
    return ret;
  }

  private Vector outgoingMsgs = new Vector();
  private Vector incomingMsgs = new Vector();


  private void sendMsg(String msg) {
    outgoingMsgs.addElement(msg);
    synchronized (outgoingMsgs) {
      outgoingMsgs.notifyAll();
    }
  }
  private String getMsg() {
    String ret = null;
    if (incomingMsgs.size() > 0) {
      synchronized (incomingMsgs) {
        ret = (String)incomingMsgs.elementAt(0);
        incomingMsgs.removeElementAt(0);
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

      if (portName.equals("serial1")) {
        com.dalsemi.system.TINIOS.enableSerialPort1();
      }
      try {
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
        SerialPort sp = (SerialPort)portId.open("MACH5", 0);
        sp.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        input = sp.getInputStream();
        output = sp.getOutputStream();
      } catch (Exception exp) {
        System.err.println("TiniMach5LocomotionResource: Error initializing serial port '"+portName+"'");
        exp.printStackTrace();
        return;
      }

       byte [] data = new byte[128];

       while (true) {
         /*
          * Write any pending output messages
          */
          String msg = null;
          synchronized (outgoingMsgs) {
            while (outgoingMsgs.isEmpty()) {
              try {
                outgoingMsgs.wait();
              } catch (InterruptedException ex) {}
            }
            msg = (String)outgoingMsgs.elementAt(0);
            outgoingMsgs.removeElementAt(0);
          }
          try {
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
               ch = (byte)input.read();
               data[dataptr++] = ch;
             }
           } catch (IOException ioe) {
             ioe.printStackTrace();
           }
           // only ones I'm interested in are the responses to the "QP"
           if (msg.startsWith("QP"))
             incomingMsgs.addElement(new String(data, 0, dataptr));
       }

    }


  }
/*
  public static void main (String argv[]) {
    TiniMach5LocomotionResource resource = new TiniMach5LocomotionResource();
    resource.setParameters(new Hashtable());

    resource.setSpeed(100.0);
    resource.forward();
    long [] wheels = resource.getWheelPositions();
    System.out.println("Wheels: "+wheels[0]+":"+wheels[1]);
  }
*/
}