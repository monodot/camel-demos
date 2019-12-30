package com.cleverbuilder.cameldemos.components;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.apache.camel.language.xpath.XPathBuilder.xpath;

/**
 * Created by tdonohue on 22/04/2018.
 */
public class XsltComponent extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Test
    public void testXslt() throws InterruptedException {
        mockOutput.expectedMessageCount(1);

        // How to implement an XPath based assertion (import this static xpath method)
        mockOutput.expectedMessagesMatches(xpath("count(//Person) = 3"));

        template.sendBody("direct:start", "<staff><person>Jane</person><person>Alex</person><person>Steve</person></staff>");

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("xslt:com/cleverbuilder/cameldemos/components/mytransform.xslt")
                        .log("${body}")
                        .to("mock:output");
            }
        };
    }
}
