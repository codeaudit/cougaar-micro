package org.cougaar.microedition.asset;

import java.util.Hashtable;

public interface Resource extends Asset {
  public void setName(String n);
  public String getName();

  public void setParameters(Hashtable t);
  public Hashtable getParameters();
}