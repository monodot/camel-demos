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

    @EndpointInject(uri = "mock:final")
    private MockEndpoint mockFinal;


    @Test
    public void testBodyContains() throws Exception {
        mockOutput.expectedMessageCount(1);
        mockFinal.expectedMessageCount(1);

        template.sendBody("direct:start", "Cilla Black");

        assertMockEndpointsSatisfied();
    }


    @Test
    public void thatThatOtherwiseDoesntFuckThingsUp() throws Exception {
//        mockOutput.expectedMessageCount(1);
        mockFinal.expectedMessageCount(1);

        template.sendBody("direct:start", "Bobby");

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
                            .when(simple("${body} contains 'Priscilla White'"))
                                .log("Priscilla White")
                            .otherwise()
                                .log("None of the above")
                        .end()
                        .log("Finished!")
                        .to("mock:final");

                from("direct:process-file")
                        .to("mock:output");
            }
        };
    }

}
