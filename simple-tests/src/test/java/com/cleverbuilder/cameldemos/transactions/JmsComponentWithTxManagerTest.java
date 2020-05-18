package com.cleverbuilder.cameldemos.transactions;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.JmsTransactionManager;

/**
 * This test is also partially duplicated in the JmsComponentWithTxManagerBMTest,
 * if you want to see the exact transactional states being logged out using Byteman.
 */
public class JmsComponentWithTxManagerTest extends CamelTestSupport {

    private static final int MAXIMUM_REDELIVERIES = 4; // this is set on the ActiveMQConnectionFactory itself
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsComponentWithTxManagerTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Create an embedded broker on port 61619
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61619");
        broker.setPersistent(false);
        broker.start();
    }

    /**
     * In this test, the entire route is transactional because
     * the receiving `jms` component is wired to a transaction
     * manager. This means that the transactional steps will be
     * rolled back when the Exception is thrown at the end.
     * The result of this test is that there is
     * 1 message on ActiveMQ's dead letter queue.
     */
    @Test
    public void messageIsRolledBackEvenWithoutAnExplicitTransactedCommand() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // This route is transactional because the `jms` component
                // is already wired up to a transaction manager
                from("jms:queue:HELLO.WORLD")
//                        .transacted()
                        .to("log:message?showHeaders=true")
                        .to("mock:observer")
                        .to("jms:queue:BYE.WORLD?exchangePattern=InOnly")
                        // This exception will cause the message to be rolled back to HELLO.WORLD
                        .throwException(new CamelException("Something bad happened"));
            }
        });
        context.start();

        // Assert that we'll receive 2 x exchanges....
        // Which is the message being consumed, rolled back and then consumed again.
//        NotifyBuilder notify = new NotifyBuilder(context).whenFailed(2).create();

        MockEndpoint observer = getMockEndpoint("mock:observer");
        observer.expectedMessageCount(MAXIMUM_REDELIVERIES + 1);

        // Send a message to the first queue...
        template.sendBody("jms:queue:HELLO.WORLD", "Oh hi there");

        assertMockEndpointsSatisfied();

        // Assert that nothing arrives on the BYE.WORLD queue (because it should have been rolled back)
        LOGGER.info("Checking that nothing arrived in the intended output queue");
        Object message = consumer.receiveBody("jms:queue:BYE.WORLD", 5000L);
        assertNull(message);

        // Assert that the message actually arrives on the DLQ (because it was rolled back so many times, and retries were exhausted)
        // The default name for the Dead Letter Queue in ActiveMQ 5.x is "ActiveMQ.DLQ"
        LOGGER.info("Waiting for message to arrive on the DLQ");
        Object message2 = consumer.receiveBody(
                "jms:queue:ActiveMQ.DLQ", 5000L);
        assertEquals("Oh hi there", message2);

    }


    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        // Create an embedded ActiveMQ broker
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("tcp://localhost:61619" +
                "?jms.redeliveryPolicy.maximumRedeliveries=" + MAXIMUM_REDELIVERIES);
        // Uncomment this if you want to try this test with an external broker
        // activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");
        activeMQConnectionFactory.setUserName("admin");
        activeMQConnectionFactory.setPassword("admin");

        // Create a transaction manager and attach the connection factory to it
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(activeMQConnectionFactory);

        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(activeMQConnectionFactory);
        jmsComponent.setTransactionManager(transactionManager); // expects a PlatformTransactionManager

        // Add the activemq component into the registry so we can use it in our routes
        registry.bind("jms", jmsComponent);

        // Need to add the transaction manager into the registry,
        // otherwise we'll get: "No bean could be found in the registry of type: PlatformTransactionManager"
        registry.bind("transactionManager", transactionManager);
    }

}
