cd /pieShare
echo $1
env LTTYPE=$1 mvn -Dit.test=LoadTestLT verify # env runs the mvn command with the enviroment var LTTYPE set