#!/bin/csh

setenv OPENMAPDIR /home/rtaschle/openmap/openmap-4.4.2
setenv JXTADIR /home/rtaschle/jxta
setenv IMGDIR /home/rtaschle/bbn/micro/data/images
setenv JXTAPEER p2pgui
setenv JXTAPW myp2pgui


/usr/java/jdk1.3.1_02/bin/java -cp ../micro/data/cougaarMEdomain.jar:$OPENMAPDIR/lib/openmap.jar:$OPENMAPDIR/share/data/shape:$JXTADIR/shell/lib/jxta.jar:$JXTADIR/shell/lib/log4j.jar:$JXTADIR/shell/lib/jxtasecurity.jar:$JXTADIR/shell/lib/jxtaptls.jar:$JXTADIR/shell/lib/minimalBC.jar:$JXTADIR/shell/lib/cryptix32.jar:$JXTADIR/shell/lib/cryptix-asn1.jar -Dnet.jxta.tls.principal=$JXTAPEER -Dnet.jxta.tls.password=$JXTAPW -Dopenmap.iconDir=$IMGDIR org.cougaar.microedition.demo.ugs.P2PDemoUI openmap.properties

