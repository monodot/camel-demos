package com.cleverbuilder.cameldemos.spring.components;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by tdonohue on 19/02/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration
public class RestDslSparkRest extends TestSupport {

    @Autowired
    protected ProducerTemplate template;

    @Before
    public void setUp() throws Exception {
        deleteDirectory("target/output/customers");
    }

    @Test
    public void testTwoStepsInSubroute() {
        Exchange response = template.send("http://localhost:8080/testing", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello, world!");
            }
        });

        assertNotNull(response);
        assertEquals("Thanks!", response.getMessage().getBody(String.class));
    }

}