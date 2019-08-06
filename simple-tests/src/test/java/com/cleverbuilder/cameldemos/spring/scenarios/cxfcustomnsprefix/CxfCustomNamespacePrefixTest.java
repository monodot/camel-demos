package com.cleverbuilder.cameldemos.spring.scenarios.cxfcustomnsprefix;

import com.oracle.webservices.internal.literal.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.TestPropertySource;
import xyz.tomd.customerservice.types.CustomerItem;
import xyz.tomd.customerservice.types.GetCustomersResponseElement;
import xyz.tomd.customerservice.types.ObjectFactory;

/**
 * Testing out customising the response from CXF.
 * For example, namespaces in the response may need to be manually massaged,
 * or set in a certain way which is expected by a legacy web service client.
 */
@TestPropertySource("classpath:application.properties")
public class CxfCustomNamespacePrefixTest extends CamelSpringTestSupport {

    private static final int CXF_PORT = 8189;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
                "com/cleverbuilder/cameldemos/spring/scenarios/" +
                        "cxfcustomnsprefix/CxfCustomNamespacePrefixTest-context.xml");
    }

    /**
     * Use the Camel HTTP component to invoke the service and check that
     * the response contains the correctly-prefixed FileItem attribute value
     * @throws Exception
     */
    @Test
    public void testWithHttpComponent() throws Exception {
        String request = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getCustomersElement xmlns=\"http://customerservice.tomd.xyz/types/\" xmlns:ns2=\"http://www.oracle.com/webservices/internal/literal\">" +
                "<location xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<criteria xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "</getCustomersElement>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        String response = template.requestBody(
                "http://localhost:" + CXF_PORT + "/CustomerService",
                request, String.class);



        assertTrue("CustomerItem element is not prefixed with ns0", response.contains("ns0:CustomerItem"));
    }

    /**
     * Creates some fake data to return in the response.
     */
    public static class TestResponseProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            ObjectFactory factory = new ObjectFactory();

            CustomerItem customer = factory.createCustomerItem();
            customer.setFirstName("Cilla");
            customer.setLastName("Black");

            List customers = new List();
            customers.getItem().add(customer);

            GetCustomersResponseElement response = factory.createGetCustomersResponseElement();
            response.setResult(customers);

            exchange.getMessage().setBody(response);
        }
    }

}
