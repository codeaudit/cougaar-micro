#
# Makefile for CougaarME
# Depends on GNU Make.  Works for linux or Win2k.
#

##
## Set these three parameters to match your local installation
##

#PLATFORM=linux
#KVMROOT=/home/wwright/j2me_cldc
#JAVAC=javac

PLATFORM=win32
KVMROOT=c:/dev/j2me_cldc
JAVAC=c:/dev/jdk122/bin/javac


TOP=../src
TMPOUT=../src
OUTDIR=classes

PREVERIFY=$(KVMROOT)/bin/$(PLATFORM)/preverify
JAR=jar
KVMCLASSES=$(KVMROOT)/bin/common/api/classes

SRCFILES  = $(wildcard $(TOP)/org/cougaar/microedition/asset/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/node/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/io/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/kvm/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/ldm/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/plugin/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/plugin/test/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/shared/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/shared/tinyxml/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/shared/tinyxml/util/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/util/*.java)
SRCFILES += $(wildcard $(TOP)/tutorial/*.java)

SRCFILES += $(wildcard $(TOP)/gov/nasa/jpl/sensorweb/*.java)

OBJFILES = $(patsubst %.java,%.class,$(SRCFILES))


all: $(OBJFILES) cougaarme.jar

preverify: $(OBJFILES)
	$(PREVERIFY) -d $(OUTDIR) -classpath "$(KVMCLASSES)" \
              $(TMPOUT) || exit 1; \

cougaarme.jar: preverify 
	$(JAR) -cf cougaarme.jar \
	        -C $(OUTDIR) org \
	        -C $(OUTDIR) tutorial \
		-C $(OUTDIR) gov

PalmMain.prc: cougaarme.jar
	java -classpath $(KVMROOT)/tools/palm/src/palm.jar palm.database.MakePalmApp \
	-v -v -classpath $(OUTDIR) -bootclasspath "$(KVMCLASSES)" -networking \
	-JARtoPRC cougaarme.jar org.cougaar.microedition.kvm.PalmMain


#$(OBJFILES): %.class: %.java
#	$(JAVAC) -classpath "$(TMPOUT)" -bootclasspath "$(KVMCLASSES)" $< 

$(OBJFILES): 
	$(JAVAC) -classpath "$(TMPOUT)" -bootclasspath "$(KVMCLASSES)" $(SRCFILES)

clean: 
	rm -rf $(OBJFILES) $(OUTDIR)

$(OBJFILES): $(SRCFILES)

