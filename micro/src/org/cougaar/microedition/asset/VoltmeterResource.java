/*
 * <copyright>
 *  Copyright 1999-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.asset;

import java.util.*;

import org.cougaar.microedition.util.*;
import org.cougaar.microedition.ldm.*;

/**
 * create a Voltmeter Resource.
 */
public abstract class VoltmeterResource extends SensorResource {

  public VoltmeterResource() {
    setName("Voltage");
  }

}
