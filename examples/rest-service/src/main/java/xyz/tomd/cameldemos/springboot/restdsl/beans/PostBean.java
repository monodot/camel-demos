package xyz.tomd.cameldemos.springboot.restdsl.beans;

import xyz.tomd.cameldemos.springboot.restdsl.types.PostRequestType;
import org.springframework.stereotype.Component;
import xyz.tomd.cameldemos.springboot.restdsl.types.ResponseType;

@Component
public class PostBean {

    public ResponseType response(PostRequestType input) {
        // We create a new object of the ResponseType
        // Camel will be able to serialise this automatically to JSON
        return new ResponseType("Thanks for your post, " + input.getName() + "!");
    }
}
