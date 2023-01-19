package com.driver.services;

import com.driver.models.*;
import com.driver.repositories.CardRepository;
import com.driver.repositories.BookRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    private int max_allowed_books;

    @Value("${books.max_allowed_days}")
    private int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    private int fine_per_day;

    public int getMax_allowed_books() {
        return max_allowed_books;
    }

    public void setMax_allowed_books(int max_allowed_books) {
        this.max_allowed_books = max_allowed_books;
    }

    public int getGetMax_allowed_days() {
        return getMax_allowed_days;
    }

    public void setGetMax_allowed_days(int getMax_allowed_days) {
        this.getMax_allowed_days = getMax_allowed_days;
    }

    public int getFine_per_day() {
        return fine_per_day;
    }

    public void setFine_per_day(int fine_per_day) {
        this.fine_per_day = fine_per_day;
    }

    public String issueBook(int cardId, int bookId) throws Exception {

        Book temp = bookRepository5.findById(bookId).get();
        Card card = cardRepository5.findById(cardId).get();
        CardStatus status = CardStatus.valueOf("ACCEPTED");
        List<Book> book = card.getBooks();
        String id ;
        if (bookRepository5.findById(bookId) == null || temp.isAvailable() == false) {
            throw new Exception("Book is either unavailable or not present");
        } else if (cardRepository5.findById(cardId) == null || card.getCardStatus().equals(status)) {
            throw new Exception("Card is invalid");
        } else if (book.size() >= 3) {
            throw new Exception("Book limit has reached for this card");
        } else {
            Transaction t = new Transaction();
            t.setBook(temp);
            t.setCard(card);
            Transaction transaction = transactionRepository5.save(t);
            id=transaction.getTransactionId();
        }

        return id; // return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception {

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL,
                true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        transaction.setFineAmount(100);
        Book b=bookRepository5.findById(bookId).get();
        b.setAvailable(true);
        Card card=cardRepository5.findById(cardId).get();

        Transaction returnBookTransaction = new Transaction();
        returnBookTransaction.setBook(b);
        returnBookTransaction.setFineAmount(100);
        returnBookTransaction.setCard(card);
        return returnBookTransaction; // return the transaction after updating all details
    }
}