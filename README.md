LinuxJavaFixes
=============

Simple javaagent to fix problems in linux with java.

1. Aimed to resolve bug  with java gui apps: "Hotkeys not functional in non-latin keyboard layout in 13.10 and 14.04" https://bugs.launchpad.net/unity/+bug/1226962

My patch just instruments jdk class XKeysym - adds mapping from non latin layout to latin

This patch uses javassist to instrument class, so dont forget copy javassist lib from build to the same directory as LinuxJavaFixes-1.0.0-SNAPSHOT.jar

To run any java application just add

-javaagent:target/LinuxJavaFixes-1.0.0-SNAPSHOT.jar

to java run string

1.soapui

add line to soapui.sh

JAVA_OPTS="$JAVA_OPTS java -javaagent:[path]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar

2.oracle sqldeveloper

add line to sqldeveloper/ide/bin/jdk.conf

AddVMOption -javaagent:[path]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar

3. intellij idea

add line to idea64.vmoptions or idea.vmoptions

-javaagent:[path]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar

out of the box this app use russian to latin mapping

if you want another mapping you can create it by yourself :

run any app with java vm option  -javaagent:[path]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar=print

after that this utily begin print to console codes of entered symbols using format

XKeysymPatchAgent.keysym=[hex code]

then create file using format [hex code]=[latin code of the same button]

for example

6ca=Q

6c3=W

6d5=E

6cb=R

6c5=T

6ce=Y

6c7=U

6db=I

6dd=O

6da=P

6c6=A

6d9=S

6d7=D

6c1=F

6d0=G

6d2=H

6cf=J

6cc=K

6c4=L

6d1=Z

6de=X

6d3=C

6cd=V

6c9=B

6d4=N

6d8=M

and replace hex codes wuth yours

to use custom mapping use following option to run app :

-javaagent:[path]/LinuxJavaFixes-1.0.0-SNAPSHOT.jar=[your mapping file]


