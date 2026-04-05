package com.hasan.demo4.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid hatalarını yakalar
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    // Genel beklenmedik hatalar
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception ex, WebRequest request) {

        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    // Kayıt bulunamadığında
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }
}