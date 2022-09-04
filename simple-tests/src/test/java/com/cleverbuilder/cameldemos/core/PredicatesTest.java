package com.cleverbuilder.cameldemos.core;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class PredicatesTest extends CamelTestSupport {

    @Test
    public void testBodyIsNull() throws Exception {
        MockEndpoint isNull = getMockEndpoint("mock:bodyisnull-isnull");
        isNull.expectedMessageCount(1);

        template.sendBodyAndHeader("direct:bodyisnulltest", null,
                "MyCustomHeader", "foo");

        assertMockEndpointsSatisfied();

    }


    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:bodyisnulltest")
                        .choice().when(body().isNull())
                            .to("mock:bodyisnull-isnull")
                        .otherwise()
                            .to("mock:bodyisnull-otherwise");
            }
        };
    }
}
