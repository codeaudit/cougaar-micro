/*
 * <copyright>
 * 
 * Copyright 1997-2001 BBNT Solutions, LLC.
 * under sponsorship of the Defense Advanced Research Projects
 * Agency (DARPA).
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Cougaar Open Source License as published by
 * DARPA on the Cougaar Open Source Website (www.cougaar.org).
 * 
 * THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 * PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 * IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 * ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 * HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 * TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package org.cougaar.microedition.se.domain;
import java.io.*;
import java.util.*;

import org.cougaar.core.plugin.*;
import org.cougaar.core.util.*;
import org.cougaar.util.*;
import org.cougaar.domain.planning.ldm.asset.*;
import org.cougaar.core.cluster.*;
import org.cougaar.microedition.shared.tinyxml.*;
import org.cougaar.microedition.shared.*;

/**
 * Cougaar Plugin used to manage micro Cougaar assets.  Turn on debug output
 * by adding a parameter "debug" in the .ini file.
 */
public class NameServerPlugin extends SimplePlugin implements MessageListener {
  Factory f;

  private short lastPort = 7000;

  private IncrementalSubscription microAgents;

  private boolean debugging = false;

  protected void setupSubscriptions() {

    Vector parameters = getParameters();
    debugging = parameters.contains("debug");
    if (debugging) System.out.println("NameServerPlugin: Debug on.");

    f = (Factory)theLDM.getFactory("micro");
    f.getMessageTransport().addMessageListener(this);

    theLDMF.addPropertyGroupFactory(new org.cougaar.microedition.se.domain.PropertyGroupFactory());

    MicroAgent new_prototype = (MicroAgent)theLDMF.createPrototype
      (MicroAgent.class, "MicroAgent");
    new_prototype.setMicroAgentPG(makeMicroAgentPG("protoName", "protoCapabilities", "127.0.0.1", (short)1234));
    theLDM.cachePrototype("MicroAgent", new_prototype);

    microAgents = (IncrementalSubscription)subscribe(new UnaryPredicate () {
      public boolean execute(Object o) { return o instanceof MicroAgent;}});

  }

  /**
   * Create and populate a MicroAgent property group.
   */
  private MicroAgentPG makeMicroAgentPG(String name, String capabilities, String ipAddress, short port) {
    NewMicroAgentPG pg = (NewMicroAgentPG)theLDMF.createPropertyGroup("MicroAgentPG");
    pg.setName(name);
    pg.setCapabilities(capabilities);
    pg.setIpAddress(ipAddress);
    pg.setPort(port);
    return pg;
  }

  /**
   * Create and populate an ItemIdentification property group
   */
  private ItemIdentificationPG makeItemIdentificationPG(String name) {
    NewItemIdentificationPG pg = (NewItemIdentificationPG)theLDMF.createPropertyGroup("ItemIdentificationPG");
    pg.setItemIdentification(name);
    return pg;
  }

  protected void execute() {
  }

  private String readFile(String fname) throws IOException {
    ConfigFinder finder = getConfigFinder();
    BufferedReader in = new BufferedReader(new InputStreamReader(finder.open(fname)));
    StringBuffer ret = new StringBuffer();
    while (in.ready()) {
      ret.append(in.readLine());
      ret.append('\n');
    }
    return ret.toString();
  }

