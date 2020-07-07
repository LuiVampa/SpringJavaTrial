package com.sparkequation.spring.trial.api.controller.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        final ResponseErrorInfo responseErrorInfo = new ResponseErrorInfo(
                BAD_REQUEST,
                "Method Argument Not Valid",
                ex.getBindingResult()
                  .getAllErrors()
                  .stream()
                  .map(DefaultMessageSourceResolvable::getDefaultMessage)
                  .collect(Collectors.toList())
        );
        String body = null;
        try {
            body = objectMapper.writeValueAsString(responseErrorInfo);
        } catch (JsonProcessingException e) {
            logger.error("Couldn't write ResponseErrorInfo", e);
        }
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).headers(headers).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @SuppressWarnings("ConstantConditions")
    public void handleConstraintViolation(
            ConstraintViolationException ex,
            ServletWebRequest webRequest
    ) throws IOException {
        webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}
