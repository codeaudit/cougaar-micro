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
package org.cougaar.microedition.tini;

import java.util.*;

import org.cougaar.microedition.asset.*;
import org.cougaar.microedition.io.*;
import org.cougaar.microedition.util.*;
import org.cougaar.microedition.ldm.*;
import org.cougaar.microedition.plugin.*;

/**
 */
public class TiniMach5CouplingPlugIn extends PlugInAdapter {

  UnaryPredicate getPosResourcePred() {
    UnaryPredicate resourcePred = new UnaryPredicate() {
      public boolean execute(Object o) {
        if (o instanceof TiniMach5PositionResource) {
          return true;
        }
        return false;
      }
    };
    return resourcePred;
  }

  UnaryPredicate getLocResourcePred() {
    UnaryPredicate resourcePred = new UnaryPredicate() {
      public boolean execute(Object o) {
        if (o instanceof TiniMach5LocomotionResource) {
          return true;
        }
        return false;
      }
    };
    return resourcePred;
  }

  private Subscription pos_resourceSub;
  private Subscription loc_resourceSub;
  private TiniMach5LocomotionResource locresource = null;
  private TiniMach5PositionResource posresource = null;

  public void setupSubscriptions() {

    System.out.println("TiniMach5CouplingPlugIn::setupSubscriptions v2");

    pos_resourceSub = subscribe(getPosResourcePred());
    loc_resourceSub = subscribe(getLocResourcePred());

  }


  public void execute() {

    System.out.println("TiniMach5CouplingPlugIn.execute()");

    Enumeration enum = pos_resourceSub.getAddedList().elements();
    if (enum.hasMoreElements())
    {
      posresource = (TiniMach5PositionResource)enum.nextElement();
    }

    enum = loc_resourceSub.getAddedList().elements();
    if (enum.hasMoreElements())
    {
      locresource = (TiniMach5LocomotionResource)enum.nextElement();
    }

    if(posresource != null && locresource != null)
    {
      System.out.println("TiniMach5CouplingPlugIn Assigning resource");

      posresource.setLocomotionResource(locresource);
    }
  }
}