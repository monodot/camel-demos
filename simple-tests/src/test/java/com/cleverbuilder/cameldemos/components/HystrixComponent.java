package com.cleverbuilder.cameldemos.components;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by tdonohue on 30/05/2018.
 */
public class HystrixComponent extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    MockEndpoint mockResult;

    @EndpointInject(uri = "mock:hystrix")
    MockEndpoint mockHystrix;

    @EndpointInject(uri = "mock:fallback")
    MockEndpoint mockFallback;

    @Test
    public void testHystrix() throws Exception {
        mockHystrix.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.setException(new IllegalArgumentException("An exception has happened!"));
            }
        });

        mockFallback.expectedMessageCount(1);
        mockResult.expectedMessageCount(1);

        template.sendBody("direct:start", "Hello world!");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                // do something
                                exchange.getIn().setHeader("EndpointHystrix", "mock:hystrix");
                            }
                        })
//                        .hystrix()
                        .circuitBreaker()
                            .toD("${header.EndpointHystrix}")
                        .onFallbackViaNetwork()
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                    // do something more
                                    exchange.getIn().setHeader("EndpointFallback", "mock:fallback");
                                }
                            })
                            .toD("${header.EndpointFallback}")
                        .endCircuitBreaker()
                        .to("mock:result");


            }
        };
    }

}
