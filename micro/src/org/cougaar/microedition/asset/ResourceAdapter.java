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
package org.cougaar.microedition.asset;

import java.util.*;

/**
 * Base class for all Resources.
 */
public abstract class ResourceAdapter implements Resource {

  private String myName = "";
  private Hashtable attrtable = null;

  public void setName(String n) {
    myName = n;
  }

  public String getName() {
    return myName;
  }

  public void setParameters(Hashtable t) {
    attrtable = t;
    if (t != null && t.containsKey("name"))
      setName((String)t.get("name"));
    if (t != null) {
      String debug = (String)getParameters().get("debug");
      if (debug != null) {
        debugging = debug.equals("true");
      }
    }
  }

  public Hashtable getParameters() {
    return attrtable;
  }

  public boolean isDebugging() {
    return debugging;
  }

  protected boolean debugging = false;

}
