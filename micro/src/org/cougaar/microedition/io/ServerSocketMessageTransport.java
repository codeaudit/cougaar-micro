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
import org.cougaar.microedition.shared.*;
import org.cougaar.microedition.asset.*;
import java.io.*;
import java.util.*;

/**
 * This class handles sending messages to another processes server socket,
 * it also kicks off a thread which establishes this processes listening/server socket.
 */
public class ServerSocketMessageTransport implements MessageTransport {

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
  public ServerSocketMessageTransport (int myListenPort, String name) {
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

  boolean done = false;
  while (!done) {
    try {
      SocketME sock = (SocketME)MicroEdition.getObjectME("org.cougaar.microedition.kvm.KvmSocketME", "org.cougaar.microedition.tini.TiniSocketME");
      OutputStream os = sock.getOutputStream(server, port);
      byte [] data = message.getBytes();
      os.write(data);
      os.flush();
      os.close();
      sock.close();
      done = true;
    } catch (Exception e) {
      System.err.println("Unable to sendMessage " + e);
    }
    if (!done) {
      System.gc();
      try {Thread.sleep(3000);} catch (InterruptedException ie){}
    }
  }
  }

  Vector listeners = new Vector();

  public void takePacket(String data, String source) {
    Enumeration en = listeners.elements();
    while (en.hasMoreElements()) {
      MessageListener ml = (MessageListener)en.nextElement();
      ml.deliverMessage(data, source);
    }

  }

  public void addMessageListener(MessageListener ml) {
    if (!listeners.contains(ml))  {
      listeners.addElement(ml);
    }
  }

  public void removeMessageListener(MessageListener ml) {
    if (listeners.contains(ml))  {
      listeners.removeElement(ml);
    }
  }

  public void sendMessage(Encodable msg, MicroCluster dest, String op) {
    StringBuffer buf = new StringBuffer();
    buf.append(nodeName + ":");
    buf.append(msg.xmlPreamble);
    buf.append("<message op=\""+op+"\">");
    msg.encode(buf);
    buf.append("</message>");
    buf.append('\0');
    String ipAddress = dest.getClusterId().getIpAddress();
    short port = dest.getClusterId().getPort();

//    System.out.println("Sending: "+buf.toString()+" to "+ipAddress);

    sendMessage(ipAddress, port, buf.toString());
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
