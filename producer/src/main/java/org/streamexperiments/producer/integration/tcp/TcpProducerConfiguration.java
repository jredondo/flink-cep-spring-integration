package org.streamexperiments.producer.integration.tcp;

import org.springframework.integration.channel.ExecutorChannel;
import org.streamexperiments.models.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.ip.IpHeaders;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Gathers the integration stuff related to Kafka messaging, conditionally loaded
 * depending on tcp.enabled property.
 * If that property is set to "true" the configuration will be loaded, otherwise it will not.
 * Notice the integration stuff is abstracted behind {@link org.streamexperiments.producer.integration.Gateway} to which
 * an extension is provided here with {@link TcpProducerConfiguration.Gateway}
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>
 */
@Configuration
@ConditionalOnProperty(value="tcp.enabled", havingValue = "true")
public class TcpProducerConfiguration {

    /**
     * Listening port for the TCP server connection opened in this configuration class
     */
    @Value("${tcp.port}")
    private int port;

    /**
     * Extension of {@link org.springframework.integration.annotation.Gateway}r related to TCP
     * messaging.
     *
     */
    @MessagingGateway(defaultRequestChannel = "toTcpJson")
    public interface Gateway extends org.streamexperiments.producer.integration.Gateway {
        void send(Update payload, @Header(IpHeaders.CONNECTION_ID) String topic) throws MessageHandlingException;
    }

    /**
     * Thread pool executor required by {@link TcpProducerConfiguration#toTcp(ThreadPoolTaskExecutor)}
     *
     * @return ThreadPoolTaskExecutor
     */
    @Bean
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(3);
        return threadPoolTaskExecutor;
    }

    /**
     * Message Channel to which messages coming in from the implemented producer logic.
     * For an example of this logic, hava a look at {@link org.streamexperiments.producer.logic.TestProducer}
     *
     * @param executor
     * @return MessageChannel
     */
    @Bean
    public MessageChannel toTcp(ThreadPoolTaskExecutor executor) {
        return new ExecutorChannel(executor);
    }

    /**
     * Message transformer getting messages sent from {@link TcpProducerConfiguration.Gateway}
     * and delivering them to the toTcp channel.
     *
     * @return ObjectToJsonTransformer
     */
    @Transformer(inputChannel = "toTcpJson", outputChannel = "toTcp")
    @Bean
    public ObjectToJsonTransformer transformer() {
        return new ObjectToJsonTransformer();
    }

    /**
     * Non-blocking TCP server connection factory.
     *
     * @return AbstractServerConnectionFactory
     */
    @Bean
    public AbstractServerConnectionFactory cf() {
        return new TcpNioServerConnectionFactory(port);
    }

    /**
     * Channel adapter listening for TCP connections and plugging in to the
     * producer's logic by means of the toTcp channel.
     *
     * @param cf TCP server connection factory
     * @param toTcp Message channel used as output.
     * @return TcpReceivingChannelAdapter
     */
    @Bean
    public TcpReceivingChannelAdapter outbound(AbstractServerConnectionFactory cf, MessageChannel toTcp) {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();

        adapter.setConnectionFactory(cf);
        adapter.setOutputChannel(toTcp);

        return adapter;
    }

    /**
     * Message handler that will deliver messages getting out from producer's logic
     * and received by this handler by means of the toTcp channel.
     *
     * @param cf TCP server connection factory
     * @return MessageHandler
     */
    @Bean
    @ServiceActivator(inputChannel = "toTcp")
    public MessageHandler sender(AbstractServerConnectionFactory cf) {
        try {
            TcpSendingMessageHandler tcpSendingMessageHandler = new TcpSendingMessageHandler();
            tcpSendingMessageHandler.setConnectionFactory(cf);
            return tcpSendingMessageHandler;
        } catch (MessageHandlingException e) {
            return null;
        }
    }
}

