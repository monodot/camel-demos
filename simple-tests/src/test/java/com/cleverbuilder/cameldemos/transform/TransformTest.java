package com.cleverbuilder.cameldemos.transform;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by tdonohue on 08/05/2018.
 */
public class TransformTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:csv-json-output")
    MockEndpoint mockCsvJsonOutput;

    @Test
    public void testCsvToJsonWithHeader() throws InterruptedException {
        mockCsvJsonOutput.expectedMessageCount(1);
        mockCsvJsonOutput.expectedBodiesReceived("[{\"First Name\":\"Jonny\",\"Last Name\":\"Briggs\",\"City\":\"London\"},{\"First Name\":\"Carly\",\"Last Name\":\"Simon\",\"City\":\"New York City\"}]");

        template.sendBody("direct:start", "First Name,Last Name,City\n" +
                        "Jonny,Briggs,London\n" +
                        "Carly,Simon,New York City");

        assertMockEndpointsSatisfied();

    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // Set up a CSV data format and store values in a Map,
                // like Header=Value,Header=Value,Header=Value, etc.
                CsvDataFormat csv = new CsvDataFormat();
                csv.setSkipHeaderRecord(true);
                csv.setUseMaps(true);

                // Set up a simple JSON output format
                JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);

                from("direct:start")
                        .unmarshal(csv)
                        .marshal(json)
                        .to("mock:csv-json-output");
            }
        };
    }
}
