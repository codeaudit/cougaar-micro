
package cougaar.microedition.io;

import cougaar.microedition.util.*;
import java.io.*;

public class MessageTransport {

  private PacketReader reader = null;
  private PacketSender sender = null;

  private class MessageHopper extends PacketSender {
    public void takePacket (String packet) {
      System.out.println("Got: " + packet);
    }
  }

  public MessageTransport (int myListenPort) {
    reader = new PacketReader(myListenPort);
    reader.setPacketSender(new MessageHopper());
    reader.start();

    sender = new PacketSender();
  }

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
