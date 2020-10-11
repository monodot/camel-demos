package xyz.tomd.cameldemos.springboot.cxfmultiplecontexts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Demonstration of configuring multiple CXF context paths.
 */
@SpringBootApplication
@ImportResource("classpath:spring/camel-context.xml")
public class CxfMultipleContextsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CxfMultipleContextsApplication.class, args);
    }

}
