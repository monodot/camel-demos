package com.cleverbuilder.cameldemos.components;

import com.cleverbuilder.cameldemos.model.Student;
import com.cleverbuilder.cameldemos.model.StudentWithValidation;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Requires camel-bean-validator in POM
 */
public class BeanValidatorComponent extends CamelTestSupport {

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockOutput;

    @EndpointInject(uri = "direct:start")
    ProducerTemplate start;

    @Test
    public void validateJsonToBean() throws Exception {
        try {
            String json = "{\n" +
                    "  \"firstName\": \"John\",\n" +
                    "  \"lastName\" : \"doe\",\n" +
                    "  \"graduated\" : \"false\"\n" +
                    "}";
            start.sendBody(json);
            fail("Bean should have failed validation");
        } catch (CamelExecutionException ex) {
            assertIsInstanceOf(BeanValidationException.class, ex.getCause());
        }
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);
                json.setUnmarshalType(StudentWithValidation.class);

                from("direct:start")
                        .unmarshal(json)
                        .log("Body is now ${body}")
                        .log("Student first name is ${body.firstName}")

                        .to("bean-validator:foo")
                        .to("mock:output");


            }
        };
    }
}
