package com.cleverbuilder.cameldemos.perf.xslt;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.util.StopWatch;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 25/04/2018.
 */
public class SaxonPerformancePT extends AbstractBigXmlTest {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Test
    @Ignore
    public void processBigFile() throws Exception {
        mockOutput.setExpectedMessageCount(1);

        StopWatch watch = new StopWatch();

        context.getRouteController().startRoute("testroute");
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);

        System.out.println("duration: " + watch.taken() + "ms");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("file://target/bigfiles").id("testroute").noAutoStartup()
                        .log("Processing big file")
                        .to("xslt-saxon:com/cleverbuilder/cameldemos/perf/xslt/transform2way.xslt")
                        .log("File transformed")
                        .to("file:target/files/out/")
                        .to("mock:output");
            }
        };
    }

}
