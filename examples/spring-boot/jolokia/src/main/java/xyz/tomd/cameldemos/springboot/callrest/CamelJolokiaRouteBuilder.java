package xyz.tomd.cameldemos.springboot.callrest;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CamelJolokiaRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {
        from("timer:mytimer?period=1000")
                .id("TimerRoute1sec")
                .log("Hello every 1 second!");

        from("timer:mytimer2?period=5000")
                .id("TimerRoute5sec")
                .log("Goodbye every 5 seconds!");
    }
}
