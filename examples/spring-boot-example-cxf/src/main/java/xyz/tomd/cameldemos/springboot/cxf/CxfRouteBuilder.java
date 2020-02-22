package xyz.tomd.cameldemos.springboot.cxf;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CxfRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("cxf://BookService?" +
                "serviceClass=com.cleverbuilder.cameldemos.ws.BookService" +
                "&serviceName=BookService" +
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
                });
    }
}
