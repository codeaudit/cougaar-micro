set KVMROOT=c:\dev\j2me\kvm
set KVMCLASSES=%KVMROOT%\bin\common\api\classes
set KVMCLASSES=%KVMCLASSES%;%KVMROOT%\bin\kjava\api\classes
set KVMCLASSES=%KVMCLASSES%;..\data\cougaarme.jar

%KVMROOT%\bin\win32\debug\kvm_g -classpath %KVMCLASSES% org.cougaar.microedition.cluster.Node %1 %2 %3
