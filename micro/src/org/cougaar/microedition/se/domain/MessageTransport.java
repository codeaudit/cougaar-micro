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
package org.cougaar.microedition.se.domain;

import java.net.*;
import java.io.*;
import java.util.*;

import org.cougaar.domain.planning.ldm.*;

import org.cougaar.microedition.shared.*;

/**
 * Class used to send and receive messages to/from micro clusters.
 */
class MessageTransport {

  LDMServesPlugIn ldm;
  short port = 1234; // default port for incoming messages

  Vector deadclusters = new Vector();

   MessageTransport(LDMServesPlugIn ldm) {
    this.ldm = ldm;
    Thread t = new Thread(new Sender());
    t.start();
  }

  private Vector outgoingQueue =  new Vector();
  private void enqueueOutgoing(OutgoingMessage msg) {
    if (deadclusters.contains(msg.address + ":" + msg.port)) {
      deadclusters.removeElement(msg.address + ":" + msg.port);
      return;
    }
    synchronized (outgoingQueue){
      outgoingQueue.addElement(msg);
      outgoingQueue.notify();
    }
  }
  private OutgoingMessage dequeueOutgoing() {
    OutgoingMessage ret = null;
    synchronized (outgoingQueue){
      while (outgoingQueue.size() == 0)
        try {
          outgoingQueue.wait();
        } catch (InterruptedException ie) {}
       ret = (OutgoingMessage)outgoingQueue.remove(0);
    }
    return ret;
  }

  /**
   * Get the network port used by this message transport.
   */
  public short getPort() {
    return port;
  }

  /**
   * Set the network port used by this message transport.  Must
   * be called before startListener() to have any effect.
   */
  public void setPort(short port) {
    this.port = port;
  }


  /**
   * Send a message to a micro cluster.
   */
  void sendTo(MicroCluster microCluster, Encodable encodable, String op)  throws IOException {
    StringBuffer buf = new StringBuffer();
    buf.append(ldm.getClusterIdentifier().toString() + ":");
    buf.append("<?xml version=\"1.0\"?>");
    buf.append("<message op=\""+op+"\">");
    encodable.encode(buf);
    buf.append("</message>");
    buf.append('\0');
    String ipAddress = microCluster.getMicroClusterPG().getIpAddress();
    short port = microCluster.getMicroClusterPG().getPort();

    if (port == 0) { // should have a PointToPoint
      String name = microCluster.getMicroClusterPG().getName();
      // System.out.println("Sending to P2P "+name+" : "+buf.toString());
      sendP2PMessage(name, buf.toString().getBytes());
    } else {
//      System.out.println("Queueing: "+encodable+" to "+microCluster);
      enqueueOutgoing(new OutgoingMessage(ipAddress, port, buf.toString().getBytes()));
    }
  }

  /**
   * Stop trying to send messages to this guy
   */
  public void dequeue(MicroCluster mc) {
    deadclusters.addElement(mc.getMicroClusterPG().getIpAddress() + ":" + mc.getMicroClusterPG().getPort());

    synchronized (outgoingQueue){
      Enumeration outgoing = outgoingQueue.elements();
      Vector deadones = new Vector();
      String address = mc.getMicroClusterPG().getIpAddress();
      short port = mc.getMicroClusterPG().getPort();
//      System.out.println("*** Removing messages to "+address+":"+port);
      while (outgoing.hasMoreElements()) {
        OutgoingMessage om = (OutgoingMessage)outgoing.nextElement();
        if (om.address.equals(address) && (om.port == port))
          deadones.addElement(om);
      }
//      System.out.println("*** Removed "+deadones.size());
      outgoingQueue.removeAll(deadones);
    }

  }

  private void sendP2PMessage(String name, byte [] data) throws IOException {
    IO io = (IO)PtoPclients.get(name);
    if (io != null) {
      io.out.write(data);
      io.out.flush();
    } else {
      throw new RuntimeException("No client socket found for cluster: "+name);
    }
  }

  Vector listeners = new Vector();

  /**
   * Starts a thread to listen for incoming messages.  Port must be set before
   * starting.
   * @see setPort
   */
  public void startListener() {
    Thread t = new Thread(new Retriever());
    t.start();
  }

  /**
   * Add a new listener.  It will be notified through deliverMessage()
   * when a new message is received.
   */
  public void addMessageListener(MessageListener ml) {
    if (!listeners.contains(ml))  {
      Vector newListeners = (Vector)listeners.clone();
      newListeners.addElement(ml);
      listeners = newListeners;
    }
  }

  /**
   * Remove a message listener.
   */
  public void removeMessageListener(MessageListener ml) {
    if (listeners.contains(ml))  {
      Vector newListeners = (Vector)listeners.clone();
      newListeners.removeElement(ml);
      listeners = newListeners;
    }
  }

