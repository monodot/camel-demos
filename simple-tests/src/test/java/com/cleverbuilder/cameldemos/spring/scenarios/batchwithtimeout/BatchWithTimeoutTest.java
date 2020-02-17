package com.cleverbuilder.cameldemos.spring.scenarios.batchwithtimeout;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class BatchWithTimeoutTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("com/cleverbuilder/cameldemos/spring/scenarios/batchwithtimeout/BatchWithTimeoutTest-context.xml");
    }

    @Test
    public void testDoWhile() throws InterruptedException {
        getMockEndpoint("mock:loopy").expectedMessageCount(2);

        template.send("direct:start", ExchangeBuilder.anExchange(context)
                .withBody("Hello world!")
                .withHeader("BatchSize", 10).build());

        // We expect 2 exchanges, because the elapsed time between messages is 3 seconds, but we have set a Timeout of 5 seconds
        assertMockEndpointsSatisfied(10L, TimeUnit.SECONDS);
    }

}


class LoopExpressionBean {
    public static boolean loopCondition(Exchange exchange) {
        Integer batchSize = exchange.getMessage().getHeader("BatchSize", 0, Integer.class);
        Integer loopIndex = exchange.getProperty("CamelLoopIndex", 0, Integer.class);
        Instant timeout = exchange.getMessage().getHeader("TimeoutTime", Instant.class);
        Instant now = Instant.now();

        System.out.println("batchSize is: " + batchSize + ", loopIndex is: " + loopIndex);
        System.out.println("now is: " + now + ", timeoutTime is: " + timeout);

        return (loopIndex <= batchSize) && (now.isBefore(timeout));
    }
}