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

    static String [] meRoles = { "PositionProvider", "LocomotionController", "TargetingController", "TurretController", "SONARSensor" };
    static int POSITIONPROVIDER = 0;
    static int LOCOMOTIONCONTROLLER = 1;
    static int TARGETINGCONTROLLER = 2;
    static int TURRETCONTROLLER = 3;
    static int SONARSENSOR = 4;

    static String [] verbs = { "ReportPosition", "Advance", "ReportTarget", "TraverseWaypoints", "GetImage", "ControlFlashlight" };
    static int REPORTPOSITION = 0;
    static int ADVANCE = 1;
    static int REPORTTARGET = 2;
    static int TRAVERSEWAYPOINTS = 3;
    static int GETIMAGE = 4;
    static int CONTROLFLASHLIGHT = 5;

    static String [] subVerbs = { "Forward", "AcquireTargetBearing" };
    static int FORWARD = 0;
    static int ACQUIRETARGETBEARING = 1;

  }

}