package com.ibutton.container;
// iButtonContainer12.java
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
import com.ibutton.utils.CRC16;
import com.ibutton.adapter.*;
import com.ibutton.iButtonException;

/**
 * iButton container for iButton family type 12 (hex).  This family type is 
 * a 'dual addressible switch' iButton (DS2406).  It contains 2 addressible 
 * switches and or sensors.
 *  <p>//\\//\\//\\// Does not yet support memory operations \\//\\//\\//
 *
 * @version    1.01, 18 July 2000
 * @author     DS
 * @author     KLA, Pinky, Brain
 */
public class iButtonContainer12 extends iButtonContainer
{
   //--------
   //-------- Static Final Variables
   //--------

   /** channelMode for channelAccess(), channel A selection   */
   public static final byte CHANNEL_A_ONLY                 = 0x04;

   /** channelMode for channelAccess(), channel B selection   */
   public static final byte CHANNEL_B_ONLY                 = 0x08;

   /** channelMode for channelAccess(), channel A and B selection */
   public static final byte CHANNEL_BOTH                   = 0x0c;

   /** CRCMode for channelAccess(), no CRC   */
   public static final byte CRC_DISABLE                    = 0x00;

   /** CRCMode for channelAccess(), CRC after every byte   */
   public static final byte CRC_EVERY_BYTE                 = 0x01;

   /** CRCMode for channelAccess(), CRC after 8 bytes   */
   public static final byte CRC_EVERY_8_BYTES              = 0x02;

   /** CRCMode for channelAccess(), CRC after 32 bytes   */
   public static final byte CRC_EVERY_32_BYTES             = 0x03;

   /** DS2406 Write status command */
   private static final byte  WRITE_STATUS_COMMAND      = 0x55;

   /** DS2406 channel access command */
   private static final byte  CHANNEL_ACCESS_COMMAND         = (byte)0xF5;
   
   /** DS2406 read status command  */
   private static final byte  READ_STATUS_COMMAND            = (byte)0xAA;

   /** DS2406 read memory command  */
   private static final byte  READ_MEMORY_COMMAND            = (byte)0xF0;
   
   /** DS2406 extended read memory command  */
   private static final byte  EXTENDED_READ_MEMORY_COMMAND   = (byte)0xA5;

   /** internal buffer  */
   private byte[] buffer = new byte[7];

   //--------
   //-------- Variables
   //--------

   /**
    * Flag to indicate if the status of the DS2406 has been read
    */
   private boolean            statusHasBeenRead = false;

   /**
    * Flag to indicate if the status of the DS2406 has been read
    */
   private int                currentRawStatus = 0;

   //--------
   //-------- Constructor
   //--------

