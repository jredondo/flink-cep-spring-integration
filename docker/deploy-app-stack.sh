COMPOSE_FILE=/hosthome/jredondo/Projects/spring-integration-kafka-example/docker/docker-app-stack.yml
docker-machine ssh manager1 REGISTRY_IP=192.168.99.147 MANAGER_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer
