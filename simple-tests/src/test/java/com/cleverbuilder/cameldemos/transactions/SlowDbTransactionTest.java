package com.cleverbuilder.cameldemos.transactions;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SlowDbTransactionTest extends CamelTestSupport {

    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;
    private static final Logger LOG = LoggerFactory.getLogger(SlowDbTransactionTest.class);
    private int JETTY_PORT = 8717;



    @Override
    @Before
    public void setUp() throws Exception {
        // Initialise an in-memory Derby database with the help of Spring
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.DERBY)
                .addScript("sql/customers.sql").build();

        jdbcTemplate = new JdbcTemplate(database);

        super.setUp();
    }

    /**
     * Test that a slow database transaction can be rolled back if the client has already gone away.
     */
    @Test
    public void testSlowDbTransaction() throws IOException, InterruptedException {
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        HttpClient httpClient = new HttpClient();
        HttpConnectionManagerParams params = httpClient.getHttpConnectionManager().getParams();
        params.setSoTimeout(1000); // in milliseconds

        HttpMethod httpMethod = new GetMethod("http://localhost:" + JETTY_PORT);

        int status = httpClient.executeMethod(httpMethod);
        // is this happening in the same thread?
        assertEquals(200, status);

//        try {
            // Make a request to the slow-responding web service, with a low timeout (1 sec).
//            String response = template.requestBody("http://localhost:" + JETTY_PORT + "?socketTimeout=1000", null, String.class);
//            template.sendBody("http://localhost:" + JETTY_PORT + "?socketTimeout=1000", null);
//        } catch (Exception ex) {
//            LOG.warn("Trapping exception and continuing: " + ex.getClass() + ", " + ex.getMessage());
//        }

        Thread.sleep(5000L);
        // Assert that the route completes successfully within 10 seconds
        assertTrue(notify.matches(10, TimeUnit.SECONDS));

        // Now do we get an exception?
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty://http://localhost:" + JETTY_PORT)
                        .log("Received request. Sleeping....")
                        .delay(5000L)
                        .log("I've woken up. Returning response now.")
                        .setBody(simple("OK BACK"));
            }
        };
    }


    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        registry.bind("configurer", new HttpClientConfigurer() {
            @Override
            public void configureHttpClient(HttpClientBuilder clientBuilder) {
                clientBuilder.setConnectionTimeToLive(1L, TimeUnit.SECONDS);
            }
        });
    }
}
