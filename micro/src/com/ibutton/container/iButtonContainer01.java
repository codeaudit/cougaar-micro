package com.ibutton.container;
// iButtonContainer01.java
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

import com.ibutton.adapter.*;

/** 
 * iButton container for iButton family type 01 (hex), DS1990A/DS2401. 
 *
 *  @version    1.01, 18 July 2000
 *  @author     DS
 */
public class iButtonContainer01 extends iButtonContainer
{
   public iButtonContainer01()
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
   public iButtonContainer01(DSPortAdapter sourceAdapter,byte[] newAddress)
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
   public iButtonContainer01(DSPortAdapter sourceAdapter,long newAddress)
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
   public iButtonContainer01(DSPortAdapter sourceAdapter,String newAddress)
   {
     super(sourceAdapter, newAddress);
   }

   /**
    * Retrieve the Dallas Semiconductor part number of the iButton 
    * as a string.  For example 'Crypto iButton' or 'DS1992'.
    *
    * @return  string represetation of the iButton name.
    */
   public String getiButtonPartName()
   {
      return "DS1990A";
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
      return "DS2401";
   }

   /**
    * Retrieve a short description of the function of the iButton type.
    *
    * @return  <code>String</code> representation of the function description.
    */
   public String getDescription()
   {
      return "64 bit unique serial number";
   }
}
