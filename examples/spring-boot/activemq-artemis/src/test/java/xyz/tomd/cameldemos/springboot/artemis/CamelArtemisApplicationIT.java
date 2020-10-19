package xyz.tomd.cameldemos.springboot.artemis;

import org.apache.activemq.artemis.jms.client.ActiveMQConnection;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.jms.*;
import javax.naming.NamingException;

/**
 * Integration test - test that Camel inserts into a separate ActiveMQ Artemis broker succesfully
 * using Testcontainers (Artemis running in a Docker container)
 *
 * You need Docker installed for this.
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(properties = {
        "spring.artemis.user = jazz",
        "spring.artemis.password = hands"
})
@Ignore
public class CamelArtemisApplicationIT {

    private static final Logger LOG = LoggerFactory.getLogger(CamelArtemisApplicationIT.class);

    // Use a community image of ActiveMQ Artemis on Docker Hub
    private static final String ARTEMIS_IMAGE = "vromero/activemq-artemis:2.11.0-alpine";
    private static final int ARTEMIS_TCP_PORT = 61616;

    // Start an Artemis container before running tests
    // We also want to wait until Artemis says this: "Server is now live"
    static GenericContainer<?> artemis = new GenericContainer<>(ARTEMIS_IMAGE)
            .withExposedPorts(ARTEMIS_TCP_PORT)
            .withEnv("ARTEMIS_USERNAME", "jazz")
            .withEnv("ARTEMIS_PASSWORD", "hands")
            .waitingFor(Wait.forLogMessage(".*AMQ221007: Server is now live.*\n", 1));

    // We tell Spring that it should use the dynamically generated port of our Artemis testcontainer
    // See: https://spring.io/blog/2020/03/27/dynamicpropertysource-in-spring-framework-5-2-5-and-spring-boot-2-2-6
    @DynamicPropertySource
    static void artemisProperties(DynamicPropertyRegistry registry) {
        artemis.start();
        registry.add("spring.artemis.port", artemis::getFirstMappedPort);
    }

    /**
     * Create a consumer on the outbound queue, and ensure that 1 message arrives.
     * @throws NamingException
     * @throws JMSException
     * @throws InterruptedException
     */
    @Test
    public void testOneMessageArrivesOnOutboundQueue() throws NamingException, JMSException, InterruptedException {
        LOG.info("Artemis test container is exposing port: " + artemis.getFirstMappedPort());

        // Create a connection to our containerised Artemis
        // Use the Artemis client API directly
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
                "tcp://localhost:" + artemis.getFirstMappedPort())
                .setUser("jazz")
                .setPassword("hands");
        ActiveMQConnection connection = (ActiveMQConnection) factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create a consumer on the 'PROCESSED' queue
        Queue queue = session.createQueue("PROCESSED");
        MessageConsumer consumer = session.createConsumer(queue);
        connection.start();

        // Wait 5s for a message to be received
        TextMessage messageReceived = (TextMessage) consumer.receive(5000);

        LOG.info("Message received OK in integration test: " + messageReceived.getText());

    }

}