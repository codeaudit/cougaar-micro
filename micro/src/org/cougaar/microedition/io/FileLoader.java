/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */

package org.cougaar.microedition.io;

/**
 * This interface provides a description of a generic file loader.
 * Versions for the Tini board and KVM will be written.
 */
public interface FileLoader {

	/**
	 * The string where the Protocol, hostName and hostPort are stored.
	 * Basicly, the URL minus the file and its local path.
	 * @since   Oct 2, 2000
	 */
	String pnp = "";

	/**
	 * Concatenates the Protocol, hostName and hostPort and stores in pnp.
	 *
	 * @param   protocol	"http", etc.
	 * @param   hostName	"www.bbn.com", etc
	 * @param   hostPort	use default if = zero, default for http is 80
	 * @since   Oct 2, 2000
	 */
  void configure(String protocol, String hostName, short hostPort);

	/**
	 * Returns the contents of the pnp String. (pnp=Protocol + hostName + hostPort).
	 *
	 * @return  the contents of the String pnp.
	 * @since   Oct 2, 2000
	 */
	String showConfig();

	/**
	 * opens the URL constructed from the pnp and file name and returns contents.
	 *
	 * @param   fileName	file name to be concatenated onto the pnp.
	 * @return  String that contains the contents of the file
	 * @exception	possible IO or URL exceptions
	 * @since   Oct 2, 2000
	 */
	String getFile(String fileName) throws Exception;

}
