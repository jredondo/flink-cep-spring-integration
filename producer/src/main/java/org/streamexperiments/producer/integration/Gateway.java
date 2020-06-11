package org.streamexperiments.producer.integration;

import org.streamexperiments.models.Update;
import org.springframework.messaging.MessageHandlingException;

/**
 * Functional interface that is thought to be extended by the any of the implemented
 * integration mechanisms.
 *
 * @see org.streamexperiments.producer.integration.tcp.TcpProducerConfiguration.Gateway
 * @see org.streamexperiments.producer.integration.kafka.KafkaProducerConfiguration.Gateway
 * @see Sender
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>
 */
@FunctionalInterface
public interface Gateway {
    void send(Update data, String connectionId) throws MessageHandlingException;
}
