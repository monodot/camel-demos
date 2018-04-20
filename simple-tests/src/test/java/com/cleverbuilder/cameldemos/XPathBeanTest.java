package com.cleverbuilder.cameldemos;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by tdonohue on 13/02/2018.
 */
public class XPathBeanTest extends CamelTestSupport {

    protected String inputBody = "<person name='James' city='London'/>";

    @Test
    public void testJamesMessage() throws Exception {
        getMockEndpoint("mock:james").expectedMessageCount(1);
        sendBody("direct:start", inputBody);
        assertMockEndpointsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .setHeader("Name").constant("James")
                        .filter().xpath("//person[@name=in:header('Name')]")
                        .log("This is James!")
                        .to("mock:james");
            }

        };
    }


}
