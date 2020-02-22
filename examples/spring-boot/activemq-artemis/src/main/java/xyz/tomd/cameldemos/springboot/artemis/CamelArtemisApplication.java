package xyz.tomd.cameldemos.springboot.artemis;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.jms.JMSException;

@SpringBootApplication
public class CamelArtemisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamelArtemisApplication.class, args);
    }

/*
    @Bean
    public JmsComponent jmsComponent() throws JMSException {
        // Create the connectionfactory which will be used to connect to Artemis
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        cf.setBrokerURL("tcp://localhost:61616");
        cf.setUser("admin");
        cf.setPassword("admin");

        // Create the Camel JMS component and wire it to our Artemis connectionfactory
        JmsComponent jms = new JmsComponent();
        jms.setConnectionFactory(cf);
        return jms;
    }
*/

}
