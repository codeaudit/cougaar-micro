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
  Image image;
  ImageObserver observer;
  String robotId;
  public RobotImageDisplay(String robotId) {
    this.setTitle("Image from "+robotId);
    this.robotId=robotId;
    setSize(660,480);
    try {
      String urlBase=System.getProperty("imageUrlBase");
      String urlSuffix=System.getProperty("imageUrlSuffix");
      String fullUrl=urlBase+robotId+urlSuffix;
      System.out.println("Retrieving image from url: ["+fullUrl+"]");
      //image=Toolkit.getDefaultToolkit().getImage(new URL("file:/home/krotherm/mc/pic.jpg"));
      //image=Toolkit.getDefaultToolkit().getImage(new URL(fullUrl));
      image=Toolkit.getDefaultToolkit().createImage(new URL(fullUrl));
      addNotify();
      repaint();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    try {
      xScreenLoc=Integer.parseInt(System.getProperty(robotId+".image.x"));
      yScreenLoc=Integer.parseInt(System.getProperty(robotId+".image.y"));
    } catch (Exception ex) {
    }

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
      System.out.println("Error loading image ");
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