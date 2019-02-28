package com.cleverbuilder.cameldemos.spring.scenarios.dailytimer;

import com.cleverbuilder.cameldemos.spring.scenarios.errorhandler.Transformer;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DailyTimerTest extends TestSupport {

  @Autowired
  CamelContext context;

  @EndpointInject(uri = "mock:output")
  MockEndpoint mockOutput;

  @Test
  public void testRoute() throws Exception {
    mockOutput.expectedMinimumMessageCount(2);

    context.getRouteDefinitions().get(0).adviceWith(context,
            new AdviceWithRouteBuilder() {
              @Override
              public void configure() throws Exception {
                weaveAddLast().to("mock:output");
              }
            });

    mockOutput.assertIsSatisfied();

  }

}
