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

package org.cougaar.microedition.demo;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;

import org.cougaar.lib.planserver.*;
import org.cougaar.core.plugin.*;
import org.cougaar.core.util.*;
import org.cougaar.util.*;
import org.cougaar.core.cluster.*;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.domain.planning.ldm.asset.*;

import org.cougaar.microedition.se.domain.*;


public class PSP_ReportHeatIndex extends PSP_BaseAdapter implements PlanServiceProvider, UseDirectSocketOutputStream, UISubscriber
{

  class AllRecords implements UnaryPredicate
  {
    public boolean execute(Object o)
    {
      if (o instanceof HeatIndexRecord)
      {
        HeatIndexRecord rec = (HeatIndexRecord)o;
	return true;
      }
      return false;
    }
  }

  public PSP_ReportHeatIndex()
  {
    super();
  }

  public PSP_ReportHeatIndex(String pkg, String id) throws RuntimePSPException
  {
    setResourceLocation(pkg, id);
  }

  public boolean test(HttpInput query_parameters, PlanServiceContext sc)
  {
    super.initializeTest(); // IF subclass off of PSP_BaseAdapter.java
    return false;  // This PSP is only accessed by direct reference.
  }

  public void execute(
      PrintStream cout,
      HttpInput query_parameters,
      PlanServiceContext psc,
      PlanServiceUtilities psu) throws Exception
  {
    Collection recs = psc.getServerPlugInSupport().queryForSubscriber( new AllRecords());
    Iterator rec_iter = recs.iterator();
    cout.println("Pod ID\t\t Time\t\t HeatIndex(F)");
    while (rec_iter.hasNext())
    {
      HeatIndexRecord hirec = (HeatIndexRecord)rec_iter.next();
      cout.println(hirec.GetPodId()+"\t\t "+hirec.GetRecordTime()+"\t\t "+hirec.GetHeatIndex());
    }
  }

  public boolean returnsXML()
  {
    return false;
  }

  public boolean returnsHTML()
  {
    return true;
  }

  public String getDTD()
  {
    return null;
  }

  public void subscriptionChanged(Subscription subscription)
  {
  }
}
