package com.cleverbuilder.cameldemos.core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

public class MEPTest extends CamelTestSupport {

    private static final int BROKER_PORT = 61619;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        // Let's create an embedded ActiveMQ broker on port 61619
        // Doing this explicitly, instead of letting the Connection Factory create a broker itself.
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:" + BROKER_PORT);
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.start();

        super.setUp();
    }

    @Test
    public void testJmsSendingOneWay() throws Exception {
        // Add a mock endpoint so we can do some assertions
        AdviceWithRouteBuilder.adviceWith(context, "main",
                a -> a.weaveAddLast().to("mock:output")
        );
        context.start();

        MockEndpoint mock = getMockEndpoint("mock:output");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("A reply message");

//        template.sendBody("direct:start-oneway", "Nothing");
        Object response = template.requestBody("direct:start-oneway", "Nothing");

//        assertMockEndpointsSatisfied();
    }

    @Test
    public void testJmsSendingRequestReply() throws Exception {
        context.start();
//        Object response = template.requestBody("direct:start-requestreply", "Nothing2");

        template.sendBody("direct:start-requestreply", "Nothing2");

//        assertNotNull(response);

    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start-oneway").id("main")
                        .log("Sending to queue")
                        .to("jms:queue:ADDRESSES")
                        .log("Now logging to a file...")
                        .to("file:target/output");


                from("direct:start-requestreply").id("main-requestreply")
                        .log("Sending to queue")
                        .to("jms:queue:ADDRESSES");

//                from("jms:queue:ADDRESSES")
//                        .setBody(constant("A reply message"));
            }
        };
    }

    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        ActiveMQConnectionFactory amq = new ActiveMQConnectionFactory();
        amq.setBrokerURL("tcp://localhost:" + BROKER_PORT);

        JmsComponent jms = new JmsComponent();
        jms.setConnectionFactory(amq);

        registry.bind("jms", jms);
    }
}
