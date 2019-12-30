package com.cleverbuilder.cameldemos.components;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 16/05/2018.
 */
public class JacksonTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Test
    public void testJackson() throws InterruptedException {
        mockOutput.expectedMessageCount(1);

        CustomerPojo customer = new CustomerPojo();
        customer.setFirstName("James");
        customer.setLastName("McGowan");

        Map data = new HashMap<String, Object>();
        data.put("colour", "blue");
        data.put("food", "eggs");
        data.put("location", "London");
        customer.setData(data);

        // How to implement an XPath based assertion (import this static xpath method)
        mockOutput.expectedMessageCount(1);
        mockOutput.expectedBodiesReceived("{\"firstName\":\"James\",\"lastName\":\"McGowan\",\"data\":{\"colour\":\"blue\",\"location\":\"London\",\"food\":\"eggs\"}}");

        template.sendBody("direct:start", customer);

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .marshal().json(JsonLibrary.Jackson)
                        .to("mock:output");
            }
        };
    }
}


class CustomerPojo {

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
