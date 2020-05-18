package com.cleverbuilder.cameldemos.scenarios;

import com.ibm.wsdl.util.xml.DOM2Writer;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.util.UrlUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.WSDLGetUtils;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.Service;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Map;

public class GenerateWsdlTest extends CamelTestSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateWsdlTest.class);

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("bean:wsdlGenerator");
            }
        };
    }

    @Test
    public void testReturnsHelloWorld() throws Exception {
        Object response = template.requestBody("direct:start", "YEP");

        assertEquals("Hello world!", ((String) response));
    }


    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        registry.bind("wsdlGenerator", new WsdlGenerator());
    }

    @WebService(targetNamespace = "org.apache.cxf.ws.WsdlTest")
    public static class StuffImpl {
        @WebMethod
        public void doStuff() {
        }
    }


    class WsdlGenerator {

        public String generateWsdl() {
            // See: WsdlGetUtilsTest
            JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
            factory.setServiceBean(new StuffImpl());
            factory.setAddress("http://localhost:" + 8084 + "/Stuff");
            Server server = factory.create();

            try {
                Message message = new MessageImpl();
                Exchange exchange = new ExchangeImpl();
                exchange.put(Bus.class, BusFactory.newInstance().createBus());
//                exchange.put(Bus.class, getBus());
                exchange.put(Service.class, server.getEndpoint().getService());
                exchange.put(Endpoint.class, server.getEndpoint());
                message.setExchange(exchange);

                Map<String, String> map = UrlUtils.parseQueryString("wsdl");
                String baseUri = "http://localhost:" + 8084 + "/Stuff";
                String ctx = "/Stuff";

                WSDLGetUtils utils = new WSDLGetUtils();
                Document doc = utils.getDocument(message, baseUri, map, ctx, server.getEndpoint().getEndpointInfo());

                LOGGER.info("HIYA..." + doc.toString());
//                W3CDOMStreamWriter writer = new W3CDOMStreamWriter();
                String output = DOM2Writer.nodeToString(doc.getDocumentElement());
                LOGGER.info("Done?");

            } finally {
                server.stop();
            }


            return "Hello world!";
        }

    }
}
