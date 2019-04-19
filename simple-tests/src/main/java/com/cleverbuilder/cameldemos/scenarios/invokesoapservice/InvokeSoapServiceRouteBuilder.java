package com.cleverbuilder.cameldemos.scenarios.invokesoapservice;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;

/**
 * Invoking a soap service using the CXF component.
 * Needs camel-cxf dependency.
 */
public class InvokeSoapServiceRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:start")
                .setBody(constant("12345"))
                .bean(GetBookRequestBuilder.class)

                .setHeader(CxfConstants.OPERATION_NAME, constant("GetBook"))
                .setHeader(CxfConstants.OPERATION_NAMESPACE, constant("http://www.cleverbuilder.com/BookService/"))

                // Invoke our test service using CXF
                .to("cxf://http://localhost:8423/test/BookService"
                        + "?serviceClass=com.cleverbuilder.bookservice.BookService"
                        + "&wsdlURL=/wsdl/BookService.wsdl")

                // You can retrieve fields from the response using the Simple language
                .log("The title is: ${body[0].book.title}")

                .to("mock:output");

    }
}
