package com.cleverbuilder.cameldemos.errorhandling;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ErrorReportingRouteTest extends CamelTestSupport {

    @Test
    public void testSendsAMessage() throws Exception {

    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:log-error")
                        .setHeader("to", constant("error.team@example.com"))
                        .to("smtp://erroruser@smtp.example.com");
            }
        };
    }
}
