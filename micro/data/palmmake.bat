setlocal
set J2ME_PATH=d:/j2me/j2me_cldc
cd ..
del /s /q classes
mkdir classes
set CLASSPATH=classes;%J2ME_PATH%/api/classes.zip
rem %J2ME_PATH%\bin\preverify -classpath %CLASSPATH% -d classes tmpclasses
d:\cldc\j2me\bin\win32\preverify -classpath %CLASSPATH% -d classes tmpclasses
set JAVA_COMPILER=NONE 
set CLASSPATH=classes
cd classes
jar cf ..\palmnode.jar org
cd ..
java -classpath %J2ME_PATH%/palm/tools/palm/src/palm.jar palm.database.MakePalmApp -v -v -classpath classes -bootclasspath %J2ME_PATH%/api/classes.zip -networking -JARtoPRC palmnode.jar org.cougaar.microedition.kvm.PalmMain
cd data
