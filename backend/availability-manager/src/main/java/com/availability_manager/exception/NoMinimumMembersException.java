package com.availability_manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoMinimumMembersException extends RuntimeException {
    public NoMinimumMembersException(String message) {
        super(message);
    }
}
