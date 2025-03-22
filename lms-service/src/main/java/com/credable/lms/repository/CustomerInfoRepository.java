// lms-service/src/main/java/com/credable/lms/repository/CustomerInfoRepository.java
package com.credable.lms.repository;

import com.credable.lms.model.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Long> {
    Optional<CustomerInfo> findByCustomerNumber(String customerNumber);
}