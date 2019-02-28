package com.cleverbuilder.cameldemos.perf;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 16/05/2018.
 */
public class BigPropertyTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(BigPropertyTest.class);

    private static final String PROPERTY_NAME = "GreetingMessages";
    private static final String LOOP_COUNT = "LoopCount";

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    /**
     * Testing how big the JVM can get if we collect lots of things on a
     * Property on the Exchange.
     *
     * With 1,000,000 loops and 512M heap size => java.lang.OutOfMemoryError: GC overhead limit exceeded
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testBigProperty() throws Exception {
        int loopCount = 1000000;

        mockOutput.expectedMessageCount(1);

        template.sendBodyAndProperty("direct:start", "Hello world!", LOOP_COUNT, loopCount);

        assertMockEndpointsSatisfied(5L, TimeUnit.SECONDS);

        List<GreetingMessage> greetings = mockOutput.getExchanges().get(0).getProperty(PROPERTY_NAME, List.class);
        assertEquals(loopCount, greetings.size());

        LOG.info("Memory usage: total memory=" +
                Runtime.getRuntime().totalMemory());
        //Memory usage: at loopCount=1,500,000, total memory=514850816
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start")
                        .loop(exchangeProperty(LOOP_COUNT))
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                List greetings = exchange.getProperty(PROPERTY_NAME, List.class);

                                if (greetings == null) {
                                    greetings = new ArrayList<GreetingMessage>();
                                }

                                GreetingMessage greeting = new GreetingMessage();
//                                greeting.setId("12345");
                                greeting.setId(RandomStringUtils.random(5));
//                                greeting.setCode("ABC");
                                greeting.setCode(RandomStringUtils.random(3));
//                                greeting.setGreeting("Hello, world!");
                                greeting.setGreeting(RandomStringUtils.random(15));
//                                greeting.setDescription("A typical way to be greeted by a computer program");
                                greeting.setDescription(RandomStringUtils.random(50));

                                greetings.add(greeting);
                                exchange.setProperty(PROPERTY_NAME, greetings);
                            }
                        })
                        .end()
                        .to("mock:output");
            }
        };
    }

}


class GreetingMessage {

    private String id;
    private String code;
    private String greeting;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
