// lms-service/src/main/java/com/credable/lms/dto/LoanStatusRequest.java
package com.credable.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatusRequest {
    @NotBlank(message = "Customer number is required")
    private String customerNumber;
}