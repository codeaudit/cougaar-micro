/*
 * MicroEdition.java
 *
 * Copyright 2000 by BBN Technologies, LLC. All Rights Reserved
 *
 */


package cougaar.microedition.util;

/**
 * This class contains one static method which returns the appropriate object given the ME type.
 */
public class MicroEdition {

/**
 * This static variable stores the kvm (j2me) configuration. If null we assume tini (java1.1).
 */
  static String kvmConfig = System.getProperty("microedition.configuration");

/**
 * This constructor does nothing.
 */
  public MicroEdition() {
  }

  /**
   * This static method returns the appropriate object given the ME type.
   *
   * @param   kvmClassname    String which represents the class to be instantiated if our vm is kvm.
   * @param   tiniClassname   String which represents the class to be instantiated if our vm is jvm 1.1.
   * @return  Object which is an appropriate instantiation of one of the two passed class names.
   */
  public static Object getObjectME(String kvmClassname, String tiniClassname) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

    String classname = null;

    if (kvmConfig == null)
      classname = tiniClassname;
    else
      classname = kvmClassname;

    Class claz = Class.forName(classname);

    return (Object)claz.newInstance();

  }

}