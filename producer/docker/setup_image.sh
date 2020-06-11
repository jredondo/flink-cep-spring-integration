#!/bin/bash

REGISTRY_IP=192.168.99.147
TAG=$REGISTRY_IP/producer:latest
DOCKERFILE=producer/docker/Dockerfile

gradle shared:clean
gradle producer:clean
gradle producer:build

docker build -t $TAG -f $DOCKERFILE .
docker push $TAG

