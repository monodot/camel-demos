package xyz.tomd.cameldemos.springboot.multiplecontexts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath*:camel-*.xml")
@EnableAutoConfiguration
public class MultipleContextsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultipleContextsApplication.class, args);
    }

}
