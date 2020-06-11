package org.streamexperiments.cep;

import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.time.Time;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.*;

import org.streamexperiments.cep.config.Configuration;
import org.streamexperiments.cep.operators.DefinedFunctions;
import org.streamexperiments.cep.serialization.UpdateDeserializationSchema;
import org.streamexperiments.cep.serialization.UpdateSerializationSchema;
import org.streamexperiments.models.Update;

public class CEP {

    private static Logger logger = LogManager.getLogger(CEP.class);
    private static final String CONFIG = "application.yaml";

    public static void main(String[] args) throws Exception {
        final Configuration.Kafka config = loadConfiguration().getKafka();
        final String KAFKA_TOPIC_PRODUCER = config.getProducerTopic();
        final String KAFKA_TOPIC_CONSUMER = config.getConsumerTopic();
        final Properties properties = loadProperties(config);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        UpdateDeserializationSchema deserializationSchema = new UpdateDeserializationSchema();
        UpdateSerializationSchema serializationSchema = new UpdateSerializationSchema(KAFKA_TOPIC_CONSUMER);

        DataStream<Update> incomingStream = env
                .addSource(new FlinkKafkaConsumer<Update>(KAFKA_TOPIC_PRODUCER, deserializationSchema, properties))
                .setParallelism(1)
                .flatMap(
                        new DefinedFunctions.LogFlatMapFunction())
                .setParallelism(1);

        DataStream<Collection<Update>> windowedStream15secs = incomingStream
                .keyBy((KeySelector<Update, String>) Update::getSender)
                .timeWindow(Time.seconds(15), Time.seconds(5))
                // Tumbling windows for testing
                //.timeWindow(Time.seconds(15))
                .apply(new DefinedFunctions.WindowedUpdatesFunction());


        FlinkKafkaProducer<Update> kafkaProducer = buildKafkaProducer(KAFKA_TOPIC_CONSUMER, properties, serializationSchema);

        DataStreamSink<Update> returnStream = incomingStream.addSink(kafkaProducer);

        returnStream.setParallelism(1);

        env.execute("Producer/Flink/Consumer Demonstration");
    }

    private static FlinkKafkaProducer<Update> buildKafkaProducer(String KAFKA_TOPIC_CONSUMER, Properties properties, UpdateSerializationSchema serializationSchema) {
        FlinkKafkaProducer<Update> kafkaProducer = new FlinkKafkaProducer<>(KAFKA_TOPIC_CONSUMER,
                serializationSchema,
                properties,
                FlinkKafkaProducer.Semantic.NONE);
        kafkaProducer.setWriteTimestampToKafka(true);
        return kafkaProducer;
    }

    private static Properties loadProperties(Configuration.Kafka config) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", config.getBootstrapServers());
        properties.setProperty("zookeeper.connect", config.getZookeeperConnect());
        properties.setProperty("group.id", config.getGroupId());
        return properties;
    }

    private static Configuration loadConfiguration() {
        Yaml yaml = new Yaml();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(CONFIG);
        return yaml.loadAs(in, Configuration.class);
    }
}
