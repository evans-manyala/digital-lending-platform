// lms-service/src/main/java/com/credable/lms/repository/LoanRepository.java
package com.credable.lms.repository;

import com.credable.lms.model.Loan;
import com.credable.lms.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByScoringToken(String scoringToken);
    
    List<Loan> findByCustomerNumberOrderByCreatedAtDesc(String customerNumber);
    
    @Query("SELECT l FROM Loan l WHERE l.customerNumber = ?1 AND (l.status = ?2 OR l.status = ?3) ORDER BY l.createdAt DESC")
    List<Loan> findActiveLoans(String customerNumber, LoanStatus status1, LoanStatus status2);
    
    @Query("SELECT l FROM Loan l WHERE l.status = ?1 AND l.retryCount < ?2")
    List<Loan> findLoansForRetry(LoanStatus status, int maxRetries);
}