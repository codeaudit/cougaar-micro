@echo off
REM
REM This batch file starts a CougaarME node using the KVM. 
REM Option 1: Provide one argument (the name of the XML config file)
REM Option 2: Provide three arguments (the name of the node, then the IP 
REM           address and port of the CougaarSE configuration server)
REM


REM Set this to the root of your KVM distribution
set KVMROOT=c:\dev\j2me_cldc

REM These shouldn't need to be changed
set KVMCLASSES=%KVMROOT%\bin\common\api\classes
set KVMCLASSES=%KVMCLASSES%;..\..\..\data\cougaarme.jar;.

@echo on

%KVMROOT%\bin\win32\kvm -heapsize 1M -classpath %KVMCLASSES% org.cougaar.microedition.node.Node %1 %2 %3
