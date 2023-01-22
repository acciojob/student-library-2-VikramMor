package com.driver.services;

import com.driver.models.*;
import com.driver.repositories.CardRepository;
import com.driver.repositories.BookRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    int max_allowed_books;

    @Value("${books.max_allowed_days}")
    int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    int fine_per_day;

//    public int getMax_allowed_books() {
//        return max_allowed_books;
//    }
//
//    public void setMax_allowed_books(int max_allowed_books) {
//        this.max_allowed_books = max_allowed_books;
//    }
//
//    public int getGetMax_allowed_days() {
//        return getMax_allowed_days;
//    }
//
//    public void setGetMax_allowed_days(int getMax_allowed_days) {
//        this.getMax_allowed_days = getMax_allowed_days;
//    }
//
//    public int getFine_per_day() {
//        return fine_per_day;
//    }
//
//    public void setFine_per_day(int fine_per_day) {
//        this.fine_per_day = fine_per_day;
//    }

    public String issueBook(int cardId, int bookId) throws Exception {

        Book book = bookRepository5.findById(bookId).get();
        Card card = cardRepository5.findById(cardId).get();

        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setCard(card);
        transaction.setIssueOperation(true);

        if(book==null || !book.isAvailable()) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository5.save(transaction);
            throw new Exception("Book is either unavailable or not present");
        }
        if(card==null || card.getCardStatus().equals(CardStatus.DEACTIVATED)) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository5.save(transaction);
            throw new Exception("Card is invalid");
        }
        if(card.getBooks().size()>=max_allowed_books) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository5.save(transaction);
            throw new Exception("Book limit has reached for this card");
        }

        book.setAvailable(false);
        book.setCard(card);

        transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        List<Book> bookList = card.getBooks();
        if(bookList==null) {
            bookList = new ArrayList<>();
        }
        bookList.add(book);
        card.setBooks(bookList);

        List<Transaction> transactionList = book.getTransactions();
        if(transactionList==null) {
            transactionList = new ArrayList<>();
        }
        transactionList.add(transaction);
        book.setTransactions(transactionList);

        cardRepository5.save(card);

        return transaction.getTransactionId();
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception {

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        Date issueDate = transaction.getTransactionDate();

        long issueTime = Math.abs(System.currentTimeMillis() - issueDate.getTime());

        long daysPassed = TimeUnit.DAYS.convert(issueTime, TimeUnit.MILLISECONDS);

        int fineAmount = 0;
        if(daysPassed>getMax_allowed_days) {
            fineAmount = (int) (fine_per_day*(daysPassed-getMax_allowed_days));
        }

        Book book = bookRepository5.findById(bookId).get();
        Card card = cardRepository5.findById(cardId).get();

        book.setAvailable(true);
        book.setCard(null);

        Transaction returnBookTransaction = new Transaction();
        returnBookTransaction.setBook(book);
        returnBookTransaction.setCard(card);
        returnBookTransaction.setIssueOperation(false);
        returnBookTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        returnBookTransaction.setFineAmount(fineAmount);

        List<Book> bookList = card.getBooks();
        bookList.remove(book);
        card.setBooks(bookList);

        List<Transaction> transactionList = book.getTransactions();
        if(transactionList==null) {
            transactionList = new ArrayList<>();
        }
        transactionList.add(returnBookTransaction);
        book.setTransactions(transactionList);

        cardRepository5.save(card);

        return returnBookTransaction;
    }
}