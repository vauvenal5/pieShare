cd /pieShare
echo $1
# env runs the mvn command with the enviroment var LTTYPE set
# Dtest is set to force skiping the unit tests by not finding any
# DfailIfNoTests this is disabled to not fail after no unit tests where found
# Dit.test sets the loadTest to be executed
env LTTYPE=$1 mvn verify -Dtest=LoadTestIT -Dit.test=LoadTestIT -DfailIfNoTests=false