package cougaar.microedition.shared;

import java.util.*;

//--------
public class NameTablePair {
  public String name;
  public Hashtable table;

  public NameTablePair(String n, Hashtable t) {
    name = n;
    if (t != null)
      table = (Hashtable)(t.clone());
    else
      table = null;
  }
}
