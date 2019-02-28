package com.cleverbuilder.cameldemos.core;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by tdonohue on 12/05/2018.
 */
public class PollEnrich extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Test
    public void testEnrich() throws InterruptedException {
        mockOutput.expectedBodiesReceived("henlo");

        template.sendBody("direct:start", "nothing");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .pollEnrich("file:src/test/data/?fileName=hello.txt&noop=true")
                        .to("mock:output");
            }
        };
    }
}
