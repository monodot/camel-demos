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

    @EndpointInject(uri = "mock:starts-with-true")
    MockEndpoint mockStartsWithTrue;

    @EndpointInject(uri = "mock:greater-than-true")
    MockEndpoint mockGreaterThanTrue;

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

        template.sendBodyAndHeader("direct:header", "Hello, world!",
                "EggType", "scrambled");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Test
    public void contains() throws InterruptedException {
        mockContainsTrue.expectedMessageCount(1);

        template.sendBody("direct:contains", "bacon and eggs");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Test
    public void startsWith() throws InterruptedException {
        mockStartsWithTrue.expectedMessageCount(1);

        template.sendBody("direct:starts-with", "bacon sandwich");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void greaterThan() throws InterruptedException {
        mockGreaterThanTrue.expectedMessageCount(1);

        template.sendBodyAndHeader("direct:greater-than", "breakfast",
                "CupsOfCoffee", "6");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:directory/files/input")
                        .to("log:mylogger?showHeaders=true&showBody=true")
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

                from("direct:starts-with")
                        .choice()
                            .when(simple("${body} starts with 'bacon'"))
                                .to("mock:starts-with-true");

                from("direct:greater-than")
                        .choice()
                            .when(simple("${header.CupsOfCoffee} > 5"))
                                .to("mock:greater-than-true");
            }
        };
    }
}
