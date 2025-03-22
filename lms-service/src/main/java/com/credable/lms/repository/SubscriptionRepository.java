// lms-service/src/main/java/com/credable/lms/repository/SubscriptionRepository.java
package com.credable.lms.repository;

import com.credable.lms.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByCustomerNumber(String customerNumber);
    boolean existsByCustomerNumber(String customerNumber);
}