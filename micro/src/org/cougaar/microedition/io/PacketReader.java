/*
 * PacketReader.java
 *
 * Copyright 2000 by BBN Technologies, LLC. All Rights Reserved
 *
 */

package org.cougaar.microedition.io;

import org.cougaar.microedition.util.*;
import java.io.*;

/**
 * This class handles listening on a server socket port in a second thread.
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

      StringBuffer msg = new StringBuffer();
      int bite;

      try {
        ServerSocketME ss = (ServerSocketME) MicroEdition.getObjectME("org.cougaar.microedition.kvm.KvmServerSocketME", "org.cougaar.microedition.tini.TiniServerSocketME");
        ss.openServerSocket(myListenPort);
        System.out.println("Listening on " + myListenPort);

        while (true) {
          try {
            bufr = ss.acceptInputStream();
            while (true) {
              bite = bufr.read();
              if (bite <= 0)
                break;
              msg.append((char)bite);
            }
            bufr.close();
            if (msg.length() > 0) {
              String message = msg.toString();
              String source = getSource(message);
              deliverer.takePacket(getMessage(message), source);
            }
            msg.setLength(0);
          } catch (Exception ex) {
            System.err.println("Exception processing input message(len:"+msg.length()+"):\n'"+msg+"'");
            ex.printStackTrace();
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
}
