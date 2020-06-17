package org.streamexperiments.consumer;

import org.streamexperiments.consumer.logic.Consumer;
import org.streamexperiments.consumer.logic.IntegrityVerifierConsumer;
import org.streamexperiments.consumer.logic.SimpleConsumer;
import org.streamexperiments.models.Update;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;

import java.util.ArrayList;
import java.util.List;

/**
 * Example consumer implementation showing decoupling of integration and logic by means of Spring Integration framework.
 * TCP integration is all abstracted behind the tcpServiceChannel, for which a @ServiceActivator must be provided.
 * Seemingly, Kafka integration is all abstracted behind the kafkaServiceChannel.
 * A @ServiceActivator for it must be provided.
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>.
 */
@Configuration
public class ExampleConsumerImpl {

    private List<Consumer> consumers = new ArrayList<>();

    public ExampleConsumerImpl() {
        consumers.add(new SimpleConsumer());
        consumers.add(new IntegrityVerifierConsumer());
    }

    private void process(Update update) {
        consumers.forEach(consumer -> consumer.consume(update));
    }

    @ServiceActivator(inputChannel = "kafkaServiceChannel")
    public void kafkaToService(Update update) {
        process(update);
    }

    @ServiceActivator(inputChannel = "tcpServiceChannel")
    public void tcpToService(Update update) {
        process(update);
    }


}
