# activemq-artemis

Demonstrates using Apache Camel and Spring Boot to send and receive messages to an ActiveMQ Artemis broker.

To run, first **download and start an ActiveMQ broker, running on port 61616**. Configure the credentials as `myuser`/`mypassword`. I do this by running Artemis in a container with podman/docker:

    podman run -it --rm -p 8161:8161 -p 61616:61616 -p 5672:5672 \
        -e ARTEMIS_USERNAME=myuser -e ARTEMIS_PASSWORD=mypassword \
        vromero/activemq-artemis:2.11.0-alpine

Then run the app:

    mvn clean spring-boot:run

This will send a message to ActiveMQ every 5 seconds, consume the message and write it to the console:

```
2020-09-13 13:21:55.492  INFO 13679 --- [           main] x.t.c.s.artemis.CamelArtemisApplication  : Started CamelArtemisApplication in 2.46 seconds (JVM running for 2.751)
2020-09-13 13:21:56.601  INFO 13679 --- [er[HELLO.WORLD]] route2                                   : Received a message - HELLO from Camel!
2020-09-13 13:22:01.509  INFO 13679 --- [er[HELLO.WORLD]] route2                                   : Received a message - HELLO from Camel!
```

## Configuring the connection to ActiveMQ

In this example, Camel's JMS component uses the `ConnectionFactory` which is [automatically created by Spring Boot](https://docs.spring.io/spring-boot/docs/2.2.4.RELEASE/reference/htmlsingle/#boot-features-artemis), using the properties set in `spring.artemis.*`. 

If you want to manually configure the `ConnectionFactory` and JMS component instead, you can add something like this to the Spring Boot bootstrap class `CamelArtemisApplication`:

```java
@Bean
public JmsComponent jmsComponent() throws JMSException {
    // Create the connectionfactory which will be used to connect to Artemis
    ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
    cf.setBrokerURL("tcp://localhost:61616");
    cf.setUser("admin");
    cf.setPassword("admin");

    // Create the Camel JMS component and wire it to our Artemis connectionfactory
    JmsComponent jms = new JmsComponent();
    jms.setConnectionFactory(cf);
    return jms;
}
```

## Running an integration test

This example has an **integration test** for you to use. It uses [Testcontainers][1] to run a containerised test instance of ActiveMQ Artemis, configures Camel to talk to the test instance, and then verifies that a message arrives in a queue on the broker.

See the [CamelArtemisApplicationIT class](./src/test/java/xyz/tomd/cameldemos/springboot/artemis/CamelArtemisApplicationIT.java). 

**Ensure you have Docker installed**, then run the test using:

    mvn clean test -Dtest=CamelArtemisApplicationIT

[1]: https://www.testcontainers.org/
