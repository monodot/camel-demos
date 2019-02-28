package com.cleverbuilder.cameldemos.scaffolding;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

/**
 * Created by tdonohue on 21/05/2018.
 */
public class AddRouteBuilder {

    @Test
    public void testAddRouteBuilder() throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new MyRouteBuilder());

        context.start();

        Thread.sleep(10000);

    }

    public static class MyRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("timer:hello?period=5000")
                    .log("Hello");
        }
    }
}
