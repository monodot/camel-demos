package com.cleverbuilder.cameldemos.components;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * Created by tdonohue on 07/06/2018.
 */
public class SagaComponent extends CamelTestSupport {


    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .saga();

                // TODO complete this
            }
        };
    }
}
