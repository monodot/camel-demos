package com.cleverbuilder.cameldemos.spring.scenarios.errorhandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tdonohue on 17/05/2018.
 */
public class ErrorHandler {

    private List<BusinessError> errors;

    public ErrorHandler() {
        errors = new ArrayList<>();
    }

    public void addError(BusinessError error) {
        errors.add(error);
    }

    public void addError(String object, String id, String message) {
        errors.add(new BusinessError(object, id, message));
    }

    public List<BusinessError> getErrors() {
        return errors;
    }
}
