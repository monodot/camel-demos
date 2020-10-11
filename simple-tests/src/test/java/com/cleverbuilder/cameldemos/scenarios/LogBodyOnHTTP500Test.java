package com.cleverbuilder.cameldemos.scenarios;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class LogBodyOnHTTP500Test extends CamelTestSupport {

    @Test
    public void responseBodyShouldBeLogged() throws Exception {
        template.sendBody("direct:start", "Hello!");
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(HttpOperationFailedException.class)
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                // https://stackoverflow.com/questions/41008023/apache-camel-unable-to-get-the-exception-body
                                final HttpOperationFailedException e =
                                        exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
                                // Do something with the responseBody
                                final String responseBody = e.getResponseBody();
                            }
                        })
                        .log(LoggingLevel.ERROR, "Received error response - ${header.CamelExceptionCaught.getResponseBody()}")
                        // Rethrow the exception
                        .handled(false);

                from("direct:start")
                        .to("http://localhost:8084/failService");

                // Create a test HTTP endpoint which will always return HTTP 500
                from("jetty:http://localhost:8084/failService")
                        .setBody(constant("FISH FINGER BAP!!!!"))
                        // Throw a 500
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("500"));
            }
        };
    }
}
