
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
import com.ibutton.utils.CRC8;
import com.ibutton.adapter.*;
import java.util.Vector;
import java.util.Enumeration;


/**
 * iButton container for iButton family type 89 (hex), DS1982/DS2502. <p>
 *
 *  @version    0.00, 28 Aug 2000
 *  @author     DS
 */
public class iButtonContainer89
   extends iButtonContainer
{

   //--------
   //-------- Constructors
   //--------

   /**
    * Default constructor
    */
   public iButtonContainer89 ()
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
   public iButtonContainer89 (DSPortAdapter sourceAdapter, byte[] newAddress)
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
   public iButtonContainer89 (DSPortAdapter sourceAdapter, long newAddress)
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
   public iButtonContainer89 (DSPortAdapter sourceAdapter, String newAddress)
   {
      super(sourceAdapter, newAddress);
   }

   //--------
   //-------- Methods
   //--------

   /**
    * Retrieve the Dallas Semiconductor part number of the iButton
    * as a string.  For example 'DS1992'.
    *
    * @return string represetation of the iButton name.
    */
   public String getiButtonPartName ()
   {
      return "DS1982";
   }

   /**
    * Retrieve the alternate Dallas Semiconductor part numbers or names.
    * A 'family' of MicroLAN devices may have more than one part number
    * depending on packaging.  There can also be nicknames such as
    * 'Crypto iButton'.
    *
    * @return string represetation of the alternate names.
    */
   public String getAlternateNames ()
   {
      return "DS2502";
   }

   /**
    * Retrieve a short description of the function of the iButton type.
    *
    * @return string represetation of the function description.
    */
   public String getDescription ()
   {
      return "1024 bit Electrically Programmable Read Only Memory "
             + "(EPROM) partitioned into four 256 bit pages."
             + "Each memory page can be permanently write-protected "
             + "to prevent tampering.  Architecture allows software "
             + "to patch data by supersending a used page in favor of "
             + "a newly programmed page.";
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
      Vector bank_vector = new Vector(3);

      // Address number in read-only-memory
      bank_vector.addElement(new MemoryBankROM(this));

      // EPROM main bank
      MemoryBankEPROM mn = new MemoryBankEPROM(this);

      mn.numberPages          = 4;
      mn.size                 = 128;
      mn.pageLength           = 32;
      mn.extraInfo            = false;
      mn.extraInfoLength      = 0;
      mn.extraInfoDescription = null;
      mn.numCRCBytes          = 1;
      mn.normalReadCRC        = true;
      mn.READ_PAGE_WITH_CRC   = ( byte ) 0xC3;

      bank_vector.addElement(mn);

      // EPROM status write protect pages bank
      MemoryBankEPROM st = new MemoryBankEPROM(this);

      st.bankDescription      = "Write protect pages and Page redirection";
      st.numberPages          = 1;
      st.size                 = 8;
      st.pageLength           = 8;
      st.generalPurposeMemory = false;
      st.extraInfo            = false;
      st.extraInfoLength      = 0;
      st.extraInfoDescription = null;
      st.numCRCBytes          = 1;
      st.normalReadCRC        = true;
      st.READ_PAGE_WITH_CRC   = MemoryBankEPROM.STATUS_READ_PAGE_COMMAND;
      st.WRITE_MEMORY_COMMAND = MemoryBankEPROM.STATUS_WRITE_COMMAND;

      bank_vector.addElement(st);

      // setup OTP features in main memory
      mn.mbLock         = st;
      mn.lockPage       = true;
      mn.mbRedirect     = st;
      mn.redirectOffset = 1;
      mn.redirectPage   = true;

      return bank_vector.elements();
   }
}
