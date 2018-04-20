package com.cleverbuilder.cameldemos.spring.filter;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by tdonohue on 19/02/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleFileFilterTest extends TestSupport {

    @EndpointInject(uri = "mock:output")
    protected MockEndpoint mockEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Before
    public void setUp() throws Exception {
        deleteDirectory("target/files/in");
        template.sendBodyAndHeader("file:target/files/in", "Hello World", Exchange.FILE_NAME, "hello.csv");
        template.sendBodyAndHeader("file:target/files/in", "Hello World", Exchange.FILE_NAME, "hello.txt");
    }

    @Test
    public void hello() throws Exception {
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.assertIsSatisfied(5000L);
    }

}