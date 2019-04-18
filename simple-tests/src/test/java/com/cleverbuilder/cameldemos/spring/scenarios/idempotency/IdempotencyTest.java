package com.cleverbuilder.cameldemos.spring.scenarios.idempotency;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class IdempotencyTest extends CamelSpringTestSupport {

    @EndpointInject(uri = "direct:start")
    Endpoint startEndpoint;

    @Test
    public void testDuplicateMessagesAreIgnored() throws Exception {
        getMockEndpoint("mock:process").expectedMessageCount(1);
        getMockEndpoint("mock:process").expectedBodiesReceived("I am the FIRST message");

        Map<String, Object> headers = new HashMap<>();
        headers.put("X-MY-Source", "AAA");
        headers.put("X-MY-Class", "ABC123");
        headers.put("X-MY-Destination", "ZYX999");
        headers.put("X-MY-SeqNo", "78");

        template.sendBodyAndHeaders(startEndpoint,
                new File("src/test/data/dupemessage1.txt"),
                headers);
        template.sendBodyAndHeaders(startEndpoint,
                new File("src/test/data/dupemessage2.txt"),
                headers);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("com/cleverbuilder/cameldemos/spring/scenarios/idempotency/IdempotencyTest-context.xml");
    }
}
