BASE_PATH=/hosthome/jredondo/Projects/spring-integration-producer-consumer
COMPOSE_FILE=$BASE_PATH/docker/docker-cep-stack.yml
docker-machine ssh manager1 REGISTRY_IP=192.168.99.147 MANAGER_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer
