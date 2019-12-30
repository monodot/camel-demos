package com.cleverbuilder.cameldemos.spring.transform;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleUnmarshal extends AbstractJUnit4SpringContextTests {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Produce("direct:start2")
    private ProducerTemplate template;

    @Test
    public void unmarshalCsv() throws Exception {
        List<String> firstline = new ArrayList<>();
        firstline.add("James");
        firstline.add("Blake");
        firstline.add("London");

        List<String> secondline = new ArrayList<>();
        secondline.add("Thomas");
        secondline.add("Myers");
        secondline.add("London");

        List<List<String>> expected = new ArrayList<>();
        expected.add(firstline);
        expected.add(secondline);

        mockOutput.expectedMessageCount(1);

        String csv = "James,Blake,London\nThomas,Myers,London";

        template.sendBody("direct:start2", csv);

        mockOutput.assertIsSatisfied(5L, TimeUnit.SECONDS);

        List output = mockOutput.getExchanges().get(0).getIn().getBody(List.class);
        Assert.assertEquals(expected, output);
    }

}