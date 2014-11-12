jars=/home/vijay/ProgramAnalysis/StaticAnalysis/jars
bin=/home/vijay/ProgramAnalysis/StaticAnalysis/bin
snitchMF=SnitchManifest
dataMF=DataManifest
controlMF=Manifest2

cd $jars
pwd
## removing the current version of rt.jar(doesn't matter if instrumented or not).
# cp /usr/lib/jvm/java-6-openjdk-i386/jre/lib/rt.jar rt.jar
## installing a non-instrumented version of rt.jar
# sudo cp /home/vijay/TestSubjects/instrumentedrt/rt.jar /usr/lib/jvm/java-6-openjdk-i386/jre/lib
cd $bin
jar cvfm $jars/control.jar $jars/Manifest2 edu/uci/spiderlab
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
echo control.jar $jar_message {jar_result_id - $jar_result_id}
echo control.jar $message {cp_result_id - $cp_result_id}