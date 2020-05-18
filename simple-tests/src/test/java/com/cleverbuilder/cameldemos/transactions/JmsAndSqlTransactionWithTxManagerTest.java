package com.cleverbuilder.cameldemos.transactions;

import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelException;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Testing transactional behaviour of routes which contain
 * both a DB and a JMS component in them.
 */
public class JmsAndSqlTransactionWithTxManagerTest extends CamelTestSupport {

    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Let's create an embedded ActiveMQ broker on port 61619
        // Doing this explicitly, instead of letting the Connection Factory create a broker itself.
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61619");
        broker.setPersistent(false);
        broker.start();

        // Create an embedded Derby database
        // Initialise our in-memory Derby database
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.DERBY)
                .addScript("sql/customers.sql").build();

        jdbcTemplate = new JdbcTemplate(database);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        database.shutdown();
        super.tearDown();
    }

    @Test
    public void testStartDirectWithoutTransactedKeyword() throws Exception {
        template.sendBody("direct:without-transacted", "hello");

        Thread.sleep(1000L);

        // The records are never inserted into the DB, because the transaction is rolled back.
        assertEquals(new Integer(0),
                jdbcTemplate.queryForObject("select count(*) from customers", Integer.class));
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:without-transacted")
                        .to("sql:insert into customers ( 'Jeff', 'Banks' )")
                        .setBody(constant("Some random text here just to fill the body"))
                        .to("activemq:queue:CUSTOMERS.OUT?exchangePattern=InOnly")
                        .throwException(new CamelException("Throwing my toys out of the pram"));
            }
        };
    }

    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        // Create a transaction manager and attach the connection factory to it
//        JmsTransactionManager transactionManager = new JmsTransactionManager();
//        transactionManager.setConnectionFactory(activeMQConnectionFactory);
//
//        registry.bind(transactionManager);
    }
}
