package com.cleverbuilder.cameldemos.spring.scenarios.errorhandler;

/**
 * Created by tdonohue on 17/05/2018.
 */
public class BusinessError {

    private String object;
    private String id;
    private String message;

    public BusinessError(String object, String id, String message) {
        this.object = object;
        this.id = id;
        this.message = message;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
