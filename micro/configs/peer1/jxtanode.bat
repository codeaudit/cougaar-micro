@echo OFF

CALL %ALP_INSTALL_PATH%\bin\setlibpath.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"
set PROJDIR=C:\Projects\JXTA
set JXTADIR=C:\Projects\JXTA
set JXTAPEER="peer1"
set JXTAPW="jxtapeer"

set MYPROPERTIES= -Dorg.cougaar.domain.micro=org.cougaar.microedition.se.domain.Domain -Dorg.cougaar.microedition.ServerPort=%2 %MYPROPERTIES% -Dorg.cougaar.core.cluster.SharedPlugInManager.watching=false -Dorg.cougaar.core.cluster.enablePublishException=false -Dorg.cougaar.core.cluster.showTraffic=false -Dorg.cougaar.core.cluster.idle.verbose=false -Dorg.cougaar.useBootstrapper=false -Dnet.jxta.tls.principal=%JXTAPEER% -Dnet.jxta.tls.password=%JXTAPW%

set LIBPATHS=%JXTADIR%\props;%PROJDIR%\micro\data\cougaarMEdomain.jar;%JXTADIR%\shell\lib\jxta.jar;%JXTADIR%\shell\lib\jxtashell.jar;%JXTADIR%\shell\lib\log4j.jar;%JXTADIR%\shell\lib\jxtasecurity.jar;%JXTADIR%\shell\lib\beepcore.jar;%JXTADIR%\shell\lib\cryptix-asn1.jar;%JXTADIR%\shell\lib\cryptix32.jar;%JXTADIR%\shell\lib\jxtaptls.jar;%JXTADIR%\shell\lib\minimalBC.jar;%ALP_INSTALL_PATH%\sys\xerces.jar;%ALP_INSTALL_PATH%\sys\xalan.jar;%LIBPATHS%

@ECHO ON

C:\JBuilder4\jdk1.3\bin\java.exe %MYPROPERTIES% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS%

