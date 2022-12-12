# Lesson 3
We have our simple Rest API exposing invoices from the previous labs:

![Hexagonal Architecture Diagram](../../lesson%201/lab/diagram1.png)

### Publish events

To connect to kafka we need a kafka client. Therefore, we need to add a few dependencies.
```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>
</dependencies>
```

To send events, we can use the `KafkaTemplate` provided by spring.
```java
package com.mih.training.invoice.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class MyPublisher {
    private static final Logger log = LoggerFactory.getLogger(MyPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MyPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<Void> sendToTopic(String message) {
        log.info("sendToTopic - message: {}", message);
        return kafkaTemplate.send("mytopic", message)
                .thenAccept(res -> {
                    log.info("sendToTopic - message: {}, done", message);
                }).exceptionally(err -> {
                    log.warn("sendToTopic - message: {}, error: {}", message, err);
                    return null;
                });
    }
}

```

We can test our service with an embedded kafka instance.
```java
package com.mih.training.invoice.events;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.ExecutionException;

@EmbeddedKafka(topics = "mytopic")
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}", classes = {MyPublisher.class, KafkaAutoConfiguration.class})
class MyPublisherTest {

    @Autowired
    MyPublisher myPublisher;

    @Test
    public void test() throws ExecutionException, InterruptedException {
        myPublisher.sendToTopic("test").get();
    }

}

```
Consume events:

```java
package com.mih.training.invoice.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class MyConsumer {
    private static final Logger log = LoggerFactory.getLogger(MyConsumer.class);

    @KafkaListener(groupId = "group-1", topics = "mytopic")
    public void onMessage(ConsumerRecord<String, String> message) {
        log.info("onMessage -  received message: " + message);
    }
}

```

```text
2022-12-12T14:51:32.839+02:00  INFO 7153 --- [ntainer#0-0-C-1] c.m.training.invoice.events.MyConsumer   : onMessage -  received message: ConsumerRecord(topic = mytopic, partition = 0, leaderEpoch = 0, offset = 0, CreateTime = 1670849477934, serialized key size = -1, serialized value size = 4, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = test)
```
### Docker

We are using `docker-compose` to start our kafka instance locally.

Create `docker-compose.yaml` in a folder (i.e: sandbox) with 3 services defined:
```yaml
version: '3'
services:
  kafka-broker:
    image: confluentinc/cp-kafka:5.5.2
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      - KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka-broker:9092
      - KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
      - KAFKA_ADVERTISED_HOST_NAME=kafka-broker
      - KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1
      - KAFKA_LOG4J_ROOT_LOGLEVEL=ERROR
      - KAFKA_LOG4J_LOGGERS=org.apache.zookeeper=ERROR,org.apache.kafka=ERROR,kafka=ERROR,kafka.cluster=ERROR,kafka.controller=ERROR,kafka.coordinator=ERROR,kafka.log=ERROR,kafka.server=ERROR,kafka.zookeeper=ERROR,state.change.logger=ERROR
    depends_on:
      - zookeeper

  zookeeper:
    image: confluentinc/cp-zookeeper:5.5.2
    container_name: kafka-zookeeper
    expose:
      - "2181"
    environment:
      - ZOOKEEPER_CLIENT_PORT=2181
  schema-registry:
      image: confluentinc/cp-schema-registry:5.3.1
      depends_on:
        - zookeeper
        - kafka-broker
      ports:
        - "8091:8091"
      environment:
        SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL: zookeeper:2181
        SCHEMA_REGISTRY_HOST_NAME: schema-registry
        SCHEMA_REGISTRY_LISTENERS: http://schema-registry:8091
      extra_hosts:
        - "localhost:127.0.0.1"
```

In our app create a new file named: `Dockerfile` that starts our application.

```text
FROM amazoncorretto:17-al2022-jdk

COPY target/lib /app/lib
COPY target/classes /app/classes

EXPOSE 8080 8080

ENTRYPOINT ["java", "-cp", "/app/classes:/app/lib/*", "<main-class-full-name>"]
```

Create a `Run Configuration` to start the application as a container connected to the docker sandbox.
Don't forget to add `--network sandbox_default` to allow your new container to connect to kafka.

_Depending on the folder name you used, the network may be called differently._

### Configuration

We run the application with the docker profile and use docker specific properties from `application-docker.yaml`
```yaml
spring:
  kafka:
    properties:
      schema.registry.url: http://schema-registry:8091
    producer:
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
      properties:
        schema.registry.url: http://schema-registry:8091
    bootstrap-servers: kafka:9092
```

### Avro messages
We can send Avro messages from our application. To do this, instead of sending simple string payloads we switch to a new serializer.

```xml
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-streams-avro-serde</artifactId>
            <version>${confluent.version}</version>
        </dependency>
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-schema-registry-client</artifactId>
            <version>${confluent.version}</version>
        </dependency>
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-avro-serializer</artifactId>
            <version>${confluent.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.avro</groupId>
            <artifactId>avro</artifactId>
            <version>1.11.0</version>
        </dependency>

        <repositories>
        <repository>
            <id>confluent</id>
            <url>https://packages.confluent.io/maven</url>
        </repository>
        </repositories>
```

Let's create our first avro message: `src/main/resources/avro/mytopic/InvoiceEvent.avsc`

```json
{
    "doc": "Internal representation of a paid invoice",
    "type": "record",
    "name": "MyEvent",
    "namespace": "com.mih.training.invoice.events",
    "fields": [
        {
            "doc": "Invoice Number",
            "name": "invoiceNumber",
            "default": null,
            "type": [
                "null",
                "string"
            ]
        },
        {
            "doc": "Invoice Date",
            "name": "invoiceDate",
            "default": null,
            "type": [
                "null",
                "long"
            ]
        },
        {
            "doc": "Total Amount Due",
            "name": "amoundDue",
            "default": null,
            "type": [
                "null",
                "double"
            ]
        }
    ]
}

```
To be able to use Java classes created from the avro files, we can use a plugin
```xml

<plugins>
        <plugin>
        <groupId>org.apache.avro</groupId>
        <artifactId>avro-maven-plugin</artifactId>
        <version>1.9.0</version>
        <executions>
            <execution>
                <phase>pre-clean</phase>
                <goals>
                    <goal>schema</goal>
                </goals>
                <configuration>
                    <sourceDirectory>${project.basedir}/src/main/resources/avro</sourceDirectory>
                    <includes>
                        <include>**/*.avsc</include>
                    </includes>
                    <outputDirectory>${project.basedir}/src/main/java</outputDirectory>
                    <imports>
                        <import>
                            ${project.basedir}/src/main/resources/avro/mytopic/InvoiceEvent.avsc
                        </import>
                    </imports>
                    <stringType>String</stringType>
                </configuration>
            </execution>
        </executions>
        </plugin>
</plugins>
```

Run `mvn clean install`.

Check `com.mih.training.invoice.events.InvoiceEvent` was generated.

Update publisher and consumer with the new payload type.

More about transactions [here](https://docs.spring.io/spring-kafka/reference/html/#ex-jdbc-sync)
