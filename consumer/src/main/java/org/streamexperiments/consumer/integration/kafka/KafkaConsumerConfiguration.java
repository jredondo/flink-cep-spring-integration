package org.streamexperiments.consumer.integration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.streamexperiments.config.kafka.KafkaAppProperties;
import org.streamexperiments.models.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionOffset;

import java.util.Map;

/**
 * Gathers Spring Integration stuff related to Kafka integration conditionally loaded
 * depending on kafka.enabled property.
 * If that property is set to "true" the configuration will be loaded, otherwise it will not.
 * The expected usage is by means of a @ServiceActivator having "kafkaServiceChannel" as input channel.
 *
 * @see org.streamexperiments.consumer.ExampleConsumerImpl#kafkaToService(Update)
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>
 */
@Configuration
@ConditionalOnProperty(value="kafka.enabled", havingValue = "true")
public class KafkaConsumerConfiguration {

    /**
     * Kafka broker properties.
     *
     * @See {@link KafkaAppProperties}
     *
      */
    @Autowired
    public KafkaAppProperties properties;

    /**
     * Message payload transformer bean, from Json to Update objects.
     *
     * @return JsonToObjectTransformer
     */
    @Transformer(inputChannel = "fromKafka", outputChannel = "kafkaServiceChannel")
    @Bean
    public JsonToObjectTransformer kafkaTransformer() {
        return new JsonToObjectTransformer(Update.class);
    }

    /**
     * Default Kafka Consumer factory. Couple or properties configured as example.
     *
     * @param properties
     * @return ConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, String> kafkaConsumerFactory(KafkaProperties properties) {
        Map<String, Object> consumerProperties = properties
                .buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, this.properties.getGroupId());
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    /**
     * Listener container required by adapter {@link KafkaConsumerConfiguration#container(ConsumerFactory)}
     *
     * @param kafkaConsumerFactory As given by {@link KafkaConsumerConfiguration#kafkaConsumerFactory(KafkaProperties)}
     * @return KafkaMessageListenerContainer
     */
    @Bean
    public KafkaMessageListenerContainer<String, String> container(
            ConsumerFactory<String, String> kafkaConsumerFactory) {
        return new KafkaMessageListenerContainer<>(kafkaConsumerFactory,
                new ContainerProperties(new TopicPartitionOffset(this.properties.getTopic(), 0)));
    }

    /**
     * Message-driven channel adapter for the given Kafka topic.
     * It will use the container listener to drive out incoming messages.
     *
     * @param container Listener container required by the message-driven implementation.
     * @param fromKafka Direct channel coming in from Kafka topic.
     * @return KafkaMessageDrivenChannelAdapter
     */
    @Bean
    public KafkaMessageDrivenChannelAdapter<String, String>
    adapter(KafkaMessageListenerContainer<String, String> container, DirectChannel fromKafka) {
        KafkaMessageDrivenChannelAdapter<String, String> kafkaMessageDrivenChannelAdapter =
                new KafkaMessageDrivenChannelAdapter<>(container);
        kafkaMessageDrivenChannelAdapter.setOutputChannel(fromKafka);
        return kafkaMessageDrivenChannelAdapter;
    }

    /**
     * Direct channel into which the receiving adapter {@link KafkaConsumerConfiguration#adapter(KafkaMessageListenerContainer, DirectChannel)}
     * dumps received messages.
     *
     * @return DirectChannel
     */
    @Bean
    public DirectChannel fromKafka() {
        return new DirectChannel();
        //SubscribableKafkaChannel channel = new SubscribableKafkaChannel(template, factory, properties.getTopic());
        //channel.setGroupId(properties.getGroupId());
        //return channel;
    }


}
