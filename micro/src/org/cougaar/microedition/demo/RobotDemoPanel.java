package org.cougaar.microedition.demo;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.util.Iterator;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel for drawing robot locations.
 */
public class RobotDemoPanel extends JPanel {

  double lat1, lon1, lat2, lon2  ;
  AffineTransform at;
  MouseHandler myMouseHandler=new MouseHandler();

  class MouseHandler extends MouseAdapter {
    int MOUSE_REGION=20;
    public void mouseClicked(MouseEvent e) {
      double x, y;
      double lat, lon;
      Point p=new Point();
      System.out.println("Mouse clicked at: "+e.getPoint());
      x=e.getX();
      y=e.getY();
      RobotProxy rp;

      System.out.println("mouse 1: "+(e.getModifiers()&e.BUTTON1_MASK));
      System.out.println("mouse 2: "+(e.getModifiers()&e.BUTTON2_MASK));
      System.out.println("mouse 3: "+(e.getModifiers()&e.BUTTON3_MASK));
      for (Iterator iter=RobotDemoUI.getRobotInfo(); iter.hasNext();) {
        rp=(RobotProxy)iter.next();
        //p=inverseTransform(x,y, p);
        lat=p.getY();
        lon=p.getX();
        RobotDemoPanel src=(RobotDemoPanel)e.getSource();
        p=src.transformCoordinates(rp.getLat(), rp.getLon(), p);
        double rpx=p.getX();
        double rpy=p.getY();
            System.out.println("Checking to see if mouse click touched close to robot: "+rp);
            System.out.println("   which has [x,y] of ["+rpx+", "+rpy+"]");
        if (x <= rpx+MOUSE_REGION && x >= rpx-MOUSE_REGION) {
          if (y <= rpy+MOUSE_REGION && y >= rpy-MOUSE_REGION*2) {
            System.out.println("Looks like mouse click touched close to robot: "+rp);
            System.out.println("   which has [x,y] of ["+rpx+", "+rpy+"]");
            if ((e.getModifiers() & InputEvent.BUTTON1_MASK)== InputEvent.BUTTON1_MASK) {
              System.out.println("Toggling light from "+rp.isLightOn()+" to "+!rp.isLightOn()+".");
              rp.setLightOn(!rp.isLightOn());
            }
            if ((e.getModifiers() & (InputEvent.BUTTON3_MASK|InputEvent.BUTTON2_MASK))!= 0) {
              System.out.println("Toggling pic from "+rp.isPictureAvailable()+" to "+!rp.isPictureAvailable()+".");
              rp.setPictureAvailable(!rp.isPictureAvailable());
              RobotImageDisplay rid=new RobotImageDisplay(rp.getId());
              rid.setVisible(true);
            }
            src.repaint();
            //src.update(src.getGraphics());
          }
        }
      }

    }
  }

  public RobotDemoPanel() {
    addMouseListener(myMouseHandler);
  }
  public RobotDemoPanel(double lat1, double lon1,
                        double lat2, double lon2) {
    this.lat1=lat1;
    this.lon1=lon1;
    this.lat2=lat2;
    this.lon2=lon2;
    addMouseListener(myMouseHandler);
  }

  public void draw(Shape s, Graphics2D g2, AffineTransform at) {
    synchronized(this) {
    if (at==null) {
      g2.draw(s);
    } else {
      g2.draw(at.createTransformedShape(s));
    }
    }
  }


  Point transformCoordinates(double lat, double lon, Point p) {

    double x=getWidth()/(lon2-lon1)*(lon-lon1);
    double y=getHeight()/(lat2-lat1)*(lat-lat1);

    if (p==null) {
      p = new Point();
    }
    p.setLocation(x, y);

    //System.out.println("Transform lat, lon of ("+lat+", "+lon+") at [x,y] of ["+x+", "+y+"]");

    return p;
  }
  Point inverseTransform(double x, double y, Point p) {

    double w=getWidth();
    double h=getHeight();

    double lat= (y*(lat2-lat1)/h)+lat1;
    double lon= (x*(lon2-lon1)/w)+lon1;

    if (p==null) {
      p = new Point();
    }
    p.setLocation(lon, lat);

    //System.out.println("Invert to lat, lon of ("+lat+", "+lon+") from [x,y] of ["+x+", "+y+"]");

    return p;
  }

  void drawRect(Graphics2D g2, double lat, double lon, int w, int h, double orientation) {
    drawRect(g2, lat, lon, w, h, orientation, false);
  }
  void fillRect(Graphics2D g2, double lat, double lon, int w, int h, double orientation) {
    drawRect(g2, lat, lon, w, h, orientation, true);
  }
  void drawRect(Graphics2D g2, double lat, double lon, int w, int h, double orientation, boolean fill) {
    performTransformedAction(g2,
        new DrawRectAction(lat, lon, w, h, orientation, fill));
  }

