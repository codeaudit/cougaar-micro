#!/bin/csh

# workaround for glibc bug.  limits initial thread size to 2 MB
# for bash shell
#ulimit -s 2048
# or, for tcsh
limit stacksize 2048


setenv COUGAAR_INSTALL_PATH /home/rtaschle/cougaar8.4
setenv ALP_INSTALL_PATH /home/rtaschle/cougaar8.4
#setenv COUGAAR_INSTALL_PATH /home/rtaschle/kevin/cougaar8.4
#setenv ALP_INSTALL_PATH /home/rtaschle/kevin/cougaar8.4

$COUGAAR_INSTALL_PATH/bin/setlibpath.sh
$COUGAAR_INSTALL_PATH/bin/setarguments.sh

setenv PROJDIR /home/rtaschle/bbn
setenv JXTADIR /home/rtaschle/jxta/platform/binding/java/lib
setenv JXTAPEER peer1
setenv JXTAPW jxtapeer

setenv JXTAJARLOC /home/rtaschle/jxta/platform/jxta.jar
#setenv JXTAJARLOC /home/rtaschle/bbn/testjxta.jar


setenv MYPROPERTIES "-Dorg.cougaar.domain.glm=org.cougaar.domain.glm.ldm.GLMDomain -Dorg.cougaar.domain.micro=org.cougaar.microedition.se.domain.Domain -Dorg.cougaar.microedition.ServerPort=$2 -Dorg.cougaar.core.cluster.SharedPlugInManager.watching=false -Dorg.cougaar.core.cluster.enablePublishException=false -Dorg.cougaar.core.cluster.showTraffic=false -Dorg.cougaar.core.cluster.idle.verbose=false -Dorg.cougaar.useBootstrapper=false -Dnet.jxta.tls.principal=$JXTAPEER -Dnet.jxta.tls.password=$JXTAPW"

setenv LIBPATHS $JXTADIR/props:$PROJDIR/micro/data/cougaarMEdomain.jar:"$JXTAJARLOC":$JXTADIR/log4j.jar:$JXTADIR/jxtasecurity.jar:$JXTADIR/beepcore.jar:$JXTADIR/cryptix-asn1.jar:$JXTADIR/cryptix32.jar:$JXTADIR/jxtaptls.jar:$JXTADIR/minimalBC.jar:$JXTADIR/org.mortbay.jetty.jar:$JXTADIR/servlet.jar:$COUGAAR_INSTALL_PATH/lib/core.jar:$COUGAAR_INSTALL_PATH/lib/build.jar:$COUGAAR_INSTALL_PATH/lib/planserver.jar:$COUGAAR_INSTALL_PATH/lib/glm.jar:$COUGAAR_INSTALL_PATH/lib/xerces.jar:$COUGAAR_INSTALL_PATH/lib/xalan.jar:$ALP_INSTALL_PATH/sys/xerces.jar:$ALP_INSTALL_PATH/sys/xalan.jar



java $MYPROPERTIES -classpath $LIBPATHS org.cougaar.core.society.Node -c -n $1
