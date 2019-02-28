package com.cleverbuilder.cameldemos.components;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Requires camel-jetty in the POM
 */
public class RestDslJetty extends CamelTestSupport {

    /**
     * Sending a POST request
     */
    @Test
    public void testJettyRestPost() {
        Exchange response = template.send("http://localhost:8080/customers", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello, world!");
            }
        });

        assertNotNull(response);
        assertEquals("POST invoked!", response.getOut().getBody(String.class));
    }

    /**
     * Performing a GET request with query string parameters
     */
    @Test
    public void testJettyQueryString() {
        String response = template.requestBody("http://localhost:8080/customers/search?city=London&country=GB", null, String.class);

        assertNotNull(response);
        assertEquals("Search invoked! Country = GB, City = London", response);
    }


    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                restConfiguration()
                        .component("jetty")
                        .host("localhost").port("8080");

                rest("/customers")
                        .get().to("direct:get")
                        .post().to("direct:post") //.to("mock:post")
                        .get("/search?country={country}&colour={colour}&city={city}").to("direct:search")
                        .delete().to("direct:delete");

                from("direct:post").setBody(constant("POST invoked!"));
                from("direct:get").setBody(constant("GET invoked!"));
                from("direct:delete").setBody(constant("DELETE invoked!"));
                from("direct:search").setBody(simple("Search invoked! Country = ${header.country}, City = ${header.city}"));

            }
        };
    }

}
