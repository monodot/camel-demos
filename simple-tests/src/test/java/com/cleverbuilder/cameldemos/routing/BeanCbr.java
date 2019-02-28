package com.cleverbuilder.cameldemos.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 22/04/2018.
 */
public class BeanCbr extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Test
    public void testHelloWorld() throws InterruptedException {
        mockOutput.expectedMessageCount(1);

        template.sendBody("direct:start", "Fried eggs");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .choice()
                            .when(method(MyChoiceBean.class, "isFriedEggs"))
                                .to("mock:output");
            }
        };
    }

}
