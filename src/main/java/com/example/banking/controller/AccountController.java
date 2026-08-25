package com.example.banking.controller;

import com.example.banking.controller.AmountRequest;
import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;
import com.example.banking.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestParam String accountNumber) {

        return ResponseEntity.ok(
                accountService.createAccount(accountNumber)
        );
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<Void> deposit(
            @PathVariable String accountNumber,
            @RequestBody AmountRequest request) {

        accountService.deposit(accountNumber, request.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Void> withdraw(
            @PathVariable String accountNumber,
            @RequestBody AmountRequest request) {

        accountService.withdraw(accountNumber, request.amount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getBalance(accountNumber)
        );
    }

    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getTransactionHistory(accountNumber)
        );
    }
}