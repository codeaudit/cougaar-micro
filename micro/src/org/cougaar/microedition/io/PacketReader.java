
package cougaar.microedition.io;


public interface PacketReader {

  public void setPacketSender (PacketSender ps);

  public void start ();

  public void stop ();
}
