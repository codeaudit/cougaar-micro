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

    static String [] verbs = { "ReportPosition", "Advance", "TraverseWaypoints", "ReportTarget", "ControlFlashlight", "RotateTurret", "ReportDetection", "GetImage" };
    static int REPORTPOSITION = 0;
    static int ADVANCE = 1;
    static int TRAVERSEWAYPOINTS = 2;
    static int REPORTTARGET = 3;
    static int CONTROLFLASHLIGHT = 4;
    static int ROTATETURRET = 5;
    static int REPORTDETECTION = 6;
    static int GETIMAGE = 7;

  }

}