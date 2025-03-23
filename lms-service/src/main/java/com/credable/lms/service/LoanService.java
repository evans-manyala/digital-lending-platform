package com.credable.lms.service;

import com.credable.common.exception.ServiceException;
import com.credable.lms.dto.LoanRequest;
import com.credable.lms.dto.LoanResponse;
import com.credable.lms.dto.ScoringResponse;
import com.credable.lms.model.CustomerInfo;
import com.credable.lms.model.Loan;
import com.credable.lms.model.LoanStatus;
import com.credable.lms.model.Subscription;
import com.credable.lms.repository.LoanRepository;
import com.credable.lms.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final KycService kycService;
    private final ScoringService scoringService;

    @Autowired
    public LoanService(
            LoanRepository loanRepository,
            SubscriptionRepository subscriptionRepository,
            KycService kycService,
            ScoringService scoringService) {
        this.loanRepository = loanRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.kycService = kycService;
        this.scoringService = scoringService;
    }

    @Transactional
    public LoanResponse processLoanRequest(LoanRequest loanRequest) {
        String customerNumber = loanRequest.getCustomerNumber();
        
        // Check if customer is subscribed
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByCustomerNumber(customerNumber);
        if (subscriptionOpt.isEmpty()) {
            throw new ServiceException("Customer is not subscribed to the loan service");
        }
        
        // Check if customer has active loan
        Optional<Loan> activeLoan = loanRepository.findActiveByCustomerNumber(customerNumber);
        if (activeLoan.isPresent()) {
            throw new ServiceException("Customer already has an active loan or a pending loan request");
        }
        
        // Get customer information
        CustomerInfo customerInfo = kycService.getCustomerInfo(customerNumber);
        
        // Create loan record with PENDING status
        Loan loan = new Loan();
        loan.setLoanReferenceNumber(generateLoanReference());
        loan.setCustomerNumber(customerNumber);
        loan.setAmount(loanRequest.getAmount());
        loan.setStatus(LoanStatus.PENDING);
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());
        loan = loanRepository.save(loan);
        
        // Initiate scoring process
        try {
            String token = scoringService.initiateQueryScore(customerNumber);
            loan.setScoringToken(token);
            loan = loanRepository.save(loan);
            
            // Query score
            ScoringResponse scoringResponse = scoringService.queryScore(token);
            
            // Update loan based on scoring results
            updateLoanBasedOnScoring(loan, scoringResponse);
            
            return buildLoanResponse(loan, customerInfo);
        } catch (ServiceException e) {
            // If scoring fails, update loan status to REJECTED
            loan.setStatus(LoanStatus.REJECTED);
            loan.setRejectionReason("Scoring engine failed: " + e.getMessage());
            loan.setUpdatedAt(LocalDateTime.now());
            loanRepository.save(loan);
            
            throw e;
        }
    }
    
    @Transactional
    public LoanResponse getLoanStatus(String loanReference) {
        Optional<Loan> loanOpt = loanRepository.findByLoanReferenceNumber(loanReference);
        if (loanOpt.isEmpty()) {
            throw new ServiceException("Loan not found");
        }
        
        Loan loan = loanOpt.get();
        
        // Check if loan is still pending and we need to retry scoring
        if (loan.getStatus() == LoanStatus.PENDING && loan.getScoringToken() != null) {
            try {
                ScoringResponse scoringResponse = scoringService.queryScore(loan.getScoringToken());
                updateLoanBasedOnScoring(loan, scoringResponse);
            } catch (ServiceException e) {
                // Ignore scoring error when checking status - we'll show the current state
            }
        }
        
        CustomerInfo customerInfo = kycService.getCustomerInfo(loan.getCustomerNumber());
        return buildLoanResponse(loan, customerInfo);
    }
    
    private void updateLoanBasedOnScoring(Loan loan, ScoringResponse scoringResponse) {
        if ("No Exclusion".equals(scoringResponse.getExclusion())) {
            // Check if requested amount is within limit
            if (loan.getAmount().compareTo(BigDecimal.valueOf(scoringResponse.getLimitAmount())) <= 0) {
                loan.setStatus(LoanStatus.APPROVED);
                loan.setApprovedAmount(loan.getAmount());
            } else {
                loan.setStatus(LoanStatus.APPROVED);
                loan.setApprovedAmount(BigDecimal.valueOf(scoringResponse.getLimitAmount()));
                loan.setRejectionReason("Requested amount exceeds limit. Maximum approved: " + scoringResponse.getLimitAmount());
            }
        } else {
            loan.setStatus(LoanStatus.REJECTED);
            loan.setRejectionReason(scoringResponse.getExclusionReason());
        }
        
        loan.setScore(scoringResponse.getScore());
        loan.setUpdatedAt(LocalDateTime.now());
        loanRepository.save(loan);
    }
    
    private LoanResponse buildLoanResponse(Loan loan, CustomerInfo customerInfo) {
        LoanResponse response = new LoanResponse();
        response.setLoanReferenceNumber(loan.getLoanReferenceNumber());
        response.setCustomerNumber(loan.getCustomerNumber());
        response.setCustomerName(customerInfo.getFirstName() + " " + customerInfo.getLastName());
        response.setRequestedAmount(loan.getAmount());
        response.setApprovedAmount(loan.getApprovedAmount());
        response.setStatus(loan.getStatus().name());
        response.setRejectionReason(loan.getRejectionReason());
        response.setScore(loan.getScore());
        response.setCreatedAt(loan.getCreatedAt());
        response.setUpdatedAt(loan.getUpdatedAt());
        return response;
    }
    
    private String generateLoanReference() {
        return "LN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}