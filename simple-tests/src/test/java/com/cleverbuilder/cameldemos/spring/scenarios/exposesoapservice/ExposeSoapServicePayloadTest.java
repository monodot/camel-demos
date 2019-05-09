package com.cleverbuilder.cameldemos.spring.scenarios.exposesoapservice;

import com.cleverbuilder.bookservice.BookService;
import com.cleverbuilder.bookservice.GetAllBooks;
import com.cleverbuilder.bookservice.GetAllBooksResponse;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.cxf.BusFactory;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.assertEquals;

@RunWith(CamelSpringRunner.class)
// This will look by default for ExposeSoapServicePayloadTest-context.xml on the classpath, or the XML location can be explicitly specified
@ContextConfiguration
@TestPropertySource("classpath:application.properties")
public class ExposeSoapServicePayloadTest {

    private static final int CXF_PORT = 8189;

    @Test
    public void testServiceResponseIsExpectedString() throws Exception {
        // See Camel source: CxfConsumerTest.java, CxfHolderConsumerTest.java

        // This must be a JaxWsProxyFactory, to be able to read the JAX-WS annotations on BookService (which specify things like namespaces, etc.)
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();

        ClientFactoryBean clientBean = proxyFactory.getClientFactoryBean();
        clientBean.setAddress("http://localhost:" + CXF_PORT + "/BookService");
        clientBean.setServiceClass(BookService.class);
        clientBean.setBus(BusFactory.newInstance().createBus());

        BookService bookService = (BookService) proxyFactory.create();
        GetAllBooksResponse result = bookService.getAllBooks(new GetAllBooks());

        assertEquals(GetAllBooksResponse.class, result.getClass());

    }

}
