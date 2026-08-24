package com.example.banking.service;
import com.example.banking.entity.Account;
import com.example.banking.exception.AccountNotFoundException;
import com.example.banking.exception.InsufficientBalanceException;
import com.example.banking.exception.InvalidAmountException;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.banking.entity.Transaction;
import com.example.banking.entity.TransactionType;
import java.math.BigDecimal;
import com.example.banking.entity.Transaction;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(String accountNumber) {
        Account account = new Account(accountNumber);
        return accountRepository.save(account);
    }

    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        Account account = getAccount(accountNumber);
        account.setBalance(account.getBalance().add(amount));

        accountRepository.save(account);

        transactionRepository.save(
                new Transaction(
                        accountNumber,
                        TransactionType.DEPOSIT,
                        amount
                )
        );
    }

    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        Account account = getAccount(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance for account: " + accountNumber);
        }

        account.setBalance(account.getBalance().subtract(amount));

        accountRepository.save(account);

        transactionRepository.save(
                new Transaction(
                        accountNumber,
                        TransactionType.WITHDRAWAL,
                        amount
                )
        );
    }

    public BigDecimal getBalance(String accountNumber) {
        return getAccount(accountNumber).getBalance();
    }

    private Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found: " + accountNumber));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Amount must be greater than zero");
        }
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        getAccount(accountNumber);

        return transactionRepository
                .findByAccountNumberOrderByTimestampDesc(accountNumber);
    }
}