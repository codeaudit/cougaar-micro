#!/bin/csh

setenv OPENMAPDIR /home/rtaschle/openmap/openmap-4.4.2

java -cp ../micro/data/cougaarMEdomain.jar:$OPENMAPDIR/lib/openmap.jar org.cougaar.microedition.demo.ugs.map.VPFContextInfoPlugIn /mnt/cdrom/vmaplv0 ./context.txt $1
