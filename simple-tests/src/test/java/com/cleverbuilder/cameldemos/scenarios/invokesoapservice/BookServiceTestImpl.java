package com.cleverbuilder.cameldemos.scenarios.invokesoapservice;

import com.cleverbuilder.bookservice.*;

public class BookServiceTestImpl implements BookService {

    private static Book shadyBook;
    private static Book influenceBook;

    static {
        shadyBook = new Book();
        shadyBook.setAuthor("EL James");
        shadyBook.setTitle("Fifty Shades of Pink");
        shadyBook.setID("55555");

        influenceBook = new Book();
        influenceBook.setAuthor("Dale Carnegie");
        influenceBook.setTitle("How to Win Friends and Influence People");
        influenceBook.setID("66666");
    }

    @Override
    public GetBookResponse getBook(GetBook parameters) {
        GetBookResponse response = new GetBookResponse();
        response.setBook(shadyBook);

        return response;
    }

    @Override
    public AddBookResponse addBook(AddBook parameters) {
        AddBookResponse response = new AddBookResponse();
        response.setBook(parameters.getBook());

        return response;
    }

    @Override
    public GetAllBooksResponse getAllBooks(GetAllBooks parameters) {
        GetAllBooksResponse response = new GetAllBooksResponse();
        response.getBook().add(shadyBook);
        response.getBook().add(influenceBook);

        return response;
    }
}
