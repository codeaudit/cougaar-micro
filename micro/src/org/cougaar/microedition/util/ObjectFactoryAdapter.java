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

import java.util.*;

/**
 * This adapter class implements the functions of an ObjectFactory.  Just override
 * addClasses to call addClass with each class specific to this JVM.
 */
public abstract class ObjectFactoryAdapter implements ObjectFactory {

  public ObjectFactoryAdapter() {
    addClasses();
  }

  protected Vector classes = new Vector();

  protected abstract void addClasses();

  protected void addClass(Class clazz) {
      classes.addElement(clazz);
  }

  protected void addClass(String className) {
    try {
      addClass(Class.forName(className));
    } catch (Exception ex) {
      System.err.println("Error initializing ObjectFactory with class "+className);
      ex.printStackTrace();
    }
  }

  public Object getObjectME(Class ofType) {
    Object ret = null;
    for (Enumeration enum = classes.elements(); enum.hasMoreElements();) {
      Class clazz = (Class) enum.nextElement();
      if (ofType.isAssignableFrom(clazz)) {
        try {
          ret = clazz.newInstance();
          break;
        } catch (Exception iae) {
          System.err.println("Error instantiating "+ofType.getName());
        }
      }
    }
    return ret;
  }


}