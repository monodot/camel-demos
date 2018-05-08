package com.cleverbuilder.cameldemos.core;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 24/04/2018.
 */
public class MessageModel extends CamelTestSupport {

    @EndpointInject(uri = "mock:finish")
    MockEndpoint mockFinish;

    @Test
    public void eggs() throws InterruptedException {
        mockFinish.expectedHeaderReceived("EggType", "Scrambled");
        mockFinish.expectedBodyReceived().constant("Hello, eggs!");

        template.sendBody("direct:start", "Hello, world!");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .process(new EggProcessor())
                        .to("mock:finish");
            }
        };
    }

    /**
     * Here's the custom bean that's used in the Camel route
     */
    public class EggProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            Message message = exchange.getIn();
            String header = message.getHeader("MyHeader", String.class);
            String body = message.getBody(String.class);

            message.setHeader("EggType", "Scrambled");
            message.setBody("Hello, eggs!");
        }

    }
}
