set KVMROOT=c:\dev\j2me_cldc
set KVMCLASSES=%KVMROOT%\bin\common\api\classes
set KVMCLASSES=%KVMCLASSES%;..\..\data\cougaarme.jar;.

%KVMROOT%\kvm\vmWin\build\kvm_g -classpath %KVMCLASSES% org.cougaar.microedition.cluster.Node %1 %2 %3
