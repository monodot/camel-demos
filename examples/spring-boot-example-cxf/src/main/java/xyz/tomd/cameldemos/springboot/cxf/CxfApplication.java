package xyz.tomd.cameldemos.springboot.cxf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demonstration of configuring multiple CXF context paths.
 */
@SpringBootApplication
//@ImportResource("classpath:spring/camel-context.xml")
public class CxfApplication {

    public static void main(String[] args) {
        SpringApplication.run(CxfApplication.class, args);
    }

}