  void drawLine(Graphics2D g2, double lat, double lon, int len, double orientation) {
    drawLine(g2, lat, lon, len, orientation, false);
  }
  void drawDashedLine(Graphics2D g2, double lat, double lon, int len, double orientation) {
    drawLine(g2, lat, lon, len, orientation, true);
  }
  void drawLine(Graphics2D g2, double lat, double lon, int len, double orientation, boolean wantDashes) {
    performTransformedAction(g2,
        new DrawLineAction(lat, lon, len, orientation, wantDashes));
  }
  void drawLine_prev(Graphics2D g2, double lat, double lon, int len, double orientation, boolean wantDashes) {
    int x,y;
    Point p=new Point();

    p = transformCoordinates(lat, lon, p);
    x=(int)p.getX();
    y=(int)p.getY();

    System.out.println("Drawing lat, lon of ("+lat+", "+lon+") at [x,y] of ["+x+", "+y+"]");
    AffineTransform at = new AffineTransform();
    at.rotate(Math.toRadians(orientation), x, y);
    AffineTransform orig=g2.getTransform();
    g2.setTransform(at);

        Stroke initStroke=g2.getStroke();
    if (wantDashes) {
        Stroke dashStroke=new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, (new float[] { 10f }), 0 );
        g2.setStroke(dashStroke);
    }
    g2.drawLine(x, y, x, y-len);
    if (wantDashes) {
        g2.setStroke(initStroke);
    }
    g2.setTransform(orig);
  }

  /**
   * Abstract base class for actions which are performed by RobotDemoPanel on
   * transformed objects.
   */
  abstract class TransformAction {
    abstract public void execute(Graphics2D g2, int x, int y);
    abstract public double getLat();
    abstract public double getLon();
    abstract public double getOrientation();
  }

  class DrawCircleAction extends TransformAction {
    double lat, lon;
    int dia, offset;
    double orientation;
    boolean fill;
    DrawCircleAction(double lati, double loni, int diai, int offseti,
      double orientationi, boolean filli) {
      lat=lati; lon=loni; dia=diai; offset=offseti;
      orientation=orientationi; fill=filli;
    }
    public double getLat() {return lat; }
    public double getLon() { return lon; }
    public double getOrientation() { return orientation; }
    public void execute(Graphics2D g2, int x, int y) {
      if (fill) {
        g2.fillArc(x-dia/2,y-offset-dia, dia, dia,0, 360);
      } else {
        g2.drawArc(x-dia/2,y-offset-dia, dia, dia,0, 360);
      }
    }
  }
  class DrawRectAction extends TransformAction {
    double lat, lon;
    int w, h;
    double orientation;
    boolean fill;
    DrawRectAction(double lati, double loni, int wi, int hi, double orientationi, boolean filli) {
      lat=lati; lon=loni; w=wi; h=hi;
      orientation=orientationi; fill=filli;
    }
    public double getLat() {return lat; }
    public double getLon() { return lon; }
    public double getOrientation() { return orientation; }
    public void execute(Graphics2D g2, int x, int y) {
      if (fill) {
        g2.fillRect(x-w/2, y-h/2, w, h);
      } else {
        g2.drawRect(x-w/2, y-h/2, w, h);
      }
    }
  }
  class DrawLineAction extends TransformAction {
    double lat, lon;
    int len;
    double orientation;
    boolean wantDashes;
    DrawLineAction(double lati, double loni, int leni, double orientationi, boolean dashes) {
      lat=lati; lon=loni; len=leni;
      orientation=orientationi; wantDashes=dashes;
    }
    public double getLat() {return lat; }
    public double getLon() { return lon; }
    public double getOrientation() { return orientation; }
    public void execute(Graphics2D g2, int x, int y) {
      Stroke initStroke=g2.getStroke();
      if (wantDashes) {
        Stroke dashStroke=new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, (new float[] { 10f }), 0 );
        g2.setStroke(dashStroke);
      }
      g2.drawLine(x, y, x, y-len);
      if (wantDashes) {
        g2.setStroke(initStroke);
      }
    }
  }

  void performTransformedAction(Graphics2D g2, TransformAction ta) {
    int x,y;
    Point p=new Point();
    double lat=ta.getLat();
    double lon=ta.getLon();
    double orientation=ta.getOrientation();

    p = transformCoordinates(lat, lon, p);
    x=(int)p.getX();
    y=(int)p.getY();

    //System.out.println("Transformed lat, lon of ("+lat+", "+lon+") to [x,y] of ["+x+", "+y+"]");
    AffineTransform at = new AffineTransform();
    at.rotate(Math.toRadians(orientation), x, y);
    AffineTransform orig=g2.getTransform();
    g2.setTransform(at);

    ta.execute(g2, x, y);

    g2.setTransform(orig);
  }

  void drawCircle(Graphics2D g2, double lat, double lon, int dia, int offset,
      double orientation) {
      drawCircle(g2, lat, lon, dia, offset, orientation, false);
  }

  void fillCircle(Graphics2D g2, double lat, double lon, int dia, int offset,
      double orientation) {
      drawCircle(g2, lat, lon, dia, offset, orientation, true);
  }

  void drawCircle(Graphics2D g2, double lat, double lon, int dia, int offset,
      double orientation, boolean fill) {
      performTransformedAction(g2,
        new DrawCircleAction(lat, lon, dia, offset, orientation, fill));
  }

  public void fill(Shape s, Graphics2D g2, AffineTransform at) {
    synchronized(this) {
      if (at==null) {
        g2.fill(s);
      } else {
        g2.fill(at.createTransformedShape(s));
      }
    }
  }

//  public void paintComponent(Graphics g) {
  public void paint(Graphics g) {
    synchronized(this) {
//    super.paintComponent(g);
    super.paint(g);

    //System.out.println("rbpanel paint");
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    RobotProxy rp;
    for (Iterator iter=RobotDemoUI.getRobotInfo(); iter.hasNext();) {
      rp=(RobotProxy)iter.next();
      rp.draw(this, g2);
    }
    }
  }

}