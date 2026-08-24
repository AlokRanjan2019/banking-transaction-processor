package com.example.banking.service;

import com.example.banking.entity.Account;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.exception.InsufficientBalanceException;
import com.example.banking.exception.InvalidTransferException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionRepository = mock(TransactionRepository.class);

        transferService = new TransferService(
                accountRepository,
                transactionRepository
        );
    }

    @Test
    void shouldTransferMoneySuccessfully() {

        Account source = new Account("ACC001");
        Account destination = new Account("ACC002");

        source.setBalance(new BigDecimal("1000"));
        destination.setBalance(new BigDecimal("500"));

        when(accountRepository.findByAccountNumber("ACC001"))
                .thenReturn(Optional.of(source));

        when(accountRepository.findByAccountNumber("ACC002"))
                .thenReturn(Optional.of(destination));

        transferService.transfer(
                "ACC001",
                "ACC002",
                new BigDecimal("300")
        );

        assertEquals(
                new BigDecimal("700"),
                source.getBalance()
        );

        assertEquals(
                new BigDecimal("800"),
                destination.getBalance()
        );

        verify(accountRepository).save(source);
        verify(accountRepository).save(destination);
        verify(transactionRepository, times(2))
                .save(any());
    }

    @Test
    void shouldRejectTransferWhenBalanceIsInsufficient() {

        Account source = new Account("ACC001");
        Account destination = new Account("ACC002");

        source.setBalance(new BigDecimal("100"));

        when(accountRepository.findByAccountNumber("ACC001"))
                .thenReturn(Optional.of(source));

        when(accountRepository.findByAccountNumber("ACC002"))
                .thenReturn(Optional.of(destination));

        assertThrows(
                InsufficientBalanceException.class,
                () -> transferService.transfer(
                        "ACC001",
                        "ACC002",
                        new BigDecimal("500")
                )
        );

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldRejectTransferToSameAccount() {

        assertThrows(
                InvalidTransferException.class,
                () -> transferService.transfer(
                        "ACC001",
                        "ACC001",
                        new BigDecimal("100")
                )
        );
    }
}