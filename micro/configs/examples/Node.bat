@echo OFF

REM
REM This batch file starts a CougaarSE node that can serve as the 
REM configuration and name server for CougaarME nodes. You can start
REM it like this:
REM    C:\>Node MotherNode
REM
REM Then when you start your CougaarME nodes, point them to the mother node
REM by giving them three arguments like this:
REM    C:\>KvmNode Tutorial 127.0.0.1 1235
REM

REM calls setlibpath.bat which sets the path to the required jar files.
REM calls setarguments.bat which sets input parameters for system behavior
CALL %COUGAAR_INSTALL_PATH%\bin\setlibpath.bat
CALL %COUGAAR_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"

set COUGAAR_ME_ROOT=c:\alp\micro

set MYPROPERTIES=%MYPROPERTIES% 
set LIBPATHS=%COUGAAR_INSTALL_PATH%\lib\core.jar;..\..\data\cougaarMEDomain.jar

@ECHO ON

java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS% %2 %3



