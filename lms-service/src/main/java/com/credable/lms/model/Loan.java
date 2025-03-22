// lms-service/src/main/java/com/credable/lms/model/Loan.java
package com.credable.lms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String customerNumber;
    private BigDecimal amount;
    private BigDecimal approvedAmount;
    private Integer score;
    
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    
    private String exclusion;
    private String exclusionReason;
    
    private String scoringToken;
    private int retryCount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = LoanStatus.PENDING;
        this.retryCount = 0;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}