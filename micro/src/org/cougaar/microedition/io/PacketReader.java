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

package org.cougaar.microedition.io;

import org.cougaar.microedition.util.*;
import java.io.*;

/**
 * This class handles listening on a server socket port in a second thread.
 * It should probably be refactored into one for server sockets and one for
 * input/output streams.
 */
public class PacketReader {

/**
 * This variable declares the InputStream to read from.
 */
  private InputStream bufr = null;

/**
 * This variable declares the Thread.
 */
  private Thread runner = null;

/**
 * This variable declares the private object that provides the run method.
 */
  private Retriever retriever = new Retriever();

/**
 * This variable stores the sender object, what to do with the stuff once we read it.
 */
  private MessageTransport deliverer = null;

/**
 * This variable holds the value of the server socket port on which I am to listen.
 */
  private int myListenPort;

/**
 * This private class provides the run method.
 */
  private class Retriever implements Runnable {

    public void run () {

      StringBuffer msg = null;
      int bite;

      try {
        ServerSocketME ss = null;
        if (bufr == null) {
          ss = (ServerSocketME) MicroEdition.getObjectME("org.cougaar.microedition.kvm.KvmServerSocketME", "org.cougaar.microedition.tini.TiniServerSocketME");
          ss.openServerSocket(myListenPort);
          System.out.println("Listening on " + myListenPort);
        } else {
          try {Thread.sleep(3000);}catch (InterruptedException ie){}
          System.out.println("Using registration message stream");
        }

        while (true) {
          try {
            if (ss != null) {
              bufr = ss.acceptInputStream();
            }
            msg = readMessage(bufr);
            if (ss != null) {
              bufr.close();
            }
            deliverMessage(msg);
          } catch (Exception ex) {
            System.err.println("SocketException:"+ex);
            if (ss == null) {
              System.out.println("Shutting down connection");
              return;
            }
          }
        }
      } catch (ClassNotFoundException cnfe) {
        System.err.println("Error configuring message recv: ClassNotFoundException");
        cnfe.printStackTrace();
      } catch (IllegalAccessException iae) {
        System.err.println("Error configuring message recv: IllegalAccessException");
        iae.printStackTrace();
      } catch (InstantiationException ie) {
        System.err.println("Error configuring message recv: InstantiationException");
        ie.printStackTrace();
      } catch (IOException ioe) {
        System.err.println("Error configuring message recv: IOException");
        ioe.printStackTrace();
      }
    }
  }

  private String getSource(String msg) {
    return msg.substring(0, msg.indexOf(":"));
  }

  private String getMessage(String msg) {
    return msg.substring(msg.indexOf(":")+1);
  }

  /**
   * This constructor takes the server socket port and saves it in an object variable.
   *
   * @param   port    server socket port on which I am to listen
   * @return  none
   */
  public PacketReader (int port) {
    myListenPort = port;
  }

  /**
   * This constructor takes the InputStream object and saves it in an object variable.
   *
   * @param   port    server socket port on which I am to listen
   * @return  none
   */
  public PacketReader (InputStream in) {
    this.bufr = in;
  }

  /**
   * This method saves the packet handler object.
   *
   * @param   ps    the MessageTransport object that will do something with the incoming message.
   * @return  none
   */
  public void setMessageTransport (MessageTransport ps) {
    deliverer = ps;
  }

  /**
   * This method startes the runner thread.
   *
   * @param   none
   * @return  none
   */
  public void start () {
    if (runner == null || !runner.isAlive()) {
      runner = new Thread(retriever);
      runner.start();
    }
  }

  private StringBuffer readMessage(InputStream in) throws IOException {
    StringBuffer msg = new StringBuffer();
    int bite;
    while (true) {
      bite = bufr.read();
      if (bite <= 0)
        break;
      msg.append((char)bite);
    }
    return msg;
  }

  private void deliverMessage(StringBuffer msg) {
    if (msg.length() > 0) {
      String message = msg.toString();
      String source = getSource(message);
      deliverer.takePacket(getMessage(message), source);
    }
  }
}
