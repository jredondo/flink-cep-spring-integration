FROM openjdk:8-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

ARG DEPENDENCY=producer/build

COPY ${DEPENDENCY}/libs/producer-0.0.jar .

CMD ["java","-jar","producer-0.0.jar", "--spring.kafka.bootstrap-servers=192.168.99.147:9092,192.168.99.148:9092,192.168.99.149:9092,192.168.99.150:9092"]
