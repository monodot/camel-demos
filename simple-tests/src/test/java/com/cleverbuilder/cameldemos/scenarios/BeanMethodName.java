package com.cleverbuilder.cameldemos.scenarios;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 20/04/2018.
 */
public class BeanMethodName extends CamelTestSupport {

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
                        // Try mis-spelling "sayHello" and the test will fail
                        .bean(new RobotBean(), "sayHello")
                        .to("log:mylogger")
                        .to("mock:output");
            }
        };
    }

    /**
     * Here's the custom bean that's used in the Camel route
     */
    public class RobotBean {
        private int counter = 0;

        public int count() {
            return counter++;
        }

        public String sayHello() {
            return "I am a robot, znnnnnnk: " + this.count();
        }
    }


}
