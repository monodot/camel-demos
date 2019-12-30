package xyz.tomd.cameldemos.springboot.databasetx;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
public class DatabaseTxApplicationTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CamelContext camelContext;

    @EndpointInject(uri = "mock:output")
    MockEndpoint mockEndpoint;

    @Autowired
    ProducerTemplate template;

    /**
     * This test is a little crappy, but it proves the point.
     * Since the route has 'transacted', the failure of the second
     * database insert causes the first insert to be rolled back.
     * @throws Exception
     */
    @Test
    public void testThatTheTransactionIsRolledBack() throws Exception {
        try {
            template.sendBody("direct:start", "");
        } catch (CamelExecutionException ex) {
            // Do nothing. We expect there to be an exception.
        }

        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.assertIsSatisfied(10, TimeUnit.SECONDS);

        // there should be 0 rows in the database because we've rolled back
        int rows = jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
        assertEquals(0, rows);
    }
}