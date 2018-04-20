package com.cleverbuilder.cameldemos.perf.xslt;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Tests to see the performance of the standard XSLT component when processing
 * large XML files. Just uses standard XSLT component as it comes out of the box.
 * No splitting. XML input factory is: com.sun.xml.internal.stream.XMLInputFactoryImpl
 *
 * Run using:
 *
 * export MAVEN_OPTS="-Xmx1024m"
 * mvn clean test -Dtest=UnoptimisedTest -Dmemory=512
 *
 * Results:
 * - At 100,000 records (~435MB) and 512M memory => FAIL! "java.lang.OutOfMemoryError: GC overhead limit exceeded"
 * - At 100,000 records and 1024M memory => OK
 * - At 100,000 records and 768M memory => OK, duration: 26204ms
 * - At 50,000 records (~217MB) and 512M memory => OK, duration: 7948ms
 */
public class UnoptimisedTest extends AbstractBigXmlTest {

    @EndpointInject(uri = "mock:output")
    protected MockEndpoint mockEndpoint;

    @Test
    public void processBigFile() throws Exception {
        mockEndpoint.setExpectedMessageCount(1);

        StopWatch watch = new StopWatch();

        context.startRoute("testroute");
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);

        System.out.println("duration: " + watch.stop() + "ms");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("file://target/bigfiles").id("testroute").noAutoStartup()
                        .log("Processing big file")
                        .to("xslt:com/cleverbuilder/cameldemos/perf/xslt/transform.xslt")
                        .log("File transformed")
                        .to("file:target/files/out/")
                        .to("mock:output");
            }
        };
    }
}
