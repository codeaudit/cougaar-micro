/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.kvm;

import org.cougaar.microedition.util.ObjectFactoryAdapter;
import java.util.*;

/**
 * Defines classes specific to the KVM.
 */
public class KvmObjectFactory extends ObjectFactoryAdapter {

  public void addClasses() {
    addClass("org.cougaar.microedition.kvm.KvmServerSocketME");
    addClass("org.cougaar.microedition.kvm.KvmSocketME");
    addClass("org.cougaar.microedition.kvm.KvmFileLoader");
  }
}