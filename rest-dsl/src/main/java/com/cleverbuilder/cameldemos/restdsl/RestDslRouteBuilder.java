package com.cleverbuilder.cameldemos.restdsl;

import com.cleverbuilder.cameldemos.restdsl.types.PostRequestType;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RestDslRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        /**
         * Use 'restlet', which is a very simple component for providing REST
         * services. Ensure that camel-restlet or camel-restlet-starter is
         * included as a Maven dependency first.
         */
        restConfiguration()
                .component("restlet")
                .host("localhost").port("8080")
                .bindingMode(RestBindingMode.auto);

        /**
         * Configure the REST API (POST, GET, etc.)
         */
        rest().path("/api").consumes("application/json")
                .get()
                    .to("bean:helloBean")
                .post().type(PostRequestType.class)
                    .to("bean:postBean");
    }
}
