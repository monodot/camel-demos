package xyz.tomd.cameldemos.springboot.components.jms;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(CamelSpringBootRunner.class)
@MockEndpointsAndSkip("jms:*")
@EnableAutoConfiguration
@SpringBootTest
public class JmsComponentTest {

    @Autowired
    CamelContext camelContext;

    @EndpointInject("mock:jms:queue:MY.QUEUE")
    MockEndpoint mockJms;

    @Test
    public void testJms() throws Exception {
        mockJms.expectedMessageCount(1);
        mockJms.expectedBodiesReceived("Test message!");

        // Wait for 3 seconds
        mockJms.assertIsSatisfied(3000L);
    }

    // Adding a sample Camel route here as an example.
    //
    // However, in a real application, you wouldn't add a route here;
    // the test discovers your RouteBuilder class under src/main/java/...
    @Configuration
    @EnableAutoConfiguration
    public static class TestConfig {

        @Bean
        public RouteBuilder routeBuilder() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    // Use a timer to generate messages every 5 seconds
                    from("timer:mytimer?period=5000")
                            .transform(constant("Test message!"))
                            .to("jms:queue:MY.QUEUE");
                }
            };
        }

    }

}
