package com.ibutton.container;
// iButtonContainer1D.java
/*---------------------------------------------------------------------------
 * Copyright (C) 2000 Dallas Semiconductor Corporation, All Rights Reserved.
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

// imports
import com.ibutton.iButtonException;
import com.ibutton.adapter.OneWireIOException;
import com.ibutton.utils.CRC16;
import com.ibutton.adapter.*;

/**
 * iButton container for iButton family type 1D (hex).  This family type is 
 * a 'counter' iButton (DS2423).  It contains 4 counters assosiated with 
 * pages 12 to 15.  
 * <p>//\\//\\//\\// Does not yet support memory operations \\//\\//\\//
 *
 * @version    1.01, 18 July 2000
 * @author     DS
 */
public class iButtonContainer1D extends iButtonContainer
{
   //--------
   //-------- Static Final Variables
   //--------

   /**
    * DS2423 read memory command
    */
   private static final byte  READ_MEMORY_COMMAND      = (byte)0xA5;
   
   //--------
   //-------- Variables
   //--------

   /**
    * Internal buffer
    */
   private byte[] buffer = new byte[14];

   public iButtonContainer1D()
   {
     super();
   }

   /**
    * Create a container with a provided adapter object 
    * and the address of the iButton or 1-Wire device.
    * 
    * @param  sourceAdapter     adapter object required to communicate with 
    * this iButton.
    * @param  newAddress        address of this 1-Wire device 
    */
   public iButtonContainer1D(DSPortAdapter sourceAdapter,byte[] newAddress)
   {
     super(sourceAdapter, newAddress);
   }

   /**
    * Create a container with a provided adapter object 
    * and the address of the iButton or 1-Wire device.
    * 
    * @param  sourceAdapter     adapter object required to communicate with 
    * this iButton.
    * @param  newAddress        address of this 1-Wire device 
    */
   public iButtonContainer1D(DSPortAdapter sourceAdapter,long newAddress)
   {
     super(sourceAdapter, newAddress);
   }

   /**
    * Create a container with a provided adapter object 
    * and the address of the iButton or 1-Wire device.
    * 
    * @param  sourceAdapter     adapter object required to communicate with 
    * this iButton.
    * @param  newAddress        address of this 1-Wire device 
    */
   public iButtonContainer1D(DSPortAdapter sourceAdapter,String newAddress)
   {
     super(sourceAdapter, newAddress);
   }

   //--------
   //-------- Information methods
   //--------

   /**
    * Retrieve the Dallas Semiconductor part number of the iButton 
    * as a string.  For example 'DS1992'.
    *
    * @return  <code>String</code> representation of the iButton name.
    */
   public String getiButtonPartName()
   {
      return "DS2423";
   }

   /**
    * Retrieve the alternate Dallas Semiconductor part numbers or names.  
    * A 'family' of 1-Wire Network devices may have more than one part number 
    * depending on packaging.  There can also be nicknames such as 
    * 'Crypto iButton'.
    *
    * @return  <code>String</code> representation of the alternate names.
    */
   public String getAlternateNames()
   {
      return "";
   }

   /**
    * Retrieve a short description of the function of the iButton type.
    *
    * @return  <code>String</code> representation of the function description.
    */
   public String getDescription()
   {
      return "1-Wire counter with 4096 bits of read/write, nonvolatile " +
             "memory.  Memory is partitioned into sixteen pages of 256 bits each.  " +
             "256 bit scratchpad ensures data transfer integrity.  " +
             "Has overdrive mode.  Last four pages each have 32 bit " +
             "read-only non rolling-over counter.  The first two counters " +
             "increment on a page write cycle and the second two have " +
             "active-low external triggers.";
   }

   //--------
   //-------- Custom Methods for this 1-Wire Device Type  
   //--------

   /**
    * Read the counter value from a page on this iButton.  
    *
    * @param  counterPage    page number of the counter to read
    * 
    * @return  counter 4 byte value. Since this value is unsigned then 
    * the value is returned in an 8 byte long integer.
    *
    * @throws OneWireIOException
    * @throws iButtonException 
    */
   public long readCounter(int counterPage)
      throws OneWireIOException, iButtonException 
   {
      // check if counter page provided is valid
      if ((counterPage < 12) || (counterPage > 15))
         throw new iButtonException("iButtonContainer1D-invalid counter page");

      // select the device 
      if (adapter.select(address))
      {
         int crc16;

         // read memory command
         buffer[0] = READ_MEMORY_COMMAND;
         crc16 = CRC16.compute(READ_MEMORY_COMMAND);

         // address of last data byte before counter
         int address = (counterPage << 5) + 31;

         // append the address
         buffer[1] = (byte)address;  
         crc16 = CRC16.compute(buffer[1], crc16);
         buffer[2] = (byte)(address >>> 8);  
         crc16 = CRC16.compute(buffer[2], crc16);

         // now add the read bytes for data byte,counter,zero bits, crc16
         for (int i = 3; i < 14; i++)
            buffer[i] = (byte)0xFF;

         // send the block
         adapter.dataBlock(buffer, 0, 14);
         
         // calculate the CRC16 on the result and check if correct
         if (CRC16.compute(buffer, 3, 11, crc16) == 0xB001)
         {
            // extract the counter out of this verified packet
            long return_count = 0;
            for (int i = 4; i >= 1; i--)
            {
               return_count <<= 8;
               return_count |= (buffer[i+3] & 0xFF);
            }  
            // return the result count
            return return_count;
         }
      }

      // device must not have been present
      throw new OneWireIOException("iButtonContainer1D-device not present");
   }
}
