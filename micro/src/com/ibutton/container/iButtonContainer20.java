
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

import java.util.Vector;
import java.util.Enumeration;
import com.ibutton.*;
import com.ibutton.utils.*;
import com.ibutton.adapter.*;
import com.ibutton.container.*;
import java.io.*;


/**
 * iButton container for iButton family type 20 (hex).
 * The DS2450 is a Quad A\D converter.
 * <p>
 * WARNING! When converting Analog voltages to digital, the user of the device must
 * gaurantee that the voltage seen by the channel of the quad a\d does not exceed
 * the selected input range of the device.  If this happens, the device will default
 * to reading 0 volts.  The is NO way to know if the device is reading a higher than
 * specified voltage or NO voltage.
 * <p>
 * Typical Setup: <p>
 * <ul>
 * <li> readDevice()
 * <li> setResolution(), setRange() ...
 * <li> writeDevice()
 * </ul>
 * <p>
 * Typical Read voltage: <p>
 * <ul>
 * <li> doADConvert()
 * <li> getVoltage()
 * </ul>
 *
 * @version    0.00, 28 Aug 2000
 * @author     JK,DSS
 */
public class iButtonContainer20
   extends iButtonContainer
   implements ADContainer
{

   //--------
   //-------- Static Final Variables
   //--------

   /** Offset of BITMAP in array returned from read state */
   public static final int BITMAP_OFFSET = 24;

   /** Offset of ALARMS in array returned from read state */
   public static final int ALARM_OFFSET = 8;

   /** Offset of external power offset in array returned from read state */
   public static final int EXPOWER_OFFSET = 20;

   /** Channel A number */
   public static final int CHANNELA = 0;

   /** Channel B number */
   public static final int CHANNELB = 1;

   /** Channel C number */
   public static final int CHANNELC = 2;

   /** Channel D number */
   public static final int CHANNELD = 3;

   /** No preset value */
   public static final int NO_PRESET = 0;

   /** Preset value to zeros */
   public static final int PRESET_TO_ZEROS = 1;

   /** Preset value to ones */
   public static final int PRESET_TO_ONES = 2;

   /** Number of channels */
   public static final int NUM_CHANNELS = 4;

   /** DS2450 Convert command */
   private static final byte CONVERT_COMMAND = ( byte ) 0x3C;

   //--------
   //-------- Variables
   //--------

   /**
    * Voltage readout memory bank
    */
   private MemoryBankAD readout;

   /**
    * Control/Alarms/calibration memory banks vector
    */
   private Vector regs;

   //--------
   //-------- Constructors
   //--------

   /**
    * Default constructor
    */
   public iButtonContainer20 ()
   {
      super();

      // initialize the memory banks
      initMem();
   }

   /**
    * Create a container with a provided adapter object
    * and the address of the iButton or 1-Wire device.
    *
    * @param  sourceAdapter     adapter object required to communicate with
    * this iButton.
    * @param  newAddress        address of this 1-Wire device
    */
   public iButtonContainer20 (DSPortAdapter sourceAdapter, byte[] newAddress)
   {
      super(sourceAdapter, newAddress);

      // initialize the memory banks
      initMem();
   }

   /**
    * Create a container with a provided adapter object
    * and the address of the iButton or 1-Wire device.
    *
    * @param  sourceAdapter     adapter object required to communicate with
    * this iButton.
    * @param  newAddress        address of this 1-Wire device
    */
   public iButtonContainer20 (DSPortAdapter sourceAdapter, long newAddress)
   {
      super(sourceAdapter, newAddress);

      // initialize the memory banks
      initMem();
   }

   /**
    * Create a container with a provided adapter object
    * and the address of the iButton or 1-Wire device.
    *
    * @param  sourceAdapter     adapter object required to communicate with
    * this iButton.
    * @param  newAddress        address of this 1-Wire device
    */
   public iButtonContainer20 (DSPortAdapter sourceAdapter, String newAddress)
   {
      super(sourceAdapter, newAddress);

      // initialize the memory banks
      initMem();
   }

   //--------
   //-------- Methods
   //--------

   /**
    * getName returns the name of the iButton that this class contains.
    *
    * @return <code>String</code> representation of the iButtons name
    */
   public String getiButtonPartName ()
   {
      return "DS2450";
   }

   /**
    * getAlternateNames returns any other possible name for this iButton.
    *
    * @return <code>String</code> representation of the iButtons other names
    */
   public String getAlternateNames ()
   {
      return "1-wire Quad A/D Converter";
   }

   /**
    * getDescription returns a String that contains a brief description of the functionalitly
    * of this iButton.
    *
    * @return <code>String</code> containing a description of the functionalitly
    */
   public String getDescription ()
   {
      return "Four high-impedance inputs for measurement of analog "
             + "voltages.  User programable input range.  Very low "
             + "power.  Built-in multidrop controller.  Channels "
             + "not used as input can be configured as outputs "
             + "through the use of open drain digital outputs. "
             + "Capable of use of Overdrive for fast data transfer. "
             + "Uses on-chip 16-bit CRC-generator to garantee good data.";
   }

   /**
    * Returns the maximum speed this iButton can communicate at.
    */
   public int getMaxSpeed ()
   {
      return DSPortAdapter.SPEED_OVERDRIVE;
   }

   /**
    * Return an enumeration of memory banks. Look at the
    * MemoryBank, PagedMemoryBank and OTPMemoryBank classes.
    */
   public Enumeration getMemoryBanks ()
   {
      Vector bank_vector = new Vector(5);

      // Address number in read-only-memory
      bank_vector.addElement(new MemoryBankROM(this));

      // readout
      bank_vector.addElement(readout);

      // control/alarms/calibration
      for (int i = 0; i < 3; i++)
         bank_vector.addElement(regs.elementAt(i));

      return bank_vector.elements();
   }

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
   public int getNumberADChannels ()
   {
      return NUM_CHANNELS;
   }

   /**
    * Query to see if this A/D measuring device has high/low
    * alarms.
    *
    * @return boolean, true if has high/low trips
    */
   public boolean hasADAlarms ()
   {
      return true;
   }

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
   public double[] getADRanges (int channel)
   {
      double[] ranges = new double [2];

      ranges [0] = 5.12;
      ranges [1] = 2.56;

      return ranges;
   }

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
   public double[] getADResolutions (int channel, double range)
   {
      double[] res = new double [16];

      for (int i = 0; i < 16; i++)
         res [i] = range / ( double ) (1 << (i + 1));

      return res;
   }

   /**
    * Query to see if this A/D supports doing multiple voltage
    * conversions at the same time.
    *
    * @return boolean, true if can do multi-channel voltage reads.
    */
   public boolean canADMultiChannelRead ()
   {
      return true;
   }

   //--------
   //-------- A/D IO Methods
   //--------

   /**
    * This method retrieves the entire A/D control/status and alarm pages.
    * It reads this and verifies the data with the onboard CRC generator.
    * Use the byte array return from this method with static
    * utility methods to extract the status, alarm and other register values.
    * Appended to the data is 2 bytes that represent a bitmap
    * of changed bytes.  These bytes are used in the 'writeADRegisters()'
    * in conjuction with the 'set' methods to only write back the changed
    * register bytes.
    *
    * @return <code>byte[]<\code> register page contents verified
    *  with onboard CRC
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public byte[] readDevice ()
      throws OneWireIOException, iButtonException
   {
      byte[]       read_buf = new byte [27];
      MemoryBankAD mb;

      // read the banks, control/alarm/calibration
      for (int i = 0; i < 3; i++)
      {
         mb = ( MemoryBankAD ) regs.elementAt(i);

         mb.readPageCRC(0, (i != 0), read_buf, i * 8);
      }

      // zero out the bitmap
      read_buf [24] = 0;
      read_buf [25] = 0;
      read_buf [26] = 0;

      return read_buf;
   }

   /**
    * This method write the bytes in the provided A/D register pages that
    * have been changed by the 'set' methods.  It knows which state has
    * changed by looking at the bitmap fields appended to the
    * register data.  Any alarm flags will be automatically
    * cleared.  Only VCC powered indicator byte in physical location 0x1C
    * can be written in the calibration memory bank.
    *
    * @param  state - byte array of register pages
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void writeDevice (byte[] state)
      throws OneWireIOException, iButtonException
   {
      int          start_offset, len, i, bank, index;
      boolean      got_block;
      MemoryBankAD mb;

      // Force a clear of the alarm flags
      for (i = 0; i < 4; i++)
      {

         // check if POR or alarm high/low flag present
         index = i * 2 + 1;

         if ((state [index] & ( byte ) 0xB0) != 0)
         {

            // clear the bits
            state [index] &= ( byte ) 0x0F;

            // set to write in bitmap
            Bit.arrayWriteBit(1, index, BITMAP_OFFSET, state);
         }
      }

      // only allow physical address 0x1C to be written in calibration bank
      if ((state [BITMAP_OFFSET + 2] & ( byte ) 0x10) == ( byte ) 0x10)
         state [BITMAP_OFFSET + 2] = ( byte ) 0x10;
      else
         state [BITMAP_OFFSET + 2] = 0;

      // loop through the three memory banks collecting changes
      for (bank = 0; bank < 3; bank++)
      {
         start_offset = 0;
         len          = 0;
         got_block    = false;
         mb           = ( MemoryBankAD ) regs.elementAt(bank);

         // loop through each byte in the memory bank
         for (i = 0; i < 8; i++)
         {

            // check to see if this byte needs writing (skip control register for now)
            if (Bit.arrayReadBit(bank * 8 + i, BITMAP_OFFSET, state) == 1)
            {

               // check if already in a block
               if (got_block)
                  len++;

                  // new block
               else
               {
                  got_block    = true;
                  start_offset = i;
                  len          = 1;
               }

               // check for last byte exception, write current block
               if (i == 7)
                  mb.write(start_offset, state, bank * 8 + start_offset, len);
            }
            else if (got_block)
            {

               // done with this block so write it
               mb.write(start_offset, state, bank * 8 + start_offset, len);

               got_block = false;
            }
         }
      }

      // clear out the bitmap
      state [24] = 0;
      state [25] = 0;
      state [26] = 0;
   }

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
      throws OneWireIOException, iButtonException
   {
      byte[]   read_buf = new byte [8];
      double[] ret_dbl  = new double [4];

      // get readout page
      readout.readPageCRC(0, false, read_buf, 0);

      // convert to array of doubles
      for (int ch = 0; ch < 4; ch++)
      {
         ret_dbl [ch] = interpretVoltage(byteArrayToLong(read_buf, ch * 2, 2),
                                         getADRange(ch, state));
      }

      return ret_dbl;
   }

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
      throws OneWireIOException, iButtonException
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      // get readout page
      byte[] read_buf = new byte [8];

      readout.readPageCRC(0, false, read_buf, 0);

      return interpretVoltage(byteArrayToLong(read_buf, channel * 2, 2),
                              getADRange(channel, state));
   }

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
      throws OneWireIOException, iButtonException
   {

      // call with set presets to 0
      doADConvert(channel, PRESET_TO_ZEROS, state);
   }

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
      throws OneWireIOException, iButtonException
   {

      // call with set presets to 0
      int[] presets = new int [4];

      for (int i = 0; i < 4; i++)
         presets [i] = PRESET_TO_ZEROS;

      doADConvert(doConvert, presets, state);
   }

   /**
    * This method is used to perform voltage conversion on all specified
    * channels.  The method 'getVoltage()' can be used to read the result
    * of the conversion.
    *
    * @param channel - integer 0,1,2,3 representing the channels A,B,C,D.
    * @param preset - int representing the preset value:
    *           NO_PRESET (0), PRESET_TO_ZEROS (1), and PRESET_TO_ONES (2).
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    * @throws IllegalArgumentException
    */
   public void doADConvert (int channel, int preset, byte[] state)
      throws OneWireIOException, iButtonException, IllegalArgumentException
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      // perform the conversion (do fixed max conversion time)
      doADConvert(( byte ) (0x01 << channel), ( byte ) (preset << channel),
                  1440, state);
   }

   /**
    * This method is used to perform voltage conversion on all specified
    * channels.  The method 'getVoltage()' can be used to read the result
    * of the conversion.
    *
    * @param doConvert - boolean array representing which channels
    *                    to perform conversion on.
    * @param preset - int array representing the preset values:
    *           NO_PRESET (0), PRESET_TO_ZEROS (1), and PRESET_TO_ONES (2).
    * @param state - byte array of the current state of the
    *                device returned from 'readDevice()'.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void doADConvert (boolean[] doConvert, int[] preset, byte[] state)
      throws OneWireIOException, iButtonException
   {
      byte input_select_mask = 0;
      byte read_out_control  = 0;
      int  time              = 160;   // Time required in micro Seconds to covert.

      // calculate the input mask, readout control, and conversion time
      for (int ch = 3; ch >= 0; ch--)
      {

         // input select
         input_select_mask <<= 1;

         if (doConvert [ch])
            input_select_mask |= 0x01;

         // readout control
         read_out_control <<= 2;

         if (preset [ch] == PRESET_TO_ZEROS)
            read_out_control |= 0x01;
         else if (preset [ch] == PRESET_TO_ONES)
            read_out_control |= 0x02;

         // conversion time
         time += (80 * getADResolution(ch, state));
      }

      // do the conversion
      doADConvert(input_select_mask, read_out_control, time, state);
   }

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
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      // extract alarm value and convert to voltage
      long temp_long =
         ( long ) (state [ALARM_OFFSET + channel * 2 + alarmType] & 0x00FF)
         << 8;

      return interpretVoltage(temp_long, getADRange(channel, state));
   }

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
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      return (Bit.arrayReadBit(2 + alarmType, channel * 2 + 1, state) == 1);
   }

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
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      return (Bit.arrayReadBit(4 + alarmType, channel * 2 + 1, state) == 1);
   }

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
   public double getADResolution (int channel, byte[] state)
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      int res = state [channel * 2] & 0x0F;

      // return resolution, if 0 then 16 bits
      if (res == 0)
         res = 16;

      return getADRange(channel, state) / ( double ) (1 << res);
   }

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
   public double getADRange (int channel, byte[] state)
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      return (Bit.arrayReadBit(0, channel * 2 + 1, state) == 1) ? 5.12
                                                                : 2.56;
   }

   /**
    * This method is used to detect if the output is enabled for the
    * specified channel from the provided register buffer.  The register
    * buffer is retrieved from the 'readDevice()' method.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param state - byte array of the current state of the device
    *                  returned from 'readDevice()'.
    *
    * @return - boolean, true if output is enabled on specified channel
    *
    * @throws IllegalArgumentException
    */
   public boolean isOutputEnabled (int channel, byte[] state)
      throws IllegalArgumentException
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      return (Bit.arrayReadBit(7, channel * 2, state) == 1);
   }

   /**
    * This method is used to detect if the output is enabled for the
    * specified channel from the provided register buffer.  The register
    * buffer is retrieved from the 'readDevice()' method.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param state - byte array of the current state of the device
    *                  returned from 'readDevice()'.
    *
    * @return - boolean, false if output is conducting to ground and true
    *           not conducting.
    *
    * @throws IllegalArgumentException
    */
   public boolean getOutputState (int channel, byte[] state)
      throws IllegalArgumentException
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      return (Bit.arrayReadBit(6, channel * 2, state) == 1);
   }

   /**
    * This method is used to detect if the device has seen a
    * Power-On-Reset (POR).  If this has occured it may be necessary
    * set the state of the device to the desired values.   The register
    * buffer is retrieved from the 'readDevice()' method.
    *
    * @param state - byte array of the current state of the device
    *                  returned from 'readDevice()'.
    *
    * @return - boolean, false if output is conducting to ground and true
    *           not conducting.
    */
   public boolean getDevicePOR (byte[] state)
   {
      return (Bit.arrayReadBit(7, 1, state) == 1);
   }

   /**
    * This method is used to extract the state of the external power
    * indicator from the provided register buffer.  Use 'setPower'
    * to set or clear the external power indicator flag. The
    * register buffer is retrieved from the 'readDevice()' method.
    *
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    *
    * @return - boolean, true if set to external power operation.
    */
   public boolean isPowerExternal (byte[] state)
   {
      return (state [EXPOWER_OFFSET] != 0);
   }

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
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      int offset = ALARM_OFFSET + channel * 2 + alarmType;

      state [offset] =
         ( byte ) ((voltageToInt(alarm, getADRange(channel, state)) >>> 8)
                   & 0x00FF);

      // set bitmap field to indicate this register has changed
      Bit.arrayWriteBit(1, offset, BITMAP_OFFSET, state);
   }

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
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      // change alarm enable
      Bit.arrayWriteBit(((alarmEnable) ? 1
                                       : 0), 2 + alarmType, channel * 2 + 1,
                                             state);

      // set bitmap field to indicate this register has changed
      Bit.arrayWriteBit(1, channel * 2 + 1, BITMAP_OFFSET, state);
   }

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
   public void setADResolution (int channel, double resolution, byte[] state)
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      // convert voltage resolution into bit resolution
      int div      = ( int ) (getADRange(channel, state) / resolution);
      int res_bits = 0;

      do
      {
         div >>>= 1;

         res_bits++;
      }
      while (div != 0);

      res_bits -= 1;

      if (res_bits == 16)
         res_bits = 0;

      // check for valid bit resolution
      if ((res_bits < 0) || (res_bits > 15))
         throw new IllegalArgumentException("Invalid resolution");

      // clear out the resolution
      state [channel * 2] &= ( byte ) 0xF0;

      // set the resolution
      state [channel * 2] |= ( byte ) ((res_bits == 16) ? 0
                                                        : res_bits);

      // set bitmap field to indicate this register has changed
      Bit.arrayWriteBit(1, channel * 2, BITMAP_OFFSET, state);
   }

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
   public void setADRange (int channel, double range, byte[] state)
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      // convert range into bit value
      int range_bit;

      if ((range > 5.00) & (range < 5.30))
         range_bit = 1;
      else if ((range > 2.40) & (range < 2.70))
         range_bit = 0;
      else
         throw new IllegalArgumentException("Invalid range");

      // change range bit
      Bit.arrayWriteBit(range_bit, 0, channel * 2 + 1, state);

      // set bitmap field to indicate this register has changed
      Bit.arrayWriteBit(1, channel * 2 + 1, BITMAP_OFFSET, state);
   }

   /**
    * This method is used to set the output enable and state for the
    * specified channel in the provided register buffer.  The
    * register buffer is retrieved from the 'readDevice()' method.
    * The method 'writeDevice()' must be called to finalize these
    * changes to the device.  Note that multiple 'set' methods can
    * be called before one call to 'writeDevice()'.
    *
    * @param channel - integer specifying channel in the range
    *                  [0 to (getNumberChannels() - 1)].
    * @param outputEnable - boolean, true if output is enabled
    * @param outputState - boolean, false if output is conducting to
    *           ground and true not conducting.  This parameter is not
    *           used if outputEnable is false.
    * @param state - byte array of the current state of the
    *                device returned from 'readDevice()'.
    */
   public void setOutput (int channel, boolean outputEnable,
                          boolean outputState, byte[] state)
   {

      // check for valid channel value
      if ((channel < 0) || (channel > 3))
         throw new IllegalArgumentException("Invalid channel number");

      // output enable bit
      Bit.arrayWriteBit(((outputEnable) ? 1
                                        : 0), 7, channel * 2, state);

      // optionally set state
      if (outputEnable)
         Bit.arrayWriteBit(((outputState) ? 1
                                          : 0), 6, channel * 2, state);

      // set bitmap field to indicate this register has changed
      Bit.arrayWriteBit(1, channel * 2, BITMAP_OFFSET, state);
   }

   /**
    * This method is used to set or clear the external power flag
    * in the provided register buffer.  The
    * register buffer is retrieved from the 'readDevice()' method.
    * The method 'writeDevice()' must be called to finalize these
    * changes to the device.  Note that multiple 'set' methods can
    * be called before one call to 'writeDevice()'.
    *
    * @param external - boolean, true if setting external power is used
    * @param state - byte array of the current state of the
    *               device returned from 'readDevice()'.
    */
   public void setPower (boolean external, byte[] state)
   {

      // sed the flag
      state [EXPOWER_OFFSET] = ( byte ) (external ? 0x80
                                                  : 0);

      // set bitmap field to indicate this register has changed
      Bit.arrayWriteBit(1, EXPOWER_OFFSET, BITMAP_OFFSET, state);
   }

   //--------
   //-------- Utility methods
   //--------

   /**
    * This method is used to convert a raw voltage long value for the DS2450
    * into a valid voltage.  Requires the max voltage value.
    *
    * @param rawVoltage - long of the raw voltage
    * @param range - max voltage
    *
    * @return - float representing the calculated voltage based on the range
    */
   public static double interpretVoltage (long rawVoltage, double range)
   {
      return ((( double ) rawVoltage / 65535.0) * range);
   }

   /**
    * This method is used to convert a voltage double value to the DS2450
    * specific int value.  Requires the max voltage value.
    *
    * @param rawVoltage - long of the raw voltage
    *
    * @param voltage
    * @param range - max voltage
    *
    * @return - int representing the DS2450 voltage
    */
   public static int voltageToInt (double voltage, double range)
   {
      return ( int ) ((voltage * 65535.0) / range);
   }

   //--------
   //-------- Private methods
   //--------

   /**
    * Create the memory bank interface to read/write
    */
   private void initMem ()
   {

      // readout
      readout = new MemoryBankAD(this);

      // control
      regs = new Vector(3);

      MemoryBankAD temp_mb = new MemoryBankAD(this);

      temp_mb.bankDescription      = "A/D Control and Status";
      temp_mb.generalPurposeMemory = false;
      temp_mb.startPhysicalAddress = 8;
      temp_mb.readWrite            = true;
      temp_mb.readOnly             = false;

      regs.addElement(temp_mb);

      // Alarms
      temp_mb                      = new MemoryBankAD(this);
      temp_mb.bankDescription      = "A/D Alarm Settings";
      temp_mb.generalPurposeMemory = false;
      temp_mb.startPhysicalAddress = 16;
      temp_mb.readWrite            = true;
      temp_mb.readOnly             = false;

      regs.addElement(temp_mb);

      // calibration
      temp_mb                      = new MemoryBankAD(this);
      temp_mb.bankDescription      = "A/D Calibration";
      temp_mb.generalPurposeMemory = false;
      temp_mb.startPhysicalAddress = 24;
      temp_mb.readWrite            = true;
      temp_mb.readOnly             = false;

      regs.addElement(temp_mb);
   }

   /**
    * This constructs a long from a LSByte byte array of specified length.
    *
    * @param  byteArray - byte array to convert to a long (LSByte first)
    * @param  offset - byte offset into the array where to start to convert
    * @param  len - number of bytes to use to convert to a long
    *
    * @returns <code>long<\code> value constructed from bytes
    */
   private static long byteArrayToLong (byte[] byteArray, int offset, int len)
   {
      long val = 0;

      // Concatanate the byte array into one variable.
      for (int i = (len - 1); i >= 0; i--)
      {
         val <<= 8;
         val |= (byteArray [offset + i] & 0x00FF);
      }

      return val;
   }

   /**
    * This method is used to perform voltage conversion on all specified
    * channels.  The method 'getVoltage()' can be used to read the result
    * of the conversion.
    *
    * @param inputSelectMask - input select mask
    * @param readOutControl - read out control
    * @param timeUs - time in microseconds for conversion
    * @param state - byte array of the current state of the
    *                device returned from 'readDevice()'.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   private void doADConvert (byte inputSelectMask, byte readOutControl,
                             int timeUs, byte[] state)
      throws OneWireIOException, iButtonException
   {

      // check if no conversions
      if (inputSelectMask == 0)
      {
         throw new IllegalArgumentException(
            "No conversion will take place.  No channel selected.");
      }

      // Create the command block to be sent.
      byte[] raw_buf = new byte [5];

      raw_buf [0] = CONVERT_COMMAND;
      raw_buf [1] = inputSelectMask;
      raw_buf [2] = ( byte ) readOutControl;
      raw_buf [3] = ( byte ) 0xFF;
      raw_buf [4] = ( byte ) 0xFF;

      // calculate the CRC16 up to and including readOutControl
      int crc16 = CRC16.compute(raw_buf, 0, 3, 0);

      // Send command block.
      if (adapter.select(address))
      {
         if (isPowerExternal(state))
         {

            // good power so send the entire block (with both CRC)
            adapter.dataBlock(raw_buf, 0, 5);

            // Wait for complete of conversion
            try
            {
               Thread.sleep((timeUs / 1000) + 10);
            }
            catch (InterruptedException e){}
            ;

            // calculate the rest of the CRC16
            crc16 = CRC16.compute(raw_buf, 3, 2, crc16);
         }
         else
         {

            // parasite power so send the all but last byte
            adapter.dataBlock(raw_buf, 0, 4);

            // setup power delivery
            adapter.setPowerDuration(adapter.DELIVERY_INFINITE);
            adapter.startPowerDelivery(adapter.CONDITION_AFTER_BYTE);

            // get the final CRC byte and start strong power delivery
            raw_buf [4] = ( byte ) adapter.getByte();
            crc16       = CRC16.compute(raw_buf, 3, 2, crc16);

            // Wait for power delivery to complete the conversion
            try
            {
               Thread.sleep((timeUs / 1000) + 1);
            }
            catch (InterruptedException e){}
            ;

            // Turn power off.
            adapter.setPowerNormal();
         }
      }
      else
         throw new iButtonException("iButtonContainer20 - Device not found.");

      // check the CRC result
      if (crc16 != 0x0000B001)
         throw new OneWireIOException(
            "iButtonContainer20 - Failure during conversion - Bad CRC");

      // check if still busy
      if (adapter.getByte() == 0x00)
         throw new OneWireIOException("Conversion failed to complete.");
   }
}
