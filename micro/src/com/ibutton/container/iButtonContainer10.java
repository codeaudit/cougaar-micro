package  com.ibutton.container;
// iButtonContainer10.java
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
import  com.ibutton.*;
import  com.ibutton.utils.CRC8;
import  com.ibutton.adapter.*;

/** 
 * iButton container for iButton family type 10 (hex).  This family type is 
 * a 'temperature' iButton (DS1920 (can) DS1820 (plastic)). 
 *
 * History
 * <ul> 
 * <li> 0.00 -> 0.01 Fixed issue with truncation of hi-res 
 *                   temperature for negative odd values
 *
 * <li> 0.02 -> 1.01 Temperature trip point functionality added.
 * <li> 1.01 -> 1.02 Changed trip point methods.
 * </ul>
 *
 * @version    1.02, 20 July 2000
 * @author     0.0 - 0.02 DS;  1.01 JK; 1.02 DS
 */
public class iButtonContainer10
   extends iButtonContainer
{
   //--------
   //-------- Static Final Variables
   //--------

   /**
    * DS1920 convert temperature command 
    */
   private static final byte CONVERT_TEMPERATURE_COMMAND = 0x44;

   /**
    * DS1920 read scratchpad command
    */
   private static final byte READ_SCRATCHPAD_COMMAND = (byte)0xBE;

   /**
    * DS1920 write scratchpad command
    */
   private static final byte WRITE_SCRATCHPAD_COMMAND = (byte)0x4E;

   /**
    * DS1920 copy scratchpad command
    */
   private static final byte COPY_SCRATCHPAD_COMMAND = (byte)0x48;

   /**
    * DS1920 recall eeprom command
    */
   private static final byte RECALL_EEPROM_COMMAND = (byte)0xB8;

   public iButtonContainer10()
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
   public iButtonContainer10(DSPortAdapter sourceAdapter,byte[] newAddress)
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
   public iButtonContainer10(DSPortAdapter sourceAdapter,long newAddress)
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
   public iButtonContainer10(DSPortAdapter sourceAdapter,String newAddress)
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
    * @return string representation of the iButton name.
    */
   public String getiButtonPartName() 
   {
      return  "DS1920";
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
      return  "DS1820";
   }

   /**
    * Retrieve a short description of the function of the iButton type.
    *
    * @return string representation of the function description.
    */
   public String getDescription() 
   {
      return  "Digital thermometer measures temperatures from " + 
               "-55C to 100C in typically 0.2 seconds.  +/- 0.5C " + 
               "Accuracy between 0C and 70C. 0.5C standard " +
               "resolution, higher resolution through interpolation." + 
               "Contains high and low temperature set points for" + 
               "generation of alarm.";
   }

   //--------
   //-------- Custom Methods for this iButton or 1-Wire Device Type  
   //--------

   /**
    * Read the temperature value from this iButton or 1-Wire device.  The 
    * temperature that is returned is in Celsius.  Warning, this method 
    * takes over 3/4 second to complete.  
    *
    * @return temperature in Celsius from device
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public float readTemperature()
      throws OneWireIOException, iButtonException
   {
      // select the device 
      if (adapter.select(address)) 
      {
         // Setup Power Delivery
         adapter.setPowerDuration(adapter.DELIVERY_INFINITE); 
         adapter.startPowerDelivery(adapter.CONDITION_AFTER_BYTE); 

         // send the convert temperature command
         adapter.putByte(CONVERT_TEMPERATURE_COMMAND);

         // delay for 750 ms
         try 
         {
            Thread.sleep(750);
         }
         catch (InterruptedException e) 
         {
         }

         // Turn power back to normal.
         adapter.setPowerNormal();

         // check to see if the temperature conversion is over
         if (adapter.getByte() != 0xFF)
            throw  new OneWireIOException("iButtonContainer10-temperature conversion not complete");

         // read the result
         byte[] buffer = new byte[8];
         readScratch(buffer);

         // calculate the high-res temperature
         int tsht = buffer[0]/2; // (0.01)
         if ((buffer[1] & 0x01) == 0x01) 
            tsht |= -128; // (0.01)

         float tmp = (float)tsht;
         float cr = buffer[6];
         float cpc = buffer[7];
         if (cpc == 0) 
            throw  new OneWireIOException("iButtonContainer10-invalid temperature");
         else 
            tmp = tmp - (float)0.25 + (cpc - cr)/cpc;

         return  tmp;
      }

      // device must not have been present
      throw  new OneWireIOException("iButtonContainer10-device not present");
   }

   /**
    * Read the temperature value from this iButton or 1-Wire device.  The 
    * temperature that is returned is in Fahrenheit.  Warning, this method 
    * takes over 3/4 second to complete.  
    *
    * @return temperature in Fahrenheit from device
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public float readTemperatureFahrenheit()
      throws OneWireIOException, iButtonException
   {
      return convertToFahrenheit(readTemperature());
   }

   /**
    * Set the temperature trip points in Celsius.  Will convert and truncate trips  
    * to whole degrees Celsius.
    *
    * @parameter tripHigh - the temperature that the high trip point will engage
    *           an alarm at.
    *
    * @parameter tripLow - the temperature that the low trip point will engage
    *           an alarm at.
    *
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void setTrips(float tripHigh, float tripLow)
      throws OneWireIOException, iButtonException
   {
      // Variables.
      byte[] send_block = new byte[2];

      // First commence error checking.
      if ((tripHigh > 100.0 || tripHigh < -55.0) || (tripLow > 100.0 || tripLow < -55.0)) 
      {
         throw  new IllegalArgumentException(
            "Value for trip points not in accepted range.  Must be -55 C <-> +100 C.");
      }

      // Set up block to send.
      send_block[0] = (byte)tripHigh;
      send_block[1] = (byte)tripLow;

      // Write it to the Scratchpad.
      writeScratchpad(send_block);

      // Place in memory.
      copyScratchpad();

      return;
   }

   /**
    * Set the temperature trip points in Fahrenheit.  Will convert and truncate  
    * trips to whole degrees Celsius.
    *
    * @parameter tripHigh - the temperature that the high trip point will engage
    *           an alarm at.
    *
    * @parameter tripLow - the temperature that the low trip point will engage
    *           an alarm at.
    *
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void setTripsFahrenheit(float tripHigh, float tripLow)
      throws OneWireIOException, iButtonException
   {
      setTrips(convertToCelsius(tripHigh),convertToCelsius(tripLow)); 
   }

   /**
    * Set the temperature trip points in Celsius.  Will convert and truncate trips  
    * to whole degrees Celsius.
    *
    * @parameter trips - array of trips with high trip first and low trip
    *       second.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void setTrips(float[] trips)
      throws OneWireIOException, iButtonException
   {
      setTrips(trips[0], trips[1]);
   }

   /**
    * Set the temperature trip points in Fahrenheit.  Will convert and truncate trips  
    * to whole degrees Celsius.
    *
    * @parameter trips - array of trips with high trip first and low trip
    *       second.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public void setTripsFahrenheit(float[] trips)
      throws OneWireIOException, iButtonException
   {
      setTripsFahrenheit(trips[0], trips[1]);
   }

   /**
    * Gets the trip points from the EEPROM of the DS1820/DS1920 in Celsius.
    *
    * @returns <code>float[]<\code> trips - this array holds the trips.
    *      The first is the High trip point and the second is the low.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public float[] getTrips()
      throws OneWireIOException, iButtonException
   {
      // Variables.
      byte[] send_block = new byte[8];
      float[] return_trips = new float[2];

      if (adapter.select(address)) 
         adapter.putByte(RECALL_EEPROM_COMMAND);
      else 
         throw  new OneWireIOException("iButtonContainer10 - Device not found.");

      // read the scratchpad
      readScratch(send_block);

      // extract data and return.
      // high temp trip
      return_trips[0] = new Byte(send_block[2]).floatValue(); 
      // low temp trip
      return_trips[1] = new Byte(send_block[3]).floatValue();
 
      return  return_trips;
   }

   /**
    * Gets the trip points from the EEPROM of the DS1820/DS1920 in Fahrenheit.
    *
    * @returns <code>float[]<\code> trips - this array holds the trips.
    *      The first is the High trip point and the second is the low.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public float[] getTripsFahrenheit()
      throws OneWireIOException, iButtonException
   {
      float[] trips = getTrips();
      trips[0] = convertToFahrenheit(trips[0]);
      trips[1] = convertToFahrenheit(trips[1]);

      return trips;
   }

   /**
    * Convert a temperature from Celsius to Fahrenheit.  
    *
    * @param  celsiusTemperature     the temperature value to convert
    * 
    * @return  the fahrenheit conversion of the supplied temperature
    */
   static public float convertToFahrenheit(float celsiusTemperature) 
   {
      return  (float)(celsiusTemperature*9.0/5.0 + 32.0);
   }

   /**
    * Convert a temperature from Fahrenheit to Celsius.  
    *
    * @param  fahrenheitTemperature     the temperature value to convert
    * 
    * @return  the celsius conversion of the supplied temperature
    */
   static public float convertToCelsius(float fahrenheitTemperature) 
   {
      return  (float)((fahrenheitTemperature - 32.0)*5.0/9.0);
   }

   //--------
   //-------- Private Methods  
   //--------

   /**
    * Read the 8 bytes from the scratchpad and verify CRC8 returned.  
    *
    * @param  data  buffer to store the scratchpad data
    * 
    * @throws OneWireIOException
    * @throws iButtonException
    */
   private void readScratch(byte[] data)
      throws OneWireIOException, iButtonException
   {
      // select the device 
      if (adapter.select(address)) 
      {
         // construct a block to read the scratchpad
         byte[] buffer = new byte[10];

         // read scratchpad command
         buffer[0] = (byte)READ_SCRATCHPAD_COMMAND;

         // now add the read bytes for data bytes and crc8
         for (int i = 1; i < 10; i++) 
            buffer[i] = (byte)0xFF;
      
         // send the block
         adapter.dataBlock(buffer, 0, buffer.length);

         // see if crc is correct
         if (CRC8.compute(buffer, 1, 9) == 0) 
            System.arraycopy(buffer,1,data,0,8); 
         else
            throw new OneWireIOException("iButtonContainer10-Error reading CRC8 from device.");
      }
      else
         throw new OneWireIOException("iButtonContainer10-Device not found on 1-Wire Network");
   }

   /**
    * Writes to the Scratchpad.
    *
    * @param data <code>byte[]<\code> this is the data to be written to the
    *                      scratchpad.  Cannot be more than two bytes in size.
    *                      First byte of data must be the High Trip Point and
    *                      second byte must be Low Trip Point.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    * @throws IllegalArgumentException
    */
   private void writeScratchpad(byte[] data)
      throws OneWireIOException, iButtonException, IllegalArgumentException
   {
      // Variables.
      byte[] write_block = new byte[3];
      byte[] buffer = new byte[8];

      // First do some error checking.
      if (data.length != 2) 
         throw  new IllegalArgumentException("Bad data.  Data must consist of only TWO bytes.");

      // Prepare the write_block to be sent.
      write_block[0] = WRITE_SCRATCHPAD_COMMAND;
      write_block[1] = data[0];
      write_block[2] = data[1];

      // Send the block of data to the DS1920.
      if (adapter.select(address)) 
         adapter.dataBlock(write_block, 0, 3);
      else 
         throw  new OneWireIOException("iButtonContainer10 - Device not found");

      // Check data to ensure correctly recived.
      buffer = new byte[8];
      readScratch(buffer);

      // verify data
      if ((buffer[2] != data[0]) || (buffer[3] != data[1]))
         throw  new OneWireIOException("iButtonContainer10 - data read back incorrect");

      return;
   }

   /**
    * Copies the contents of the User bytes of the ScratchPad to the EEPROM.
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   private void copyScratchpad()
      throws OneWireIOException, iButtonException
   {
      // select the device 
      if (adapter.select(address)) 
      {
         // Setup Power Delivery
         adapter.setPowerDuration(adapter.DELIVERY_INFINITE); 
         adapter.startPowerDelivery(adapter.CONDITION_AFTER_BYTE); 

         // send the copy command
         adapter.putByte(COPY_SCRATCHPAD_COMMAND);

         // delay for 10 ms
         try 
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e) 
         {
         }

         // Turn power back to normal.
         adapter.setPowerNormal();
      }
      else 
         throw  new OneWireIOException("iButtonContainer10 - device not found");
   }

}

