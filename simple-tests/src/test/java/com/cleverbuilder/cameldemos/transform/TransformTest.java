package com.cleverbuilder.cameldemos.transform;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
//import org.apache.camel.model.dataformat.CsvDataFormat;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.File;

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

    @Test
    public void testAlbumTracksCsv() throws InterruptedException {
        String expected = "[{\"Artist\":\"BK\",\"Title\":\"P.O.S. 51\",\"Remix\":\"\",\"Length\":\"5:25\"},{\"Artist\":\"Oxia\",\"Title\":\"Contrast\",\"Remix\":\"\",\"Length\":\"3:03\"},{\"Artist\":\"Exit EEE\",\"Title\":\"Epidemic\",\"Remix\":\"Edison Factor Remix\",\"Length\":\"5:25\"},{\"Artist\":\"Mark Gray\",\"Title\":\"99.9\",\"Remix\":\"\",\"Length\":\"4:46\"}";
        mockCsvJsonOutput.expectedMessageCount(1);

        template.sendBody("direct:start", new File("src/test/data/album_tracks.csv"));

        assertMockEndpointsSatisfied();

        String output = mockCsvJsonOutput.getExchanges().get(0).getIn().getBody(String.class);
        assertTrue(output.startsWith(expected));
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // Set up a CSV data format and store values in a Map,
                // like Header=Value,Header=Value,Header=Value, etc.

                // Set up a CSV data format
                CsvDataFormat csv = new CsvDataFormat();
                csv.setUseMaps(true);

                // Set up a simple JSON output format
                JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);

                from("direct:start")
                        .unmarshal(csv)
                        .marshal(json)
                        .to("log:mylogger?showHeaders=true")
                        .to("mock:csv-json-output");
            }
        };
    }
}