   public iButtonContainer12()
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
   public iButtonContainer12(DSPortAdapter sourceAdapter,byte[] newAddress)
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
   public iButtonContainer12(DSPortAdapter sourceAdapter,long newAddress)
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
   public iButtonContainer12(DSPortAdapter sourceAdapter,String newAddress)
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
      return "DS2406";
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
      return "Dual Addressable Switch, DS2407";
   }

   /**
    * Retrieve a short description of the function of the iButton type.
    *
    * @return  <code>String</code> representation of the function description.
    */
   public String getDescription()
   {
      return "1-Wire Dual Addressable Switch.  PIO pin channel " +
             "A sink capability of typical 50mA at 0.4V with " +
             "soft turn-on; optional channel B typical 10 mA at " +
             "0.4V.  1024 bits of Electrically Programmable " +
             "Read Only Memory (EPROM) partitioned into four 256 " +
             "bit pages.  7 bytes of user-programmable status " +
             "memory to control the device."; 
   }

   //--------
   //-------- Custom Methods for this 1-Wire Device Type  
   //--------

   /**
    * Set the switch state for a single switch.  A 'TRUE' state is where the 
    * PIO pin is conductive to ground and a 'FALSE' state is where the PIO 
    * is floating. 
    *
    * @param  channelAState     state for channel A to set
    * 
    * @throws OneWireIOException
    * @throws iButtonException 
    */
   public void setSwitchState(boolean channelAState)
      throws OneWireIOException, iButtonException 
   {
      setSwitchState(channelAState,false);
   }

   /**
    * Set the switch state for a dual switch.  A 'TRUE' state is where the 
    * PIO pin is conductive to ground and a 'FALSE' state is where the PIO 
    * is floating. 
    *
    * @param  channelAState     state for channel A to set
    * @param  channelBState     state for channel B to set
    * 
    * @throws OneWireIOException
    * @throws iButtonException 
    */
   public void setSwitchState(boolean channelAState, boolean channelBState)
      throws OneWireIOException, iButtonException 
   {
      // select the device 
      if (adapter.select(address))
      {
         // create a block to set the switch state
         // read memory and counter command
         int crc16;

         // write status command
         buffer[0] = WRITE_STATUS_COMMAND;
         crc16 = CRC16.compute(WRITE_STATUS_COMMAND);

         // address of switch state in status
         buffer[1] = 0x07;
         crc16 = CRC16.compute(0x07, crc16);
         buffer[2] = 0x00;
         crc16 = CRC16.compute(0x00, crc16);
      
         // create state byte to write
         int ffstate = 0x13;
         if (!channelAState)
            ffstate |= 0x20;
         if (!channelBState)
            ffstate |= 0x40;
   
         // write state
         buffer[3] = (byte)ffstate;
         crc16 = CRC16.compute((byte)ffstate, crc16);

         // read CRC16
         buffer[4] = (byte)0xFF;
         buffer[5] = (byte)0xFF;

         // send the block
         adapter.dataBlock(buffer, 0, 6);

         // calculate the CRC16 on the result and check if correct
         if (CRC16.compute(buffer, 4, 2, crc16) == 0xB001)
            return;  
      }

      // device must not have been present
      throw new iButtonException("iButtonContainer12-device not present");
   }

   /**
    * Set the switch state for a dual switch.  A 'TRUE' state is where the 
    * PIO pin is conductive to ground and a 'FALSE' state is where the PIO 
    * is floating. 
    *
    * @param  clearActivity     <code>true</code> if the activity flags are 
    * cleared when the stat is read
    * 
    * @throws OneWireIOException
    * @throws iButtonException 
    */
   public int readSwitchState(boolean clearActivity)
      throws OneWireIOException, iButtonException 
   {
      // select the device 
      if (adapter.select(address))
      {
         int crc16;

         // channel access command 
         buffer[0] = CHANNEL_ACCESS_COMMAND;
         crc16 = CRC16.compute(CHANNEL_ACCESS_COMMAND & 0x0FF);

         // send the control bytes
         if (clearActivity)
         {
            buffer[1] = (byte)0xD5;
            crc16 = CRC16.compute((byte)0xD5, crc16);
         }
         else
         {
            buffer[1] = (byte)0x55;
            crc16 = CRC16.compute((byte)0x55, crc16);
         }
         buffer[2] = (byte)0xFF;
         crc16 = CRC16.compute((byte)0xFF, crc16);

         // read the info, dummy and CRC16
         for (int i = 3; i < 7; i++)
            buffer[i] = (byte)0xFF;
         
         // send the block
         adapter.dataBlock(buffer, 0, 7);

         // calculate the CRC16 on the result and check if correct
         if (CRC16.compute(buffer, 3, 4, crc16) == 0xB001)
         {
            statusHasBeenRead = true;
            currentRawStatus = buffer[3];
            return currentRawStatus;
         }  
      }

      // device must not have been present
      throw new OneWireIOException("iButtonContainer12-device not present");
   }
    
   /**
    * Read the number of channels on this 1-Wire switch.  The method 
    * readSwitchState MUST be called before this method to get a current 
    * reading of this information.  
    *
    * @return  number of channels, 1 (Channel A only) or 2 (Channel A and B).
    */
   public int getNumberOfChannels()
   {
      return ((currentRawStatus & 0x40) == 0x40) ? 2 : 1;
   }

   /**
    * Read the state of the A channel PIO switch.  The method 
    * readSwitchState MUST be called before this method to get a current 
    * reading of this information.  
    *
    * @return  <code>true</code> if PIO channel A is tied to ground and 
    * <code>false</code> if not.  
    */
   public boolean getChannelAState()
   {
      return !((currentRawStatus & 0x01) == 0x01);
   }

   /**
    * Read the state of the B channel PIO switch.  The method 
    * readSwitchState MUST be called before this method to get a current 
    * reading of this information.  
    *
    * @return <code>true</code> if PIO channel B is tied to ground and 
    * <code>false</code> if not.  
    */
   public boolean getChannelBState()
   {
      return !((currentRawStatus & 0x02) == 0x02);
   }
          
   /**
    * Read the activity latch of the A channel PIO switch.  The method 
    * readSwitchState MUST be called before this method to get a current 
    * reading of this information.  
    *
    * @return  <code>true</code> if actvitiy detected on channel A and 
    * <code>false</code> if not.  
    */
   public boolean getChannelAActivity()
   {
      return ((currentRawStatus & 0x10) == 0x10);
   }

   /**
    * Read the activity latch of the B channel PIO switch.  The method 
    * readSwitchState MUST be called before this method to get a current 
    * reading of this information.  
    *
    * @return  <code>true</code> if actvitiy detected on channel B and 
    * <code>false</code> if not.
    */
   public boolean getChannelBActivity()
   {
      return ((currentRawStatus & 0x20) == 0x20);
   }

   /**
    * Read the sensed level on the A channel of this device.  The method 
    * readSwitchState MUST be called before this method to get a current 
    * reading of this information.  
    *
    * @return  <code>true</code> if the sensed level channel A  is 'high' 
    * and <code>false</code> if the sensed level is 'low'.  
    */
   public boolean getChannelALevel()
   {
      return ((currentRawStatus & 0x04) == 0x04);
   }

   /**
    * Read the sensed level on the B channel of this device.  The method 
    * readSwitchState MUST be called before this method to get a current 
    * reading of this information.  
    *
    * @return  <code>true</code> if the sensed level channel B is 'high' 
    * and <code>false</code> if the sensed level is 'low'.
    */
   public boolean getChannelBLevel()
   {
      return ((currentRawStatus & 0x08) == 0x08);
   }

   /**
    * Read the devices is getting supplied with power.  The method 
    * readSwitchState MUST be called before this method to get a current 
    * reading of this information.
    *
    * @return  <code>true</code> if the device is getting supplied with 
    * power and <code>false</code> if the device is parasite powered.  
    */
   public boolean isPowerSupplied()
   {
      return ((currentRawStatus & 0x80) == 0x80);
   }
   
   /**
    * Used to access the PIO channels to sense the logical status of
    * the output node.  This method supports all the modes of
    * communication with the part as described in the datasheet for
    * the DS2406/2407.
    * 
    * @param inbuffer The input buffer.  Depending on the other options chosen
    * to this method, this will contain data to be written to
    * the channels or it will have no interesting data in the
    * case of a read-only channel access.
    * @param toggleRW By selecting toggleRW to be true, the part will alternately 
    * read and write bytes from and to this channel.  Setting 
    * toggleRW to false means that only one operation will occur,
    * whichever operation is selected by readInitially.
    * @param readInitially If readInitially is true, the first operation to occur will
    * be a read, else it will be a write.  If toggleRW is false,
    * the operation chosen by this flag is the only operation that
    * will occur.  If toggleRW is true, this operation is the one
    * that will occur first, then the other will occur.  For example,
    * if toggleRW is true and readInitially is false (and you only
    * have one channel communication), 8 bits will be written to channel
    * A and then 8 bits will be read from channel A.
    * @param CRCMode The 2406/7 supports 4 CRC generation modes for error detection
    * when performing channel access.  This argument should have one
    * of the following values:
    *     CRC_DISABLE        Never generate a CRC
    *     CRC_EVERY_BYTE     Generate a CRC after every byte transmission.
    *     CRC_EVERY_8_BYTES  Generate a CRC after every 8 bytes.
    *     CRC_EVERY_32_BYTES Generate a CRC after every 32 bytes.
    * Invalid values will be masked to valid values.  The CRC is 16 bits,
    * and does not get passed back with the output.  The function returns 
    * null on a CRC failure.
    * @param channelMode The 2406/7 supports 3 modes of channel communication.  This
    * argument should take one of the following values:
    *     CHANNEL_A_ONLY  Only communicate with PIO A
    *     CHANNEL_B_ONLY  Only communicate with PIO B
    *     CHANNEL_BOTH    Communicate with both PIO's
    * If CHANNEL_BOTH is selected, data is written and read
    * alternatingly from the input buffer to the two channels.
    * See the datasheet for a description of operation in this
    * mode.  If communicating with both channels, it is up to the
    * caller to format the data correctly in the input buffer
    * so the correct channel gets the correct data.  Similarly,
    * any return data must be parsed by the user.
    * @param clearActivity True to reset the activity latch.
    * @param interleave The value for the Interleave Control bit.  If true, operates
    * in synchronous mode.  False operates in asynchronous mode.
    * See the datasheet for a discussion of asynchronous and 
    * synchronous mode.  This argument only matters if communicating
    * with both channels.
    * @return If any bytes were read, this returns a byte array of data 
    * read from the channel access.  If no bytes were read, it 
    * will return the input buffer that was to be written.  If 
    * an error occurs (such as an invalid CRC), the method 
    * returns null.
    * @exception com.ibutton.iButtonException
    * @exception OneWireIOException
    */   
   public byte[] channelAccess(byte[] inbuffer, boolean toggleRW, 
                               boolean readInitially, int CRCMode, int channelMode, 
                               boolean clearActivity, boolean interleave)
      throws iButtonException, OneWireIOException
   {
      CRCMode = CRCMode & 0x03;                      //MASK THIS TO ACCEPTABLE VALUE
      channelMode = channelMode & 0x0c;              //MASK THIS TO ACCEPTABLE VALUE
      if (channelMode==0) channelMode = 0x04;        //CHANNELMODE CANNOT BE 0
      if (interleave && (channelMode!=CHANNEL_BOTH)) //CANNOT INTERLEAVE WITH ONLY 1 CHANNEL
            interleave = false;
      if (adapter.select(address))
      {
         int crc16;
         int i;

         //now figure out how many bytes my output buffer needs to be
         int inlength = inbuffer.length;
         if (toggleRW) inlength = (inlength << 1);  //= inlength * 2
         switch (CRCMode)
         {
             case CRC_EVERY_BYTE     : //we need to allow for 2 CRC bytes for every byte of the length
                                       inlength = inlength * 3; //length + 2*length
                                       break;
             case CRC_EVERY_8_BYTES  : //we need to allow for 2 CRC bytes for every 8 bytes of length
                                       inlength = inlength + ((inlength >> 3) << 1);  //(length DIV 8) * 2
                                       break;
             case CRC_EVERY_32_BYTES : //we need to allow for 2 CRC bytes for every 32 bytes of length
                                       inlength = inlength + ((inlength >> 5) << 1);  //(length DIV 32) * 2
                                       break;
         }

         byte[] outputbuffer = new byte[inlength + 3 + 1]; //3 control bytes + 1 information byte
         outputbuffer[0] = CHANNEL_ACCESS_COMMAND;
         
         crc16 = CRC16.compute(CHANNEL_ACCESS_COMMAND & 0x0FF);

         // send the control bytes
         
         outputbuffer[1] = (byte) (CRCMode | channelMode | (clearActivity ? 0x80 : 0x00) | (interleave ? 0x10 : 0x00) |
                     (toggleRW ? 0x20 : 0x00) | (readInitially ? 0x40 : 0x00));

         outputbuffer[2] = (byte)0xFF;
         crc16 = CRC16.compute(outputbuffer,1,2,crc16);
         for (i=3;i<outputbuffer.length;i++)
            outputbuffer[i] = (byte) 0xff;
            
         //now for the hard part: putting the right outputbuffer into the array
         //first lets see if we can skip this stage, ie on just a read
         
         /*
                At this point we have 16 options:
                Initial  Toggle  CRC   Description
             0   write    off     0    Only write these bytes, CRC disabled
             1   write    off     1    Write these bytes, CRC for every byte
             2   write    off     8    Write these bytes, CRC for every 8 bytes
             3   write    off     32   Write these bytes, CRC for every 32 bytes
             4   write    on      0    Write a byte, read a byte, no CRC
             5   write    on      1    Write a byte, CRC, read a byte, CRC
             6   write    on      8    Write a byte, read a byte X 4 then a CRC
             7   write    on      32   Write a byte, read a byte X 16 then a CRC
             8   read     off     0    Read this many bytes, CRC disabled
             9   read     off     1    Read this many bytes, CRC for every byte
             a   read     off     8    Read this many bytes, CRC for every 8 bytes
             b   read     off     32   Read this many bytes, CRC for every 32 bytes
             c   read     on      0    Read a byte, write a byte, no CRC
             d   read     on      1    Read a byte, CRC, write a byte, CRC
             e   read     on      8    Read a byte, write a byte X 4 then a CRC
             f   read     on      32   Read a byte, write a byte X 16 then a CRC
               
             Options 0-3 require that we space the input buffer for the CRCs.
             Options 8-b require no extra work, since we have already loaded the buffer with FF's for reads.
             Options 4 and c require that we interleave the write bytes and the read FF's
             Options 5 and d require that we interleace write byte, CRC space, read byte, CRC space
             Other options are really messy 
                
             ...Brain            
          */
          int j=4; //outputbuffer 0-2 is command bytes, outputbuffer[3] is return info
          int option = outputbuffer[1] & 0x63;  //get the bits out we want for toggle, initial, and CRC
          option = ((option >> 3) | option) & 0x0f; //now lets make it a number 0-15

          /*switch (option)
          {
              case 0    :
              case 1    :
              case 2    :
              case 3    : for (i=0;i<inbuffer.length;i++)
                          {
                             outputbuffer[j] = inbuffer[i];
                             j = j + fixJ(i+1,option);
                          }
                          break;
              case 4    :
              case 5    :
              case 6    :
              case 7    : for (i=0;i<inbuffer.length;i++)
                          {
                              outputbuffer[j] = inbuffer[i];
                              j = j + fixJ((i*2)+1,option);
                              //then we plug in a read space
                              j = j + fixJ((i*2)+2,option);
                          }
                          break;
              case 8    :
              case 9    :
              case 0x0a :
              case 0x0b : 
                          break;  //nothing needs to be done
              case 0x0c :
              case 0x0d :
              case 0x0e :
              case 0x0f : for (i=0;i<inbuffer.length;i++)
                          {
                              //first we plug in a read space
                              j = j + fixJ((i*2)+1,option);
                              outputbuffer[j] = inbuffer[i];
                              j = j + fixJ((i*2)+2,option);
                          }
                          break;
          }*/
          
          /* this next section of code replaces the previous section to reduce redundant code.
             here we are formatting the output buffer so it has FF's in the right places
             for reading the CRC's and reading the data from the channels.  the previous code 
             is left because it makes a little more sense in that form. at least i think so.
             
             ...Pinky
          */
          if ((option < 8) || (option > 0x0b))  //if this is not a read-only (which we need do nothing for)
          {
               for (i=0;i<inbuffer.length;i++)
               {
                   if (option>0x0b)                   //then we are reading first
                       j = j + fixJ((i*2)+1,option);  //  leave a space for a read, and the CRC if need be
                   outputbuffer[j] = inbuffer[i];     //write this data
                   if (option<0x04)                   //if this is only a write
                       j = j + fixJ(i+1,option);      //  leave a space for CRC if needed, else just increment
                   else                               //else we are toggling
                   {
                       if (option<0x08)                  //this is a write-first toggle
                           j = j + fixJ((i*2)+1,option); //   so leave a space for a read
                       j = j + fixJ((i*2)+2,option);     //now leave a space for the CRC
                   }
               }
          }
          
                
          // now our output buffer should be set correctly        
         
         // send the block Pinky!
         adapter.dataBlock(outputbuffer, 0, outputbuffer.length);

         // calculate the CRC16 within the resulting buffer for integrity
         //start at offset 3 for the information byte
         crc16 = CRC16.compute(outputbuffer[3], crc16);
         j = 0;                  //j will be how many bytes we are into the buffer - CRC bytes read
         int k=0;                //index into the return buffer
         boolean fresh = false;  //whether or not we need to reinitialize the CRC calculation
         byte[] returnbuffer = new byte[inbuffer.length];
         for (i=4;i<outputbuffer.length;i++)
         {
            if (CRCMode!=CRC_DISABLE)
            {
                if (fresh)
                {   
                    crc16 = CRC16.compute(outputbuffer[i]);
                    fresh = false;
                }
                else
                    crc16 = CRC16.compute(outputbuffer[i], crc16);
            }
            if ((!toggleRW && readInitially) || (toggleRW && readInitially && ((j & 0x01) == 0x00))
                                            || (toggleRW && !readInitially && ((j & 0x01) == 0x01)))
            {
                returnbuffer[k] = outputbuffer[i];
                k++;
            }
            j++;
            if ((fixJ(j,option)>1) && (CRCMode!=CRC_DISABLE)) //means that we should look for a CRC
            {
                crc16 = CRC16.compute(outputbuffer,i+1,2,crc16);
                i += 2;
                if (crc16!=0xb001)
                    return null;    //invalid CRC!!!
                
                fresh = true;
            }
         }
         //now that we got the right bytes out of the array
         return returnbuffer;
      }
      // device must not have been present
      throw new OneWireIOException("iButtonContainer12-device not present");
   }
    
   /**
    * This method returns how much we should increment the index variable into
    * our output buffer.  should be called after every setting of a value.
    *
    * @param current_index  current index into the channel access array
    * @param option_mask    contains data on CRC generation
    *
    * @return amount to increment the index variable
    */
   private int fixJ(int current_index, int option_mask)
   {
      //assume that current_index started at 0, but this function is never called at 0
      switch (option_mask & 0x03)
      {
        case 0x00 : return 1;    //no crc
        case 0x01 : return 3;    //2-byte CRC after every byte
        default   : //must be 0x02 (after 8 bytes) or 0x03 (after 32 bytes)
                    if ((current_index & (8 + (24 * (option_mask & 0x01)) - 1))==0) 
                        return 3;
        /* OK let me explain that last piece of code:
           The only return values are going to be 1 and 3, 1 for a normal increment
           and 3 if we want to leave space to recieve a CRC.
           
           So the mask gets the bits out that are concerned with the CRC.  When its 0 it
           means that the CRC is disabled, so the next location into our destination 
           array we need to copy into is just the next available location.
           
           When it is 1, it means we will recieve a CRC after each transmission, so 
           we should leave a 2 byte space for it (thus increament by 3).
           
           When it is 2, it means that after every 8 bytes we want to recieve a CRC
           byte pair.  When it is a 3, it means that every 32 bytes we want the CRC
           pair.  So what we want to check is if the current_index is divisible by 8
           or 32 (we do not call this method with current_index==0).  Since 8 and 32
           are powers of 2 we do it with the '&' operator and 7 or 31 as the other
           value (2^n - 1).  The (8+(24 * option_mask&0x01)) bit just returns me 8
           or 32.
        */
      }
      return 1;
   }
    
   /**
    * Reads the 8 bytes of status memory from the part.
    * 
    * @return Byte array with the 8 bytes of status bits.
    *
    * @throws OneWireIOException
    * @throws iButtonException 
    */
   public byte[] readStatusMemory()
      throws iButtonException, OneWireIOException
   {
      return read(READ_STATUS_COMMAND,0,8, true);
   }

   /**
    * Reads the entire contents of this part's memory (128 bytes).
    * 
    * @return Byte array with the contents of this part's EPROM memory.
    *
    * @throws OneWireIOException
    * @throws iButtonException 
    */
   public byte[] readMemory()
      throws iButtonException, OneWireIOException
   {
      return read(READ_MEMORY_COMMAND,0,128, true);
   }

   /**
    * Reads one page (out of four) from the part.  Since the part 
    * is EPROM (i.e. programmable once) pages of memory can be 
    * 'overridden' by redirecting the page addresses.  By calling 
    * with extended==true, you recieve the result of any redirection.  
    * With extended==false, you raed the raw data from memory with 
    * no redirection.
    * 
    * Also, the extended read performs a CRC calculation whereas 
    * the non-extended read does not.  To read the contents of a 
    * raw (non-redirected) page, use readMemory().
    * @param page Page number to read (only the 2 least significant bits matter).
    * @param extended Whether to follow page redirection.  See above for a discussion.
    * @return Byte array with the contents of the requested page (32 bytes).
    *
    * @throws OneWireIOException
    * @throws iButtonException
    */
   public byte[] readMemoryPage(int page, boolean extended)
      throws iButtonException, OneWireIOException
   {
      /* 1. We truncate the page number because there are only 4 pages (0-3).
            Leaving the page number as is breaks the crc if we ask for page 5,
            which actually returns page 1.
         2. All pages are 32 bytes.
         3. For extended page reads, the part provides a CRC for each page.
            For non-extended page reads, no CRC is provided, unless you
            read to the end of memory.
       */
      return read(extended ? EXTENDED_READ_MEMORY_COMMAND : READ_MEMORY_COMMAND,
                  (page & 0x03) << 5, 32,
                  extended ? true : false);                                                            
   }

   /** 
    *  This function does the actual work of any reads from memory.
    *
    *  @param  COMMAND the 1-wire protocol command to send
    *  @param  target_address the byte address to read from.  only matters on page reads.
    *  this is turned into two bytes in the protocol
    *  @param  length the number of bytes to read.  8,32,or 128.
    *  @param  doCRC  whether or not to perform a CRC calculation
    *  
    *  NOTE: This function should not be called just by any method.  It assumes that
    *        calling functions know what they are doing (as far as parameters).
    *
    */
   private byte[] read(byte COMMAND, int target_address, int length, boolean doCRC) 
      throws iButtonException, OneWireIOException
   {
      boolean extended = (COMMAND == EXTENDED_READ_MEMORY_COMMAND);
      byte[] buffer = new byte[3+length+2 ]; 
      int i;
      int redirect_count=0;
      int crc=0;
      boolean done_reading = false;
      buffer[0] = COMMAND;                //read status memory command
      buffer[1] = (byte) target_address;  //there are only 4 pages (128 bytes), i had left the 
                                          //     address alone but this breaks the crc calculation
      buffer[2] = 0;                      //only 128 bytes, so address will fit in first byte
      for (i = 3;i<buffer.length;i++)
         buffer[i] = (byte)0x0ff;         //blank out the rest to do a read

      if (doCRC)
      {
         crc = CRC16.compute(COMMAND);
         crc = CRC16.compute((byte)target_address, crc);
         crc = CRC16.compute(0x00, crc);
      }

      /*The 2407 has bytes for page redirecting since it has EPROM and not RAM.  This way a page
        can be 'overridden' by redirecting its page address.  For this reason we have to watch for this
        redirection on an EXTENDED_READ_MEMORY_COMMAND
       */
      while (!done_reading)
      {
         adapter.reset();
         adapter.select(address);
         if (!extended)
         {
            adapter.dataBlock(buffer,0,buffer.length);
            done_reading = true;
         }
         else   //HANDLE AN EXTENDED READ
         {
            adapter.dataBlock(buffer,0,6);    //send enough to get the redirect byte and the CRC
            
            /* CRC CHECK HERE */
            for (i=3;i<6;i++)
               crc = CRC16.compute(buffer[i], crc);

            if (crc!=0xB001) 
               throw new OneWireIOException("Invalid CRC: Device may be missing.");
              
            if (buffer[3]!=(byte)0xff)        //NOT 0x0ff means we are redirected, so read again
            {
               buffer[0] = EXTENDED_READ_MEMORY_COMMAND;     //reset the beginning of the buffer
               buffer[1] = (byte) ((~buffer[3]) << 5);
               buffer[2] = 0;
               buffer[3] = buffer[4] = buffer[5] = (byte)0xff;
               crc = CRC16.compute(EXTENDED_READ_MEMORY_COMMAND);
               crc = CRC16.compute(buffer[1], crc);
               crc = CRC16.compute(0x00, crc);
               redirect_count++;
               if (redirect_count==4) return null;  //guard against infinite looping pages
            }
            else
            {
               //want the page data to start at byte 3, so clean up first
               buffer[3] = buffer[4] = buffer[5] = (byte)0xff;
               adapter.dataBlock(buffer,3,buffer.length-3);
               done_reading = true;
               //in extended read page, the crc is cleared before the page data begins
               crc = 0;
            }
         }
      }
      
      if (doCRC)
      {
         for (i=3;i<buffer.length;i++)
            crc = CRC16.compute((byte)buffer[i],crc);
      }

      if ((crc == 0xB001)||(!doCRC))  //magic crc number
      { 
         byte[] buf = new byte[length];
         System.arraycopy(buffer, 3, buf, 0, length);
         return buf;
      }

      throw new OneWireIOException("Invalid CRC: Device may be missing.");
   }
}
