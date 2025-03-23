package com.credable.lms.controller;

import com.credable.common.dto.ApiResponse;
import com.credable.lms.dto.SubscriptionRequest;
import com.credable.lms.dto.SubscriptionResponse;
import com.credable.lms.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        SubscriptionResponse response = subscriptionService.subscribe(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subscription successful", response));
    }
}