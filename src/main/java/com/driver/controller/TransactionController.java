package com.driver.controller;

import com.driver.models.Transaction;
import com.driver.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction/issueBook")
    public ResponseEntity<String> issueBook(@RequestParam("cardId") int cardId, @RequestParam("bookId") int bookId) throws Exception{
        String id=transactionService.issueBook(cardId, bookId);
        return new ResponseEntity<>(id, HttpStatus.ACCEPTED);
    }

    @PostMapping("/transaction/returnBook")
    public ResponseEntity<Transaction> returnBook(@RequestParam("cardId") int cardId, @RequestParam("bookId") int bookId) throws Exception{
        Transaction transaction=transactionService.returnBook(cardId, bookId);
        return new ResponseEntity<Transaction>(transaction, HttpStatus.ACCEPTED);
    }
}