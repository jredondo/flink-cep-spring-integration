#!/bin/bash
function startMachine {
    docker-machine stop $1
    docker-machine start $1
    docker-machine ssh $1 sudo cp $CERT /usr/local/share/ca-certificates/
    docker-machine ssh $1 sudo update-ca-certificates
}

CERT=/hosthome/jredondo/Projects/data_monitor/docker/certs/domain.crt

startMachine manager1 
startMachine worker0
startMachine worker1
#startMachine worker2



    
