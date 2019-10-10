package com.cleverbuilder.cameldemos.transactions;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelException;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jms.connection.JmsTransactionManager;

import java.util.concurrent.TimeUnit;

/**
 * Let's see what happens with a transaction manager configured...
 */
public class JmsComponentWithTxManagerTest extends CamelTestSupport {

    /**
     * In this test, the entire route is transactional because
     * the receiving `jms` component is wired to a transaction
     * manager. This means that the transactional steps will be
     * rolled back when the Exception is thrown at the end.
     * The outcome of this test is actually that there is
     * 1 message on the dead letter queue.
     */
    @Test
    public void messageIsRolledBackEvenWithoutAnExplicitTransactedCommand() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // This route is transactional because the `jms` component
                // is already wired up to a transaction manager
                from("jms:queue:HELLO.WORLD")
                        .to("log:message")
                        .to("jms:queue:BYE.WORLD")
                        // This exception will cause the message to be rolled back to HELLO.WORLD
                        .throwException(new CamelException("Something bad happened"));
            }
        });
        context.start();

        // Assert that we'll receive 2 x exchanges....
        // Which is the message being consumed, rolled back and then consumed again.
        NotifyBuilder notify = new NotifyBuilder(context).whenFailed(2).create();

        // Send a message to the first queue...
        template.sendBody("jms:queue:HELLO.WORLD", "Oh hi there");

        // Assert that we received >= 2 x failed exchanges within 5 seconds
        assertTrue(notify.matches(5, TimeUnit.SECONDS));

        // Assert that nothing arrived on the BYE.WORLD queue (because it should have been rolled back)
        Object message = consumer.receiveBody("jms:queue:BYE.WORLD", 5000L);
        assertNull(message);

        // Assert that the message actually arrived on the DLQ (because it was rolled back so many times, retries were exhausted)
        Object message2 = consumer.receiveBody("jms:queue:DLQ", 5000L);
        assertEquals("Oh hi there", message2);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        // Create an embedded ActiveMQ broker
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("vm://localhost?broker.persistent=false&broker.useJmx=false");

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

        return registry;
    }

}
