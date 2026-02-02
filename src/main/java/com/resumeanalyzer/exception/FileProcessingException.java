package com.resumeanalyzer.exception;

/**
 * Exception thrown when file processing fails.
 * This includes PDF extraction, text reading, and other file operations.
 */
public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
