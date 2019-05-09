package com.cleverbuilder.cameldemos.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 24/04/2018.
 */
public class RoutingLanguagesTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:pass")
    MockEndpoint mockPass;

    @EndpointInject(uri = "mock:otherwise")
    MockEndpoint mockOtherwise;

    @Test
    public void simple() throws InterruptedException {
        mockPass.expectedMessageCount(1);

        template.sendBody("direct:simple", "Hello, world!");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Test
    public void builder() throws InterruptedException {
        mockPass.expectedMessageCount(1);

        template.sendBody("direct:builder", "Hello, world!");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Test
    public void xpath() throws InterruptedException {
        mockPass.expectedMessageCount(1);

        template.sendBody("direct:xpath", "<greeting><text>Hello, world!</text></greeting>");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Test
    public void regex() throws InterruptedException {
        mockPass.expectedMessageCount(1);

//        template.sendBody("direct:regex", "hello, world!");

        template.sendBodyAndHeader("direct:regex", "Message body",
                "MyHeader", "hello, world!");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    // Needs camel-jsonpath dependency in Maven POM
    @Test
    public void jsonpath() throws InterruptedException {
        mockPass.expectedMessageCount(1);

        template.sendBody("direct:jsonpath", "{ \"greeting\": { \"text\": \"Hello, world!\" } }");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:simple")
                        .choice()
                            .when(simple("${body} == 'Hello, world!'"))
                                .to("mock:pass")
                            .otherwise()
                                .to("mock:otherwise");

                from("direct:builder")
                        .choice()
                            .when(body().isEqualTo("Hello, world!"))
                                .to("mock:pass")
                            .otherwise()
                                .to("mock:otherwise");

                from("direct:regex")
                        .choice()
                            .when(header("MyHeader").regex("[Hh]ello, world!"))
                                .to("mock:pass")
                            .otherwise()
                                .to("mock:otherwise");

                from("direct:xpath")
                        .choice()
                            .when(xpath("//greeting/text = 'Hello, world!'"))
                                .to("mock:pass")
                            .otherwise()
                                .to("mock:otherwise");

                from("direct:jsonpath")
                        .choice()
                            .when().jsonpath("$.greeting[?(@.text == 'Hello, world!')]")
                                .log("jsonpath expression evaluated to true")
                                .to("mock:pass")
                            .otherwise()
                                .log("jsonpath expression evaluated to NOT true")
                                .to("mock:otherwise");

            }
        };
    }
}
