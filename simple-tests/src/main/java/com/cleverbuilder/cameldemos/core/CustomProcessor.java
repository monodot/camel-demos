package com.cleverbuilder.cameldemos.core;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CustomProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getMessage().setBody("Hello, world!");
    }
}
