@echo OFF

CALL %COUGAAR_INSTALL_PATH%\bin\setlibpath.bat
CALL %COUGAAR_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"

set MYPROPERTIES= -Dorg.cougaar.microedition.ServerPort=%2 %MYPROPERTIES% -Dorg.cougaar.domain.planning.ldm.lps.ComplainingLP.level=0 -Dorg.cougaar.core.cluster.SharedPlugInManager.watching=false -Dorg.cougaar.core.cluster.enablePublishException=false
set LIBPATHS=C:\projects\cougaar\micro\TINI\cougaarMEdomain.jar;%LIBPATHS%

@ECHO ON

java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS%

