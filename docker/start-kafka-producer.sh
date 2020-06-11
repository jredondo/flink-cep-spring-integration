KAFKA_PATH=/home/jredondo/Projects/kafka_2.12-2.5.0
$KAFKA_PATH/bin/kafka-console-producer.sh --bootstrap-server 192.168.99.147:9092 --topic $1
