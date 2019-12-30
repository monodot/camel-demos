package com.cleverbuilder.cameldemos.components;

import com.cleverbuilder.cameldemos.model.ResponseObject;
import com.cleverbuilder.cameldemos.model.Student;
import org.apache.camel.BindToRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class RestConsumerTest extends CamelTestSupport {

    private static final String COMPONENT = "jetty";
    private static final String PRODUCER_COMPONENT = "undertow"; // Jetty is no longer a producer

    @BindToRegistry("studentService")
    StudentService studentService = new StudentService();

    @Override
    @Before
    public void setUp() throws Exception {
        deleteDirectory("target/output");
        super.setUp();
    }

    /**
     * Sending a POST request
     */
    @Test
    public void testRestPost() {
        Exchange response = template.send("http://localhost:8080/customers", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello, world!");
            }
        });

        assertNotNull(response);
        assertEquals("POST invoked!", response.getMessage().getBody(String.class));
}

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                restConfiguration()
                        .producerComponent(PRODUCER_COMPONENT);

                from("direct:start")
                        .setBody(constant("Hello"))
                        .to("rest:post:customers");


                restConfiguration()
                        .component(COMPONENT)
                        .bindingMode(RestBindingMode.json)

                        // required because Camel needs to know which component
                        // will be used to *invoke* REST services
                        .producerComponent(PRODUCER_COMPONENT)
                        .host("localhost").port("8080");

                rest("/customers")
                        .get().to("direct:get")
                        .post().bindingMode(RestBindingMode.off).to("direct:post") //.to("mock:post")
                        .get("/search?country={country}&colour={colour}&city={city}").bindingMode(RestBindingMode.off).to("direct:search")
                        .delete().to("direct:delete");

                from("direct:post").setBody(constant("POST invoked!"));
                from("direct:get").setBody(constant("GET invoked!"));
                from("direct:delete").setBody(constant("DELETE invoked!"));
                from("direct:search").setBody(simple("Search invoked! Country = ${header.country}, City = ${header.city}"));

                rest("/testing")
                        .bindingMode(RestBindingMode.off)
                        .get()
                            .to("direct:get")
                        .post().route()
                            .to("file:target/output/customers")
                            .setBody(constant("Thanks!"));

                rest("/simplepost")
                        .bindingMode(RestBindingMode.off)
                        .post().route()
                            .log("Body received was: ${body}")
                            .setBody(simple("Thanks, ${body}!"));

                rest("/books/{bookId}")
//                        .bindingMode(RestBindingMode.off)
                        .get().route()
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                    Map response = new HashMap<String, String>();
                                    response.put("bookId", exchange.getMessage().getHeader("bookId"));
                                    exchange.getMessage().setBody(response);
                                }
                            });

                rest("/students")
                        .bindingMode(RestBindingMode.json)
                        .get().outType(Student[].class).route()
                            .to("bean:studentService?method=getStudents")
                            .log("Body is now ${body}")
                            .endRest()
                        .post().type(Student.class).produces("application/json").route()
                            .log("Receiving a student - ${body.getFirstName()}")
                            .to("bean:studentService?method=addStudent")
                            .process(exchange -> {
                                ResponseObject response = new ResponseObject();
                                response.setStatus("OK");
                                response.setAdded("yes");
                                exchange.getIn().setBody(response);
                            });

                from("direct:invoke-rest")
                        .setHeader("countryCode", constant("GB"))
                        .to("rest:get:country/get/iso2code/{countryCode}?host=services.groupkt.com:80");

                from("direct:invoke-rest-post")
                        .to("rest:post:students?host=localhost:8080"
                                + "&outType=com.cleverbuilder.cameldemos.model.ResponseObject")
                        .log("Response status was ${body.status}");



            }
        };
    }

    public class StudentService {

        private List<Student> students;

        public StudentService() {
            students = new ArrayList();
        }

        public List<Student> getStudents() {
            return students;
        }

        public void setStudents(List<Student> students) {
            this.students = students;
        }

        public void addStudent(Student student) {
            this.students.add(student);
        }

    }

}


