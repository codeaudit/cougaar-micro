/**
 * Title:        SBX2 JAR project<p>
 * Description:  My attempt to make an SBX2 jar file containing all the needed API to SBX2.<p>
 * Copyright:    Copyright (c) B Boyes<p>
 * Company:      Systronix<p>
 * @author B Boyes
 * @version 1.0 2000 Dec 28
 */

package com.systronix.sbx2;

import com.dalsemi.system.*;
import java.util.*;



/**
 * SBX2 miscellaneous I/O such as the buzzer and on-board green LED.
 * These are all driven by a TPIC6B273 octal FET register.
 * The register has eight positive edge-triggered D-type flip-flops.
 * Outputs are low-side, open drain DMOS transistors.
 * Outputs can drive low only, they float high when not driven.
 * Each output has a typical on resistance of 5 ohms
 * Each output can sink 150 mA continuously at a drain voltage of 35V or less.
 * Output off-state drain current is 8 uA max (leakage, more or less).
 * Each output has a 50V diode clamp, and 500mA typical current limiting.
 * All outputs are cleared to inactive on an SBX2 reset
 * <p>
 * Only install JP1 if your LCD module has its
 * own current limit resistor (the 20x4 LCDs sold by Systronix do).
 * If JP1 is installed, backlight current is pulled directly through a FET,
 * which has a typical on-resitance of 5 ohms.
 * If JP1 is not installed, backlight current is pulled through one 10 ohm resistor.
 * <A HREF=http://www.systronix.com/access/lcd.htm> Link to 20x4 LCD info</A>
 */
public class Misc {

  // make these public?
  static final int sbxCs0 = 0x380060;   // all but UART
  static final int sbxCs1 = 0x380080;   // UART

  protected static DataPort sbx2 = new DataPort ();

  private static byte miscReg = 0;
  private static final byte miscAdr = 0x06;   // address of misc register

  /**
   * This is the SBX2 on-board green LED, located near the 5x2 UART headers.
   * This is bit Misc.7
   */
  private static final byte greenLed = (byte) 0x80;

  /**
   * The on-board SBX2 buzzer. It oscillates at about 3 KHz when powered.
   * This is Misc.2
   */
  private static byte intBuzzer = 0x04;

  /**
   * External indicator connector P4, could drive a buzzer, relay, lamp, etc.
   * This is Misc.1
   */
  private static byte extBuzzer = 0x02;

  /**
   * H to L transition clocks the DS1804 LCD contrast control pot
   * This is Misc.5
   */
  private static byte contrastClk = 0x20;

  /**
   * H means LCD contrast to a higher voltage when clocked, L goes to lower voltage.
   * This is Misc.4
   */
  private static byte contrastDir = 0x10;

  /**
   * LCD Backlight off (Misc.6 and Misc.3 = 0) --
   * No current is pulled through the LCD backlight.
   */
  public static final byte OFF = 0x00;

  /**
   * LCD Backlight DIM (Misc.6 = 0, Misc.3 = 1) --
   * This pulls backlight current through two 10 ohm resistors.
   */
  public static final byte DIM = 0x08;

  /**
   * LCD Backlight Bright (Misc.6 and Misc.3 = 1) --
   * Misc.6 provides "direct" drive (no current limit resistor) if JP1 is installed.
   */
  public static final byte BRT = 0x48;

  /**
   * drive LCD contrast darker
   */
  public static final boolean DARK = true;
  /**
   * drive LCD contrast lighter
   */
  public static final boolean LIGHT = false;







  //-------------------------------------------------------
  /**
   * Set the green SBX2 LED either on or off
   * true = on, false = off
   */
  synchronized public static void setLed (boolean onOff) {
    if (true==onOff) {
      // OR the LED bit on
      miscReg |= greenLed;
    }
    else {
      // AND it off
      miscReg &= ~greenLed;
    }
    setMisc();

  } // end method setLed

  //-------------------------------------------------------
  /**
   * Set the on board ("internal") buzzer either on or off
   * <br> true = on, false = off
   */
  synchronized public static void setIntBuzzer (boolean onOff) {
    if (true==onOff) {
      // OR the bit on
      miscReg |= intBuzzer;
    }
    else {
      // else AND it off
      miscReg &= ~intBuzzer;
    }
    setMisc();

  } // end method setIntBuzzer

