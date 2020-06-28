KAFKA_PATH=/home/jredondo/Projects/kafka_2.12-2.5.0
$KAFKA_PATH/bin/kafka-console-consumer.sh --bootstrap-server 192.168.99.147:9094,192.168.99.148:9094,192.168.99.149:9094,192.168.99.150:9094 --topic $1 --from-beginning
