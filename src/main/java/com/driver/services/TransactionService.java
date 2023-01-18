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
    BookRepository bookRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Value("${books.max_allowed}")
    public
    int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public
    int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public
    int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        Book book = bookRepository.findById(bookId).get();
        Card card = cardRepository.findById(cardId).get();

        Transaction transaction = new Transaction();

        transaction.setBook(book);
        transaction.setCard(card);
        transaction.setIssueOperation(true);

        if(book == null || !book.isAvailable()){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new Exception("Book is either unavailable or not present");
        }

        if(card == null || card.getCardStatus().equals(CardStatus.DEACTIVATED)){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new Exception("Card is invalid");
        }

        if(card.getBooks().size() >= max_allowed_books){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new Exception("Book limit has reached for this card");
        }

        book.setCard(card);
        book.setAvailable(false);
        List<Book> listOfBooks = card.getBooks();
        listOfBooks.add(book);
        card.setBooks(listOfBooks);

        bookRepository.updateBook(book);

        transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        transactionRepository.save(transaction);

        return transaction.getTransactionId();
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactionList = transactionRepository.find(cardId, bookId,TransactionStatus.SUCCESSFUL, true);

        Transaction transaction = transactionList.get(transactionList.size() - 1);

        Date issueDate = transaction.getTransactionDate();

        long issueTime = Math.abs(System.currentTimeMillis() - issueDate.getTime());

        long no_of_days_passed = TimeUnit.DAYS.convert(issueTime, TimeUnit.MILLISECONDS);

        int fine = 0;
        if(no_of_days_passed > getMax_allowed_days){
            fine = (int)((no_of_days_passed - getMax_allowed_days) * fine_per_day);
        }

        Book book = transaction.getBook();

        book.setAvailable(true);
        book.setCard(null);



        //Remve that book from that card list

        bookRepository.updateBook(book);

        Transaction transaction1 = new Transaction();
        transaction1.setBook(transaction.getBook());
        transaction1.setCard(transaction.getCard());
        transaction1.setIssueOperation(false);
        transaction1.setFineAmount(fine);
        transaction1.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        transactionRepository.save(transaction1);

        return transaction1;
    }
}