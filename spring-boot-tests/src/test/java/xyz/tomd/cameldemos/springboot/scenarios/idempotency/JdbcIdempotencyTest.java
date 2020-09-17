package xyz.tomd.cameldemos.springboot.scenarios.idempotency;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = JdbcIdempotencyTest.Config.class,
        properties = {}
)
@AutoConfigureTestDatabase // requires spring-boot-test-autoconfigure dependency
public class JdbcIdempotencyTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate template;

    @EndpointInject("mock:process")
    MockEndpoint mockEndpoint;

    @Autowired
    DataSource dataSource;

    @Test
    public void testDuplicateMessagesAreIgnored() throws Exception {
        mockEndpoint.expectedMessageCount(1);

        Map<String, Object> headers = new HashMap<>();
        headers.put("X-MY-Source", "AAA");
        headers.put("X-MY-Class", "ABC123");
        headers.put("X-MY-Destination", "ZYX999");
        headers.put("X-MY-SeqNo", "78");

        template.sendBodyAndHeaders("direct:start",
                new File("src/test/data/dupemessage1.txt"),
                headers);
        template.sendBodyAndHeaders("direct:start",
                new File("src/test/data/dupemessage2.txt"),
                headers);

        mockEndpoint.assertIsSatisfied();
    }

    @Configuration
    static class Config {

        /* Copy this into your main Spring Boot Application class */

        /**
         * Defines a database-based repository for storing message identifiers
         * to allow messages to be deduplicated using the Idempotent Consumer.
         * Expects a DataSource; will use the default one created by Spring Boot.
         * @param dataSource
         * @return
         */
        @Bean(name = "jdbcMessageIdRepository")
        JdbcMessageIdRepository jdbcMessageIdRepository(DataSource dataSource) {
            JdbcMessageIdRepository repository = new JdbcMessageIdRepository(dataSource,
                    "myProcessor");
            return repository;
        }

    }
}
