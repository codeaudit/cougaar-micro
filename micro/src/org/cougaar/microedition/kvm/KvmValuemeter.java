/*

 * <copyright>

 *  Copyright 1999-2000 Defense Advanced Research Projects

 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and

 *  Raytheon Systems Company (RSC) Consortium).

 *  This software to be used only in accordance with the

 *  COUGAAR licence agreement.

 * </copyright>

 */

package org.cougaar.microedition.kvm;



import org.cougaar.microedition.asset.*;



/**

 * create a

 */

public class KvmValuemeter extends ValueResource {



  static String[] Units = { "" };



  PalmOut po = null;



  public KvmValuemeter() {

    po = new PalmOut();

  }



  public void setChan(int c) {}



  public void setUnits(String u) { }



  public long getValue() {

    int ival = po.getValue();

    long ret = (long)ival;

    return ret;

  }

}

