package xyz.tomd.cameldemos.playgrounds.springboot;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // I've got my nice Camel REST service here...
        // Using the servlet component.
        restConfiguration()
                .bindingMode(RestBindingMode.auto)
                .component("servlet");

        rest()
                .path("/go-sports")

                .get("/")
                .route()
                .transform(simple("I'm your resource for all the sports!"))
                .endRest()

                .post("/")
                .route()
                .to("log:mylogger?showAll=true")
                .transform(simple("Thanks, sport!"))
                .endRest();

    }
}
