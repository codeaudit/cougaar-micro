@echo OFF

CALL %ALP_INSTALL_PATH%\bin\setlibpath.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"

set MYPROPERTIES= -Dorg.cougaar.domain.micro=org.cougaar.microedition.domain.Domain -Dorg.cougaar.microedition.ServerPort=%2 %MYPROPERTIES%
set LIBPATHS=C:\projects\cougaar\micro\TINI\cougaarMEdomain.jar;%LIBPATHS%

@ECHO ON

java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS%

