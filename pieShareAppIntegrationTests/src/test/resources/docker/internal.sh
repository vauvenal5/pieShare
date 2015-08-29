cd /pieShare
# git pull not necessary any more because the image gets newly created before each loadTest run
# git pull
# env runs the mvn command with the enviroment var LTTYPE set
# Dtest is set to force skiping the unit tests by not finding any
# DfailIfNoTests this is disabled to not fail after no unit tests where found
# Dit.test sets the loadTest to be executed
echo $1
echo $2
env LTTYPE=$1 LTFILES=$2 mvn clean verify -Dit.test=LoadTestIT -DfailIfNoTests=false
#while true;
#do
#	sleep 1000
#done