package org.streamexperiments.producer.integration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.streamexperiments.config.kafka.KafkaAppProperties;
import org.streamexperiments.models.Update;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

/**
 * Gathers the integration stuff related to Kafka messaging, conditionally loaded
 * depending on kafka.enabled property.
 * If that property is set to "true" the configuration will be loaded, otherwise it will not.
 * Notice the integration stuff is abstracted behind {@link org.streamexperiments.producer.integration.Gateway} to which
 * an extension in provided here with {@link KafkaProducerConfiguration.Gateway}
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>
 */
@Configuration
@ConditionalOnProperty(value="kafka.enabled", havingValue = "true")
public class KafkaProducerConfiguration {

    /**
     * Extension of {@link org.springframework.integration.annotation.Gateway}r related to Kafka
     * messaging.
     *
     */
    @MessagingGateway(defaultRequestChannel = "toJson")
    public interface Gateway extends org.streamexperiments.producer.integration.Gateway {
        void send(Update payload, @Header(KafkaHeaders.TOPIC) String topic) throws MessageHandlingException;
    }

    /**
     * Executor channel for multi-threaded message handling.
     *
     * @param executor
     * @return MessageChannel
     */
    @Bean
    public MessageChannel toKafka(ThreadPoolTaskExecutor executor) {
        return new ExecutorChannel(executor);
    }

    /**
     * Kafka producer factory. Couple of properties set up as example.
     *
     * @param kafkaProperties
     * @return ProducerFactory
     */
    @Bean
    public ProducerFactory<String, String> kafkaProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        producerProperties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    /**
     * Message handler sending to Kafka topic by means of the provided Kafka template.
     *
     * @param kafkaTemplate
     * @param properties
     * @return MessageHandler
     */
    @ServiceActivator(inputChannel = "toKafka")
    @Bean
    public MessageHandler handler(KafkaTemplate<String, String> kafkaTemplate, KafkaAppProperties properties) {

        KafkaProducerMessageHandler<String, String> handler =
                new KafkaProducerMessageHandler<>(kafkaTemplate);
        handler.setMessageKeyExpression(new LiteralExpression(properties.getMessageKey()));
        return handler;
    }


    /**
     * Message payload transformer bean, from Json to Update objects.
     *
     * @return JsonToObjectTransformer
     */
    @Transformer(inputChannel = "toJson", outputChannel = "toKafka")
    @Bean
    public ObjectToJsonTransformer transformer() {
        return new ObjectToJsonTransformer();
    }

    /**
     * Kafka template used on message sending.
     *
     * @param kafkaProducerFactory
     * @return KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    /**
     * Spring's kafka topic implementation bean.
     *
     * @param properties
     * @return NewTopic
     */
    @Bean
    public NewTopic topic(KafkaAppProperties properties) {
        return new NewTopic(properties.getTopic(), 1, (short) 1);
    }
}
