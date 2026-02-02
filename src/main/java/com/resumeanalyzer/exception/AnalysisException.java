package com.resumeanalyzer.exception;

/**
 * Exception thrown when resume analysis fails.
 * This is a generic exception for analysis-related errors.
 */
public class AnalysisException extends RuntimeException {

    public AnalysisException(String message) {
        super(message);
    }

    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

}
