#!/bin/csh

# workaround for glibc bug.  limits initial thread size to 2 MB
# for bash shell
#ulimit -s 2048
# or, for tcsh
limit stacksize 2048


setenv COUGAAR_INSTALL_PATH /home/rtaschle/cougaar9.0
setenv ALP_INSTALL_PATH /home/rtaschle/cougaar9.0


#$COUGAAR_INSTALL_PATH/bin/setlibpath.sh
$COUGAAR_INSTALL_PATH/bin/setarguments.sh

setenv PROJDIR /home/rtaschle/bbn
setenv JXTADIR /home/rtaschle/jxta/platform/binding/java/lib
setenv JXTAPEER peer2
setenv JXTAPW jxtapeer

setenv JXTAJARLOC /home/rtaschle/jxta/platform/jxta.jar
#setenv JXTAJARLOC /home/rtaschle/bbn/testjxta.jar


setenv MYPROPERTIES "-Dorg.cougaar.domain.glm=org.cougaar.domain.glm.ldm.GLMDomain -Dorg.cougaar.domain.micro=org.cougaar.microedition.se.domain.Domain -Dorg.cougaar.microedition.ServerPort=$2 -Dorg.cougaar.core.cluster.SharedPlugInManager.watching=false -Dorg.cougaar.core.cluster.enablePublishException=false -Dorg.cougaar.core.cluster.showTraffic=false -Dorg.cougaar.core.cluster.idle.verbose=false -Dorg.cougaar.useBootstrapper=false -Dnet.jxta.tls.principal=$JXTAPEER -Dnet.jxta.tls.password=$JXTAPW"

setenv JXTALIBPATHS $JXTAJARLOC:$JXTADIR/log4j.jar:$JXTADIR/jxtasecurity.jar:$JXTADIR/beepcore.jar:$JXTADIR/cryptix-asn1.jar:$JXTADIR/cryptix32.jar:$JXTADIR/jxtaptls.jar:$JXTADIR/minimalBC.jar:$JXTADIR/org.mortbay.jetty.jar:$JXTADIR/servlet.jar

setenv COUGAARLIBPATHS $ALP_INSTALL_PATH/lib/core.jar:$ALP_INSTALL_PATH/lib/glm.jar:$ALP_INSTALL_PATH/lib/planserver.jar:$ALP_INSTALL_PATH/lib/webserver.jar:$ALP_INSTALL_PATH/lib/webtomcat.jar:$ALP_INSTALL_PATH/sys/xerces.jar:$ALP_INSTALL_PATH/sys/xalan.jar:$ALP_INSTALL_PATH/sys/servlet.jar:$ALP_INSTALL_PATH/sys/tomcat_core.jar:$ALP_INSTALL_PATH/sys/etomcat.jar:$ALP_INSTALL_PATH/sys/tomcat.jar:$ALP_INSTALL_PATH/sys/core_util.jar:$ALP_INSTALL_PATH/sys/tomcat_modules.jar:$ALP_INSTALL_PATH/sys/tomcat_util.jar:$ALP_INSTALL_PATH/sys/facade22.jar:$ALP_INSTALL_PATH/lib/csmart.jar:$ALP_INSTALL_PATH/sys/grappa1_2.jar

setenv LIBPATHS $PROJDIR/micro/data/cougaarMEdomain.jar:$PROJDIR/micro/data/props:$JXTALIBPATHS:$COUGAARLIBPATHS



java $MYPROPERTIES -classpath $LIBPATHS $MYCLASSES $MYARGUMENTS
