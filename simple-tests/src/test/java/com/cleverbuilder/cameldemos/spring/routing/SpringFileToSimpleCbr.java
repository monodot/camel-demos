package com.cleverbuilder.cameldemos.spring.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
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
public class SpringFileToSimpleCbr extends TestSupport {

    @Autowired
    protected ProducerTemplate template;

    @Before
    public void setUp() throws Exception {
        deleteDirectory("target/files");
    }

    @Test
    public void testProcess() throws Exception {
        // TODO - doesn't seem to do anything
        template.sendBodyAndHeader("file:target/files/input", "Cilla Black",
                Exchange.FILE_NAME, "artist.txt");

        assertFileExists("target/files/cilla_black/artist.txt");
    }

}