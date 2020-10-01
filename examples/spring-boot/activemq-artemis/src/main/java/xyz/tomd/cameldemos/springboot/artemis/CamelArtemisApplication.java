package xyz.tomd.cameldemos.springboot.artemis;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.jms.ConnectionFactory;

@SpringBootApplication
public class CamelArtemisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamelArtemisApplication.class, args);
    }

}
