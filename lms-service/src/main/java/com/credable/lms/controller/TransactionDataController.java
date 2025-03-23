package com.credable.lms.controller;

import com.credable.common.dto.ApiResponse;
import com.credable.lms.service.TransactionService;
import com.credable.lms.soap.transaction.TransactionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionDataController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionDataController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{customerNumber}")
    public ResponseEntity<List<TransactionData>> getTransactionData(@PathVariable String customerNumber) {
        List<TransactionData> transactions = transactionService.getTransactionData(customerNumber);
        return ResponseEntity.ok(transactions);
    }
}