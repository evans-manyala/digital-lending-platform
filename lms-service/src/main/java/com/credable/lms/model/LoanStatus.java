// lms-service/src/main/java/com/credable/lms/model/LoanStatus.java
package com.credable.lms.model;

public enum LoanStatus {
    PENDING,
    SCORING_INITIATED,
    APPROVED,
    DISBURSED,
    REJECTED,
    FAILED
}