FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

ARG DEPENDENCY=consumer/build

COPY ${DEPENDENCY}/libs/consumer-0.0.jar .

ENTRYPOINT ["java","-jar","consumer-0.0.jar", "--spring.kafka.bootstrap-servers=192.168.99.147:9092"]
