package org.streamexperiments.cep.serialization;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.streamexperiments.models.Update;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateSerializationSchema implements KafkaSerializationSchema<Update> {
    private static Logger logger = LogManager.getLogger(UpdateSerializationSchema.class);

    private String topic;
    private ObjectMapper mapper = new ObjectMapper();

    public UpdateSerializationSchema(String topic) {
        super();
        this.topic = topic;

    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(Update update, Long timestamp) {
        byte[] blob = null;

        try {
            blob = mapper.writeValueAsBytes(update);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return new ProducerRecord<>(topic, blob);
    }
}
