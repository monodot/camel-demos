package xyz.tomd.cameldemos.springboot.artemis;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelArtemisRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {

        // Send a message to a queue every 5 seconds
        from("timer:mytimer?period=5000").routeId("generate-route")
                .transform(constant("HELLO from Camel!"))
                .to("jms:queue:INCOMING");

        // Receive the message from the queue and send it to another queue
        from("jms:queue:INCOMING").routeId("receive-route")
                .log("Received a message - ${body} - sending to outbound queue")
                .to("jms:queue:PROCESSED?exchangePattern=InOnly");

    }
}
