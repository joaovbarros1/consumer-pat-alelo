package br.com.alelo.consumer.consumerpat.infra.exception;

import org.springframework.validation.FieldError;

public record ValidationErrorData(String field, String message) {
    public ValidationErrorData(FieldError fieldError) {
        this(fieldError.getField(), fieldError.getDefaultMessage());
    }
}