/*
 * PacketReader.java
 *
 * Copyright 2000 by BBN Technologies, LLC. All Rights Reserved
 *
 */

package cougaar.microedition.io;

import cougaar.microedition.util.*;
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
  private PacketSender deliverer = null;

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
        ServerSocketME ss = (ServerSocketME) MicroEdition.getObjectME("cougaar.microedition.kvm.KvmServerSocketME", "cougaar.microedition.tini.TiniServerSocketME");
        ss.openServerSocket(myListenPort);
        System.out.println("Listening on " + myListenPort);

        while (true) {
          bufr = ss.acceptInputStream();
          while (true) {
            bite = bufr.read();
            if (bite <= 0)
              break;
            msg.append((char)bite);
          }
          deliverer.takePacket(msg.toString());
          bufr.close();
          msg.setLength(0);
        }
      } catch (Exception e) {
        System.err.println("Unable to setup ServerSocket " + e);
      }
    }
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
   * @param   ps    the PacketSender object that will do something with the incoming message.
   * @return  none
   */
  public void setPacketSender (PacketSender ps) {
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
