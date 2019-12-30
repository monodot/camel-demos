package com.cleverbuilder.cameldemos.components;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 16/05/2018.
 */
public class RestInvokeXStreamTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Test
    public void testXstream() throws InterruptedException {
        mockOutput.expectedMessageCount(1);

        // Set up our POJO
        Penguin penguin = new Penguin();
        penguin.setFirstName("Pingu");
        penguin.setLastName("The Great");

        // Add some important facts to a map
        Map data = new HashMap<String, Object>();
        data.put("colour", "black with white bits");
        data.put("food", "fish");
        data.put("location", "Antarctica");
        penguin.setData(data);

        // Assert that we'll receive an XML representation of this Penguin at our mock endpoint
        mockOutput.expectedMessageCount(1);
        mockOutput.expectedBodiesReceived("<?xml version='1.0' encoding='UTF-8'?>" +
                "<penguin>" +
                "<firstName>Pingu</firstName>" +
                "<lastName>The Great</lastName>" +
                "<data>" +
                "<entry><string>colour</string><string>black with white bits</string></entry>" +
                "<entry><string>location</string><string>Antarctica</string></entry>" +
                "<entry><string>food</string><string>fish</string></entry>" +
                "</data>" +
                "</penguin>");

        template.sendBody("direct:start", penguin);

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Set up an XStream object to give a proper name to our XML root element
                // Otherwise XStream uses the fully-qualified class name as the root element name
                XStream penguinConfig = new XStream();
                penguinConfig.alias("penguin", Penguin.class);

                from("direct:start")
                        .marshal(new XStreamDataFormat(penguinConfig))
                        .to("rest:post:hello?host=localhost:8084&bindingMode=xml");

                // Stand up a simple HTTP endpoint for testing
                // This should receive the marshalled XML
                from("jetty:http://localhost:8084/hello")
                        .streamCaching() // This is required otherwise the initial request gets thrown away after it is read once.
                        .log("Received request! - ${body}")
                        .to("mock:output");
            }
        };
    }
}

@XStreamAlias("penguin")
class Penguin {

    private String firstName;
    private String lastName;
    private Map data;

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

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
