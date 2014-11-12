#!/bin/sh
jars=/home/vijay/ProgramAnalysis/Blinky/jars
bin=/home/vijay/ProgramAnalysis/Blinky/bin
snitchMF=SnitchManifest
dataMF=DataManifest
controlMF=Manifest

cd $jars
pwd
## removing the current version of rt.jar(doesn't matter if instrumented or not).
# cp /usr/lib/jvm/java-6-openjdk-i386/jre/lib/rt.jar rt.jar
## installing a non-instrumented version of rt.jar
# sudo cp /home/vijay/TestSubjects/instrumentedrt/rt.jar /usr/lib/jvm/java-6-openjdk-i386/jre/lib
cd $bin
jar cvfm $jars/blinky.jar $jars/Manifest org/spideruci
jar_result_id=$?
if [ $jar_result_id ] 
then
	jar_message="Created"
else
	jar_message="... errr ... Something went worng."
fi

## installing the original version of rt.jar
# sudo cp rt.jar /usr/lib/jvm/java-6-openjdk-i386/jre/lib
# cp_result_id=$?
# if [ $cp_result_id ] 
# then
# 	message="Copied"
# else
# 	message="... errr ... Something went worng."
# fi

clear
pwd
echo blinky.jar $jar_message {jar_result_id - $jar_result_id}
echo blinky.jar $message {cp_result_id - $cp_result_id}