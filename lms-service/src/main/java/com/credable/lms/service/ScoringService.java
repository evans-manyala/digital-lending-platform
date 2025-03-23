package com.credable.lms.service;

import com.credable.common.exception.ServiceException;
import com.credable.lms.dto.ScoringResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScoringService {

    @Value("${scoring.api.base-url}")
    private String scoringBaseUrl;

    @Value("${scoring.api.client-token}")
    private String clientToken;

    @Value("${scoring.api.retry-count}")
    private int retryCount;

    @Value("${scoring.api.retry-delay}")
    private long retryDelay;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ScoringService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String initiateQueryScore(String customerNumber) {
        try {
            String url = scoringBaseUrl + "/api/v1/scoring/initiateQueryScore/" + customerNumber;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("client-token", clientToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ServiceException("Failed to initiate query score");
            }
        } catch (Exception e) {
            throw new ServiceException("Error initiating query score: " + e.getMessage(), e);
        }
    }

    public ScoringResponse queryScore(String token) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < retryCount) {
            try {
                String url = scoringBaseUrl + "/api/v1/scoring/queryScore/" + token;
                
                HttpHeaders headers = new HttpHeaders();
                headers.set("client-token", clientToken);
                
                HttpEntity<String> entity = new HttpEntity<>(headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        String.class
                );
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return objectMapper.readValue(response.getBody(), ScoringResponse.class);
                } else {
                    throw new ServiceException("Failed to query score");
                }
            } catch (Exception e) {
                lastException = e;
                attempts++;
                
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ServiceException("Retry interrupted: " + ie.getMessage(), ie);
                }
            }
        }
        
        throw new ServiceException("Failed to query score after " + retryCount + " attempts: " + 
                (lastException != null ? lastException.getMessage() : "Unknown error"), lastException);
    }

    public String registerClient(String url, String name, String username, String password) {
        try {
            String registerUrl = scoringBaseUrl + "/api/v1/client/createClient";
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("url", url);
            requestBody.put("name", name);
            requestBody.put("username", username);
            requestBody.put("password", password);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                    registerUrl,
                    requestBody,
                    String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
                return (String) responseMap.get("token");
            } else {
                throw new ServiceException("Failed to register client with scoring engine");
            }
        } catch (Exception e) {
            throw new ServiceException("Error registering client: " + e.getMessage(), e);
        }
    }
}