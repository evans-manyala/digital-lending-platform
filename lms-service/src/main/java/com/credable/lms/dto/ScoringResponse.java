// lms-service/src/main/java/com/credable/lms/dto/ScoringResponse.java
package com.credable.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoringResponse {
    private Long id;
    private String customerNumber;
    private Integer score;
    private BigDecimal limitAmount;
    private String exclusion;
    private String exclusionReason;
}