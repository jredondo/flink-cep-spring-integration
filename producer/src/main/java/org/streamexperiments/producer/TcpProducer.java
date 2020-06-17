package org.streamexperiments.producer;

import org.springframework.beans.factory.annotation.Value;
import org.streamexperiments.producer.integration.Sender;
import org.streamexperiments.producer.integration.tcp.TcpProducerConfiguration;
import org.streamexperiments.producer.logic.TestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Example TCP producer using decoupled integration ({@link TcpProducerConfiguration.Gateway})
 * and logic ({@link TestProducer}).
 */
@Component
@ConditionalOnProperty(value="tcp.enabled", havingValue = "true")
public class TcpProducer implements ApplicationListener<TcpConnectionOpenEvent> {

    @Value("${data-monitor.producer-uuid}")
    private String producerUUID;

    @Value("${data-monitor.throughput}")
    private long throughput;

    @Value("${data-monitor.N}")
    private long N;

    @Autowired
    private TcpProducerConfiguration.Gateway tcpGateway;

    private TestProducer producer;

    @PostConstruct
    private void init() {
        producer = new TestProducer(throughput, N, producerUUID);
    }

    /**
     * Strats producer once a TCP connection open event has been caught.
     *
     * @param event TcpConnectionOpenEvent
     */
    @Override
    public void onApplicationEvent(TcpConnectionOpenEvent event) {
        producer.addSender(new Sender(tcpGateway, event.getConnectionId()));
        producer.start();
    }

    @PreDestroy
    public void onExit() {
        producer.shutdown();
    }
}
