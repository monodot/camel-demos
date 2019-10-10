package com.cleverbuilder.cameldemos.transactions;

import org.apache.camel.CamelException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * See what happens with DB transactions without a transaction manager...
 * We use camel-sql component, which uses spring-jdbc under the bonnet,
 * so we're going to take advantage of some Spring APIs here.
 */
public class SqlComponentWithoutTxManagerTest extends CamelTestSupport {

    private EmbeddedDatabase database;
    private JdbcTemplate jdbcTemplate;

    /**
     * Without a transaction manager, throwing an exception at the end
     * of a route does not cause any previous database inserts to be
     * rolled back.
     */
    @Test
    public void recordsAreInsertedWithoutATransactionManager() throws Exception {
        context.getComponent("sql", SqlComponent.class).setDataSource(database);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .log("Writing all the databases")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Jeff', 'Mills')")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Susan', 'Smith')")
                        .throwException(new CamelException("Something bad happened"));
            }
        });
        context.start();

        // We're invoking a Direct (synchronous) endpoint so the exception will
        // be thrown right back to us, so we should catch it.
        try {
            template.sendBody("direct:start", null);
        } catch (CamelExecutionException ex) {

        }

        // The 2 x records actually both get inserted into the DB,
        // because there was no transaction spanning them both.
        assertEquals(new Integer(2),
                jdbcTemplate.queryForObject("select count(*) from customers", Integer.class));
    }

    /**
     * When using the 'transacted' flag without a transaction manager,
     * Camel will fail to start, because no transaction manager can be found.
     */
    @Test(expected = FailedToCreateRouteException.class)
    public void usingTransactedWithoutATransactionManager() throws Exception {
        context.getComponent("sql", SqlComponent.class).setDataSource(database);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start-with-transacted")
                        .transacted()
                        .log("Writing all the databases")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Jeff', 'Mills')")
                        .to("sql:INSERT INTO customers ( first_name, last_name ) VALUES ('Susan', 'Smith')")
                        .throwException(new CamelException("Something bad happened"));
            }
        });
        context.start();
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