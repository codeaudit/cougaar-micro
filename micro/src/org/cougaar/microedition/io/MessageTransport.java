/*
 * MessageTransport.java
 *
 * Copyright 2000 by BBN Technologies, LLC. All Rights Reserved
 *
 */

package cougaar.microedition.io;

import cougaar.microedition.util.*;
import java.io.*;

/**
 * This class handles sending messages to another processes server socket,
 * it also kicks off a thread which establishes this processes listening/server socket.
 */
public class MessageTransport {

/**
 * This variable declares the reader class whose thread listens on a serversocket port.
 */
  private PacketReader reader = null;

/**
 * This variable declares the sender class that handles incoming messages.
 */
  private PacketSender sender = null;

/**
 * This private class handles incoming messages, it should be replaced with something more useful.
 */
  private class MessageHopper extends PacketSender {
    public void takePacket (String packet) {
      System.out.println("Got: " + packet);
    }
  }

/**
 * This constructor starts a listening/reader thread as well as instantiates a sender object.
 *
 * @param   myListenPort    the port number the reader thread should listen on.
 */
  public MessageTransport (int myListenPort) {
    reader = new PacketReader(myListenPort);
    reader.setPacketSender(new MessageHopper());
    reader.start();

    sender = new PacketSender();
  }

/**
   * This method sends a message to the desired server on a particular port.
   *
   * @param   server    a string that represents the host name or ip address of the recepient.
   * @param   port      an int representing the port number the recepient is listening on.
   * @param   message   a string representing the message to be sent.
   * @return  none
   */
  public void sendMessage(String server, int port, String message) {

    try {
      SocketME sock = (SocketME)MicroEdition.getObjectME("cougaar.microedition.kvm.KvmSocketME", "cougaar.microedition.tini.TiniSocketME");
      sender.bindToSocket(sock.getOutputStream(server, port));
      sender.takePacket(message);
      sender.closeSender();
    } catch (Exception e) {
      System.err.println("Unable to sendMessage " + e);
    }
  }

/**
   * This main is for testing
   *
   * @param   args    array of 3 strings, my port, remote host remote port
   * @return  none
   */
  public static void main (String[] argv) {
    if (argv.length != 3)
      System.exit(0);

    int j = 1;
    int myListenPort = Integer.parseInt(argv[0]);
    String host = argv[1];
    int port = Integer.parseInt(argv[2]);
    MessageTransport m = new MessageTransport(myListenPort);

    for (;;) {
      try {Thread.sleep(5000);} catch (Exception e) {}
      String outgoing = new String("Message #" + j++);
      System.out.println("Sending: " + outgoing);
      m.sendMessage(host, port, outgoing);
    }
  }
}
