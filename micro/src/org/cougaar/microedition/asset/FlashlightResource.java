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

/**
 * Class for FlashlightResources.
 */
abstract public class FlashlightResource extends ResourceAdapter {

  static private boolean debug=true;


  abstract public boolean isOn() ;

  /**
    Attempts to set the Flashlight to the value indicated.
    @value true for on; false for off
    @return actual value after the method completes execution
  */
  abstract public boolean setOn(boolean value) ;

  public FlashlightResource() {
    setName("Flashlight");
  }

}
