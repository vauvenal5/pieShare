if [[ $# != 1 ]]; then
	echo "Wrong number of parameters!"
	exit
fi

nodes=$1

if [[ $nodes -lt 2 ]]; then
	echo "You need at least two nodes!"
	exit
fi

#add image removal!!! or move git clone to internal file

cd loadTest
docker build -t vauvenal5/master .
docker build -t vauvenal5/slave .

#start all slave nodes
for (( i = 1; i < $nodes; i++ )); do
	docker run vauvenal5/slave "slave" &
done

#start the master node
docker run vauvenal5/master "master"