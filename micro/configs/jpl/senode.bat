@echo OFF

CALL %ALP_INSTALL_PATH%\bin\setlibpath.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"

set MYPROPERTIES= -Dorg.cougaar.domain.micro=org.cougaar.microedition.se.domain.Domain -Dorg.cougaar.microedition.ServerPort=%2 %MYPROPERTIES% -Dorg.cougaar.domain.planning.ldm.lps.ComplainingLP.level=0 -Dorg.cougaar.core.cluster.SharedPlugInManager.watching=false -Dorg.cougaar.core.cluster.enablePublishException=false -Dorg.cougaar.core.cluster.showTraffic=false -Dorg.cougaar.core.cluster.idle.verbose=false
set LIBPATHS=C:\projects\JPL\micro\data\cougaarMEdomain.jar;%LIBPATHS%

@ECHO ON

java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS%

