package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Genre;
import com.driver.repositories.AuthorRepository;
import com.driver.repositories.BookRepository;
import com.driver.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository2;

    @Autowired
    AuthorRepository authorRepository;

    public void createBook(Book book) {
        int authorId = book.getAuthor().getId();
        Author author = authorRepository.findById(authorId).get();
        author.getBooksWritten().add(book);
        book.setAuthor(author);
        authorRepository.save(author);
    }

    public List<Book> getBooks(String genre, boolean available, String author) {
        List<Book> temp = bookRepository2.findByAvailability(available);
        List<Book> books = new ArrayList<>();
        Genre gen = Genre.valueOf(genre);
        for (Book b : temp) {
            if (author == null) {

                if (b.getGenre().equals(gen)) {
                    books.add(b);
                }
            } else {
                if (b.getGenre().equals(gen) && b.getAuthor().getName().equalsIgnoreCase(author)) {
                    books.add(b);
                }
            }
        }
        // find the elements of the list by yourself
        return books;
    }
}