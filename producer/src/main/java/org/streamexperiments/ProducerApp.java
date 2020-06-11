package org.streamexperiments;

import org.streamexperiments.config.kafka.KafkaAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(KafkaAppProperties.class)
public class ProducerApp {

    public static void main(String[] args) throws Exception {
        SpringApplication springApplication =
                new SpringApplicationBuilder()
                        .sources(ProducerApp.class)
                        .web(WebApplicationType.NONE)
                        .build();

        springApplication.run(args);


    }
}
