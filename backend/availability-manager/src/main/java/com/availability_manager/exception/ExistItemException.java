package com.availability_manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExistItemException extends RuntimeException{
    public ExistItemException(String message) {
        super(message);
    }
}
