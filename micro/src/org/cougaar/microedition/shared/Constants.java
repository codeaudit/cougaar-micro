package org.cougaar.microedition.shared;

public interface Constants {

  public interface Aspects {

    static int LATITUDE = 100;
    static int LONGITUDE = 101;
    static int HEADING = 102;
    static int BEARING = 103;
    static int DETECTION = 104;
    static int SCANDIR = 105;
    static int IMAGE = 106;
    static int FLASHLIGHT = 107;

  }

  public interface Robot  {

    static int min = 2;

    static String [] roles = { "SurveillanceProvider" };
    static int SURVEILLANCEPROVIDER = 0;

    static String [] meRoles = { "Everything", "PositionProvider", "LocomotionController", "TargetingController", "FlashlightController", "TurretController", "SONARSensor" };
    static int EVERYTHING = 0;
    static int POSITIONPROVIDER = 1;
    static int LOCOMOTIONCONTROLLER = 2;
    static int TARGETINGCONTROLLER = 3;
    static int FLASHLIGHTCONTROLLER = 4;
    static int TURRETCONTROLLER = 5;
    static int SONARSENSOR = 6;

    static String [] verbs = { "ReportPosition", "Advance", "TraverseWaypoints",
      "ReportTarget", "ControlFlashlight", "RotateTurret", "ReportDetection",
      "GetImage", "StartSystem", "SetOrientation" };
    static int REPORTPOSITION = 0;
    static int ADVANCE = 1;
    static int TRAVERSEWAYPOINTS = 2;
    static int REPORTTARGET = 3;
    static int CONTROLFLASHLIGHT = 4;
    static int ROTATETURRET = 5;
    static int REPORTDETECTION = 6;
    static int GETIMAGE = 7;
    static int STARTSYSTEM = 8;
    static int SETORIENTATION = 9;

    static String [] prepositions = { "Degrees", "Speed", "Velocity", "TurretHemisphere" };
    final static int ORIENTATIONPREP= 0; // "Degrees"
    final static int SPEEDPREP= 1; // "Speed";
    final static int VELOCITYPREP=2; // "Velocity";
    final static int TURRETDIRECTIONPREP=3; // "TurretHemisphere"

    public static final int TURRET_LEFT = 0;
    public static final int TURRET_MIDDLE = 1;
    public static final int TURRET_RIGHT = 2;

  }

  public interface Geophysical {

    static final double EARTH_RADIUS_METERS = 6378137.0; //meters;
    static final double MAGNETIC_DECLINATION = 0.0; //degrees E=+ W=-
    static final double DEGTOBILLIONTHS = 1000000000.0; //10^9
    static final double BILLIONTHSTODEG = 0.000000001; //10^-9

  }
}