package com.cleverbuilder.cameldemos.components;

import com.cleverbuilder.cameldemos.model.Person;
import com.cleverbuilder.cameldemos.model.PersonImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.test.junit4.TestSupport.assertIsInstanceOf;
import static org.apache.camel.test.junit4.TestSupport.resolveMandatoryEndpoint;

public class DirectVMTest {

    private static Logger LOG = LoggerFactory.getLogger(DirectVMTest.class);

    private CamelContext camelContext1;
    private CamelContext camelContext2;

    private ProducerTemplate templateInContext1;

    @Before
    public void setUp() throws Exception {
        camelContext1 = new DefaultCamelContext();
        camelContext1.addRoutes(createRouteBuilder1());

        templateInContext1 = camelContext1.createProducerTemplate();

        camelContext1.start();

        camelContext2 = new DefaultCamelContext();
        camelContext2.addRoutes(createRouteBuilder2());
        camelContext2.start();

//        ServiceHelper.startServices(camelContext2);

//        camelContext2.addRoutes(createRouteBuilder2());


    }

    @After
    public void tearDown() throws Exception {
        camelContext1.stop();
        camelContext2.stop();
    }

    @Test
    public void testPassJavaObjectBetweenDirectVM() throws Exception {
        MockEndpoint mock = camelContext2.getEndpoint("mock:result", MockEndpoint.class);
        mock.expectedMessageCount(1);

        Person person = new PersonImpl("Dave", "Lee-Travis");

        templateInContext1.sendBody("direct:start", person);

        MockEndpoint.assertIsSatisfied(camelContext2);

        assertIsInstanceOf(PersonImpl.class, mock.getExchanges().get(0).getMessage().getBody(PersonImpl.class));
    }

    /**
     * Set up the routes for Camel Context 1
     */
    private RouteBuilder createRouteBuilder1() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .log("Hello, world!")
                        .to("direct-vm:hello");
            }
        };
    }

    /**
     * Set up the routes for Camel Context 2
     */
    private RouteBuilder createRouteBuilder2() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct-vm:hello")
                        .log("Direct VM invoked!")
                        .to("mock:result");
            }
        };
    }

}
