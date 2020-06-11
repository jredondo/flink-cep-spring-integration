#!/bin/bash

REGISTRY_IP=192.168.99.147
TAG=$REGISTRY_IP/consumer:latest
DOCKERFILE=consumer/docker/Dockerfile

gradle shared:clean
gradle consumer:clean
gradle consumer:build

docker build -t $TAG -f $DOCKERFILE .
docker push $TAG

