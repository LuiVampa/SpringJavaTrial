package com.sparkequation.spring.trial.api.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Product")
public class NoSuchProductException extends RuntimeException {

    public NoSuchProductException(String message) {
        super(message);
    }
}
