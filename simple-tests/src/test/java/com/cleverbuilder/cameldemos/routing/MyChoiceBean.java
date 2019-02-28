package com.cleverbuilder.cameldemos.routing;

import org.apache.camel.Exchange;

/**
 * Created by tdonohue on 20/05/2018.
 */
public class MyChoiceBean {

    public static boolean isFriedEggs(Exchange exchange) {
        if (exchange.getIn().getBody(String.class).equals("Fried eggs")) {
            return true;
        } else {
            return false;
        }
    }

}
