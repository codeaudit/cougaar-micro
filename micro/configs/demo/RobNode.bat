@echo OFF

CALL %COUGAAR_INSTALL_PATH%\bin\setlibpath.bat
CALL %COUGAAR_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"

set MYPROPERTIES= -Dorg.cougaar.domain.micro=org.cougaar.microedition.se.domain.Domain -Dorg.cougaar.microedition.ServerPort=%2 %MYPROPERTIES% -Dorg.cougaar.domain.planning.ldm.lps.ComplainingLP.level=0 -Dorg.cougaar.core.cluster.SharedPluginManager.watching=false -Dorg.cougaar.core.cluster.enablePublishException=false
set LIBPATHS=C:\projects\JXTA\micro\data\cougaarMEdomain.jar;%LIBPATHS%

@ECHO ON

C:\JBuilder4\jdk1.3\bin\java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS%

