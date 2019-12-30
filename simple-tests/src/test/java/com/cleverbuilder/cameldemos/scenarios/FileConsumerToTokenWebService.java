package com.cleverbuilder.cameldemos.scenarios;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.BindToRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by tdonohue on 10/04/2018.
 */
public class FileConsumerToTokenWebService extends CamelTestSupport {


    @EndpointInject(uri = "mock:webService")
    protected MockEndpoint mockWebService;

    @EndpointInject(uri = "mock:tokenService")
    protected MockEndpoint mockTokenService;

    @BindToRegistry("myAggregationStrategy")
    MyAggregationStrategy myAggregationStrategy = new MyAggregationStrategy();

    @Test
    public void processBigFile() throws Exception {
        // Given
        mockWebService.expectedMessageCount(1);

        // Return a fake token
        mockTokenService.whenAnyExchangeReceived(
                e -> e.getIn().setBody("AZJWIWEJASIDJIQABSHWVLTMIWJDSIWICWIO"));

        // When
        sendBody("direct:fileConsumer", "Some data file - beep boop");

        // Then
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:fileConsumer")
                        .enrich("direct:getToken", new MyAggregationStrategy())
                        .to("log:mylogger?showAll=true")
                        .to("mock:webService");

                from("direct:getToken")
                        .setBody(simple("username=scott&password=tiger"))
                        .to("mock:tokenService"); // this would be HTTP component
            }

        };

    }

/*
    @Override
    protected Registry createCamelRegistry() throws Exception {
        return super.createCamelRegistry();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("myAggregationStrategy", new MyAggregationStrategy());
        return registry;
    }
*/

    class MyAggregationStrategy implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

            // Parse the authentication token response here
            // Put the response (the token) from the web service in the body
            oldExchange.getIn().setHeader("Authorization",
                    newExchange.getIn().getBody());

            return oldExchange;
        }
    }
}
