package com.credable.lms.config;

import com.credable.lms.service.ScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ScoringClientRegistration {

    @Value("${scoring.client.url}")
    private String clientUrl;

    @Value("${scoring.client.name}")
    private String clientName;

    @Value("${scoring.client.username}")
    private String clientUsername;

    @Value("${scoring.client.password}")
    private String clientPassword;

    private final ScoringService scoringService;

    @Autowired
    public ScoringClientRegistration(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerClient() {
        String token = scoringService.registerClient(
                clientUrl,
                clientName,
                clientUsername,
                clientPassword
        );
        
        // Update the token in the application configuration
        System.setProperty("scoring.api.client-token", token);
    }
}