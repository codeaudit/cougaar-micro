/*
 * <copyright>
 *  Copyright 1997-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package tutorial;

/**
 * This class can be published to the PLAN and subscribed to by PlugIns
 * @author ALPINE (alpine-software@bbn.com)
 * @version $Id: Status.java,v 1.1 2000-10-23 14:56:56 mtiberio Exp $
 **/
public class Status {

  String what;
  public Status(String what) {
    this.what = what;
  }

  public String toString() {
    return what;
  }
} 
