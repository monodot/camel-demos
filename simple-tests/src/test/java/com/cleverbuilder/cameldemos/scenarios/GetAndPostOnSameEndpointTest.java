package com.cleverbuilder.cameldemos.scenarios;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class GetAndPostOnSameEndpointTest extends CamelTestSupport {

    @Test
    public void testSomething() throws Exception {
        template.sendBody("http://localhost:8080/reset/request/12345678");


    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                restConfiguration().component("servlet");

                rest("/reset/request/{data}")
                        .get()
                        .post()
                        .route()
                        .log("hello");

            }
        };
    }
}
