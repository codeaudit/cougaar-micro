@echo OFF

REM calls setlibpath.bat which sets the path to the required jar files.
REM calls setarguments.bat which sets input parameters for system behavior
CALL %ALP_INSTALL_PATH%\bin\setlibpath.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"

set LIBPATHS=..\classes;%LIBPATHS%;%ALP_INSTALL_PATH%\lib\glm.jar;%ALP_INSTALL_PATH%\lib\planserver.jar;%ALP_INSTALL_PATH%\lib\xml.jar;\alp_dev\internal\microedition\TINI\cougaarMEdomain.jar

@ECHO ON

\JBuilder4\jdk1.3\bin\java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS% %2 %3



