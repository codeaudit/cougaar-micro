#
# Makefile for CougaarME
# Depends on GNU Make
#

TOP=../src
TMPOUT=../src

PLATFORM=win32
KVMROOT=c:/dev/j2me/kvm
JAVAC=c:/dev/jdk122/bin/javac


PREVERIFY=$(KVMROOT)/bin/$(PLATFORM)/preverify
JAR=jar
KVMCLASSES=$(KVMROOT)/bin/common/api/classes
KJAVACLASSES=$(KVMROOT)/bin/kjava/api/classes

SRCFILES  = $(wildcard $(TOP)/org/cougaar/microedition/asset/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/cluster/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/io/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/kvm/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/ldm/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/plugin/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/shared/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/shared/tinyxml/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/shared/tinyxml/util/*.java)
SRCFILES += $(wildcard $(TOP)/org/cougaar/microedition/util/*.java)

OBJFILES = $(patsubst %.java,%.class,$(SRCFILES))


all: $(OBJFILES) cougaarme.jar

preverify:
	$(PREVERIFY) -d classes -classpath "$(KVMCLASSES);$(KJAVACLASSES)" \
              $(TMPOUT) || exit 1; \

cougaarme.jar: preverify 
	$(JAR) -cf cougaarme.jar -C classes org

PalmMain.prc: cougaarme.jar
	java -classpath $(KVMROOT)/tools/palm/src/palm.jar palm.database.MakePalmApp \
	-v -v -classpath classes -bootclasspath "$(KVMCLASSES);$(KJAVACLASSES)" -networking \
	-JARtoPRC cougaarme.jar org.cougaar.microedition.lvm.PalmMain


$(OBJFILES): %.class: %.java
	$(JAVAC) -classpath "$(TMPOUT)" -bootclasspath "$(KVMCLASSES);$(KJAVACLASSES)" $< 

clean: 
	rm -rf $(OBJFILES)

$(OBJFILES): $(SRCFILES)

