package com.cleverbuilder.cameldemos.spring.components;

import com.cleverbuilder.cameldemos.ws.GetAllBooksResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CxfCodeFirstTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("cxf://http://localhost:8081/BookService?" +
                        "serviceClass=com.cleverbuilder.cameldemos.ws.BookService" +
                        "&dataFormat=PAYLOAD")
                        .log("Received request")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                GetAllBooksResponse response = new GetAllBooksResponse();
                                response.setContent("HELLO");
                                exchange.getMessage().setBody(response);
//                                exchange.getMessage().setBody("Hello.....");
                            }
                        })
                ;
            }
        };
    }

    @Test
    public void testSomething() throws Exception {
        Thread.sleep(30000L);
        // See Camel source: CxfConsumerTest.java, CxfHolderConsumerTest.java

//        // This must be a JaxWsProxyFactory, to be able to read the JAX-WS annotations on BookService (which specify things like namespaces, etc.)
//        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
//
//        ClientFactoryBean clientBean = proxyFactory.getClientFactoryBean();
//        clientBean.setAddress("http://localhost:" + 8081 + "/BookService");
//        clientBean.setServiceClass(BookService.class);
//        clientBean.setBus(BusFactory.newInstance().createBus());
//
//        BookService bookService = (BookService) proxyFactory.create();
//        GetAllBooksResponse result = bookService.getAllBooks("REQUESTING SOMETHING");
//
//        assertEquals(GetAllBooksResponse.class, result.getClass());



    }
}
