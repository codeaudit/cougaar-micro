/*
 * <copyright>
 *  Copyright 1999-2001 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.microedition.demo;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.awt.event.*;

/**
 */

public class RobotImageDisplay extends JFrame {

  static int xScreenLoc, yScreenLoc;
  static double screenWidth, screenHeight;
  Image image;
  ImageObserver observer;
  String robotId;
  String fullUrl;
  public RobotImageDisplay(String robotId) {
    this.setTitle("Image from "+robotId);
    this.robotId=robotId;
    setSize(660,480);
    try {
      String urlBase=System.getProperty("imageUrlBase");
      String urlSuffix=System.getProperty("imageUrlSuffix");
      fullUrl=urlBase+robotId+urlSuffix;
      System.out.println("Retrieving image from url: ["+fullUrl+"]");
      image=Toolkit.getDefaultToolkit().createImage(new URL(fullUrl));
      addNotify();
      repaint();
    } catch (Exception ex) {
        System.err.println("Error:  PSP for obtaining image from robot (ID: ["
          +robotId+"]) indicated a failure.");
        System.err.println("  Check that the PSP is configured correctly."
          +"  Url used: ["+fullUrl+"]");
        //       ex.printStackTrace();
    }

    try {
      screenWidth=Toolkit.getDefaultToolkit().getScreenSize().getWidth();
      screenHeight=Toolkit.getDefaultToolkit().getScreenSize().getHeight();
      xScreenLoc=Integer.parseInt(System.getProperty(robotId+".image.x"));
      yScreenLoc=Integer.parseInt(System.getProperty(robotId+".image.y"));
    } catch (Exception ex) {
    }

    if (xScreenLoc > screenWidth) xScreenLoc=0;
    if (yScreenLoc > screenHeight) yScreenLoc=0;

    setLocation(xScreenLoc, yScreenLoc);
    xScreenLoc+=50;
    yScreenLoc+=50;
    show();
  }

  JDialog jd;
  boolean displayedError=false;
  private void doCancelAction() {
    jd.dispose();
    jd=null;
    hide();
  }
  public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
    //System.out.println("imageUpdate "+img+" flags "+flags+" x y w h "+x+" "+y+" "+w+" "+h);
    repaint();
    if ((flags&ImageObserver.ERROR)!=0) {
      System.err.println("Error loading image for robot ["+robotId+"] from URL ["+fullUrl+"]");
      if (!displayedError) {
        displayedError=true;
        jd=new JDialog();
        jd.getContentPane().setLayout(new BorderLayout());
        jd.setTitle("Error Loading image");
        JPanel labelPanel=new JPanel();
        JPanel buttonPanel=new JPanel();
        JLabel label=new JLabel("Error loading image for "+robotId+"\n");
        labelPanel.add(label);
        jd.getContentPane().add(labelPanel);
        JButton jb1=new JButton("Cancel");
        buttonPanel.add(jb1);
        jd.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
        jb1.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            doCancelAction();
          }
        });
        Dimension size=label.getSize();
        Dimension jbsize=jb1.getSize();
        jd.setSize(size.width+jbsize.width, size.height+jbsize.height);
        System.out.println("sizes: "+size+" "+jbsize+" "+jd.getContentPane().getSize()+" "+(size.width+jbsize.width)+", "+(size.height+jbsize.height));
        jd.setSize(400, 100);
        jd.show();

        hide();
      }
    }
    if ((flags&ImageObserver.ALLBITS)==ImageObserver.ALLBITS) {
      return false;
    } else {
      return true;
    }
  }
  public void update(Graphics g) {
    paint( g);
  }
  public void paint(Graphics g) {
    if (image!=null) {
      g.drawImage(image, 10, 10, this);
    }
  }
}