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

import org.cougaar.microedition.plugin.PluginAdapter;
import org.cougaar.microedition.ldm.Subscription;
import java.util.Enumeration;
import org.cougaar.microedition.util.UnaryPredicate;

/**
 * This UnaryPredicate matches all Job objects
 */
class myPredicate implements UnaryPredicate{
  public boolean execute(Object o) {
    return (o instanceof Job);
  }
}

/**
 * This ALP Plugin subscribes to Job objects and prints them out.
 * @author ALPINE (alpine-software@bbn.com)
 * @version $Id: WorkerPlugin.java,v 1.1 2002-02-13 15:13:01 jwinston Exp $
 **/
public class WorkerPlugin extends PluginAdapter {
  // holds my subscription for Job objects (matching predicate above)
  private Subscription jobs;

  /**
   * Called when the Plugin is loaded.  Establish the subscription for
   * Job objects
   */
public void setupSubscriptions() {
  jobs = (Subscription)subscribe(new myPredicate());
//  System.out.println("WorkerPlugin:setupSubscriptions");
}

/**
 * Called when there is a change on my subscription(s).
 * This plugin just prints all new jobs to stdout
 */
public void execute () {
  Enumeration new_jobs = jobs.getAddedList().elements();
  while (new_jobs.hasMoreElements()) {
    Job job = (Job)new_jobs.nextElement();
    System.out.println("WorkerPlugin got a new Job: " + job);
    String snum = job.toString().substring((int)4);
    publishAdd( new Status("Done" + snum));
    publishRemove( job );
  }
}

}
