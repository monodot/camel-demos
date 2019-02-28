package com.cleverbuilder.cameldemos.spring.transform;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleUnmarshal extends TestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Autowired
    protected ProducerTemplate template;

    @Test
    public void unmarshalCsv() throws Exception {
        List firstline = new ArrayList<>();
        firstline.add("James");
        firstline.add("Blake");
        firstline.add("London");

        List secondline = new ArrayList<>();
        secondline.add("Thomas");
        secondline.add("Myers");
        secondline.add("London");

        List expected = new ArrayList<>();
        expected.add(firstline);
        expected.add(secondline);

        mockOutput.expectedMessageCount(1);

        String csv = "James,Blake,London\nThomas,Myers,London";

        template.sendBody("direct:start", csv);

        mockOutput.assertIsSatisfied();

        List output = mockOutput.getExchanges().get(0).getIn().getBody(List.class);
        assertEquals(expected, output);
    }

}