@setlocal

@set OPENMAPDIR=C:\work\resources\openmap-4.4.2
@set JXTADIR=C:\jxta

java -cp ..\..\data\cougaarMEdomain.jar;%OPENMAPDIR%\lib\openmap.jar;%OPENMAPDIR%\share\data\shape;%JXTADIR%\shell\lib\jxta.jar;%JXTADIR%\shell\lib\log4j.jar;%JXTADIR%\shell\lib\jxtasecurity.jar;%JXTADIR%\shell\lib\jxtaptls.jar;%JXTADIR%\shell\lib\minimalBC.jar;%JXTADIR%\shell\lib\cryptix32.jar;%JXTADIR%\shell\lib\cryptix-asn1.jar -Dnet.jxta.tls.principal="p2pgui" -Dnet.jxta.tls.password="myp2pgui" org.cougaar.microedition.demo.ugs.P2PDemoUI "file://openmap.properties"

@endlocal
