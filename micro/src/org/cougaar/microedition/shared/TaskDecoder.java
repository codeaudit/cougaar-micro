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
package cougaar.microedition.shared;
import cougaar.microedition.shared.tinyxml.*;
import java.util.*;

public class TaskDecoder extends HandlerBase {

  MicroTask t;

  public TaskDecoder() {
  }

  public void charData(String charData) {
    System.out.println("CHARDATA: "+charData);
  }

  public MicroTask decode(String data) {
    t = new MicroTask();
    try {
      XMLInputStream aStream = new XMLInputStream(data);
      // get parser instance
      XMLParser aParser = new XMLParser();
      // set this class as the handler for the parser
      aParser.setDocumentHandler(this);
      // set the input stream
      aParser.setInputStream(aStream);
      // and parse the xml
      aParser.parse();
    } catch (ParseException e) {
      // e.printStacktrace() is still a dummy in CLDC1.0
      System.out.println(e.toString());
    }
    return t;
  }

  /**
   * This method is called to indicate the start of an element (tag).
   *
   * @param name the name of the element (tag name)
   * @param attr a hashtable containing the explicitly supplied attributes (as strings)
   */
  public void elementStart(String name, Hashtable attr) throws ParseException {
    System.out.println("Element: " + name);
    if (name.equals(MicroTask.tag)) {
      getTaskAttrs(attr);
    }
    else if (name.equals(MicroAllocation.tag)) {
      getAllocationAttrs(attr);
    }
    else if (name.equals(MicroAllocationResult.tag)) {
      getAllocationResultAttrs(attr);
    }
    else if (name.equals(MicroPrepositionalPhrase.tag)) {
      getPrepositionalPhraseAttrs(attr);
    }
  }

  private void getTaskAttrs(Hashtable attr) {
    t.setVerb((String)attr.get("verb"));
  }

  private void getAllocationAttrs(Hashtable attr) {
    if (t.getAllocation() == null)
      t.setAllocation(new MicroAllocation());
  }

  private void getAllocationResultAttrs(Hashtable attr) {
    if (t.getAllocation().getReportedResult() == null)
      t.getAllocation().setReportedResult(new MicroAllocationResult());
  }

  private void getPrepositionalPhraseAttrs(Hashtable attr) {
    String prep = (String)attr.get("preposition");
    String obj = (String)attr.get("indirectObject");

    if ((prep != null) && (obj != null)) {
      t.addPrepositionalPhrase(new MicroPrepositionalPhrase(prep, obj));
    }

  }
  
  public static void main(String [] args) {
    MicroTask t = new MicroTask();
    t.setVerb("AVerb");
    MicroAllocation ma = new MicroAllocation();
    MicroAllocationResult mar = new MicroAllocationResult();
    ma.setReportedResult(mar);
    t.setAllocation(ma);

    Vector v = new Vector();
    v.add(new MicroPrepositionalPhrase("Preposition1", "Ind-Obj1"));
    v.add(new MicroPrepositionalPhrase("Preposition2", "Ind-Obj2"));
    t.setPrepositionalPhrases(v);

    StringBuffer str = new StringBuffer();
    str.append("<?xml version=\"1.0\"?>");
    t.encode(str);

    TaskDecoder d = new TaskDecoder();
    MicroTask new_t = d.decode(str.toString());
    System.out.println(new_t);
  }
}