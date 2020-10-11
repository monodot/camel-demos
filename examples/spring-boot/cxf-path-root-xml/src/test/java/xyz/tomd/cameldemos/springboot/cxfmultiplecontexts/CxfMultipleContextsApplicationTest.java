package xyz.tomd.cameldemos.springboot.cxfmultiplecontexts;

import com.cleverbuilder.bookservice.BookService;
import com.cleverbuilder.bookservice.GetAllBooks;
import com.cleverbuilder.bookservice.GetAllBooksResponse;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.cxf.BusFactory;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CxfMultipleContextsApplicationTest {

    @LocalServerPort
    int port;

    @Value("${cxf.path}")
    String cxfPath;

    @Value("${service1.path}")
    String service1Path;

    @Value("${service2.path}")
    String service2Path;

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate template;

    @Test
    public void testTwoEndpointsAreAvailable() throws Exception {
        // This Factory will create beans which can understand JAX-WS annotations
        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();

        // This Factory creates a client for the service
        ClientFactoryBean clientBean1 = proxyFactory.getClientFactoryBean();

        // Configure for endpoint 1
        clientBean1.setAddress("http://localhost:" + port + cxfPath + service1Path);
        clientBean1.setServiceClass(BookService.class); // This should be the `interface` generated from the WSDL
        clientBean1.setBus(BusFactory.newInstance().createBus());

        BookService bookServiceClient1 = (BookService) proxyFactory.create();

        // Invoke an operation named `GetAllBooks`
        GetAllBooksResponse result1 = bookServiceClient1.getAllBooks(new GetAllBooks());


        // This Factory creates a client for the service
        ClientFactoryBean clientBean2 = proxyFactory.getClientFactoryBean();

        // Configure for endpoint 1
        clientBean2.setAddress("http://localhost:" + port + cxfPath + service2Path);
        clientBean2.setServiceClass(BookService.class); // This should be the `interface` generated from the WSDL
        clientBean2.setBus(BusFactory.newInstance().createBus());

        BookService bookServiceClient2 = (BookService) proxyFactory.create();

        // Invoke an operation named `GetAllBooks`
        GetAllBooksResponse result2 = bookServiceClient2.getAllBooks(new GetAllBooks());

    }


}