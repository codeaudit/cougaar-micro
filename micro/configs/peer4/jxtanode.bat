@echo OFF
@setlocal

set ALP_INSTALL_PATH=C:\cougaar9.0
set COUGAAR_INSTALL_PATH=C:\cougaar9.0

rem CALL %ALP_INSTALL_PATH%\bin\setlibpath.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"
set PROJDIR=..\..\..
set JXTADIR=C:\JXTA\platform\binding\java\lib
set JXTAPEER="peer4"
set JXTAPW="jxtapeer"

rem set JXTAJARLOC=C:\work\code\JXTA\stable_02082002\platform\binding\java\classes\testjxta.jar
set JXTAJARLOC=C:\JXTA\platform\jxta.jar


set MYPROPERTIES= -Dorg.cougaar.domain.micro=org.cougaar.microedition.se.domain.Domain -Dorg.cougaar.microedition.ServerPort=%2 %MYPROPERTIES% -Dorg.cougaar.core.cluster.SharedPlugInManager.watching=false -Dorg.cougaar.core.cluster.enablePublishException=false -Dorg.cougaar.core.cluster.showTraffic=false -Dorg.cougaar.core.cluster.idle.verbose=false -Dorg.cougaar.useBootstrapper=false -Dnet.jxta.tls.principal=%JXTAPEER% -Dnet.jxta.tls.password=%JXTAPW%

set JXTALIBPATHS=%JXTAJARLOC%;%JXTADIR%\log4j.jar;%JXTADIR%\jxtasecurity.jar;%JXTADIR%\beepcore.jar;%JXTADIR%\cryptix-asn1.jar;%JXTADIR%\cryptix32.jar;%JXTADIR%\jxtaptls.jar;%JXTADIR%\minimalBC.jar;%JXTADIR%\org.mortbay.jetty.jar;%JXTADIR%\servlet.jar

set COUGAARLIBPATHS=%ALP_INSTALL_PATH%\lib\core.jar;%ALP_INSTALL_PATH%\lib\glm.jar;%ALP_INSTALL_PATH%\lib\planserver.jar;%ALP_INSTALL_PATH%\lib\webserver.jar;%ALP_INSTALL_PATH%\lib\webtomcat.jar;%ALP_INSTALL_PATH%\sys\xerces.jar;%ALP_INSTALL_PATH%\sys\xalan.jar;%ALP_INSTALL_PATH%\sys\servlet.jar;%ALP_INSTALL_PATH%\sys\tomcat_core.jar;%ALP_INSTALL_PATH%\sys\etomcat.jar;%ALP_INSTALL_PATH%\sys\tomcat.jar;%ALP_INSTALL_PATH%\sys\core_util.jar;%ALP_INSTALL_PATH%\sys\tomcat_modules.jar;%ALP_INSTALL_PATH%\sys\tomcat_util.jar;%ALP_INSTALL_PATH%\sys\facade22.jar;%ALP_INSTALL_PATH%\lib\csmart.jar;%ALP_INSTALL_PATH%\sys\grappa1_2.jar

set LIBPATHS=%PROJDIR%\micro\data\cougaarMEdomain.jar;%PROJDIR%\micro\data\props;%JXTALIBPATHS%;%COUGAARLIBPATHS%

@ECHO ON

java %MYPROPERTIES% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS%

@endlocal
