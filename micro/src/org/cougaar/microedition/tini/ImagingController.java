/*
 * <copyright>
 *  Copyright 1999-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.tini;

import java.util.*;
import java.io.*;

import org.cougaar.microedition.util.*;
import org.cougaar.microedition.ldm.*;
import org.cougaar.microedition.plugin.*;
import org.cougaar.microedition.asset.*;
import org.cougaar.microedition.shared.*;
import org.cougaar.microedition.shared.Constants;

public class ImagingController extends ControllerResource
{

  private String commandstring = "/root/bin/cam -s 1 -Q 3 -z 0 -t /root/bin/photo.jpg";

  public void modifyControl(String controlparameter, String controlparametervalue)
  {

  }

  public void setUnits(String u) {}
  public void setChan(int c) {}

  private boolean isStarted = false;

  public boolean isUnderControl()
  {
    return isStarted;
  }

  public void startControl()
  {
    //take picture
    if(jruntime != null)
    {
      try
      {
	System.out.println("ImagingController: start Control...");
        Process proc = jruntime.exec(commandstring);
        String line = null;

	DataInputStream ls_in = new DataInputStream(proc.getErrorStream());
        while ((line = ls_in.readLine()) != null)
	{
            System.out.println(line);
	}

        ls_in = new DataInputStream(proc.getInputStream());
        while ((line = ls_in.readLine()) != null)
	{
            System.out.println(line);
	}

	int exitVal = proc.waitFor();
        System.out.println("Process exitValue: " + exitVal);
      }
      catch (Exception e)
      {
	System.err.println("ImagingController: Unable to execute runtime environment!!!");
	e.printStackTrace();
      }
    }
    else
    {
      System.err.println("ImagingController: jruntime is null!");
    }

    isStarted = true;
  }

  public void stopControl()
  {
    isStarted = false;
  }

  public void getValues(long [] values)
  {

  }

  public void getValueAspects(int [] aspects)
  {

  }

  public int getNumberAspects()
  {
    return 0;
  }

  public boolean getSuccess()
  {
    return true;
  }

  public boolean conditionChanged()
  {
    return false;
  }

  /**
   * Constructor.  Sets name default.
   */
  public ImagingController() {}

  /**
   * Set parameters with values from my node and initialize resource.
   */
  private Runtime jruntime = null;

  public void setParameters(java.util.Hashtable t)
  {
    setName("ImagingController");
    jruntime = Runtime.getRuntime();
    if(jruntime != null)
    {
      try
      {
	System.out.println("ImagingController: runtime exec...");
        Process proc = jruntime.exec("/bin/echo Runtime object testing...");
        String line = null;

	DataInputStream ls_in = new DataInputStream(proc.getErrorStream());
        while ((line = ls_in.readLine()) != null)
	{
            System.out.println(line);
	}

        ls_in = new DataInputStream(proc.getInputStream());
        while ((line = ls_in.readLine()) != null)
	{
            System.out.println(line);
	}

	int exitVal = proc.waitFor();

      }
      catch (Exception e)
      {
	e.printStackTrace();
	System.err.println("ImagingController: Unable to execute runtime environment!!!");
      }
    }
    else
    {
      System.err.println("ImagingController: Unable to retrieve runtime environment!!!");
    }
  }
}
