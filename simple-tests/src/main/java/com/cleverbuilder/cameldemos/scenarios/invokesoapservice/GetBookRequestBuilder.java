package com.cleverbuilder.cameldemos.scenarios.invokesoapservice;

import com.cleverbuilder.bookservice.GetBook;

public class GetBookRequestBuilder {

    public GetBook getBook(String id) {
        GetBook request = new GetBook();
        request.setID(id);

        return request;
    }
}
