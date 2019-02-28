package com.cleverbuilder.cameldemos.scenarios;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * https://stackoverflow.com/questions/50610962/camel-2-21-0-pgp-encryption-not-working
 *
 * Add dependencies:
 *
 */
public class PGPEncryptionTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @Test
    public void testEncryptsOK() throws Exception {
        template.sendBody("direct:start", "My cleartext message");

        mockOutput.expectedMessageCount(1);

        mockOutput.assertIsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                String encryptKeyFileName = "pubring.gpg";
                String encryptKeyUserid = "sdude@nowhere.net";

                from("direct:start")
                        //.errorHandler(deadLetterChannel("file:source/dlc1").useOriginalMessage())
                        .process(exchange -> System.out.println( "1 >>> BEFORE:\n " + exchange.getIn().getBody(String.class)))
                        .marshal().pgp(encryptKeyFileName,encryptKeyUserid)
                        .process(exchange -> System.out.println( "1 >>> AFTER: \n" +  exchange.getIn().getBody(String.class)))
                        .to("mock:output");
            }
        };
    }
}
