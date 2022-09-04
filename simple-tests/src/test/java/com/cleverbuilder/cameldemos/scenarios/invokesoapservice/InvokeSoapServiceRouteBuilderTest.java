package com.cleverbuilder.cameldemos.scenarios.invokesoapservice;

import com.cleverbuilder.bookservice.GetBookResponse;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.cxf.message.MessageContentsList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.ws.Endpoint;

public class InvokeSoapServiceRouteBuilderTest extends CamelTestSupport {

    private static final String TEST_ENDPOINT_ADDRESS = "http://localhost:8423/test/BookService";

    protected Endpoint endpoint;

    @Before
    public void startService() throws Exception {
        // Starts a simple service that we can test our route with
        BookServiceTestImpl bookServiceTest = new BookServiceTestImpl();
        endpoint = Endpoint.publish(TEST_ENDPOINT_ADDRESS, bookServiceTest);
    }

    @After
    public void stopTestService() throws Exception {
        endpoint.stop();
    }

//    @EndpointInject(uri = "mock:output")
//    MockEndpoint mockOutput;

    @Test
    public void testInvokeSoapService() throws Exception {
        MockEndpoint mockOutput = getMockEndpoint("mock:output");
        mockOutput.expectedMessageCount(1);

        template.sendBody("direct:start", null);

        assertMockEndpointsSatisfied();

        MessageContentsList response = mockOutput.getExchanges().get(0).getMessage().getBody(MessageContentsList.class);
        GetBookResponse getBookResponse = (GetBookResponse) response.get(0);
        assertEquals("Fifty Shades of Pink", getBookResponse.getBook().getTitle());
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new InvokeSoapServiceRouteBuilder();
    }


}