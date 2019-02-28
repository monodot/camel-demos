package com.cleverbuilder.cameldemos.spring.scenarios.errorhandler;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ErrorHandlerTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    /**
     * Just a simple test that publishes a message to an endpoint,
     * and then asserts that a message arrived at the mock.
     */
    @Test
    public void testRoute() throws Exception {
        mockOutput.expectedMessageCount(1);

        template.sendBody("direct:start", "Hello, world!");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    /**
     * Provides a sample route that calls the 'sayHello' method
     * on the Bean
     */
    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .bean(new Transformer())
//                        .process(new ErrorHandler())
                        .to("mock:output");
            }
        };
    }




}
