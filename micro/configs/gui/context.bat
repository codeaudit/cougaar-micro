@setlocal

@set OPENMAPDIR=C:\work\resources\openmap-4.4.2

java -cp ..\..\data\cougaarMEdomain.jar;%OPENMAPDIR%\lib\openmap.jar org.cougaar.microedition.demo.ugs.map.VPFContextInfoPlugin d:\vmaplv0 .\context.txt %1

@endlocal
