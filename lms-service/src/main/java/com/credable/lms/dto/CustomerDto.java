// lms-service/src/main/java/com/credable/lms/dto/CustomerDto.java
package com.credable.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String idNumber;
    private String phoneNumber;
    private String email;
}