package com.example.finaljava.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

        @ExceptionHandler(MyResourceNotFoundException.class)
        public ResponseEntity<ErrorMessage> resourceNotFoundException(MyResourceNotFoundException ex,
                                                                      WebRequest request) {
                ErrorMessage message = new ErrorMessage(
                        HttpStatus.NOT_FOUND.value(),
                        new Date(),
                        ex.getMessage(),
                        request.getDescription(false));

                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex,
                                                                   WebRequest request) {
                ErrorMessage message = new ErrorMessage(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        new Date(),
                        ex.getMessage(),
                        request.getDescription(false));

                return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<Map<String, String>> handleUniqueConstraint(DataIntegrityViolationException ex) {
                Map<String, String> errors = new HashMap<>();
                String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

                if (message != null && message.toLowerCase().contains("duplicate")) {
                        String fieldName = extractFieldFromMessage(message);
                        errors.put(fieldName, fieldName + " must be unique!");
                } else {
                        errors.put("error", "Database error: " + message);
                }

                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        /**
         * Extracts the field name from the DB error message dynamically.
         * Works for both MySQL and PostgreSQL.
         */
        private String extractFieldFromMessage(String message) {
                message = message.toLowerCase();

                try {
                        // MySQL: Duplicate entry 'value' for key 'uk_category_name'
                        if (message.contains("for key")) {
                                String keyPart = message.substring(message.indexOf("for key") + 7).trim();
                                keyPart = keyPart.replaceAll("[`'\"]", ""); // remove quotes/backticks
                                return parseFieldFromConstraint(keyPart);
                        }

                        // PostgreSQL: duplicate key value violates unique constraint "uk_category_name"
                        if (message.contains("violates unique constraint")) {
                                String[] parts = message.split("\"");
                                if (parts.length >= 2) {
                                        return parseFieldFromConstraint(parts[1]);
                                }
                        }
                } catch (Exception ignored) {}

                return "error";
        }

        /**
         * Converts a constraint name like uk_category_name -> name
         * or table.uk_category_name -> name
         */
        private String parseFieldFromConstraint(String constraintName) {
                if (constraintName.contains(".")) {
                        String[] parts = constraintName.split("\\.");
                        constraintName = parts[parts.length - 1];
                }
                if (constraintName.contains("_")) {
                        String[] parts = constraintName.split("_");
                        return parts[parts.length - 1]; // take last part as field
                }
                return constraintName;
        }
}
