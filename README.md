LinuxJavaFixes
============

Simple javaagent to fix problems in linux with non latin hotkeys in gui java applications.

Aimed to walkaround bug  with java gui apps: "Hotkeys not functional in non-latin keyboard layout in 13.10 and 14.04" https://bugs.launchpad.net/unity/+bug/1226962

============
A. Swing java apps (intellij idea, oracle sql developer etc)

copy to any directory 2 files :

LinuxJavaFixes-1.0.0-SNAPSHOT.jar

javassist-3.12.1.GA.jar

add 

-javaagent:[path to directory with jar files]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar

to java run string

Examples

-soapui

  add line to soapui.sh

  JAVA_OPTS="$JAVA_OPTS java -javaagent:[path to directory with jar files]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar

-oracle sqldeveloper

  add line to sqldeveloper/ide/bin/jdk.conf

  AddVMOption -javaagent:[path to directory with jar files]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar

-intellij idea

  add line to idea64.vmoptions or idea.vmoptions

  -javaagent:[path to directory with jar files]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar

============

B. Eclipse

copy to any directory 2 files :

LinuxJavaFixes-1.0.0-SNAPSHOT.jar

javassist-3.12.1.GA.jar

add following line to eclipse.ini

-javaagent:[path to directory with jar files]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar=swt


============

Advanced part

============
C. Modify kaybindings for swing apps in case non russian layout
 
if you want another mapping you can create it by yourself :

run any app with java vm option  -javaagent:[path]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar=print

after that utily begin print to console entered symbol codes using format

XKeysymPatchAgent.keysym=[hex code]

then create file using format [hex code]=[latin code of the same button]

for example

6ca=Q

6c3=W

etc

and replace hex codes wuth yours

use following option to run app with custom mapping :

-javaagent:[path to directory with jar files]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar=[your mapping file]

============
D. Modify keybindings for swt in case non russian layout

add following line to eclipse.ini 

-javaagent:[path]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar=swt:print

then grab codes and create properties file with mapping

[your locale key]=[latin key]

then run eclipse wuth following config

-javaagent:[path]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar=swt:[path to your mapping file]


