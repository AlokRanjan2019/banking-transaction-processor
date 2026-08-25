package com.example.banking.controller;

import com.example.banking.controller.TransferRequest;
import com.example.banking.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Void> transfer(
            @RequestBody TransferRequest request) {

        transferService.transfer(
                request.fromAccount(),
                request.toAccount(),
                request.amount()
        );

        return ResponseEntity.ok().build();
    }
}