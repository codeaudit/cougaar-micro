package com.systronix.sbx2;

import com.systronix.sbx2.Misc;
import com.dalsemi.system.*;
import java.util.*;

/**
 * Title:        SBX2 JAR project
 * Description:  My attempt to make an SBX2 jar file containing all the needed API to SBX2.
 * Copyright:    Copyright (c) B Boyes
 * Company:      Systronix
 * @author B Boyes
 * @version 1.0
 */

/**
 * The SBX2 byte-wide I/O ports.
 * There are three such byte-wide ports for 24 bits total.
 * Each bit is independent of all the others.
*/
public class Parallel {

  /**
   * I/O Register 0
   */
  public static final byte INOUT0 = 0x00;

  /**
   * I/O Register 0
   */
  public static final byte INOUT1 = 0x01;

  /**
   * I/O Register 0
   */
  public static final byte INOUT2 = 0x02;

  public Parallel() {
  }

  //-------------------------------------------------------
  /** write a byte to output register address, should be INOUT0, INOUT1 or INOUT2.
   *  At the moment this is not checked. <br>
   *  The outputs are open-drain and are pulled high when not asserted.
   *  An asserted or active output is pulled low. So setting an output bit with a '1'
   *  drives that output active low. The output driver can sink at least 150 mA.<br>
   *  To use an I/O bit as an input, write a 0 to it to let it float inactive high,
   *  then an external switch or device can easily pull it active low.
   *  To be consistent, input/output bits which are active low are written and read as a '1'.
   *  Input bits which are inactive high read as a '0'. See the getInput method.
   */
  public static boolean setOutput (byte which, byte towrite) {
    boolean flag=true;
    try {
      // STRETCH1 seems to be the value used as default by TINI
      Misc.sbx2.setStretchCycles(Misc.sbx2.STRETCH2);
      Misc.sbx2.setAddress (Misc.sbxCs0 + which);
      Misc.sbx2.write (towrite);
    }
    catch ( IllegalAddressException e ){
      System.out.println ("setOutput write error " + e);
      flag = false;
    }

    return flag;
  } // end method setOutput

  //-------------------------------------------------------
  /** read the input register which should be INOUT0, INOUT1 or INOUT2.
   *  At the moment this is not checked. Returns the byte value read.
   *  The outputs are open-drain and are pulled high when not asserted.
   *  An asserted or active output is pulled low. So setting an output bit with a '1'
   *  drives that output active low. When you read the input, the input buffer is
   *  non-inverting. To be consistent, this method complements the read value so
   *  that input/output bits which are active low are written and read as a '1'.
   *  Input bits which are inactive high read as a '0'.
   */
  public static byte getInput (byte which) {
    boolean flag=true;
    byte gotdata = 0;
    try {
      // STRETCH1 seems to be the value used as default by TINI
      Misc.sbx2.setStretchCycles(Misc.sbx2.STRETCH3);
      Misc.sbx2.setAddress (Misc.sbxCs0 + which);
      // invert it so active low signals read as a 1
      gotdata= (byte) ~ Misc.sbx2.read ();
      switch (which) {
        case INOUT0:
          break;
        case INOUT1:
          break;
        case INOUT2:
          break;
        default:
          // bad location, throw an exception
          flag = false;
          break;
        } // end of switch
    }
    catch ( IllegalAddressException e ){
      System.out.println ("getInput read error " + e);
      flag = false;
    }
    return gotdata;
    // return flag;
  } // end method getInput




}