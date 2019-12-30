package com.cleverbuilder.cameldemos.spring.scenarios.dailytimer;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DailyTimerTest extends CamelSpringTestSupport {

    @EndpointInject(uri = "mock:output")
    protected MockEndpoint mockOutput;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("com/cleverbuilder/cameldemos/spring/scenarios/dailytimer/DailyTimerTest-context.xml");
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Test
    public void testRoute() throws Exception {
        AdviceWithRouteBuilder.adviceWith(context, "main-route",
                a -> a.weaveAddLast().to("mock:output")
        );
        context.start();

        mockOutput.expectedMinimumMessageCount(2);
        mockOutput.assertIsSatisfied();
    }

}
