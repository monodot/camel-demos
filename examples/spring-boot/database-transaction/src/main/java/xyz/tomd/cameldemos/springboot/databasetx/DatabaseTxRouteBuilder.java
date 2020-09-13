package xyz.tomd.cameldemos.springboot.databasetx;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTxRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {

        from("direct:start")
                // Need to add 'transacted' to mark the start of the transaction.
                // This ensures that the 1st insert is rolled back correctly when the 2nd one fails.
                // Without `.transacted()`, the 1st insert will not be rolled back!
                .transacted()
                .log("Inserting rows")
                .to("sql:insert into customers values ('Philip', 'White City')")
                .to("sql:insert into customers values ('SusanMyNameIsTooLong', 'Edinburgh')")
                .log("Done inserting!")
                .to("mock:output");

    }
}
