package org.cougaar.microedition.shared;

public interface Constants {

  public interface Aspects {

    static int LATITUDE = 100;
    static int LONGITUDE = 101;
    static int HEADING = 102;
    static int DETECTION = 103;
    static int BEARING = 104;
    static int IMAGE = 105;
    static int FLASHLIGHT = 106;

  }

  public interface Robot  {

    static int min = 2;

    static String [] roles = { "SurveillanceProvider" };
    static int SURVEILLANCEPROVIDER = 0;

    static String [] meRoles = { "Everything", "PositionProvider", "LocomotionController", "TargetingController", "TurretController", "SONARSensor" };
    static int EVERYTHING = 0;
    static int POSITIONPROVIDER = 1;
    static int LOCOMOTIONCONTROLLER = 2;
    static int TARGETINGCONTROLLER = 3;
    static int TURRETCONTROLLER = 4;
    static int SONARSENSOR = 5;

    static String [] verbs = { "ReportPosition", "Advance", "ReportTarget", "TraverseWaypoints", "GetImage", "ControlFlashlight" };
    static int REPORTPOSITION = 0;
    static int ADVANCE = 1;
    static int REPORTTARGET = 2;
    static int TRAVERSEWAYPOINTS = 3;
    static int GETIMAGE = 4;
    static int CONTROLFLASHLIGHT = 5;

    static String [] subVerbs = { "MonitorLocation", "Forward", "AcquireTargetBearing" };
    static int MONITORLOCATION = 0;
    static int FORWARD = 1;
    static int ACQUIRETARGETBEARING = 2;

  }

}