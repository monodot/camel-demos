package xyz.tomd.cameldemos.springboot.components.xpath;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by tdonohue on 23/05/2018.
 */
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(
        properties = {"foo = //greeting/text = 'Hello, world!'"}
)
public class XpathProperty {

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate template;

    @EndpointInject("mock:output-filter")
    private MockEndpoint mockOutputFilter;

    @EndpointInject("mock:output-choice")
    private MockEndpoint mockOutputChoice;

    @Test
    public void testFilter() throws Exception {
        mockOutputFilter.expectedMessageCount(1);

        template.sendBody("direct:filter", "<greeting><text>Hello, world!</text></greeting>");

        mockOutputFilter.assertIsSatisfied();
    }

    @Test
    public void testChoice() throws Exception {
        mockOutputChoice.expectedMessageCount(1);

        template.sendBody("direct:choice", "<greeting><text>Hello, world!</text></greeting>");

        mockOutputChoice.assertIsSatisfied();
    }

    @Configuration
    @EnableAutoConfiguration
    public static class TestConfig {

        @Bean
        public RouteBuilder routeBuilder() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:filter")
                            // Property gets expanded here
                            .filter().xpath("{{foo}}")
                            // FilterDefinition
                                    .log("Passed filter!")
                                    .to("mock:output-filter");

                    from("direct:choice")
                            // Property does not get expanded here
                            .choice()
                                    .when(xpath("{{foo}}"))
                                    .log("Passed choice!")
                                    .to("mock:output-choice");
                }
            };
        }
    }

}
