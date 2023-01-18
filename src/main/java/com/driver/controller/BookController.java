package com.driver.controller;

import com.driver.models.Book;
import com.driver.models.Genre;
import com.driver.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {


    @Autowired
    BookService bookService;

    @PostMapping("/book")
    public ResponseEntity<String> createBook(@RequestBody() Book book){
        bookService.createBook(book);
        return new ResponseEntity<>("Success",HttpStatus.ACCEPTED);

    }

    //Write createBook API with required annotations

    @GetMapping("/book")
    public ResponseEntity<List<Book>> getBooks(@RequestParam(value = "genre", required = false) String genre,
                                               @RequestParam(value = "available", required = false, defaultValue = "false") boolean available,
                                               @RequestParam(value = "author", required = false) String author){

        List<Book> bookList = bookService.getBooks(genre, available, author); //find the elements of the list by yourself

        return new ResponseEntity<List<Book>>(bookList, HttpStatus.OK);

    }
}