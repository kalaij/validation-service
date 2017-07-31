FROM openjdk:8-jre-alpine

WORKDIR /opt
ARG JAR_FILE

ENV MONGO_URI=mongodb://localhost:27017
ENV RABBIT_HOST=localhost
ENV RABBIT_PORT=5672

COPY ${JAR_FILE} ./validator-coordinator.jar

CMD java -jar validator-coordinator.jar --spring.data.mongodb.uri=$MONGO_URI --spring.rabbitmq.host=$RABBIT_HOST --spring.rabbitmq.port=$RABBIT_PORT
