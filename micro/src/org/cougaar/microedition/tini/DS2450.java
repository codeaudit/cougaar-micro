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

import java.io.*;
import java.net.*;
import java.util.*;
import com.ibutton.utils.*;
import com.ibutton.adapter.*;
import com.ibutton.container.*;
import com.ibutton.*;
import com.dalsemi.tininet.http.*;

public class DS2450
{
  static Vector   madv = new Vector();         // madv = my a/d vector
  static Object   adlock = new Object();       // adlock = a/d lock

  static int      madvsize;
  static int      timeout;
  boolean debugging = false;

  public DS2450(String[] args)
  {
    timeout = 20;

    if (args.length > 0)
    {
      try
      {
        timeout = Integer.parseInt(args[0]);
      }
      catch (NumberFormatException e)
      {
      }
    }
    // initialization
    try
    {
      DSPortAdapter   pa = new TINIExternalAdapter();
      File            f = new File("DS2450.txt");
      if (f.exists())
      {
        // Read 1-Wire Net Addresses and A/D identifiers.
        /*
         * NOTE: The OneWire A/Ds are read in order, and the
         *       corresponding index numbers are assigned in this
         *       same order.  Since this code uses index numbers
         *       to perform operations on switches, changing the
         *       order of the A/Ds in the DS2450.txt file
         *       changes the operation of the code.
         */
        BufferedReader  br =
                  new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String          s = null;
        while ((s = br.readLine()) != null)
        {
          int     k1 = s.indexOf("=");
          String  sn = s.substring(0, k1).trim();
          String  sa = s.substring(k1 + 1).trim();
          byte addr[] = Address.toByteArray(sn);
          try
          {
            madv.addElement(new MyADContainer(pa, addr, sa));
          }
          catch (OneWireIOException e)
          {
            System.out.println(e);
          }
        }
        br.close();
      }
      else   // identifier file does not exist, read from 1-wire bus
      {
        try {
          File newDS2450;
          newDS2450 = new File("newDS2450.txt");
          BufferedWriter bw = new BufferedWriter(new FileWriter(newDS2450));
          pa.targetFamily(0x20);
          for (Enumeration e = pa.getAlliButtons();e.hasMoreElements(); )
          {
            iButtonContainer ibc = (iButtonContainer)e.nextElement();
            byte                addr[] = ibc.getAddress();
            String              sn = ibc.getAddressAsString();
            madv.addElement(new MyADContainer(pa, addr, sn));
            bw.write(sn,0,sn.length());
            bw.newLine();
          }
          bw.flush();
          bw.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        catch (OneWireIOException e) {
          System.out.println();
        }
      }

      madvsize = madv.size();

      if (madvsize > 0)
      {
        int     i = 0;
        boolean level = true;
        byte[]  adstate;
        double[] advoltages;
        // default A/D setup
        double adrange = 5.12;       // volts
        double adresolution = 0.01;  // volts
        int alarmtype = 1;           // 1 = high
        double adalarmvalue = 3.0;   // volts
        boolean alarmenable = false; // disabled
        boolean outputenable = true;  // enabled
        boolean outputstate = false;    // false = logic 0

        for (Enumeration e = madv.elements(); e.hasMoreElements(); )
        {
          MyADContainer madc = (MyADContainer)e.nextElement();
          try
          {
            synchronized (adlock)
            {
              adstate = madc.readDevice();
              for (int adchan=0; adchan < madc.getNumberADChannels(); adchan++)
              {
                 madc.setADRange(adchan, adrange, adstate);
                 madc.setADResolution(adchan, adresolution, adstate);
                 madc.setADAlarm(adchan, alarmtype, adalarmvalue, adstate);
                 madc.setADAlarmEnable(adchan, alarmtype, alarmenable, adstate);
                 madc.setOutput(adchan, outputenable, outputstate, adstate);
              }

              madc.writeDevice(adstate);
            }
            i++;
          }
          catch (Throwable t)
          {
            i += 1;
          }
        }
      }
      else
      {
        System.out.println("No A/D Converters");
      }
    }
    catch (Exception e)
    {
      System.out.println(e);
    }
  }

  /*
   * This method reads the A/D level on the indicated channel on the indicated
   * converter IC.
   */
  public double readVoltage(int adindex, int adchannel)
  {
    double voltage = 0.0;
    try
    {
      if (adindex > -1 && adchannel > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);
          if (adchannel < madc.getNumberADChannels())
          {
            synchronized (adlock)
            {
              byte[] adstate = madc.readDevice();
              madc.doADConvert(adchannel, adstate);
              voltage = madc.getADVoltage(adchannel, adstate);
            }
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return voltage;
  }

  /*
   * This method reads the A/D level on all channels on the indicated
   * converter IC.
   */
  public double[] readAllVoltages(int adindex)
  {
    double[] voltages = {0.0, 0.0, 0.0, 0.0};
    try
    {
      if (adindex > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);
          boolean[] convertChannels = {true, false, true, false};
          synchronized (adlock)
          {
            byte[] adstate = madc.readDevice();
            madc.doADConvert(convertChannels, adstate);
            voltages = madc.getADVoltage(adstate);
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return voltages;
  }



  /*
   * This method reads the alarm trigger on the indicated channel on the indicated
   * converter IC.  The alarm will only be triggered by a previous readVoltage()
   * call.  The alarm flags for all channels in the device are cleared.
   */
  public boolean readAlarm(int adindex, int adchannel, int alarmtype)
  {
    boolean triggered = false;
    try
    {
      if (adindex > -1 && adchannel > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);
          if (adchannel < madc.getNumberADChannels())
          {
            synchronized (adlock)
            {
              byte[] adstate = madc.readDevice();
              if (madc.hasADAlarmed(adchannel, alarmtype, adstate))
              {
                triggered = true;
                if (debugging) {System.out.println("A/D ALARM event has occurred.")};
                madc.writeDevice(adstate);     // clear alarm flags for all channels
              }
            }
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return triggered;
  }

  /*
   * This method reads the alarm triggers on all channels on the indicated
   * converter IC.  The alarms will only be triggered by a previous readAllVoltages()
   * call.  The alarm flags for all channels in the device are cleared when read.
   */
  public boolean[] readAllAlarms(int adindex, int alarmtype)
  {
    int numberofADchannels = 4;
    boolean triggered[] = new boolean [numberofADchannels];
    try
    {
      if (adindex > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);
          for (int i = 0; i < madc.getNumberADChannels(); i++)
          {
            triggered[i] = false;
          }
          synchronized (adlock)
          {
            byte[] adstate = madc.readDevice();
            for (int chancount = 0; chancount < madc.getNumberADChannels(); chancount++)
            {
              if (madc.hasADAlarmed(chancount, alarmtype, adstate))
              {
                triggered[chancount] = true;
                System.out.println("A/D ALARM event has occurred on channel " + chancount + ".");
              }
            }
            madc.writeDevice(adstate);     // clear alarm flags for all channels
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return triggered;
  }

  /*
   * This method configures A/D Output capability on the indicated channel.
   */
  public void configureADOutput(int adindex, int adchannel, boolean outputenable, boolean outputstate)
  {
    try
    {
      if (adindex > -1 && adchannel > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);
          if (adchannel < madc.getNumberADChannels())
          {
            synchronized (adlock)
            {
              byte[] adstate = madc.readDevice();
              madc.setOutput(adchannel, outputenable, outputstate, adstate);
              madc.writeDevice(adstate);
            }
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return;
  }

  /*
   * This method configures A/D conversion on the indicated channel.
   */
  public void configureAD(int adindex, int adchannel, double adrange, double adresolution)
  {
    // double adrange = 2.56 or 5.12;       // volts
    // double adresolution = 0.01;  // volts
    try
    {
      if (adindex > -1 && adchannel > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);
          if (adchannel < madc.getNumberADChannels())
          {
            synchronized (adlock)
            {
              byte[] adstate = madc.readDevice();
              madc.setADRange(adchannel, adrange, adstate);
              madc.setADResolution(adchannel, adresolution, adstate);
              madc.writeDevice(adstate);
            }
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return;
  }


  /*
   * This method configures A/D alarm on the indicated channel.  You should use
   * the configureAD method before using this method.
   */
  public void configureAlarm(int adindex, int adchannel, int alarmtype, double alarmtrigger, boolean alarmenable)
  {
    // int alarmtype = 1 (high) or 0 (low)
    // double alarmtrigger = some voltage level within adrange
    // boolean alarmenable = true (enable the alarm) or false (disable the alarm)
    try
    {
      if (adindex > -1 && adchannel > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);
          if (adchannel < madc.getNumberADChannels())
          {
            synchronized (adlock)
            {
              byte[] adstate = madc.readDevice();
              madc.setADAlarm(adchannel, alarmtype, alarmtrigger, adstate);
              madc.setADAlarmEnable(adchannel, alarmtype, alarmenable, adstate);
              madc.writeDevice(adstate);
            }
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return;
  }

  /*
   * This method reads the status of the indicated A/D converter IC and prints
   * it to standard output.
   */
  public void readStatus(int adindex)
  {
    double voltage = 0.0;
    try
    {
      if (adindex > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);

          synchronized (adlock)
          {
            byte[] adstate = madc.readDevice();
            System.out.println("\n" + madc.getADName());
            System.out.println("Number of A/D Channels supported: " + madc.getNumberADChannels() + ".");
            for (int adchan=0; adchan < madc.getNumberADChannels(); adchan++)
            {
              System.out.println ("Channel " + adchan + " status");
              double adrangeset = madc.getADRange(adchan, adstate);
              System.out.println("A/D range set at: 0 to " + adrangeset + " volts.");
              double adresolutionset = madc.getADResolution(adchan, adstate);
              System.out.println("A/D resolution set at: " + adresolutionset + " volts.");
              int highalarm = 1;
              int lowalarm = 0;
              double alarmvoltageset = madc.getADAlarm(adchan, highalarm, adstate);
              System.out.println("Alarm trigger voltage set at: " + alarmvoltageset + " volts.");
              boolean alarmenabled = madc.getADAlarmEnable(adchan, highalarm, adstate);
              System.out.println("Alarm enabled: " + alarmenabled + ".");
              boolean outputenabled = madc.isOutputEnabled(adchan, adstate);
              System.out.println("Output enabled: " + outputenabled + ".");
              boolean outputlogiclevel = madc.getOutputState(adchan, adstate);
              System.out.println("Output logic level (true = high/false = low): " + outputlogiclevel + ".");
              boolean powerexternal = madc.isPowerExternal(adstate);
              System.out.println("Power set to external: " + powerexternal + ".");

            }
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return;
  }

  /*
   * This method reads the name of the onewire device IC.
   */
  public String readDeviceName(int adindex)
  {
    String devname = " ";
    try
    {
      if (adindex > -1)
      {
        if (adindex < madvsize)
        {
          MyADContainer   madc = (MyADContainer)madv.elementAt(adindex);

          synchronized (adlock)
          {
            devname = madc.getADName();
          }
        }
      }
    }
    catch (Throwable t)
    {
    }
    return devname;
  }

  class MyADContainer extends iButtonContainer20
  {
    String  namea;
    byte[] lastadstate;
    public String getADName()
    {
      return(namea);
    }
    public MyADContainer(DSPortAdapter pa, byte[] addr,
                   String sa) throws iButtonException
    {
      namea = sa;
      setupContainer(pa, addr);
      synchronized (adlock)
      {
        lastadstate = readDevice();
      }
    }
  }
}
