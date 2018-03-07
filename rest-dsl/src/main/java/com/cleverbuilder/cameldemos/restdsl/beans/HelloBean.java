package com.cleverbuilder.cameldemos.restdsl.beans;

import org.springframework.stereotype.Component;

/**
 * Created by tdonohue on 07/03/2018.
 */
@Component
public class HelloBean {

    public String sayHello() {
        return "Hello, world!";
    }

}
