package com.cleverbuilder.cameldemos.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by tdonohue on 19/05/2018.
 */
public class SimpleContainsToDirect extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    private MockEndpoint mockOutput;

    @Test
    public void testBodyContains() throws Exception {
        mockOutput.expectedMessageCount(1);

        template.sendBody("direct:start", "Cilla Black");

        assertMockEndpointsSatisfied();
    }


    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .log("Received a message!")
                        .choice()
                            .when(simple("${body} contains 'Cilla Black'"))
                            .to("direct:process-file")
                        .endChoice();

                from("direct:process-file")
                        .to("mock:output");
            }
        };
    }

}
