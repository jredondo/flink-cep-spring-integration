#COMPOSE_FILE=/home/jredondo/Projects/flink-cep-spring-integration/docker/docker-producer-consumer-kafka-stack.yml
COMPOSE_FILE=/hosthome/jredondo/Projects/flink-cep-spring-integration/docker/docker-producer-consumer-kafka-stack.yml
docker-machine ssh manager1 REGISTRY_IP=192.168.99.147 MANAGER_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer
#eval "$(docker-machine env manager1)"
#env REGISTRY_IP=192.168.99.147 MANAGER_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer
#eval "$(docker-machine env -u)"

