package org.cougaar.microedition.demo;

import javax.swing.UIManager;
import java.awt.*;
import java.util.Vector;
import java.util.Iterator;

/**
 *
 */

public class RobotDemoUI {
  static long SLEEP_TIME=5000;
  boolean packFrame = false;
  RobotDemoUIFrame frame;
  String url;

  /**Construct the application*/
  public RobotDemoUI() {
    initializeSystemProperties();
    url=System.getProperty("robotDataUrl");
    System.out.println("Getting robot data from url: "+url);
    frame = new RobotDemoUIFrame(this);
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);

  }

  /**
   * @return URL to use to get robot data
   */
  String getUrl() { return url; }

  private static Vector robotProxies=new Vector();
  public static Iterator getRobotInfo() {
    return robotProxies.iterator();
  }


  RobotUpdateThread robotUpdateThread;


  //void addRobotInfo(RobotProxy rp) { robotProxies.add(rp); }

  public void startUpdates() {
    if (robotUpdateThread !=null) {
      robotUpdateThread.finish();
    }
    robotUpdateThread=new RobotUpdateThread();
    robotUpdateThread.start();
  }

  public void stopUpdates() {
    if (robotUpdateThread!=null) {
      robotUpdateThread.finish();
    }
  }

    static int steps=0;  // just for testing stuff

  class RobotUpdateThread extends Thread {
    RobotUpdateThread() {
      synchronized  (this) {
        robotProxies.clear();
      }  // end sync
    } // end ctor

    /* ======================
  RobotProxy makeNewRobotProxy(int xloc, int yloc, double heading) {
    RobotProxy rp=new RobotProxy(xloc, yloc);
    rp.setHeading(heading);
    return rp;
  }
  =================== */

    boolean keepPolling=true;
  public void finish() {
    keepPolling=false;
  }

  RobotProxy getRobotInfo(String id) {
    RobotProxy rp, ret=null;
    for (Iterator it=robotProxies.iterator(); it.hasNext()&& ret==null; ) {
      rp=(RobotProxy)it.next();
      if (rp.getId().equals(id)) {
        ret=rp;
      }
    }
    return ret;
  }
  void addRobotInfo(RobotProxy rp) { robotProxies.add(rp);  }

  synchronized public boolean updateRobot(RobotDataImport.RobotData rd) {
      Double d;
      String id=rd.getId();
      RobotProxy rp=getRobotInfo(id);
      if (rp==null) {
        rp = new RobotProxy(id, rd.getLat(), rd.getLon(), rd.isLightOn(), rd.getPictureAvailable());
        //System.out.println("RD new : "+id+", "+rd.getLat()+", "+rd.getLon()+", l:"+rd.isLightOn()+", "+rd.getPictureAvailable());
        d=rd.getHeading();
        if (d!=null) { rp.setHeading(d.doubleValue()); }
        d=rd.getBearing();
        if (d!=null) { rp.setBearing(d.doubleValue()); }
        addRobotInfo(rp);
      } else {
        //System.out.println("RD up : "+id+", "+rd.getLat()+", "+rd.getLon()+", l:"+rd.isLightOn()+", "+rd.getPictureAvailable());
        rp.update(rd.getLat(), rd.getLon(), rd.isLightOn(), rd.getPictureAvailable());
        d=rd.getHeading();
        if (d!=null) { rp.setHeading(d.doubleValue()); }
        d=rd.getBearing();
        if (d!=null) { rp.setBearing(d.doubleValue()); }
      }
      return true;
    }
  /**
   * Obtain updated information from robots
   * @return true if any robot had info updated
   */
    synchronized public boolean updateRobots() {
      RobotProxy rp;
      // System.out.println("in update robots");
      double heading=0;
      ++steps;
      for (Iterator iter=RobotDemoUI.getRobotInfo(); iter.hasNext();) {
        rp=(RobotProxy)iter.next();
        if (rp.getId().equalsIgnoreCase("spinner")) {
          rp.setHeading(rp.heading+15);
          rp.setLightOn(!rp.isLightOn());
        } else if (rp.getId().equalsIgnoreCase("spinner2")) {
          rp.setHeading(rp.heading-45);
        } else {

          if (!rp.getId().startsWith("stopped_")) {
            if (steps == 2) {
              rp.setId("stopped_"+rp.getId());
              rp.setBearing(2);
              steps++;
            } else if (steps == 10) {
              rp.setId("stopped_"+rp.getId());
              rp.setBearing(-15);
              steps++;
            } else {
              rp.stepForward(5);
            }
          }
        }
      }
      return true;
    }

    public void run() {
      System.out.println("Thread started "+this);

//      while (keepGoing) {
      while (true) {

        // Get new data from URL
        if (keepPolling) {
          RobotDataImport rdi=new RobotDataImport(getUrl());
          Iterator it=rdi.iterator();

          // Use each record to update the system
          while (it.hasNext()) {
            RobotDataImport.RobotData rd=(RobotDataImport.RobotData)it.next();
            if (updateRobot(rd)) {
              frame.repaint();
            }
          }
        }
        try {
          sleep(SLEEP_TIME);
        } catch (Exception ex) {
        }
      }
      //System.out.println("Thread done "+this);
    }
  }

  private void initializeSystemProperties() {
    System.setProperty("imageUrlBase","file:/home/krotherm/mc/");
    System.setProperty("imageUrlSuffix",".jpg");
    /*
    System.setProperty("name1.image.x","0");
    System.setProperty("name1.image.y","0");
    System.setProperty("name2.image.x","661");
    System.setProperty("name2.image.y","0");
    System.setProperty("name3.image.x","0");
    System.setProperty("name3.image.y","481");
    */
  }

  /**Main method*/
  public static void main(String[] args) {
    if (args.length>0) {
        System.setProperty("robotDataUrl", args[0]);
    } else {
        System.setProperty("robotDataUrl", "file:/data/robot/input.txt");
    }

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    new RobotDemoUI();
  }
}