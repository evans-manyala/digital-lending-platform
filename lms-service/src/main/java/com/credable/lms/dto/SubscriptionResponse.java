// lms-service/src/main/java/com/credable/lms/dto/SubscriptionResponse.java
package com.credable.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean subscribed;
    private String message;
}