This is a README file for the top level micro directory...

Welcome to the world of Cougaar MicroEdition.

Some things to help you get started...

CougaarME is currently built around 2 Java platforms:

1) The Dallas Semiconductor TINI (Tiny InterNet Interface) Board. 
The TINI runs a limited version of Java 1.1. Information on the 
TINI board, its Engineering Platform (transition module) and 
sensor/addon "iButtons" can be found at:
www.systronix.com/
www.ibutton.com/
www.dalsemi.com/
www.smartsc.com/tini/

We currently build our TINI executables from either a JBuilder (v3 or v4
using a TINI conversion/FTP plugin) running under NT or a javac 1.1 under 
same. There is a conversion process needed to consolidate ".class" files 
into a ".tini" file digestable by the TINI. This is explained at the above 
web sites.

2) Suns Java 2 MicroEdition Connection Limited Device Configuration 
(J2ME-CLDC) also known as KVM (for its Kilobyte footprint Virtual 
Machine). J2ME is a limited and mutated version of Java 2. It runs 
on Windows or Unix/Linux platforms and Palm pilots as well. Information 
about KVM can be gleened from the following web sites:
www.kvmworld.com/
www.javasoft.com/j2me/?frontpage-javaplatform
java.sun.com/j2me
java.sun.com/people/shommel/KVM/

We currently build our KVM executables under Linux using the standard SUN
javac 1.3 distribution.  We use a modified KVM distribution from kvmworld, 
not the vanilla distribution from SUN which doesn't work under Linux. All Palm 
development to date has been done under a Palm Emulator which is part of the
distribution.

Within this directory (micro) you will find:
Directories:
	configs where ".ini" files to seed the Big Cougaar Node, and
		where ".xml" files to seed the Micro Cougaars live.
		Also Node.bat to start Big Cougaar.
	
	data	where ".jpr" files to setup JBuilder (3 or 4) live.

	docs	where you found this file and will find other docs.

	src	where the source lives.


mtiberio@bbn.com
