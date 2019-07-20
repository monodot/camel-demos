package com.cleverbuilder.cameldemos.core;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomProcessorTest {

    @Test
    public void testCustomProcessor() throws Exception {
        CustomProcessor customProcessor = new CustomProcessor();
        CamelContext context = new DefaultCamelContext();

        Exchange exchange = new ExchangeBuilder(context).withBody("BYE THEN").build();
        customProcessor.process(exchange);

        assertEquals("Hello, world!", exchange.getMessage().getBody(String.class));
    }
}
