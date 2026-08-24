package com.example.banking.service;

import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;
import com.example.banking.entity.TransactionType;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.exception.InsufficientBalanceException;
import com.example.banking.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);

        accountService = new AccountService(
                accountRepository,
                transactionRepository
        );
    }

    @Test
    void shouldCreateAccountWithZeroBalance() {
        Account account = new Account("ACC001");

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        Account result = accountService.createAccount("ACC001");

        assertEquals("ACC001", result.getAccountNumber());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void shouldDepositMoney() {
        Account account = new Account("ACC001");

        when(accountRepository.findByAccountNumber("ACC001"))
                .thenReturn(Optional.of(account));

        accountService.deposit(
                "ACC001",
                new BigDecimal("1000")
        );

        assertEquals(
                new BigDecimal("1000"),
                account.getBalance()
        );
    }

    @Test
    void shouldRejectZeroDeposit() {

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.deposit(
                        "ACC001",
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void shouldRejectNegativeDeposit() {

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.deposit(
                        "ACC001",
                        new BigDecimal("-100")
                )
        );
    }

    @Test
    void shouldRejectWithdrawalWhenBalanceIsInsufficient() {

        Account account = new Account("ACC001");

        when(accountRepository.findByAccountNumber("ACC001"))
                .thenReturn(Optional.of(account));

        assertThrows(
                InsufficientBalanceException.class,
                () -> accountService.withdraw(
                        "ACC001",
                        new BigDecimal("100")
                )
        );
    }

    @Test
    void shouldReturnTransactionHistory() {

        Account account = new Account("ACC001");

        when(accountRepository.findByAccountNumber("ACC001"))
                .thenReturn(Optional.of(account));

        Transaction transaction = new Transaction(
                "ACC001",
                TransactionType.DEPOSIT,
                new BigDecimal("1000")
        );

        when(transactionRepository
                .findByAccountNumberOrderByTimestampDesc("ACC001"))
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                accountService.getTransactionHistory("ACC001");

        assertEquals(1, result.size());
        assertEquals(
                TransactionType.DEPOSIT,
                result.get(0).getType()
        );
    }
}