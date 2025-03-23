package com.credable.lms.controller;

import com.credable.common.dto.ApiResponse;
import com.credable.lms.dto.LoanRequest;
import com.credable.lms.dto.LoanResponse;
import com.credable.lms.dto.LoanStatusRequest;
import com.credable.lms.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<LoanResponse>> requestLoan(@Valid @RequestBody LoanRequest request) {
        LoanResponse response = loanService.processLoanRequest(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Loan request processed successfully", response));
    }

    @GetMapping("/status/{loanReference}")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanStatus(@PathVariable String loanReference) {
        LoanResponse response = loanService.getLoanStatus(loanReference);
        return ResponseEntity.ok(new ApiResponse<>(true, "Loan status retrieved successfully", response));
    }

    @PostMapping("/status")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanStatusPost(@Valid @RequestBody LoanStatusRequest request) {
        LoanResponse response = loanService.getLoanStatus(request.getLoanReferenceNumber());
        return ResponseEntity.ok(new ApiResponse<>(true, "Loan status retrieved successfully", response));
    }
}