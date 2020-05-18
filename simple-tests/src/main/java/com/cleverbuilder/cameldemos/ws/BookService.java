package com.cleverbuilder.cameldemos.ws;

import com.cleverbuilder.bookservice.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

@WebService(targetNamespace = "http://www.cleverbuilder.com/BookService/", name = "BookService")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface BookService {

    @WebMethod(operationName = "GetBook", action = "http://www.cleverbuilder.com/BookService/GetBook")
    @WebResult(name = "GetBookResponse", targetNamespace = "http://www.cleverbuilder.com/BookService/", partName = "parameters")
    public String getBook(

            @WebParam(partName = "parameters", name = "GetBook", targetNamespace = "http://www.cleverbuilder.com/BookService/")
                    String param
    );

    @WebMethod(operationName = "AddBook", action = "http://www.cleverbuilder.com/BookService/AddBook")
    @WebResult(name = "AddBookResponse", targetNamespace = "http://www.cleverbuilder.com/BookService/", partName = "parameters")
    public String addBook(

            @WebParam(partName = "parameters", name = "AddBook", targetNamespace = "http://www.cleverbuilder.com/BookService/")
                    String param
    );

    @WebMethod(operationName = "GetAllBooks", action = "http://www.cleverbuilder.com/BookService/GetAllBooks")
    @WebResult(name = "GetAllBooksResponse", targetNamespace = "http://www.cleverbuilder.com/BookService/", partName = "parameters")
    public GetAllBooksResponse getAllBooks(

            @WebParam(partName = "parameters", name = "GetAllBooks", targetNamespace = "http://www.cleverbuilder.com/BookService/")
                    String param
    );
}
