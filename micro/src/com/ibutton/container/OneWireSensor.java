
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
 * 1-Wire sensor interface class for basic sensor operations.
 * This class should be implemented for each sensor type
 * 1-Wire device.
 *
 * @version    0.00, 28 Aug 2000
 * @author     DS
 */
public interface OneWireSensor
{

   //--------
   //-------- Sensor I/O methods
   //--------

   /**
    * This method retrieves the 1-Wire device sensor state.  This state is
    * returned as a byte array.  Pass this byte array to the static query
    * and set methods.  If the device state needs to be changed then call
    * the 'writeDevice' to finalize the one or more change.
    *
    * @return <code>byte[]<\code> 1-Wire device sensor state    *
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public byte[] readDevice ()
      throws OneWireIOException, iButtonException;

   /**
    * This method write the 1-Wire device sensor state that
    * have been changed by the 'set' methods.  It knows which registers have
    * changed by looking at the bitmap fields appended to the state
    * data.
    *
    * @param  state - byte array of clock register page contents
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void writeDevice (byte[] state)
      throws OneWireIOException, iButtonException;
}
