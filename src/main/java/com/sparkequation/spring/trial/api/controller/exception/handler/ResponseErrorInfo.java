package com.sparkequation.spring.trial.api.controller.exception.handler;

import org.springframework.cglib.core.CollectionUtils;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResponseErrorInfo {
    private final HttpStatus status;
    private final String message;
    private final List<String> errors;

    public ResponseErrorInfo(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseErrorInfo that = (ResponseErrorInfo) o;
        return status == that.status &&
               Objects.equals(message, that.message) &&
               Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message, errors);
    }

    @Override
    public String toString() {
        return "ResponseErrorInfo{" +
               "status=" + status +
               ", message='" + message + '\'' +
               ", errors=" + errors.stream().sorted().collect(Collectors.joining()) +
               '}';
    }
}
