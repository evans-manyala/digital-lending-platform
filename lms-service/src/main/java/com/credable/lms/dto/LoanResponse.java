// lms-service/src/main/java/com/credable/lms/dto/LoanResponse.java
package com.credable.lms.dto;

import com.credable.lms.model.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    private Long loanId;
    private String customerNumber;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private LoanStatus status;
    private LocalDateTime requestedAt;
    private String message;
}