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

import java.util.*;
import java.io.*;

import org.cougaar.core.component.*;
import org.cougaar.core.plugin.*;


/**
 * Infrastructure plugin for commmunications between big Cougaar and
 * Cougaar Micro Edition.  Must be included before any other CougaarME domain plugins
 * because it initializes the MEMessageService.
 */
public class MicroAgentMessagePlugin extends ComponentPlugin implements ServiceProvider
{
  private boolean debug = false;
  
  private MEMessageService service = null;


  protected void setupSubscriptions() {
      debug = getParameters().contains("debug");
      service = new MEMessageService(getBindingSite().getAgentIdentifier().toString());
      getBindingSite().getServiceBroker().addService(MEMessageService.class, this);
  }
  /**
   * Called when objects in the PLAN change
   */
  protected void execute() {
  }

  
  public void releaseService(org.cougaar.core.component.ServiceBroker serviceBroker, java.lang.Object requestor, java.lang.Class clazz, java.lang.Object service) {
  }
  
  public java.lang.Object getService(org.cougaar.core.component.ServiceBroker serviceBroker, java.lang.Object requestor, java.lang.Class clazz) {
      return service;
  }
  
}
