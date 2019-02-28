package com.cleverbuilder.cameldemos.spring.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * Created by tdonohue on 19/02/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringRoutingLanguagesTest extends TestSupport {

    @EndpointInject(uri = "mock:simple")
    protected MockEndpoint mockSimple;

    @EndpointInject(uri = "mock:xpath")
    protected MockEndpoint mockXpath;

    @EndpointInject(uri = "mock:header")
    protected MockEndpoint mockHeader;

    @Autowired
    protected ProducerTemplate template;

    @Test
    public void simple() throws Exception {
        mockSimple.expectedMessageCount(1);
        template.sendBody("direct:simple", "Hello, world!");
        mockSimple.assertIsSatisfied(1000L);
    }

    @Test
    public void xpath() throws Exception {
        mockXpath.expectedMessageCount(1);
        template.sendBody("direct:xpath", "<greeting><text>Hello, world!</text></greeting>");
        mockXpath.assertIsSatisfied(1L, TimeUnit.SECONDS);
    }

    @Test
    public void header() throws Exception {
        mockHeader.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:header", "Message body",
                "MyHeader", "Hello!");
        mockHeader.assertIsSatisfied(1L, TimeUnit.SECONDS);
    }

}