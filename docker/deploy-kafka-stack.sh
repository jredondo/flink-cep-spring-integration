COMPOSE_FILE=/hosthome/jredondo/Projects/data_monitor/docker/docker-kafka-stack.yml
docker-machine ssh manager1 docker stack deploy -c $COMPOSE_FILE kafka
