package com.example.banking.service;

import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;
import com.example.banking.entity.TransactionType;
import com.example.banking.exception.InsufficientBalanceException;
import com.example.banking.exception.InvalidAmountException;
import com.example.banking.exception.InvalidTransferException;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transfer(String fromAccount,
                         String toAccount,
                         BigDecimal amount) {

        validateAmount(amount);

        if (fromAccount.equals(toAccount)) {
            throw new InvalidTransferException(
                    "Source and destination accounts must be different");
        }

        Account source = accountRepository.findByAccountNumber(fromAccount)
                .orElseThrow(() ->
                        new InvalidTransferException("Source account not found"));

        Account destination = accountRepository.findByAccountNumber(toAccount)
                .orElseThrow(() ->
                        new InvalidTransferException("Destination account not found"));

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(destination);

        transactionRepository.save(
                new Transaction(
                        fromAccount,
                        TransactionType.TRANSFER,
                        amount
                )
        );

        transactionRepository.save(
                new Transaction(
                        toAccount,
                        TransactionType.TRANSFER,
                        amount
                )
        );
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Amount must be greater than zero");
        }
    }
}