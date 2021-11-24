package com.cleverbuilder.cameldemos.scenarios;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtractCoordsFromJsonTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:input")
                        // From the first element in the array, get the first coordinate pair
                        .transform().jsonpath("$[0].geometry.coordinates[0][0]", List.class)
                        .to("log:mylogger?showAll=true")
                        .log("The first coordinate is: ${body[0]}")
                        .log("The second coordinate is: ${body[1]}")

                        .to("mock:output");
            }
        };
    }

    @Test
    public void testCanExtractCoordinates() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:output");

        // The expected coordinates returned by our JSONPath expression
        List coordinates = new ArrayList<Double>();
        coordinates.add(-104.05);
        coordinates.add(48.99);

        // Wrap it in a List (element 0 will be our first expected message body)
        List expectedBodies = new ArrayList(Collections.singletonList(coordinates));

        mock.expectedBodiesReceived(expectedBodies);

        // Example taken from: https://leafletjs.com/examples/geojson/
        template.sendBody("direct:input", "[{\n" +
                "    \"type\": \"Feature\",\n" +
                "    \"properties\": {\"party\": \"Republican\"},\n" +
                "    \"geometry\": {\n" +
                "        \"type\": \"Polygon\",\n" +
                "        \"coordinates\": [[\n" +
                "            [-104.05, 48.99],\n" +
                "            [-97.22,  48.98],\n" +
                "            [-96.58,  45.94],\n" +
                "            [-104.03, 45.94],\n" +
                "            [-104.05, 48.99]\n" +
                "        ]]\n" +
                "    }\n" +
                "}, {\n" +
                "    \"type\": \"Feature\",\n" +
                "    \"properties\": {\"party\": \"Democrat\"},\n" +
                "    \"geometry\": {\n" +
                "        \"type\": \"Polygon\",\n" +
                "        \"coordinates\": [[\n" +
                "            [-109.05, 41.00],\n" +
                "            [-102.06, 40.99],\n" +
                "            [-102.03, 36.99],\n" +
                "            [-109.04, 36.99],\n" +
                "            [-109.05, 41.00]\n" +
                "        ]]\n" +
                "    }\n" +
                "}]");

        assertMockEndpointsSatisfied();
    }

}
