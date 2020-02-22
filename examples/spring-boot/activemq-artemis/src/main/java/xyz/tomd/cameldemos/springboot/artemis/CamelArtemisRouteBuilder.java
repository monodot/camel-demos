package xyz.tomd.cameldemos.springboot.artemis;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelArtemisRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {

        // Send a message to a queue every 5 seconds
        from("timer:mytimer?period=5000")
                .setBody(constant("HELLO from Camel!"))
                .to("jms:queue:HELLO.WORLD");

        // Receive a message from a queue
        from("jms:queue:HELLO.WORLD")
                .log("Received a message - ${body}");

    }
}
