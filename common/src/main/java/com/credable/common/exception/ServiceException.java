// common/src/main/java/com/credable/common/exception/ServiceException.java
package com.credable.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final int status;

    public ServiceException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public ServiceException(String message, int status) {
        super(message);
        this.status = status;
    }
}