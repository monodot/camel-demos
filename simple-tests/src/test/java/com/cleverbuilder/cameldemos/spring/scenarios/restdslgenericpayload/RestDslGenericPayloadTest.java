package com.cleverbuilder.cameldemos.spring.scenarios.restdslgenericpayload;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RestDslGenericPayloadTest extends CamelSpringTestSupport {

    @EndpointInject(uri = "direct:start")
    Endpoint startEndpoint;

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    @Test
    public void testGenericMessageIsReceived() throws Exception {
        mockEndpoint.expectedBodiesReceived("Yes");

        Object response = template.requestBody("http://localhost:8080/test", "Some body!");

        mockEndpoint.assertIsSatisfied();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("com/cleverbuilder/cameldemos/spring/scenarios/restdslgenericpayload/RestDslGenericPayload-context.xml");
    }
}
