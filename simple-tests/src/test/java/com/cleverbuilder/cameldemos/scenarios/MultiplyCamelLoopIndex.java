package com.cleverbuilder.cameldemos.scenarios;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Created by tdonohue on 13/09/2018.
 */
public class MultiplyCamelLoopIndex extends CamelTestSupport {

  @EndpointInject(uri = "mock:output")
  MockEndpoint mockOutput;

  @Test
  public void test() throws Exception {
    mockOutput.expectedPropertyReceived("OffsetCount", "600");

    template.sendBody("direct:start", null);

    assertMockEndpointsSatisfied();
  }

  @Override
  protected RoutesBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("direct:start")
                .loop(7)
//                  .to("log:mylogger?showAll=true")
//                  .log("The loop index is now: ${property.CamelLoopIndex}")
                  .setProperty("OffsetCount")
                    .spel("#{exchange.properties['CamelLoopIndex']  * 100}")
                .end() // end loop
                .to("mock:output");
      }
    };
  }
}
