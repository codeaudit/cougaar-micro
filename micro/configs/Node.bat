@echo OFF

REM calls setlibpath.bat which sets the path to the required jar files.
REM calls setarguments.bat which sets input parameters for system behavior
CALL %COUGAAR_INSTALL_PATH%\bin\setlibpath.bat
CALL %COUGAAR_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"

set MYPROPERTIES=%MYPROPERTIES%
rem set LIBPATHS=%COUGAAR_INSTALL_PATH%\lib\core.jar;..\..\micro\SEclasses;%LIBPATHS%
rem set LIBPATHS=%COUGAAR_INSTALL_PATH%\lib\core.jar;..\..\micro\SEclasses
set LIBPATHS=%COUGAAR_INSTALL_PATH%\lib\core.jar;..\SEclasses

@ECHO ON

java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS% %2 %3



