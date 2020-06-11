package org.streamexperiments.consumer.integration.tcp;

import org.streamexperiments.models.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.*;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.messaging.MessageChannel;

/**
 * Gathers Spring Integration stuff related to TCP integration conditionally loaded
 * depending on tcp.enabled property.
 * If that property is set to "true" the configuration will be loaded, otherwise it will not.
 * The expected usage is by means of a @ServiceActivator having "tcpServiceChannel" as input channel.
 *
 * @see org.streamexperiments.consumer.ExampleConsumerImpl#tcpToService(Update)
 *
 * @author Jorge Redondo Flames <jorge.redondo@gmail.com>
 */
@Configuration
@ConditionalOnProperty(value="tcp.enabled", havingValue = "true")
public class TcpConsumerConfiguration {

    /**
     * Message payload transformer bean, from Json to Update objects.
     *
     * @return JsonToObjectTransformer
     */
    @Transformer(inputChannel = "tcpIn", outputChannel = "tcpServiceChannel")
    @Bean
    public JsonToObjectTransformer transformer() {
        return new JsonToObjectTransformer(Update.class);
    }

    /**
     * Non-blocking Client TCP connection. Conditionally loaded depending on tcp.mode property.
     * It loads with tcp.mode equals to "client".
     *
     * @return AbstractClientConnectionFactory
     */
    @Bean
    @ConditionalOnProperty(value="tcp.mode", havingValue = "client")
    public AbstractClientConnectionFactory clientConnectionFactory(@Value("${tcp.host}") String host, @Value("${tcp.port}") int port) {
        return new TcpNioClientConnectionFactory(host, port);
    }

    /**
     * Receiving channel adapter for TCP client connection.
     * Has {@link TcpConsumerConfiguration#tcpIn()} as output channel.
     * And, of course, it depends on the same condition as {@link TcpConsumerConfiguration#clientConnectionFactory(String, int)}
     *
     * @param cf (Non-IO) Client TCP connection
     * @return TcpReceivingAdapter
     */
    @Bean
    @ConditionalOnProperty(value="tcp.mode", havingValue = "client")
    public TcpReceivingChannelAdapter clientInbound(AbstractClientConnectionFactory cf) {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();

        adapter.setConnectionFactory(cf);
        adapter.setOutputChannel(tcpIn());

        adapter.setClientMode(true);

        return adapter;
    }

    /**
     * Non-blocking Server TCP connection. Conditionally loaded depending on tcp.mode property.
     * It loads with tcp.mode equals to "server".
     *
     * @return AbstractServerConnectionFactory
     */
    @Bean
    @ConditionalOnProperty(value="tcp.mode", havingValue = "server")
    public AbstractServerConnectionFactory serverConnectionFactory(@Value("${tcp.port}") int port) {
        return new TcpNioServerConnectionFactory(port);
    }


    /**
     * Receiving channel adapter for TCP client connection.
     * Has {@link TcpConsumerConfiguration#tcpIn()} as output channel.
     * And, of course, it depends on the same condition as {@link TcpConsumerConfiguration#serverConnectionFactory(int)}
     *
     * @param cf (Non-IO) Client TCP connection
     * @return TcpReceivingAdapter
     */
    @Bean
    @ConditionalOnProperty(value="tcp.mode", havingValue = "server")
    public TcpReceivingChannelAdapter serverInbound(AbstractServerConnectionFactory cf) {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();

        adapter.setConnectionFactory(cf);
        adapter.setOutputChannel(tcpIn());

        return adapter;
    }

    /**
     * Direct channel into which the receiving adapter {@link TcpConsumerConfiguration#serverInbound(AbstractServerConnectionFactory)}
     * dumps received messages.
     *
     * @return MessageChannel
     */
    @Bean
    public MessageChannel tcpIn() {
        return new DirectChannel();
    }
}