  //-------------------------------------------------------
  /**
   * Beep the on board ("internal") buzzer howLong msec
   * 20 msec gives a click, 200 msec or more a beep
   */
  public static void beepIntBuzzer (long howLong) {

  class BeepThread extends Thread {

  long howLong;

  //Misc sm = new Misc ();
  // constructor accepts argument
  private BeepThread(long howLong) {
    this.howLong = howLong;
  }

  public void run() {
    setIntBuzzer(true);
    //setIntBuzzer
    try { this.sleep(howLong); }
    catch (InterruptedException e) {
      System.out.println("WaitThread error " + e);
    }
    setIntBuzzer(false);
  } // end run method

  } // end class BeepThread

    // beep is 500 msec
    BeepThread bt = new BeepThread(howLong);
    bt.start();

  } // end method beepIntBuzzer




  //-------------------------------------------------------
  /**
   * Set the external P4 FET output either on or off.
   * true = on, false = off
   * P4 pin 1 is Vcc, pin 2 is the open-drain FET output
   * This can be used to drive an external buzzer, lamp, relay, etc up
   * to 150 mA at up to 35V. You can provide your own external voltage too, but
   * do not exceed 150 mA and 35 volts drain-to-SBX2 ground.
   * The output has a built-in 50V clamp to limit inductive transients.
   */
  public static void setExtBuzzer (boolean onOff) {
    if (true==onOff) {
      // OR the bit on
      miscReg |= extBuzzer;
    }
    else {
      // else AND it off
      miscReg &= ~extBuzzer;
    }
    setMisc();

  } // end method setExtBuzzer


  //-------------------------------------------------------
  /**
   * Set the backlight either OFF, DIM, BRT
   * The LCD backlight is driven via the P3 LCD connector.
   * P3 pin 15 is tied to SBX2 5V, this is the LED backlight anode.
   * P3 pin 16 is pulled low through SBX2 FET drivers.
   * SBX2 pulls P3 pin 16 low through 0, 1, or 2 resistors to adjust the brightness.
   * Value should only be OFF, DIM or BRT, other values may affect other Misc bits.
   */
  synchronized public static void setBacklight (byte value) {
    // clear all backlight drive to zero
    // note: ANDing with OFF clears every miscReg bit - we don't want that!
    miscReg &= ~BRT;
    // now OR in what we want
    // if value is OFF do nothing, we already cleared the bits
    if (DIM == value) {
      miscReg |= value;
    }
    else if (BRT == value) {
      miscReg |= value;
    }
    // if value was invalid we ignored it... we should throw an exception
    setMisc();

  } // end method setBacklight

  //-------------------------------------------------------
  /**
   * Set the LCD contrast adjustment, implemented in a DS1804 digital pot.
   * The DS1804 has 100 steps. Lower voltage makes the LCD darker.
   * If <code>dark</code> is true, contrast voltage will be decreased (darker display),
   * if false it will be increased (lighter, less contrasty display) by
   * <code>delta</code> (1 to 100) number of steps.
   * If <code>delta</code> is <0 or >100 no action is taken.
   * Note that the pot never "wraps around", it just stops at
   * its max or min wiper setting).
   *
   * So to initialize to max contrast (a good starting point), call like this:
   * <b><code>
   * setContrast (true, 100)
   * </b></code><br>
   *
   * This is opposite to what you might expect, i.e., a lower contrast voltage
   * makes the display darker (increased contrast), while a higher contrast voltage
   * makes the display lighter (less contrast, more 'washed out').
   */
  synchronized public static void setContrast (boolean dark, byte delta) {

    if ((delta > 100) | (delta < 1)) return;

    if (true == dark) {
      // set the Dir bit so it is low at the DS1804
      miscReg |= contrastDir;
    }
    else {
      // clear the Dir bit so it is high at the DS1804
      miscReg &= ~contrastDir;
    }

    /**
    * Now clock the dark bit delta number of times. A H->L transition on the Clk
    * signal causes one quanta of change. There are 100 levels.
    */

    do {
      // set Clk low at DS1804
      miscReg |= contrastClk;
      setMisc();
      // clear Clk high at DS1804
      miscReg &= ~contrastClk;
      setMisc();
      delta--;
    }
    while (delta > 0);

  } // end method setContrast


  //-------------------------------------------------------
  /** write miscReg to the misc output register address
   *  This updates the physical register with the correct
   *  logical combination of all the various bits which are
   *  set through
   */
  synchronized private static boolean setMisc () {
    boolean flag=true;
    try {
      // STRETCH1 seems to be the value used as default by TINI
      sbx2.setStretchCycles(sbx2.STRETCH3);
      sbx2.setAddress (sbxCs0 + miscAdr);
      sbx2.write (miscReg);
    }
    catch ( IllegalAddressException e ){
      System.out.println ("dataPort write error " + e);
      flag = false;
    }

    return flag;
  } // end method setMisc



} // end class misc