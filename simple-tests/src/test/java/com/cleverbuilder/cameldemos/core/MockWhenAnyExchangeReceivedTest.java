package com.cleverbuilder.cameldemos.core;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MockWhenAnyExchangeReceivedTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @EndpointInject(uri = "mock:henlo")
    MockEndpoint mockHenlo;

    @Test
    public void testWhenAnyExchangeReceived() throws InterruptedException {
        mockOutput.expectedBodiesReceived("henlo");

        mockHenlo.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getMessage().setBody("henlo");
            }
        });

        template.sendBody("direct:start", "nothing");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("mock:henlo")
                        .to("mock:output");

            }
        };
    }
}
