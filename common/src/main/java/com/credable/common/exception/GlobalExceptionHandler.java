// common/src/main/java/com/credable/common/exception/GlobalExceptionHandler.java
package com.credable.common.exception;

import com.credable.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleServiceException(ServiceException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(ex.getStatus())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}