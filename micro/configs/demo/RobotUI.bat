@echo OFF

CALL %COUGAAR_INSTALL_PATH%\bin\setlibpath.bat
CALL %COUGAAR_INSTALL_PATH%\bin\setarguments.bat

rem set MYARGUMENTS= -c -n "%1"

set LIBPATHS=..\classes;C:\projects\cougaar\micro\TINI\cougaarMEdomain.jar;%LIBPATHS%

@ECHO ON

java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% org.cougaar.microedition.demo.RobotDemoUI http://192.168.10.10:5555/$SurveillanceManager/ROBOTDATA.PSP
