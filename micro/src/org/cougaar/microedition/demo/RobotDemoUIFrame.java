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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 */

public class RobotDemoUIFrame extends JFrame {
  JPanel contentPane;
  JLabel statusBar = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  double lat1, lon1, lat2, lon2;

  JPanel jPanel1;
  JPanel jPanel2 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  RobotDemoUI robotDemo;

  /**Construct the frame*/
  public RobotDemoUIFrame(RobotDemoUI rd) {
    robotDemo=rd;
    initialize();
    jPanel1 = new RobotDemoPanel(lat1, lon1, lat2, lon2);
    //enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  /**Component initialization*/
  private void jbInit() throws Exception  {
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(800, 600));
    this.setTitle("MicroCougaar Robot Demo UI Main Frame");
    statusBar.setText(" ");
    jButton1.setText("Stop");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        robotDemo.stopUpdates();
      }
    });
    jButton2.setText("Start ");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        robotDemo.startUpdates();
      }
    });
    contentPane.setPreferredSize(new Dimension(600, 400));
    //jPanel1.setPreferredSize(new Dimension(600, 400));
    contentPane.add(statusBar, BorderLayout.NORTH);
    contentPane.add(jPanel1, BorderLayout.CENTER);
    contentPane.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(jButton2, null);
    jPanel2.add(jButton1, null);
  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }


  private void  initialize() {
    try {
      lat1=Double.parseDouble(System.getProperty("area.lat1"));
      lon1=Double.parseDouble(System.getProperty("area.lon1"));
      lat2=Double.parseDouble(System.getProperty("area.lat2"));
      lon2=Double.parseDouble(System.getProperty("area.lon2"));
    } catch (Exception ex) {
      lat1=40.5; lon1=-100.2; lat2=40.4; lon2=-100.1;
    }
  }


  public void repaint() {
    jPanel1.repaint();
    jPanel2.repaint();
  }

  public void update(Graphics g) { paint(g); }


}