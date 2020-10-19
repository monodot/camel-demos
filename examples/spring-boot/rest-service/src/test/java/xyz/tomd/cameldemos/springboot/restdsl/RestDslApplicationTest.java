package xyz.tomd.cameldemos.springboot.restdsl;

import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.tomd.cameldemos.springboot.restdsl.types.PostRequestType;
import xyz.tomd.cameldemos.springboot.restdsl.types.ResponseType;

import static org.junit.Assert.assertEquals;

/**
 * A test class. Note we don't use any Camel mocks in this test.
 *
 * We rely on Spring Boot to bootstrap the application, and choose a random
 * web server port.
 *
 * Then we send GET and POST requests to the REST API,
 * and perform assertions on the responses.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestDslApplicationTest {

	// Spring will inject the random port assigned to the web server
	@LocalServerPort
	int webServerPort;

	@Autowired
	TestRestTemplate testRestTemplate;

	/**
	 * An example integration test which tests that the Camel routes run
	 * as expected, when invoked via the REST API.
	 *
	 * We use Spring's TestRestTemplate, which is a pre-configured REST client
	 * which we can use to invoke the service.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetITest() throws Exception {
		// The API will be at /services/api. The context-path ("services")
		// is set in application.properties: camel.component.servlet.mapping.context-path
		// We tell TestRestTemplate to try to unmarshal the JSON into our
		// "ResponseType" object. This confirms that we're returning valid JSON
		ResponseEntity<ResponseType> response = testRestTemplate.getForEntity(
				"http://localhost:" + webServerPort + "/services/api",
				ResponseType.class);
		// We also use ResponseType, which is our custom response POJO,
		// which contains a single field, "message"

		// Check that the service returned HTTP 200 OK
		assertEquals(HttpStatus.OK, response.getStatusCode());

		// Since we unmarshalled the object into a POJO, we can also perform
		// a test assertion on it:
		assertEquals("Hello, world!", response.getBody().getMessage());
	}

	/**
	 * An example integration test (testing all the internal components together).
	 *
	 * Sends a POST request to the REST API, and verifies that the response
	 * is valid.
	 * @throws Exception
	 */
	@Test
	public void testPostITest() throws Exception {
		// Build up the test request. Create a POJO
		PostRequestType request = new PostRequestType();
		request.setName("Frederick");

		// Send the request. Spring will marshal the 'request' into JSON before sending
		ResponseEntity<ResponseType> response = testRestTemplate.postForEntity(
				"http://localhost:" + webServerPort + "/services/api",
				request,
				ResponseType.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		// Assert the "message" field in the JSON response is what we expect
		assertEquals("Thanks for your post, Frederick!",
				response.getBody().getMessage());
	}

}
