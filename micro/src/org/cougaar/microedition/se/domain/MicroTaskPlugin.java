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

import org.cougaar.core.util.*;
import org.cougaar.util.*;
import org.cougaar.core.blackboard.*;
import org.cougaar.planning.ldm.plan.*;
import org.cougaar.planning.ldm.asset.*;
import org.cougaar.core.plugin.*;

import org.cougaar.microedition.shared.*;

/**
 * Infrastructure plugin for task commmunications between big Cougaar and
 * Cougaar Micro Edition.
 */
public class MicroTaskPlugin extends ComponentPlugin implements MessageListener
{
  private IncrementalSubscription sub;
  private IncrementalSubscription taskSub;
  private boolean debug = false;
  MEMessageService service;


  /**
   * Subscribe to allocations to MicroAgents
   */
  protected void setupSubscriptions() {
    registerMessageListener();
    sub = (IncrementalSubscription)getBlackboardService().subscribe(new UnaryPredicate() {
      public boolean execute(Object o) {
        if (o instanceof Allocation) {
          Allocation a = (Allocation)o;
          return a.getAsset() instanceof MicroAgent;
        }
        return false;
      }});

    taskSub = (IncrementalSubscription)getBlackboardService().subscribe(new UnaryPredicate() {
      public boolean execute(Object o) {
        if (o instanceof Task)
          return true;
        return false;
      }});

      Collection v = getParameters();
      debug = v.contains("debug");
      if (debug)
        System.out.println("MicroTaskPlugin: debug ON");
  }

  /**
   * Called when objects in the PLAN change
   */
  protected void execute() {
    // send new or changed allocations to the micro asset.
    Collection newlyAllocedTasks = new Vector();
    Collection allocs = new Vector();
    if (sub.getAddedCollection() != null)
      allocs.addAll(sub.getAddedCollection());
    Iterator iter = allocs.iterator();
    while (iter.hasNext()) {
      Allocation alloc = (Allocation)iter.next();
      newlyAllocedTasks.add(alloc.getTask());
      processAllocation(alloc, "add");
    }

    Collection callocs = new Vector();
    if (taskSub.getChangedCollection() != null)
      callocs.addAll(taskSub.getChangedCollection());
    Iterator citer = callocs.iterator();
    while (citer.hasNext()) {
      Task task = (Task)citer.next();
      if (!newlyAllocedTasks.contains(task)) {  // only send changed tasks if they're not
        processTask(task, "change");            // associated with a new allocation
      }
    }

    Collection dallocs = new Vector();
    if (sub.getRemovedCollection() != null)
      dallocs.addAll(sub.getRemovedCollection());
    Iterator diter = dallocs.iterator();
    while (diter.hasNext())
      processAllocation((Allocation)diter.next(), "remove");
  }

  private void processTask(Task task, String op) {
    Allocation allo = (Allocation)task.getPlanElement();
    if (allo == null)
      return;
    Asset asset = allo.getAsset();
      MicroTask mt = service.newMicroTask(task);
      try {
        service.getMessageTransport().sendTo((MicroAgent)asset, mt, op);
      } catch (java.io.IOException ioe)
      {
        System.err.println("IOException sending message to MicroAgent");
        ioe.printStackTrace();
      }
  }

  /**
   * Transmit an allocation to the micro agent.
   */
  private void processAllocation(Allocation allo, String op) {
    Asset asset = allo.getAsset();
      if (debug) System.out.println("MicroTaskPlugin: Allocation to MicroAgent: " + asset);
      // encode task to send to micro agent
      MicroTask mt = service.newMicroTask(allo.getTask());

      try {
        service.getMessageTransport().sendTo((MicroAgent)asset, mt, op);
      } catch (java.io.IOException ioe)
      {
        System.err.println("IOException sending message to MicroAgent");
        ioe.printStackTrace();
      }
  }

  /**
   * Receive a message from a micro-agent.  For now, assume it's a MicroTask.
   */
  public boolean deliverMessage(String msg, String src, String srcAddress, OutputStream client, InputStream in) {
    if (msg.indexOf("<MicroTask") >= 0) {
     TaskDecoder td = new TaskDecoder();
     MicroTask mt = td.decode(msg);
     getBlackboardService().openTransaction();
     // lookup the original task
     Task t = lookupTask(mt.getUniqueID());
     if (debug) System.out.println("MicroTaskPlugin: Delivering task: " + t);
     if (t == null) {
       System.err.println("MicroTaskPlugin: Error finding task \""+mt.getUniqueID()+"\"");
       getBlackboardService().closeTransaction();
       return true;
     }

     // update the reported result
     Allocation alloc = (Allocation)t.getPlanElement();
     AllocationResult ar = decodeAllocationResult(mt);
     if ((ar == null) || (alloc == null)) {
      getBlackboardService().closeTransaction();
      if (debug) System.out.println("MicroTaskPlugin: allocation not updated.  ar="+ar+": alloc="+alloc);
      return true;
     }
     ((PlanElementForAssessor)alloc).setReceivedResult(ar);

     if (debug) System.out.println("MicroTaskPlugin: Changing alloc: "+alloc);
     getBlackboardService().publishChange(alloc);
     getBlackboardService().closeTransaction();
    }
    return true;
  }

  /**
   * Unwrap an allocation result from a MicroTask's MicroAllocationResult.
   */
  private AllocationResult decodeAllocationResult(MicroTask mt) {
    if (mt.getAllocation() == null)
      return null;
    if (mt.getAllocation().getReportedResult() == null)
      return null;
    double rating = mt.getAllocation().getReportedResult().getConfidenceRating();
    boolean success = mt.getAllocation().getReportedResult().isSuccess();
    int [] aspects = mt.getAllocation().getReportedResult().getAspects();
    /*
     * Convert Long array (in thousandths) to double array
     */
    long [] thousandths = mt.getAllocation().getReportedResult().getValues();
    double [] values = new double[thousandths.length];
    for (int i=0; i<thousandths.length; i++)
      values[i] = thousandths[i] / 1000.0;
    AllocationResult ar = new AllocationResult(rating, success, aspects, values);
    return ar;
  }

  /**
   *  Lookup a task in the PLAN by UID.
   */
  private Task lookupTask(String UID) {
    Task ret = null;
    Collection tasks_col = getBlackboardService().query(new TaskPredicate(UID));
    Iterator iter = tasks_col.iterator();
    if (iter.hasNext())
      ret = (Task)iter.next();
    return ret;
  }

  protected void registerMessageListener() {
    service = (MEMessageService)getBindingSite().getServiceBroker().getService(this, org.cougaar.microedition.se.domain.MEMessageService.class, null);
    if (service == null) {
        System.err.println("Error getting the MEMessageService: Is MicroAgentMessagePlugin initialized yet?");
    }
    service.getMessageTransport().addMessageListener(this);
  }
}

/**
 * Predicate class for looking up tasks by UID.
 */
class TaskPredicate implements UnaryPredicate {
  private String UID;
  public TaskPredicate (String UID) {
    this.UID = UID;
  }
  public boolean execute (Object o) {
    if (o instanceof Task) {
      Task t = (Task)o;
      return t.getUID().toString().equals(UID);
    }
    return false;
  }
  
  
}
