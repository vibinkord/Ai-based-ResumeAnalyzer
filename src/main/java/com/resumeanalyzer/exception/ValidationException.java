package com.resumeanalyzer.exception;

/**
 * Exception thrown when request validation fails.
 * This includes missing required fields, invalid data, etc.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
