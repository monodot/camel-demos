package com.cleverbuilder.cameldemos.spring.core;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OnExceptionTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("com/cleverbuilder/cameldemos/spring/core/OnExceptionTest-context.xml");
    }

    /**
     * If we send a message to a route where one of the steps throws an exception,
     * then test that an onException block can handle this error, preventing the
     * message from being rolled back to the message broker and retried again.
     *
     * We do this by verifying that our Mock endpoint is only invoked once.
     * @throws InterruptedException
     */
    @Test
    public void testOnExceptionHandlesExceptionWithinSameRoute() throws InterruptedException {
        // Setting an assert period forces Camel to check that ONLY the given number of messages arrive
        getMockEndpoint("mock:singleroute-observer").setAssertPeriod(5000L);
        // Since the mock endpoint will assert for 5 seconds, this is enough time to test if messages are rolled back and retried
        getMockEndpoint("mock:singleroute-observer").expectedMessageCount(1);

        template.sendBody("activemq://queue:onexception.singleroute",
                "Hello from the test code");

        assertMockEndpointsSatisfied();

    }


    /**
     * If we send a message to a route where a step in ANOTHER route (invoked
     * via a Direct component) throws an exception, then test that
     * an onException block doesn't seem to be respected. So, the same message
     * is rolled back to the queue and retried several times.
     */
    @Test
    public void testOnExceptionDoesntHandleExceptionWithinASubroute() throws InterruptedException {
        // Assert we receive at least 3 messages - indicating multiple a NOACK to the broker, and so multiple rollbacks/retries
        getMockEndpoint("mock:withsubroute-observer").expectedMessageCount(3);

        template.sendBody("activemq://queue:onexception.withsubroute",
                "Hello from the test code");

        assertMockEndpointsSatisfied();

    }

}
