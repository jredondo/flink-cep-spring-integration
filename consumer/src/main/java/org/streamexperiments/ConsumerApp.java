package org.streamexperiments;

import org.streamexperiments.config.kafka.KafkaAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Spring Boot application. See bellow.
 *
 * @see org.streamexperiments.consumer.ExampleConsumerImpl
 */

@SpringBootApplication
@EnableConfigurationProperties(KafkaAppProperties.class)
public class ConsumerApp {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(ConsumerApp.class, args);

    }


}

