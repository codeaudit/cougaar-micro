/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */


package org.cougaar.microedition.util;

/**
 * This class contains one static method which returns the appropriate object given the ME type.
 */
public class MicroEdition {

 static ObjectFactory factory = null;

/**
 * This static variable stores the kvm (j2me) configuration. If null we assume tini (java1.1).
 */
 static String kvmConfig;
 static {
   kvmConfig = System.getProperty("microedition.configuration");
   try {
     if (kvmConfig != null) {
       factory = (ObjectFactory)Class.forName("org.cougaar.microedition.kvm.KvmObjectFactory").newInstance();
     } else {
       factory = (ObjectFactory)Class.forName("org.cougaar.microedition.tini.TiniObjectFactory").newInstance();
     }
   } catch (Exception ex) {
     System.err.println("Error installing object factory");
     ex.printStackTrace();
   }
 }



/**
 * This constructor does nothing.
 */
  private MicroEdition() {
  }

  /**
   * This static method returns the appropriate object given the ME type.
   *
   * @param   ofType The abstract type of which to make a concrete instance.
   * @return  Object which is an appropriate instantiation of the ofType argument.
   */
  public static Object getObjectME(Class ofType) {
    return factory.getObjectME(ofType);
  }

  /**
   * This static method returns the appropriate object given the ME type.
   *
   * @param   ofType The name of the abstract type of which to make a concrete instance.
   * @return  Object which is an appropriate instantiation of the ofType argument.
   */
  public static Object getObjectME(String ofType) {
    Object ret = null;
    try {
      ret = factory.getObjectME(Class.forName(ofType));
    } catch (ClassNotFoundException cnfe) {
      System.err.println("Error getting micro object "+ofType);
    }
    return ret;
  }

}
