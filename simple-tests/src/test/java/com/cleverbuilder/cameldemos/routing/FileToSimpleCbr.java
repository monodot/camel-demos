package com.cleverbuilder.cameldemos.routing;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by tdonohue on 19/05/2018.
 */
public class FileToSimpleCbr extends CamelTestSupport {

    @Override
    @Before
    public void setUp() throws Exception {
        deleteDirectory("target/files");
        super.setUp();
    }

    @Test
    public void testFileToSimple() throws Exception {
        template.sendBodyAndHeader("file://target/files/input", "Cilla Black",
                Exchange.FILE_NAME, "artist.txt");

        Thread.sleep(2000L);

        assertFileExists("target/files/output/cilla_black/artist.txt");
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file://target/files/input")
                        .log("Received file!")
                        .to("direct:process-file");

                from("direct:process-file")
                        .log("Processing file!")
                        .choice()
                            .when(simple("${bodyAs(String)} contains 'Cilla'"))
                            .log("File matched routing rule! Moving to target location")
                            .to("file:target/files/output/cilla_black");

            }
        };
    }
}
