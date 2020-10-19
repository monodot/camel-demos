package com.cleverbuilder.cameldemos.filter;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SimpleFileNameFilter extends CamelTestSupport {

    @Override
    @Before
    public void setUp() throws Exception {
        deleteDirectory("target/files");
        super.setUp();
    }

    @Test
    public void testIt() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:other");
        mock.expectedMessageCount(1);

        template.sendBodyAndHeader("file://target/files/input", "Cilla Black",
                Exchange.FILE_NAME, "MYFILE-001.txt");
        template.sendBodyAndHeader("file://target/files/input", "Cilla Black",
                Exchange.FILE_NAME, "OTHERFILE-001.txt");

        assertMockEndpointsSatisfied();
        Thread.sleep(1000L); // give the component time to move the file (dirty hack)
        assertFileExists("target/files/output/MYFILE-001.txt");
        assertFileNotExists("target/files/output/OTHERFILE-001.txt");

    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:target/files/input")
                        .to("log:mylogger?showHeaders=true&showBody=true")
                        .choice()
                            .when(header("CamelFileName").startsWith("MYFILE"))
                                .log("Moving to output folder")
                                .to("file:target/files/output")
                            .otherwise()
                                .log("Moving to other folder")
                                .to("mock:other");

            }
        };
    }

}