  /**
   * Receive a message from a micro-agent.
   */
  public boolean deliverMessage(String msg, String src, String srcAddress, OutputStream clientout, InputStream clientin) {
    boolean ret = true;
    /**
     * Handle new agents registering with us
     */
    if (msg.indexOf("<registration>") >= 0) {
      try {
        RegistrationMessage rm = new RegistrationMessage(msg);
        RegistrationResponse rr = new RegistrationResponse(rm, readFile(rm.getName()+".xml"));
        if (rr.getPort() < 0)  // just assign a port
          rr.setPort(lastPort++);
        else if (rr.getPort() == 0) { // send&recv messages on this socket
          f.getMessageTransport().addPointToPointClient(clientin, clientout, rr.getName());
          ret = false; // keep the socket open
        }

        boolean reReg = false;
        Collection micros = microAgents.getCollection();
        Iterator iter = micros.iterator();
        while (iter.hasNext()) {
          MicroAgent mc = (MicroAgent)iter.next();
          MicroAgentPG mpg = mc.getMicroAgentPG();
System.out.println("Testing"+srcAddress+mpg.getIpAddress()+rm.getName()+mpg.getName());
          if (
              srcAddress.equals(mpg.getIpAddress()) &&
              rm.getName().equals(mpg.getName())
             ) {
            f.getMessageTransport().dequeue(mc);
//            rr.setPort(mpg.getPort());
            openTransaction();
            System.out.println("NameServerPlugin: Removing a MicroAgent: "+mc);
            publishRemove(mc);
            closeTransaction();

            reReg = true;
            break;
          }
        }

        StringBuffer registrationResponse = new StringBuffer();
        registrationResponse.append(theLDM.getClusterIdentifier().toString() + ":");
        registrationResponse.append(Encodable.xmlPreamble);
        rr.encode(registrationResponse);
        registrationResponse.append("\0");
        clientout.write(registrationResponse.toString().getBytes());
        clientout.flush();

//        if (!reReg) {
          MicroAgent asset = makeMicroAgent(rr, srcAddress);
          openTransaction();
          System.out.println("NameServerPlugin: Making a MicroAgent: "+asset);
          publishAdd(asset);
          closeTransaction();
//        }
      } catch (IOException ioe) {
        System.err.println(this.getClass().getName()+": Error accepting registration request");
        ioe.printStackTrace();
      }
    }
    /**
     * Handle looking up nodes by capabilities or name
     */
    else if (msg.indexOf("<agentQuery>") >= 0) {
      try {
        QueryMessage lm = new QueryMessage(msg);
        if (debugging) System.out.println("NameServerPlugin: Got a query:"+lm);
        Vector agents = new Vector();
        //lookup by capabilities
        if (lm.getCapabilitiesSubstring() != null) {
          agents = lookupByCapabilities(lm.getCapabilitiesSubstring());
        }
        // add agents matching name
        if (lm.getName() != null) {
          LookupResponse lr = lookup(lm.getName());
          if (lr != null)
            agents.addElement(lr);
        }
        Enumeration agent_enum = agents.elements();

        StringBuffer lookupResponse = new StringBuffer();
        lookupResponse.append(theLDM.getClusterIdentifier().toString() + ":");
        lookupResponse.append(Encodable.xmlPreamble);
        lookupResponse.append("<list>");
        while (agent_enum.hasMoreElements()) {
          LookupResponse lr = (LookupResponse)agent_enum.nextElement();
          lr.encode(lookupResponse);
        }
        lookupResponse.append("</list>");
        clientout.write(lookupResponse.toString().getBytes());
        clientout.write(0);
        clientout.flush();
      } catch (IOException ioe) {
        System.err.println(this.getClass().getName()+": Error processing lookup request");
        ioe.printStackTrace();
      }
    }
    return ret;
  }

  private MicroAgent makeMicroAgent(RegistrationResponse rr, String src) {
    MicroAgent asset = (MicroAgent) theLDMF.createInstance("MicroAgent");
    asset.setMicroAgentPG(makeMicroAgentPG(rr.getName(), rr.getDescription(),
    src, rr.getPort()));
    asset.setItemIdentificationPG(makeItemIdentificationPG(rr.getName()));
    return asset;
  }

  private LookupResponse unknownAgent = new LookupResponse("unknown","unknown", (short)0, "Agent not found");

  // look up a node by its name
  public LookupResponse lookup(String name) {
    LookupResponse ret = null;
    Collection micros = microAgents.getCollection();
    Iterator iter = micros.iterator();
    while (iter.hasNext()) {
      MicroAgent mc = (MicroAgent)iter.next();
      if (name.equals(mc.getItemIdentificationPG().getItemIdentification())) {
        MicroAgentPG mpg = mc.getMicroAgentPG();
        ret = new LookupResponse(mpg.getName(), mpg.getIpAddress(), mpg.getPort(),mpg.getCapabilities());
        break;
      }
    }
    return ret;
  }

  // look up a set of agents by their capabilities
  public Vector lookupByCapabilities(String capabilitiesSubstring) {
    Vector ret = new Vector();
    LookupResponse lr;
    Collection micros = microAgents.getCollection();
    Iterator iter = micros.iterator();
    while (iter.hasNext()) {
      MicroAgent mc = (MicroAgent)iter.next();
      MicroAgentPG mpg = mc.getMicroAgentPG();
      String myCapabilities = mpg.getCapabilities();
      if (myCapabilities.indexOf(capabilitiesSubstring) >= 0) {
        ret.addElement(new LookupResponse(mpg.getName() ,mpg.getIpAddress(), mpg.getPort(), mpg.getCapabilities()));
      }
    }
    return ret;
  }

  // ++++++++ Parser and utility classes ++++++++
  //--------------------------------------------------------------------------
  private static class RegistrationMessage extends HandlerBase {
    public RegistrationMessage(String msg) {
      try {
        XMLInputStream aStream = new XMLInputStream(msg);
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
    }
    private String name;
    public String getName() {
      return name;
    }
    /**
     * For now, name is the only char data in the document.
     */
    public void charData(String charData) {
      name = charData;
    }
  }
  //--------------------------------------------------------------------------
  // Parse the "look up agent by capabilities" message
  private static class QueryMessage extends HandlerBase {
    public QueryMessage(String msg) {
      try {
        XMLInputStream aStream = new XMLInputStream(msg);
        XMLParser aParser = new XMLParser();
        aParser.setDocumentHandler(this);
        aParser.setInputStream(aStream);
        aParser.parse();
      } catch (ParseException e) {
        System.out.println(e.toString());
      }
    }
    private String capabilitiesSubstring = null;
    public String getCapabilitiesSubstring() {
      return capabilitiesSubstring;
    }
    private String name = null;
    public String getName() {
      return name;
    }
    private String lastTag = "";
    public void elementStart(String name, Hashtable attr) throws ParseException {
      lastTag = name;
    }
    /**
     * For now, capabilitiesSubstring is the only char data in the document.
     */
    public void charData(String charData) {
      if (lastTag.equals("capabilitiesSubstring"))
        capabilitiesSubstring = charData;
      else if (lastTag.equals("name"))
        name = charData;
    }

