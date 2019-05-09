package com.cleverbuilder.cameldemos.scaffolding;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by tdonohue on 21/05/2018.
 */
public class AddRouteBuilderTest {

    @Test
    public void testAddRouteBuilder() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new MyRouteBuilder());
        context.start();

        // Assert that two Exchanges are generated (should take 6 seconds)
        NotifyBuilder notifyBuilder = new NotifyBuilder(context).whenDone(2).create();
        assertTrue(notifyBuilder.matches(10, TimeUnit.SECONDS));
    }

    public static class MyRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("timer:hello?period=3000")
                    .log("Hello");
        }
    }
}
