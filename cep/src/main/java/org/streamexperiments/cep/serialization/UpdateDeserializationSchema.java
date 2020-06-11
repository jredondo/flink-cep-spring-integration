package org.streamexperiments.cep.serialization;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.streamexperiments.models.Update;

public class UpdateDeserializationSchema implements KafkaDeserializationSchema<Update> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isEndOfStream(Update nextElement) {
        return false;
    }

    @Override
    public Update deserialize(ConsumerRecord<byte[], byte[]> record) throws Exception {
        return mapper.readValue(record.value(), Update.class);
    }


    @Override
    public TypeInformation<Update> getProducedType() {
        return TypeInformation.of(new TypeHint<Update>() {
        });
    }
}
