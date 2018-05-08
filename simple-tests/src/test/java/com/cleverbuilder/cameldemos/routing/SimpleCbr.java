package com.cleverbuilder.cameldemos.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 22/04/2018.
 */
public class SimpleCbr extends CamelTestSupport {

    @EndpointInject(uri = "mock:helloworld")
    MockEndpoint mockHelloWorld;

    @EndpointInject(uri = "mock:other")
    MockEndpoint mockOther;

    @EndpointInject(uri = "mock:header-true")
    MockEndpoint mockHeaderTrue;

    @EndpointInject(uri = "mock:contains-true")
    MockEndpoint mockContainsTrue;

    @Test
    public void testHelloWorld() throws InterruptedException {
        mockHelloWorld.expectedMessageCount(1);

        template.sendBody("file:directory/files/input", "Hello, world!");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Test
    public void testOther() throws InterruptedException {
        mockOther.expectedMessageCount(1);

        template.sendBody("file:directory/files/input", "Goodbye!");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Test
    public void simpleHeader() throws InterruptedException {
        mockHeaderTrue.expectedMessageCount(1);

        template.sendBodyAndHeader("direct:endswith", "Hello, world!",
                "EggType", "scrambled");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Test
    public void contains() throws InterruptedException {
        mockContainsTrue.expectedMessageCount(1);

        template.sendBody("direct:contains", "bacon and eggs");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:directory/files/input")
                        .choice()
                            .when(simple("${body} == 'Hello, world!'"))
                        .to("mock:helloworld")
                        .otherwise()
                            .to("mock:other");

                from("direct:header")
                        .choice()
                            .when(simple("${header.EggType} == 'scrambled'"))
                                .to("mock:header-true")
                            .otherwise()
                                .to("mock:header-false");

                from("direct:contains")
                        .choice()
                            .when(simple("${body} contains 'eggs'"))
                                .to("mock:contains-true")
                            .otherwise()
                                .to("mock:contains-false");
            }
        };
    }
}
