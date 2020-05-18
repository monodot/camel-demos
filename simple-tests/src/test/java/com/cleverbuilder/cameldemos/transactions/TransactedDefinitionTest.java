package com.cleverbuilder.cameldemos.transactions;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.JmsTransactionManager;

public class TransactedDefinitionTest extends CamelTestSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactedDefinitionTest.class);

    /**
     * This tries to test that a transacted definition is required.
     */
    @Test
    public void withoutTransactedDefinition() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:8084/hello")
                        .transacted()
                        .to("jms:queue:HELLO.WORLD?exchangePattern=InOnly")
                        .to("file:target/output")
                        .throwException(new CamelException("An error did occur!"));
            }
        });

        // We'll get an Exception (eventually), so we'll just catch it here,
        // so we can continue and do some further assertions
        try {
            template.sendBody("http://localhost:8084/hello", "Some body");
        } catch (CamelExecutionException ex) {

        }

        // Assert that nothing arrives on the HELLO.WORLD queue (because an exception occurred later which should have caused the transaction to roll back)
        LOGGER.info("Waiting to see if messages arrive on HELLO WORLD queue");
        Object message = consumer.receiveBody("jms:queue:HELLO.WORLD", 5000L);
        assertNull(message);
    }

    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        // Create an embedded ActiveMQ broker
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("vm://localhost?broker.persistent=false&broker.useJmx=false");
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
