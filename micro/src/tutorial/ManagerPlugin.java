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
package tutorial;

/**
 * This ALP Plugin publishes a Job object.
 * @author ALPINE (alpine-software@bbn.com)
 * @version $Id: ManagerPlugin.java,v 1.1 2002-02-13 15:13:01 jwinston Exp $
 **/
import org.cougaar.microedition.plugin.PluginAdapter;
import org.cougaar.microedition.ldm.Subscription;
import java.util.Enumeration;
import org.cougaar.microedition.util.UnaryPredicate;
public class ManagerPlugin extends PluginAdapter {

  private Subscription status;
  private int num = 0;
  private int max = Integer.MAX_VALUE;

/**
 * setupSubscriptions is called when the Plugin is loaded.  We use
 * it here to create and publish a Job object.
 */
public void setupSubscriptions() {
  publishAdd( new Job("Work" + ++num));
//  System.out.println("ManagerPlugin::setupSubscriptions");
  status = (Subscription)subscribe(new statusPredicate());

  //
  // check for a parameter that says when I should stop
  if (getParameters() != null) {
    String maxIterations = (String)getParameters().get("maxIterations");
    if (maxIterations != null) {
      max = Integer.parseInt(maxIterations);
    }
  }
}

/**
 * This plugin has no subscriptions so execute does nothing
 */
public void execute () {
  Enumeration new_status = status.getAddedList().elements();
  while (new_status.hasMoreElements()) {
    Status status = (Status)new_status.nextElement();
System.out.println("ManagerPlugin got a status: " + status.toString());
    publishRemove( status );
  }
  if (num < max)
    publishAdd( new Job("Work" + ++num));
}
}

/**
 * This UnaryPredicate matches all Job objects
 */
class statusPredicate implements UnaryPredicate{
  public boolean execute(Object o) {
    return (o instanceof Status);
  }
}

