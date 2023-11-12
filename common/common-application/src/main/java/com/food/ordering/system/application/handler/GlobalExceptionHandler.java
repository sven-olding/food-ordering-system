package com.food.ordering.system.application.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected error!")
                .build();
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleException(ValidationException validationException) {

        ErrorDTO errorDTO;
        if (validationException instanceof ConstraintViolationException) {
            String violation = extractViolationFromException((ConstraintViolationException) validationException);
            log.error(violation, validationException);
            errorDTO = ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(violation)
                    .build();
        } else {
            String exceptionMessage = validationException.getMessage();
            log.error(exceptionMessage, validationException);
            errorDTO = ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(exceptionMessage)
                    .build();
        }

        return errorDTO;
    }

    private String extractViolationFromException(ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("--"));
    }
}
