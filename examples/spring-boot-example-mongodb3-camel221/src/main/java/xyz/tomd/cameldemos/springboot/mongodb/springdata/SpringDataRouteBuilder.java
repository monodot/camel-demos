package xyz.tomd.cameldemos.springboot.mongodb.springdata;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SpringDataRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {
        from("direct:getAllUsingSpringData")
                // We follow Spring's repository method naming convention: 'findAll()'
                .bean(CatsService.class, "findAll")
                .log("Body is now: ${body}");

        from("direct:getByOwnerUsingSpringData")
                .bean(CatsService.class, "findByEnemy(${header.enemy})");

        from("direct:insertUsingSpringData")
                .bean(CatsService.class, "save");

    }
}
