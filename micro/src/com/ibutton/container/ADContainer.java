
/*---------------------------------------------------------------------------
 * Copyright (C) 1999,2000 Dallas Semiconductor Corporation, All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL DALLAS SEMICONDUCTOR BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Dallas Semiconductor
 * shall not be used except as stated in the Dallas Semiconductor
 * Branding Policy.
 *---------------------------------------------------------------------------
 */

package com.ibutton.container;

// imports
import com.ibutton.*;
import com.ibutton.adapter.*;


/**
 * 1-Wire A/D interface class for basic analog measuring
 * operations. This class should be implemented for each A/D
 * type 1-Wire device.
 *
 * @version    0.00, 27 August 2000
 * @author     DS
 */
public interface ADContainer
   extends OneWireSensor
{

   //--------
   //-------- Static Final Variables
   //--------

   /** High alarm */
   public static final int ALARM_HIGH = 1;

   /** Low Alarm */
   public static final int ALARM_LOW = 0;

   //--------
   //-------- A/D Feature methods
   //--------

   /**
    * Query to get the number of channels supported by this A/D.
    * Channel specific methods will use a channel number specified
    * by an integer from [0 to (getNumberChannels() - 1)].
    *
    * @return int containing the number of channels
    */
   public int getNumberADChannels ();

   /**
    * Query to see if this A/D measuring device has high/low
    * alarms.
    *
    * @return boolean, true if has high/low trips
    */
   public boolean hasADAlarms ();

   /**
    * Query to get an array of available ranges for the specified
    * A/D channel.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    *
    * @return array of double indicated the available ranges starting
    *         from the largest range to the smallest range.
    */
   public double[] getADRanges (int channel);

   /**
    * Query to get an array of available resolutions based
    * on the specified range on the specified A/D channel.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param range
    *
    * @return array of double, indicated the available resolutions
    */
   public double[] getADResolutions (int channel, double range);

   /**
    * Query to see if this A/D supports doing multiple voltage
    * conversions at the same time.
    *
    * @return boolean, true if can do multi-channel voltage reads.
    */
   public boolean canADMultiChannelRead ();

   //--------
   //-------- A/D IO Methods
   //--------

   /**
    * This method is used to perform voltage conversion on all specified
    * channels.  The method 'getVoltage()' can be used to read the result
    * of the conversion.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void doADConvert (int channel, byte[] state)
      throws OneWireIOException, iButtonException;

   /**
    * This method is used to perform voltage conversion on all specified
    * channels.  The method 'getVoltage()' can be used to read the result
    * of the conversion. This A/D must support multi-channel read
    * 'canMultiChannelRead()' if there are more then 1 channel is specified.
    *
    * @param doConvert - boolean array representing which channels
    *                    to perform conversion on.
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void doADConvert (boolean[] doConvert, byte[] state)
      throws OneWireIOException, iButtonException;

   /**
    * This method is used to read the voltage values.  Must
    * be used after a 'doADConvert()' method call.  Also must
    * include the last valid state from the 'readDevice()' method
    * and this A/D must support multi-channel read 'canMultiChannelRead()'
    * if there are more then 1 channel.
    *
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    *
    * @return - double[] representing the voltage values for all channels
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public double[] getADVoltage (byte[] state)
      throws OneWireIOException, iButtonException;

   /**
    * This method is used to read a channels voltage value.  Must
    * be used after a 'doADConvert()' method call.  Also must
    * include the last valid state from the 'readDevice()' method.
    * Note, if more then one channel is to be read then it is more
    * efficient to use the 'getVoltage()' method that returns all
    * channel values.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    *
    * @return - double representing the voltage value for the specified
    *                  channel
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public double getADVoltage (int channel, byte[] state)
      throws OneWireIOException, iButtonException;

   //--------
   //-------- A/D 'get' Methods
   //--------

   /**
    * This method is used to extract the alarm voltage value of the
    * specified channel from the provided state buffer.  The
    * state buffer is retrieved from the 'readDevice()' method.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param alarmType - int, representing the desired alarm, ALARM_HIGH (1)
    *               or ALARM_LOW (0)
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    *
    * @return - double representing the alarm_value in volts
    */
   public double getADAlarm (int channel, int alarmType, byte[] state)
      throws iButtonException;

   /**
    * This method is used to extract the alarm enable value of the
    * specified channel from the provided state buffer.  The state
    * buffer is retrieved from the 'readDevice()' method.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param alarmType - int, representing the desired alarm, ALARM_HIGH (1)
    *               or ALARM_LOW (0)
    * @param state - byte array of the current state of the state
    *               returned from 'readDevice()'.
    *
    * @return - boolean, true if specified alarm is enabled
    */
   public boolean getADAlarmEnable (int channel, int alarmType, byte[] state)
      throws iButtonException;

   /**
    * This method is used to check the alarm event value of the
    * specified channel from the provided state buffer.  The
    * state buffer is retrieved from the 'readDevice()' method.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param alarmType - int, representing the desired alarm, ALARM_HIGH (1)
    *               or ALARM_LOW (0)
    * @param state - byte array of the current state of the state
                    returned from 'readDevice()'.
    *
    * @return - boolean, true if specified alarm occurred
    */
   public boolean hasADAlarmed (int channel, int alarmType, byte[] state)
      throws iButtonException;

   /**
    * This method is used to extract the conversion resolution of the
    * specified channel from the provided state buffer expressed in
    * volts.  The state is retrieved from the
    * 'readDevice()' method.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param state - byte array of the current state of the state
    *               returned from 'readDevice()'.
    *
    * @return - double, resolution of channel in volts
    */
   public double getADResolution (int channel, byte[] state);

   /**
    * This method is used to extract the input voltage range of the
    * specified channel from the provided state buffer.  The state
    * buffer is retrieved from the 'readDevice()' method.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param state - byte array of the current state of the state
    *                  returned from 'readDevice()'.
    *
    * @return - double representing the input voltage range
    */
   public double getADRange (int channel, byte[] state);

   //--------
   //-------- A/D 'set' Methods
   //--------

   /**
    * This method is used to set the alarm voltage value of the
    * specified channel in the provided state buffer.  The
    * state buffer is retrieved from the 'readDevice()' method.
    * The method 'writeDevice()' must be called to finalize these
    * changes to the device.  Note that multiple 'set' methods can
    * be called before one call to 'writeDevice()'.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param alarmType - int, representing the desired alarm, ALARM_HIGH (1)
    *               or ALARM_LOW (0)
    * @param alarm - double, alarm value (will be reduced to 8 bit resolution).
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    */
   public void setADAlarm (int channel, int alarmType, double alarm,
                           byte[] state)
      throws iButtonException;

   /**
    * This method is used to set the alarm enable value of the
    * specified channel in the provided state buffer.  The
    * state buffer is retrieved from the 'readDevice()' method.
    * The method 'writeDevice()' must be called to finalize these
    * changes to the device.  Note that multiple 'set' methods can
    * be called before one call to 'writeDevice()'.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param alarmType - int, representing the desired alarm, ALARM_HIGH (1)
    *               or ALARM_LOW (0)
    * @param alarmEnable - boolean, alarm enable value
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    */
   public void setADAlarmEnable (int channel, int alarmType,
                                 boolean alarmEnable, byte[] state)
      throws iButtonException;

   /**
    * This method is used to set the conversion resolution value for the
    * specified channel in the provided state buffer.  The
    * state buffer is retrieved from the 'readDevice()' method.
    * The method 'writeDevice()' must be called to finalize these
    * changes to the device.  Note that multiple 'set' methods can
    * be called before one call to 'writeDevice()'.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param resolution - double, resolution to use in volts
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    */
   public void setADResolution (int channel, double resolution, byte[] state);

   /**
    * This method is used to set the input range for the
    * specified channel in the provided state buffer.  The
    * state buffer is retrieved from the 'readDevice()' method.
    * The method 'writeDevice()' must be called to finalize these
    * changes to the device.  Note that multiple 'set' methods can
    * be called before one call to 'writeDevice()'.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param range - double, representing the max volt range, use
    *                'getRanges()' method to get available ranges
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    */
   public void setADRange (int channel, double range, byte[] state);
}
