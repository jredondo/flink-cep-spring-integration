#COMPOSE_FILE=/home/jredondo/Projects/flink-cep-spring-integration/docker/docker-producer-consumer-kafka-stack.yml
echo "Deploying Kafka ** BACK UP ** ..."
COMPOSE_FILE=/hosthome/jredondo/Projects/flink-cep-spring-integration/docker/docker-only-kafka-stack-backup.yml
docker-machine ssh manager1 REGISTRY_IP=192.168.99.147 MANAGER_IP=192.168.99.147 docker stack deploy -c $COMPOSE_FILE producer_consumer

