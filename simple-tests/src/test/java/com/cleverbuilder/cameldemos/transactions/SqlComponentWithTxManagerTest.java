package com.cleverbuilder.cameldemos.transactions;

import org.apache.camel.CamelException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Let's see what happens with a transaction manager configured...
 */
public class SqlComponentWithTxManagerTest extends CamelTestSupport {

    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;

    /**
     * In this test, the transacted keyword indicates
     * the start of a transaction. The two database inserts
     * become part of the same transaction.
     * So when the exception occurs
     * at the end of the route, the transaction is rolled
     * back, and the 2 x rows do not get inserted.
     */
    @Test
    public void insertsAreRolledBackWithTransactedCommand() throws Exception {
        context.getComponent("sql", SqlComponent.class).setDataSource(database);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .transacted()
                        .log("Writing all the databases")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Jeff', 'Mills')")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Susan', 'Smith')")
                        .throwException(new CamelException("Something baaaaaad happened"));
            }
        });
        context.start();

        // We're invoking a Direct (synchronous) endpoint so the exception will
        // be thrown right back to us, so we should catch it.
        try {
            template.sendBody("direct:start", null);
        } catch (CamelExecutionException ex) {

        }

        // The records are never inserted into the DB, because the transaction is rolled back.
        assertEquals(new Integer(0),
                jdbcTemplate.queryForObject("select count(*) from customers", Integer.class));
    }

    /**
     * If the transacted command is not used to
     * mark the start of a transaction,
     * then both of the records are inserted.
     */
    @Test
    public void recordsAreInsertedWithoutTransactedCommand() throws Exception {
        context.getComponent("sql", SqlComponent.class).setDataSource(database);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start-without-transacted")
                        .log("Writing all the databases")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Jeff', 'Mills')")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Susan', 'Smith')")
                        .throwException(new CamelException("Something baaaaaad happened"));
            }
        });
        context.start();

        // We're invoking a Direct (synchronous) endpoint so the exception will
        // be thrown right back to us, so we should catch it.
        try {
            template.sendBody("direct:start-without-transacted", null);
        } catch (CamelExecutionException ex) {

        }

        // The records are never inserted into the DB, because the transaction is rolled back.
        assertEquals(new Integer(2),
                jdbcTemplate.queryForObject("select count(*) from customers", Integer.class));
    }

    /**
     * If the transacted command is not used to
     * mark the start of a transaction,
     * then both of the records are inserted.
     */
    @Test
    public void transactionSpansAcrossTwoRoutes() throws Exception {
        context.getComponent("sql", SqlComponent.class).setDataSource(database);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start-two-routes")
                        .log("Writing all the databases")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Jeff', 'Mills')")
                        .to("direct:start-two-routes-2");

                from("direct:start-two-routes-2")
                        .log("I'm in the second route now")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Susan', 'Smith')")
                        .throwException(new CamelException("Something baaaaaad happened"));
            }
        });
        context.start();

        // We're invoking a Direct (synchronous) endpoint so the exception will
        // be thrown right back to us, so we should catch it.
        try {
            template.sendBody("direct:start-two-routes", null);
        } catch (CamelExecutionException ex) {

        }

        // The records are never inserted into the DB, because the transaction is rolled back.
        assertEquals(new Integer(2),
                jdbcTemplate.queryForObject("select count(*) from customers", Integer.class));
    }



    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(database);

        // Need to add the transaction manager into the registry,
        // otherwise we'll get: "No bean could be found in the registry of type: PlatformTransactionManager"
        registry.bind("transactionManager", transactionManager);
    }

    /**
     * Set up an embedded Derby database here.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        // Initialise our in-memory Derby database
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.DERBY)
                .addScript("sql/customers.sql").build();

        jdbcTemplate = new JdbcTemplate(database);

        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        database.shutdown();
        super.tearDown();
    }
}