package com.example.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "Resource Not Found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "The requested resource could not be found");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse(
                "Unsupported Media Type",
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Content type not supported: " + ex.getContentType(),
                "Supported content types are: " + ex.getSupportedMediaTypes());
        return new ResponseEntity<>(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse(
                "Data Integrity Violation",
                HttpStatus.CONFLICT.value(),
                "Database constraint violation occurred",
                ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransactionSystem(TransactionSystemException ex) {
        ErrorResponse error = new ErrorResponse(
                "Transaction Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unable to commit against JDBC Connection",
                "Database transaction failed: " + ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                "An unexpected error occurred: " + ex.getClass().getSimpleName());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> validationErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> validationErrors.add(error.getField() + ": " + error.getDefaultMessage()));

        ErrorResponse error = new ErrorResponse(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "Input validation failed",
                String.join(", ", validationErrors));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        ErrorResponse error = new ErrorResponse(
                "Insufficient Stock",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "The requested quantity exceeds available stock");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(
                "Malformed JSON",
                HttpStatus.BAD_REQUEST.value(),
                "Invalid JSON format",
                "Request body contains malformed JSON");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String expectedType = requiredType != null ? requiredType.getSimpleName() : "unknown";
        ErrorResponse error = new ErrorResponse(
                "Invalid Path Variable",
                HttpStatus.BAD_REQUEST.value(),
                "Invalid path variable type: " + ex.getName(),
                "Expected " + expectedType + " but got " + ex.getValue());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                "Invalid Argument",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Please provide valid input parameters");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violation errors from @Validated annotations.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> validationErrors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            validationErrors.add(fieldName + ": " + errorMessage);
        }

        ErrorResponse error = new ErrorResponse(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "Constraint validation failed",
                String.join(", ", validationErrors));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle duplicate review exceptions.
     */
    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReview(DuplicateReviewException ex) {
        ErrorResponse error = new ErrorResponse(
                "Duplicate Review",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                "Customer has already reviewed this product");

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handle user not purchased product exceptions.
     */
    @ExceptionHandler(UserNotPurchasedProductException.class)
    public ResponseEntity<ErrorResponse> handleUserNotPurchasedProduct(UserNotPurchasedProductException ex) {
        ErrorResponse error = new ErrorResponse(
                "Purchase Required",
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                "Customer must purchase product before reviewing");

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}

class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String details;

    public ErrorResponse(String error, int status, String message, String details) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    // Getters
    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    // Setters
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
