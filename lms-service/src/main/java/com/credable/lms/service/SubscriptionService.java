package com.credable.lms.service;

import com.credable.common.exception.ServiceException;
import com.credable.lms.dto.SubscriptionRequest;
import com.credable.lms.dto.SubscriptionResponse;
import com.credable.lms.model.CustomerInfo;
import com.credable.lms.model.Subscription;
import com.credable.lms.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final KycService kycService;

    @Autowired
    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            KycService kycService) {
        this.subscriptionRepository = subscriptionRepository;
        this.kycService = kycService;
    }

    @Transactional
    public SubscriptionResponse subscribe(SubscriptionRequest request) {
        String customerNumber = request.getCustomerNumber();
        
        // Check if customer already subscribed
        Optional<Subscription> existingSubscription = subscriptionRepository.findByCustomerNumber(customerNumber);
        if (existingSubscription.isPresent()) {
            Subscription subscription = existingSubscription.get();
            CustomerInfo customerInfo = kycService.getCustomerInfo(customerNumber);
            return buildSubscriptionResponse(subscription, customerInfo);
        }
        
        // Get customer information to validate customer exists
        CustomerInfo customerInfo = kycService.getCustomerInfo(customerNumber);
        
        // Create new subscription
        Subscription subscription = new Subscription();
        subscription.setCustomerNumber(customerNumber);
        subscription.setActive(true);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());
        
        subscription = subscriptionRepository.save(subscription);
        
        return buildSubscriptionResponse(subscription, customerInfo);
    }
    
    private SubscriptionResponse buildSubscriptionResponse(Subscription subscription, CustomerInfo customerInfo) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setCustomerNumber(subscription.getCustomerNumber());
        response.setCustomerName(customerInfo.getFirstName() + " " + customerInfo.getLastName());
        response.setStatus(subscription.isActive() ? "ACTIVE" : "INACTIVE");
        response.setSubscribedAt(subscription.getCreatedAt());
        return response;
    }
}