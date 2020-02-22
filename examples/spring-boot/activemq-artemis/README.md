# activemq-artemis

Demonstrates connecting to an ActiveMQ Artemis broker using Camel on Spring Boot.

To run, first **download and start an ActiveMQ broker, running on port 61616**. Then:

    mvn clean spring-boot:run

This will send a message to ActiveMQ every 5 seconds, consume the message and write it to the console.

**NOTE:** This application works because Spring creates a connection factory from the properties set in `spring.artemis.*`. If you want to configure the connection factory and JMS component yourself, see the commented-out code in the `CamelArtemisApplication` class.
