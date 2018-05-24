package com.cleverbuilder.cameldemos.components;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by tdonohue on 24/05/2018.
 */
public class JmsWithPojo extends CamelTestSupport {

    private final static String BROKER_URL = "vm://localhost?broker.persistent=false";

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @EndpointInject(uri = "mock:output-json")
    MockEndpoint mockOutputJson;

    /**
     * Send a Java object to/from a queue, using Java serialisation.
     */
    @Test
    public void testSerialiseDeserialise() throws Exception {
        mockOutput.expectedMessageCount(1);

        template.sendBody("direct:start", new StudentPojo("Jeff", "Mills"));

        mockOutput.assertIsSatisfied();

        StudentPojo student = mockOutput.getExchanges().get(0).getIn().getBody(StudentPojo.class);
        assertEquals("Jeff", student.getFirstName());
        assertEquals("Mills", student.getLastName());
    }

    /**
     * Send a Java object to/from a queue using JSON marshalling/unmarshalling.
     */
    @Test
    public void testSerialiseJson() throws Exception {
        mockOutputJson.expectedMessageCount(1);

        template.sendBody("direct:start-json", new StudentPojo("Christa", "Ackroyd"));

        mockOutputJson.assertIsSatisfied();

        StudentPojo student = mockOutputJson.getExchanges().get(0).getIn().getBody(StudentPojo.class);
        assertEquals("Christa", student.getFirstName());
        assertEquals("Ackroyd", student.getLastName());
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // Set up the ActiveMQ broker
                ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(BROKER_URL);
                cf.setTrustedPackages(Arrays.asList("com.cleverbuilder.cameldemos.components"));
                context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

                // push a POJO to a queue
                from("direct:start")
                        .log("${body}")
                        .to("jms:queue:foo");

                // receive a POJO from a queue
                from("jms:queue:foo")
                        .to("log:mylogger?showAll=true")
                        .to("mock:output");

                // marshal to JSON and then push to queue
                from("direct:start-json")
                        .marshal().json(JsonLibrary.Jackson)
                        .to("jms:queue:json");

                // receive from queue, unmarshal from JSON
                from("jms:queue:json")
                        .to("log:mylogger?showAll=true")
                        .unmarshal().json(JsonLibrary.Jackson, StudentPojo.class)
                        .to("mock:output-json");

            }
        };
    }
}

class StudentPojo implements Serializable {

    private String firstName;
    private String lastName;

    public StudentPojo() {
    }

    public StudentPojo(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
