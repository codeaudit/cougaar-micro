package cougaar.microedition.util;

/**
 * Title:        Your Product Name
 * Description:  Your description
 * Copyright:    Copyright (c) 1999
 * Company:      Your Company
 * @author Your Name
 * @version
 */

public class MicroEdition {

  static String kvmConfig = System.getProperty("microedition.configuration");

  public MicroEdition() {
  }

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