
package cougaar.microedition.io;

import cougaar.microedition.util.*;
import java.io.*;

public class PacketReader {
  private InputStream bufr = null;
  private Thread runner = null;
  private Retriever retriever = new Retriever();
  private PacketSender deliverer = null;
  private int myListenPort;

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

  public PacketReader (int port) {
    myListenPort = port;
  }

  public void setPacketSender (PacketSender ps) {
    deliverer = ps;
  }

  public void start () {
    if (runner == null || !runner.isAlive()) {
      runner = new Thread(retriever);
      runner.start();
    }
  }
}
