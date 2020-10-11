package com.cleverbuilder.cameldemos.transactions;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.connection.JmsTransactionManager;

/**
 * Let's see what happens with a transaction manager configured...
 *
 * To run this test, make sure the `byteman` Maven profile is enabled
 * $ cd camel-demos/simple-tests
 * $ mvn clean test -Dtest=JmsComponentWithTxManagerBMTest -Pbyteman
 *
 * You can observe that:
 * The doRollback method is executed 7 times (1st time + 6 retries)
 */
@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMUnitConfig(loadDirectory="target/test-classes/byteman")
@BMScript(value="jmstxmanager.btm")
public class JmsComponentWithTxManagerBMTest extends CamelTestSupport {

    /**
     * In this test, the entire route is transactional because
     * the receiving `jms` component is wired to a transaction
     * manager. This means that the transactional steps will be
     * rolled back when the Exception is thrown at the end.
     * The result of this test is that there is
     * 1 message on ActiveMQ's dead letter queue.
     */
    @Test
    @Ignore
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

//        MockEndpoint observer = getMockEndpoint("mock:observer");
//        observer.expectedMessageCount(6);

        // Send a message to the first queue...
        template.sendBody("jms:queue:HELLO.WORLD", "Oh hi there");

//        // Assert that we received >= 2 x failed exchanges within 5 seconds
//        assertTrue(notify.matches(5, TimeUnit.SECONDS));

//        assertMockEndpointsSatisfied(10L, TimeUnit.SECONDS);
//        assertTrue(notify.matches(10, TimeUnit.SECONDS));


        // Assert that nothing arrives on the BYE.WORLD queue (because it should have been rolled back)
        Object message = consumer.receiveBody("jms:queue:BYE.WORLD", 5000L);
        assertNull(message);

        // Assert that the message actually arrives on the DLQ (because it was rolled back so many times, and retries were exhausted)
        // The default name for the Dead Letter Queue in ActiveMQ 5.x is "ActiveMQ.DLQ"
        Object message2 = consumer.receiveBody(
                "jms:queue:ActiveMQ.DLQ", 5000L);
        assertEquals("Oh hi there", message2);

//        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
//        Connection connection = connectionFactory.createConnection();
//        connection.start();
//        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        Queue helloWorldQueue = session.createQueue("HELLO.WORLD");
//        QueueBrowser queue = session.createBrowser(helloWorldQueue);

    }

    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        // Create an embedded ActiveMQ broker
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("vm://localhost?" +
                "broker.persistent=false" +
                "&broker.useJmx=false" +
                "&jms.redeliveryPolicy.maximumRedeliveries=5");
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
