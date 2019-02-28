package com.cleverbuilder.cameldemos.core;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;

/**
 * Created by tdonohue on 03/06/2018.
 */
public class BeanProcessor extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("xslt:com/cleverbuilder/cameldemos/components/mytransform.xslt")
                        .log("${body}")
                        .to("mock:output");
            }
        };
    }


}
