
package cougaar.microedition.io;

public interface MessageTransport {

  PacketReader reader = null;
  PacketSender sender = null;

  public void sendMessage(String server, int port, String message);
}
