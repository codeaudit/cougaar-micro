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
 * Each JVM may have a different object factory for creating platform-specific
 * objects like sockets.
 */
public interface ObjectFactory {
  /**
   * Return a subclass of this class appropriate for this JVM
   * @param ofType the abstract type to return a concrete subclass of.
   * @return an instance of the concrete subclass.
   */
  public Object getObjectME(Class ofType);
}