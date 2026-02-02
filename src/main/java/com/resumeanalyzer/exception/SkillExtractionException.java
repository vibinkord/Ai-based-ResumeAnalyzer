package com.resumeanalyzer.exception;

/**
 * Exception thrown when skill extraction fails.
 * This is a custom exception for skill extraction errors.
 */
public class SkillExtractionException extends RuntimeException {

    public SkillExtractionException(String message) {
        super(message);
    }

    public SkillExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

}
