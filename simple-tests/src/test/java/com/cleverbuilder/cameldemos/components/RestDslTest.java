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
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class RestDslTest extends CamelTestSupport {

    private static final String COMPONENT = "jetty";
    private static final String PRODUCER_COMPONENT = "http"; // Jetty is not a producer

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
        assertEquals("POST invoked!", response.getOut().getBody(String.class));
}

    /**
     * Performing a GET request with query string parameters
     */
    @Test
    public void testRestQueryString() {
        String response = template.requestBody("http://localhost:8080/customers/search?city=London&country=GB", null, String.class);

        assertNotNull(response);
        assertEquals("Search invoked! Country = GB, City = London", response);
    }

    @Test
    public void testSimplePost() {
        Exchange response = template.send("http://localhost:8080/simplepost", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Nigel");
            }
        });

        assertNotNull(response);
        assertEquals("Thanks, Nigel!", response.getMessage().getBody(String.class));
    }

    @Test
    public void testParametersWithJsonBinding() {
        String response = template.requestBody("http://localhost:8080/books/12345", null, String.class);

        assertNotNull(response);
        assertEquals("{\"bookId\":\"12345\"}", response);
    }

    @Test
    public void testTwoStepsInSubroute() {
        String response = template.requestBody("http://localhost:8080/testing", "Hello, world!", String.class);

        assertNotNull(response);
        assertEquals("Thanks!", response);
        assertFileExists("target/output/customers/customer.txt");
    }

    @Test
    public void testGetBinding() {
        List<Student> students = new ArrayList();
        Student john = new Student("John", "McNeil", "A");
        Student jane = new Student("Jane", "Hill", "B");
        Student bertie = new Student("Bertie", "Wooster", "A");

        students.add(john);
        students.add(jane);
        students.add(bertie);

        StudentService studentService = (StudentService) context.getRegistry().lookupByName("studentService");
        studentService.setStudents(students);

        String response = template.requestBody("http://localhost:8080/students", null, String.class);

        assertNotNull(response);
        assertEquals("[{\"firstName\":\"John\",\"lastName\":\"McNeil\",\"grade\":\"A\"},{\"firstName\":\"Jane\",\"lastName\":\"Hill\",\"grade\":\"B\"},{\"firstName\":\"Bertie\",\"lastName\":\"Wooster\",\"grade\":\"A\"}]", response);
    }

    /**
     * Test that Camel will auto bind JSON into our Student object.
     * Sends an 'addStudent' request containing some JSON.
     * Then verifies that the new student is in the 'getStudent' response
     * (which tests that the JSON was correctly unmarshalled)
     * @throws Exception
     */
    @Test
    public void testPostBinding() throws Exception {
        String charlie = "{\"firstName\":\"Charlie\",\"lastName\":\"Brown\",\"grade\":\"D\"}";

        Exchange addStudentResponse = template.send("http://localhost:8080/students", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(charlie);
            }
        });

        assertNotNull(addStudentResponse);

        String getStudentsResponse = template.requestBody("http://localhost:8080/students", null, String.class);

        assertNotNull(getStudentsResponse);
        assertEquals("[{\"firstName\":\"Charlie\",\"lastName\":\"Brown\",\"grade\":\"D\"}]", getStudentsResponse);
    }

    /**
     * Tests using the REST component to execute a simple GET with a parameter
     * against a public web service.
     * Ignored because the remote web service isn't reliable.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testInvokeRestService() throws Exception {
        Exchange response = template.send("direct:invoke-rest", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader("countryCode", "GB");
            }
        });

        String expected = "{\n" +
                "  \"RestResponse\" : {\n" +
                "    \"messages\" : [ \"Country found matching code [GB].\" ],\n" +
                "    \"result\" : {\n" +
                "      \"name\" : \"United Kingdom of Great Britain and Northern Ireland\",\n" +
                "      \"alpha2_code\" : \"GB\",\n" +
                "      \"alpha3_code\" : \"GBR\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertNotNull(response);
        assertEquals(expected, response.getOut().getBody(String.class));
    }

    /**
     * Test that we can invoke a REST POST operation and get the response
     * back as a POJO
     * @throws Exception
     */
    @Test
    public void testInvokeRestPost() throws Exception {
        String charlie = "{\"firstName\":\"Charlie\",\"lastName\":\"Brown\",\"grade\":\"D\"}";

        Object response = template.requestBodyAndHeader("direct:invoke-rest-post", charlie, "Content-Type", "application/json");

        assertNotNull(response);
        assertIsInstanceOf(ResponseObject.class, response);

        assertEquals("OK", ((ResponseObject) response).getStatus());

    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
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
                            .to("file:target/output/customers?fileName=customer.txt")
                            .setBody(constant("Thanks!"));

                rest("/simplepost")
                        .bindingMode(RestBindingMode.off)
                        .post().route()
                            .streamCaching()
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
                        .post().type(Student.class).outType(ResponseObject.class).route()
                            .log("Receiving a student - ${body.getFirstName()}")
                            .to("bean:studentService?method=addStudent")
                            .process(exchange -> {
                                ResponseObject response = new ResponseObject();
                                response.setStatus("OK");
                                response.setAdded("yes");
                                exchange.getMessage().setBody(response);
                            })
                            .log("Body is now - ${body}");

                from("direct:invoke-rest")
                        .setHeader("countryCode", constant("GB"))
                        .to("rest:get:country/get/iso2code/{countryCode}?host=services.groupkt.com:80");

                from("direct:invoke-rest-post")
                        .to("rest:post:students?host=localhost:8080"
                                + "&outType=com.cleverbuilder.cameldemos.model.ResponseObject")
                        .to("log:mylogger?showAll=true")
                        .log("Response was: ${body}");



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


