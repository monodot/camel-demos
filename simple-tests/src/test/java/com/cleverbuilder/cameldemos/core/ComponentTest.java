package com.cleverbuilder.cameldemos.core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.BindToRegistry;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ComponentTest extends CamelTestSupport {

    @Test
    public void name() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:queue:HELLO.WORLD?connectionFactory=#myCF")
                        .log("Received message - ${body}");
            }
        });

        template.sendBody("jms:queue:HELLO.WORLD", "Some body");
    }

    @BindToRegistry("myCF")
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();


}