    public String toString() {
      return "QueryMessage: capabilitiesSubstring \""+capabilitiesSubstring+
             "\" name \""+name+"\"";
    }
  }

  //--------------------------------------------------------------------------
  private static class RegistrationResponse extends HandlerBase implements Encodable {
    public RegistrationResponse(RegistrationMessage rm, String data) {
      plugins = new Vector();
      resources = new Vector();
      setName(rm.getName());
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
    }

    private Vector plugins;
    private Vector resources;
    private String description;
    private String name;
    private short port = -1;

    public String getDescription() {
      return description;
    }
    public void setDescription(String description) {
      this.description = description;
    }

    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }

    public void setPort(short port) {
      this.port = port;
    }

    public short getPort() {
      return port;
    }

    public void encode(StringBuffer registrationResponse) {
      registrationResponse.append("<registrationResponse>");
      registrationResponse.append("<port>"+port+"</port>");
      registrationResponse.append("<description>"+description+"</description>");
      Enumeration enum = plugins.elements();
      while (enum.hasMoreElements()) {
        NameTablePair ntp = (NameTablePair)enum.nextElement();
        String plugin = ntp.name;
        Hashtable nametable = ntp.table;
        registrationResponse.append("<plugin");
        if (nametable != null) {
          Enumeration ek = nametable.keys();
          while (ek.hasMoreElements()) {
            String key = (String)ek.nextElement();
            registrationResponse.append(" " + key + "=\"" + nametable.get(key) + "\"");
          }
        }
        registrationResponse.append(">"+plugin);
        registrationResponse.append("</plugin>");
      }

      enum = resources.elements();
      while (enum.hasMoreElements()) {
        NameTablePair ntp = (NameTablePair)enum.nextElement();
        String resource = ntp.name;
        Hashtable nametable = ntp.table;
        registrationResponse.append("<resource");
        if (nametable != null) {
          Enumeration ek = nametable.keys();
          while (ek.hasMoreElements()) {
            String key = (String)ek.nextElement();
            registrationResponse.append(" " + key + "=\"" + nametable.get(key) + "\"");
          }
        }
        registrationResponse.append(">"+resource);
        registrationResponse.append("</resource>");

      }
      registrationResponse.append("</registrationResponse>");
    }
    //
    // XML parsing methods
    //
    private String lastName = "";
    private Hashtable attrtable = null;

    public void elementStart(String name, Hashtable attr) throws ParseException {
      lastName = name;
      if (lastName.equals("plugin"))
        attrtable = attr;
      else if (lastName.equals("resource"))
        attrtable = attr;
    }

    public void charData(String charData) {
      if (lastName.equals("port")) {
        setPort(Short.parseShort(charData));
      }
      else if (lastName.equals("description")) {
        description = charData;
      }
      else if (lastName.equals("plugin")) {
        NameTablePair ntp = new NameTablePair(charData, attrtable);
        plugins.addElement(ntp);
      }
      else if (lastName.equals("resource")) {
        NameTablePair ntp = new NameTablePair(charData, attrtable);
        resources.addElement(ntp);
      }
    }
  }


  //--------------------------------------------------------------------------

  private static class LookupResponse implements Encodable {
    public void encode(StringBuffer lookupResponse) {
      lookupResponse.append("<nodelookupresult>\n\t<node_name>");
      lookupResponse.append(getName());
      lookupResponse.append("</node_name>\n\t<node_ip_address>");
      lookupResponse.append(getIPAddress());
      lookupResponse.append("</node_ip_address>\n\t<node_server_port>");
      lookupResponse.append(getPort());
      lookupResponse.append("</node_server_port>\n\t<node_description>");
      lookupResponse.append(getDescription());
      lookupResponse.append("</node_description>\n</nodelookupresult>");
    }
    public LookupResponse(String name, String IPAddress, short port, String description) {
      this.name = name;
      this.IPAddress = IPAddress;
      this.port = port;
      this.description = description;
    }

    public String getName() {
      return name;
    }
    public String getIPAddress() {
      return IPAddress;
    }
    public short getPort() {
      return port;
    }
    public String getDescription() {
      return description;
    }
    private String name;
    private String IPAddress;
    private short port;
    private String description;
  }
}
