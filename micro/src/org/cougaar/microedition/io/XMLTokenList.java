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

import java.util.*;
import org.cougaar.microedition.shared.*;
import org.cougaar.microedition.shared.tinyxml.*;

public class XMLTokenList {

  private class Parser extends HandlerBase {
    String lastKey = "";
    public void elementStart(String name, Hashtable attr) throws ParseException {
      lastKey = name;
      attrtable = attr;
    }

    public void charData(String charData) {
      NameTablePair ntp = new NameTablePair(charData, attrtable);
      add(lastKey, ntp);
    }
  }

  private Hashtable table = new Hashtable();
  public Hashtable attrtable = null;
  public XMLTokenList(String document) {
    try {
      XMLInputStream aStream = new XMLInputStream(document);
      // get parser instance
      XMLParser aParser = new XMLParser();
      // set this class as the handler for the parser
      aParser.setDocumentHandler(new Parser());
      // set the input stream
      aParser.setInputStream(aStream);
      // and parse the xml
      aParser.parse();
    } catch (ParseException e) {
      // e.printStacktrace() is still a dummy in CLDC1.0
      System.out.println("Error parsing token list:"+e.toString());
    }

  }

  public void add(String key, NameTablePair data) {
    Vector v = (Vector)table.get(key);
    if (v == null) {
      v = new Vector();
      table.put(key, v);
    }
    v.addElement(data);
  }

  private Vector emptyVector = new Vector();

  public Vector getTokenVect(String token) {
    Vector ret = (Vector)table.get(token);
    if (ret == null)
      ret = emptyVector;
    return ret;
  }
}
