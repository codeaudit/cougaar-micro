/*
 * MessageTransport.java
 *
 * Copyright 2000 by BBN Technologies, LLC. All Rights Reserved
 *
 */

package cougaar.microedition.io;

import cougaar.microedition.util.*;
import cougaar.microedition.shared.*;
import cougaar.microedition.naming.*;
import java.io.*;
import java.util.*;

/**
 * This class handles sending messages to another processes server socket,
 * it also kicks off a thread which establishes this processes listening/server socket.
 */
public class MessageTransport {

/**
 * This variable declares the reader class whose thread listens on a serversocket port.
 */
  private PacketReader reader = null;

  private String nodeName = null;
/**
 * This constructor starts a listening/reader thread as well as instantiates a sender object.
 *
 * @param   myListenPort    the port number the reader thread should listen on.
 */
  public MessageTransport (int myListenPort, String name) {
    reader = new PacketReader(myListenPort);
    reader.setMessageTransport(this);
    reader.start();
    nodeName = name;
  }

/**
   * This method sends a message to the desired server on a particular port.
   *
   * @param   server    a string that represents the host name or ip address of the recepient.
   * @param   port      an int representing the port number the recepient is listening on.
   * @param   message   a string representing the message to be sent.
   * @return  none
   */
  protected void sendMessage(String server, int port, String message) {

    try {
      SocketME sock = (SocketME)MicroEdition.getObjectME("cougaar.microedition.kvm.KvmSocketME", "cougaar.microedition.tini.TiniSocketME");
      OutputStream os = sock.getOutputStream(server, port);
      byte [] data = message.getBytes();
      os.write(data);
      os.flush();
      os.close();
      sock.close();
    } catch (Exception e) {
      System.err.println("Unable to sendMessage " + e);
    }
  }

  Vector listeners = new Vector();

  void takePacket(String data, ClusterId source) {
    Enumeration en = listeners.elements();
    while (en.hasMoreElements()) {
      MessageListener ml = (MessageListener)en.nextElement();
      ml.deliverMessage(data, source);
    }

  }

  public void addMessageListener(MessageListener ml) {
    if (!listeners.contains(ml))  {
      synchronized (listeners) {listeners.addElement(ml);}
    }
  }

  public void removeMessageListener(MessageListener ml) {
    if (listeners.contains(ml))  {
      synchronized (listeners) {listeners.removeElement(ml);}
    }
  }

  public void sendMessage(Encodable msg, ClusterId dest) {
    StringBuffer buf = new StringBuffer();
    buf.append(nodeName + ":");
    buf.append("<?xml version=\"1.0\"?>");
    msg.encode(buf);
    String ipAddress = dest.getIpAddress();
    short port = dest.getPort();

//    System.out.println("Sending: "+buf.toString()+" to "+ipAddress);

    sendMessage(ipAddress, port, buf.toString());
  }

  private NameMap nameMap = new NameMap();

  public NameMap getNameMap() {
    return nameMap;
  }

/**
   * This main is for testing
   *
   * @param   args    array of 3 strings, my port, remote host remote port
   * @return  none

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
 */
}
