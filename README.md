# Banking Transaction Processor

## Overview

A simple banking transaction processing service built using Java and Spring Boot.

The application supports:

- Account creation
- Deposit
- Withdrawal
- Account-to-account transfer
- Balance enquiry
- Transaction history

## Architecture

The application follows a simple layered architecture:

Controller → Service → Repository → Database

- **Controller** handles REST APIs.
- **Service** contains banking business logic.
- **Repository** handles database persistence.
- **H2** is used as the database for this kata.

## Business Rules

- Account numbers must be unique.
- Transaction amount must be greater than zero.
- Withdrawal cannot exceed the available balance.
- Source and destination accounts must be different.
- Both accounts must exist before a transfer.
- Successful transactions are recorded in the ledger.
- Transactions contain timestamps.
- Transfers are atomic using `@Transactional`.

## APIs

### Create Account

```text
POST /api/accounts?accountNumber=ACC001