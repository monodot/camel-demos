package com.cleverbuilder.cameldemos.transform;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 16/05/2018.
 */
public class DozerTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @EndpointInject(uri = "mock:output-xml")
    MockEndpoint mockOutputXml;

    @Test
    public void testDozer() throws Exception {
        mockOutput.expectedMessageCount(1);
        mockOutput.message(0).body(Customer.class);

        Lead lead = new Lead();
        lead.setName("Dave Smith");
        lead.setCountry("UK");

        template.sendBody("direct:start", lead);

        assertMockEndpointsSatisfied();

        Customer customer = mockOutput.getExchanges().get(0).getIn().getBody(Customer.class);
        assertEquals("Dave Smith", customer.getFullName());
        assertEquals("UK", customer.getLocation());
    }

    @Test
    public void testDozerXml() throws Exception {
        mockOutputXml.expectedMessageCount(1);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("dozer:mytransformation?mappingFile=com/cleverbuilder/cameldemos/transform/dozerMapping.xml&targetModel=com.cleverbuilder.cameldemos.transform.Customer")
                        .to("mock:output");

                from("direct:start-xml")
                        .unmarshal().jaxb()
                        .to("dozer:mytransformation?mappingFile=com/cleverbuilder/cameldemos/transform/dozerMapping.xml&targetModel=com.cleverbuilder.cameldemos.transform.Customer")
                        .marshal().csv()
                        .to("mock:output-xml");
            }
        };
    }

}

class Lead {

    private String name;
    private String country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}


class Customer {

    private String fullName;
    private String location;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}