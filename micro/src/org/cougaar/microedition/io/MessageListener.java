package cougaar.microedition.io;

public interface MessageListener {

  public void deliverMessage(String data, ClusterId source);
}