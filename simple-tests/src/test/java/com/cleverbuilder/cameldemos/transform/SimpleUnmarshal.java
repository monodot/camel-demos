package com.cleverbuilder.cameldemos.transform;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tdonohue on 19/05/2018.
 */
public class SimpleUnmarshal extends CamelTestSupport {

    private static final String csvInput = "James,Blake,London\nThomas,Myers,London";

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @EndpointInject(uri = "mock:output-json")
    MockEndpoint mockOutputJson;

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

        template.sendBody("direct:start", csvInput);

        assertMockEndpointsSatisfied();

        List output = mockOutput.getExchanges().get(0).getIn().getBody(List.class);
        assertEquals(expected, output);
    }

    @Test
    public void testCsvJson() throws Exception {
        mockOutputJson.expectedMessageCount(1);

        // Expect we'll receive an array containing 2 items, each of them being an array of 3 items
        mockOutputJson.expectedBodiesReceived("[[\"James\",\"Blake\",\"London\"],[\"Thomas\",\"Myers\",\"London\"]]");

        template.sendBody("direct:csv-json", csvInput);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // requires camel-csv
                from("direct:start")
                        .unmarshal().csv()
                        .to("log:mylogger?showHeaders=true&showBody=true")
                        .to("mock:output");

                from("direct:csv-json")
                        .unmarshal().csv()
                        .marshal().json()
                        .to("log:mylogger?showHeaders=true&showBody=true")
                        .to("mock:output-json");
            }
        };
    }

}