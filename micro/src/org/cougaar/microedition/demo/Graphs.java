package cougaar.microedition.demo;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import com.klg.jclass.swing.gauge.beans.*;
import com.borland.jbcl.layout.*;
import javax.swing.border.*;

public class Graphs extends JApplet {
  boolean isStandalone = false;
  String PSP;
  GridLayout gridLayout1 = new GridLayout();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel1 = new JPanel();
  JCCircularGaugeBean PDAGuage = new JCCircularGaugeBean();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  JCCircularGaugeBean lightGuage = new JCCircularGaugeBean();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel3 = new JPanel();
  JCCircularGaugeBean temperatureGuage = new JCCircularGaugeBean();
  BorderLayout borderLayout3 = new BorderLayout();
  GridLayout gridLayout3 = new GridLayout();
  Border border1;
  TitledBorder titledBorder1;
  Border border2;
  TitledBorder titledBorder2;
  Border border3;
  TitledBorder titledBorder3;
  JLabel temperatureField = new JLabel();
  JLabel lightField = new JLabel();
  JLabel PDAField = new JLabel();
  /**Get a parameter value*/
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }

  /**Construct the applet*/
  public Graphs() {
  }
  /**Initialize the applet*/
  public void init() {
    try {
      PSP = this.getParameter("PSP", "");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    try {
      jbInit();
      startListener();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  /**Component initialization*/
  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder();
    titledBorder1 = new TitledBorder(border1,"Temperature");
    border2 = BorderFactory.createEmptyBorder();
    titledBorder2 = new TitledBorder(border2,"Light");
    border3 = BorderFactory.createEmptyBorder();
    titledBorder3 = new TitledBorder(border3,"PDA");
    this.setSize(new Dimension(586, 146));
    this.getContentPane().setLayout(gridLayout1);
    gridLayout1.setColumns(3);
    jPanel1.setLayout(borderLayout1);
    jPanel1.setBorder(titledBorder3);
    jPanel1.setToolTipText("");
    PDAGuage.setDrawTickLabels(false);
    PDAGuage.setPaintCompleteBackground(true);
    PDAGuage.setScaleColor(SystemColor.info);
    PDAGuage.setTickStyle(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.TICK_REVERSE_TRIANGLE);
    PDAGuage.setType(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.TYPE_TOP_HALF_CIRCLE);
    PDAGuage.setDirection(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.DIRECTION_CLOCKWISE);
    jPanel2.setLayout(borderLayout2);
    jPanel2.setBorder(titledBorder2);
    jPanel2.setPreferredSize(new Dimension(75, 75));
    lightGuage.setDirection(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.DIRECTION_CLOCKWISE);
    lightGuage.setType(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.TYPE_TOP_HALF_CIRCLE);
    lightGuage.setTickStyle(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.TICK_REVERSE_TRIANGLE);
    lightGuage.setScaleColor(SystemColor.info);
    lightGuage.setPaintCompleteBackground(true);
    lightGuage.setDrawTickLabels(false);
    jPanel3.setLayout(borderLayout3);
    temperatureGuage.setDirection(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.DIRECTION_CLOCKWISE);
    temperatureGuage.setType(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.TYPE_TOP_HALF_CIRCLE);
    temperatureGuage.setTickStyle(com.klg.jclass.swing.gauge.beans.JCCircularGaugeBean.TICK_REVERSE_TRIANGLE);
    temperatureGuage.setScaleColor(SystemColor.info);
    temperatureGuage.setPaintCompleteBackground(true);
    temperatureGuage.setDrawTickLabels(false);
    jPanel4.setLayout(gridLayout3);
    jPanel3.setBorder(titledBorder1);
    titledBorder1.setTitleJustification(2);
    titledBorder2.setTitleJustification(2);
    titledBorder3.setTitleJustification(2);
    jPanel4.setBorder(BorderFactory.createLineBorder(Color.black));
    temperatureField.setHorizontalAlignment(SwingConstants.CENTER);
    lightField.setHorizontalAlignment(SwingConstants.CENTER);
    PDAField.setHorizontalAlignment(SwingConstants.CENTER);
    this.getContentPane().add(jPanel4, null);
    jPanel4.add(jPanel3, null);
    jPanel3.add(temperatureGuage, BorderLayout.CENTER);
    jPanel3.add(temperatureField, BorderLayout.SOUTH);
    jPanel4.add(jPanel2, null);
    jPanel2.add(lightGuage, BorderLayout.CENTER);
    jPanel2.add(lightField, BorderLayout.SOUTH);
    jPanel4.add(jPanel1, null);
    jPanel1.add(PDAGuage, BorderLayout.CENTER);
    jPanel1.add(PDAField, BorderLayout.SOUTH);
  }
  /**Start the applet*/
  public void start() {
  }
  /**Stop the applet*/
  public void stop() {
  }
  /**Destroy the applet*/
  public void destroy() {
  }
  /**Get Applet information*/
  public String getAppletInfo() {
    return "Applet Information";
  }
  /**Get parameter info*/
  public String[][] getParameterInfo() {
    String[][] pinfo =
      {
      {"PSP", "String", ""},
      };
    return pinfo;
  }
  /**Main method*/
  public static void main(String[] args) {
    Graphs applet = new Graphs();
    applet.isStandalone = true;
    JFrame frame = new JFrame();
    //EXIT_ON_CLOSE == 3
    frame.setDefaultCloseOperation(3);
    frame.setTitle("Applet Frame");
    frame.getContentPane().add(applet, BorderLayout.CENTER);
    applet.init();
    applet.start();
    frame.setSize(400,320);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
  }

  //static initializer for setting look & feel
  static {
    try {
      //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch(Exception e) {
    }
  }

  private void startListener() {
    Thread t = new Thread(new Listener(), "PSP_LISTENER");
    t.start();
  }

  private class Listener implements Runnable {
    NumberFormat fmt = new DecimalFormat("##0.00");
    public void run() {
      // Open the PSP input stream
      try {
        URL pspURL = new URL(PSP);

        URLConnection conn = pspURL.openConnection();
        InputStream in = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while (true) {
            String line = reader.readLine();
            parseLine(line);
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    private void parseLine(String line) {
      StringTokenizer st = new StringTokenizer(line, ":");
      String label = st.nextToken();
      String value = st.nextToken();
      if ((label == null) || (value == null)) return; // format error
      double val = Double.parseDouble(value);
      value = fmt.format(val);
      if (label.equals("Temperature")) {
        temperatureField.setText(value);
        temperatureGuage.setNeedleValue(val);
      }
      else if (label.equals("Light")) {
        lightField.setText(value);
        lightGuage.setNeedleValue(val);
      }
      else if (label.equals("PDA")) {
        PDAField.setText(value);
        PDAGuage.setNeedleValue(val);
      }

    }
  }

}
