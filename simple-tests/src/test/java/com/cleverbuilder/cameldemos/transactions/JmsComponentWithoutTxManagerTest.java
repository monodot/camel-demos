package com.cleverbuilder.cameldemos.transactions;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelException;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * See what happens when we don't configure a transaction manager...
 */
public class JmsComponentWithoutTxManagerTest extends CamelTestSupport {

    /**
     * In this test, no transaction manager is configured on
     * the JMS component, so the message is consumed from
     * the first queue and placed on the second queue.
     * The exception afterwards doesn't cause a rollback.
     *
     * Default acknowledgmentModeName here is AUTO_ACKNOWLEDGE
     */
    @Test
    public void messageIsConsumedWithoutATransactionManager() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:queue:HELLO.WORLD")
                        .to("log:message")
                        .to("jms:queue:BYE.WORLD")
                        // This exception will cause the message to be rolled back to HELLO.WORLD
                        .throwException(new CamelException("Something bad happened"));
            }
        });
        context.start();

        // Send a message to the first queue...
        template.sendBody("jms:queue:HELLO.WORLD", "Oh hi there");

        // Assert that our message arrived on the BYE.WORLD queue,
        // because the exception didn't cause anything to be rolled back (no transaction)
        Object message = consumer.receiveBody("jms:queue:BYE.WORLD", 5000L);
        assertEquals("Oh hi there", message.toString());
    }

    /**
     * In this test, again no transaction manager is configured.
     * acknowledgement mode is set to CLIENT_ACKNOWLEDGE,
     * and the same thing happens - message is consumed from
     * the first queue and sent to the second queue. No rollback.
     */
    @Test
    public void messageIsConsumedEvenWithClientAcknowledgeMode() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:queue:HELLO.WORLD?acknowledgementModeName=CLIENT_ACKNOWLEDGE")
                        .to("log:message")
                        .to("jms:queue:BYE.WORLD")
                        .throwException(new CamelException("Something bad happened"));
            }
        });
        context.start();

        // Send a message to the first queue...
        template.sendBody("jms:queue:HELLO.WORLD", "Oh hi there");

        // Assert that our message arrived on the BYE.WORLD queue,
        // because the exception didn't cause anything to be rolled back (no transaction)
        Object message = consumer.receiveBody("jms:queue:BYE.WORLD", 5000L);
        assertEquals("Oh hi there", message.toString());
    }

    /**
     * See what happens when using 'transacted' without a transaction manager.
     * Camel will fail to start, because no transaction manager can be found.
     */
    @Test(expected = FailedToCreateRouteException.class)
    public void usingTransactedWithoutATransactionManager() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:queue:HELLO.WORLD")
                        .transacted()
                        .to("log:message")
                        .to("jms:queue:BYE.WORLD")
                        .throwException(new CamelException("Something bad happened"));
            }
        });
        context.start();

    }

    /**
     * Just define an ActiveMQ component and connection factory,
     * without any transaction manager.
     */
    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("vm://localhost?broker.persistent=false&broker.useJmx=false");

        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(activeMQConnectionFactory);

        // Add the activemq component into the registry so we can use it in our routes
        registry.bind("jms", jmsComponent);
    }

}