  /**
   * Deliver messages to message listeners
   */
  boolean takePacket(String data, String source, String srcAddress, OutputStream client, InputStream in) {
    boolean close_it = true;
    Enumeration en = listeners.elements();
    while (en.hasMoreElements()) {
      MessageListener ml = (MessageListener)en.nextElement();
      if (!ml.deliverMessage(data, source, srcAddress, client, in)) {
        close_it = false;
      }
    }
    return close_it;
  }

  /**
   * Handle clients that only support a single I/O connection.
   */
  private class IO {
    public IO(InputStream in, OutputStream out) {
      this.in = in;
      this.out = out;
    }
    public InputStream in;
    public OutputStream out;
  }
  Hashtable PtoPclients = new Hashtable();
  public void addPointToPointClient(InputStream in, OutputStream out, String name) {
    PtoPclients.put(name, new IO(in, out));
    Thread t = new Thread (new SocketListener(in, out, name));
    t.start();
  }

  /**
   * Worker thread class for servicing messages on an InputStream
   */
  private class SocketListener implements Runnable {
    InputStream in;
    OutputStream out;
    String name;
    public SocketListener(InputStream in, OutputStream out, String name) {
      this.in = in;
      this.out = out;
      this.name = name;
    }
    /**
     * Service incoming messages
     */
    public void run () {
      StringBuffer msg = new StringBuffer();
      int bite;
      System.out.println("PointToPoint listener on " + in);
      while (true) {
        try {
          while (true) {
            bite = in.read();
            if (bite <= 0)
              break;
            msg.append((char)bite);
          }
          if (msg.length() == 0) // client is gone
            throw new SocketException("Client disconnected");
          String message = msg.toString();
          String source = getSource(message);
          takePacket(getMessage(message), source,
                     name, out, in);
          msg.setLength(0);
        } catch (SocketException ex) {
          // Socket shutdown.  Bag it.
          System.err.println("Socket listener shutting down for "+name);
          try {
            in.close();
            out.close();
          } catch (IOException ioe) {}
          return;
        } catch (Exception ex) {
          // Trouble. Report it and go on.
          System.err.println("Exception processing input message ");
          ex.printStackTrace();
        }
      }
    }
  }

  /**
   * Worker thread class for servicing incoming message server socket.
   */
  private class Retriever implements Runnable {

    /**
     * Service incoming messages
     */
    public void run () {

      StringBuffer msg = new StringBuffer();
      int bite;

      try {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("listening for micro clusters on " +port);
        while (true) {
          try {
            Socket s = ss.accept();
            //System.out.println("Accepted  on " +port);
            InputStream bufr = s.getInputStream();
            while (true) {
              bite = bufr.read();
              if (bite <= 0)
                break;
              msg.append((char)bite);
            }
            String message = msg.toString();
            String source = getSource(message);
//            System.out.println("Message from: "+source);
            boolean close_it = takePacket(getMessage(message), source,
                                          s.getInetAddress().getHostAddress(),
                                          s.getOutputStream(),
                                          s.getInputStream());
            if (close_it) {
              bufr.close();
              s.close();
            }
            msg.setLength(0);
          } catch (Exception ex) {
            // Trouble. Report it and go on.
            System.err.println("Exception processing input message ");
            ex.printStackTrace();
          }
        }
      } catch (IOException ioe) {
        System.err.println("Error configuring message recv: IOException");
        ioe.printStackTrace();
      }
    }
  }

  private class OutgoingMessage {
    public OutgoingMessage(String address, short port, byte[] message) {
      this.address = address;
      this.port = port;
      this.message = message;
    }
    byte[] message;
    String address;
    short port;
  }

  /**
   * Worker thread class for servicing outgoing messages.
   */
  private class Sender implements Runnable {

    public void run () {
      while (true) {
        OutgoingMessage msg = dequeueOutgoing();
        try {
          //System.err.println("Sending to "+msg.address+":"+msg.port);
          xmit(msg.address, msg.port, msg.message);
        } catch (Exception ex) {
          System.err.println("Error sending to "+msg.address+":"+msg.port+" Requeue.");
          enqueueOutgoing(msg);
        }
      }
    }


    /**
     * Write raw data to an IPaddress/port
     */
    private void xmit(String ipAddress, short port, byte[] data) throws IOException {
      Socket s = new Socket(ipAddress, port);
      OutputStream os = s.getOutputStream();
      os.write(data);
      os.flush();
      os.close();
      s.close();
    }
  }


  /**
   * Parse the source cluster name from a message.  Assumed message format is
   * <clusterName>:<messageText>  (Separated by a colon)
   */
  private String getSource(String msg) {
    if (msg.indexOf(":") < 0) {
      System.err.println("ERROR: Malformed message (no source)" + msg);
      return "";
    }
    return msg.substring(0, msg.indexOf(":"));;
  }

  /**
   * Parse the message text from a message.  Assumed message format is
   * <clusterName>:<messageText>  (Separated by a colon)
   */
  private String getMessage(String msg) {
    return msg.substring(msg.indexOf(":")+1);
  }


}
