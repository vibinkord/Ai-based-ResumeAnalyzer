package com.resumeanalyzer.web.controller;

import com.resumeanalyzer.exception.AnalysisException;
import com.resumeanalyzer.exception.FileProcessingException;
import com.resumeanalyzer.exception.SkillExtractionException;
import com.resumeanalyzer.exception.ValidationException;
import com.resumeanalyzer.web.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for REST API errors.
 * Centralizes exception handling across all controllers and returns consistent error responses.
 * Each exception type is mapped to an appropriate HTTP status code and error code.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ValidationException - thrown when input validation fails.
     * Returns 400 Bad Request with error code VALIDATION_ERROR.
     *
     * @param ex the ValidationException
     * @param request the web request
     * @return ResponseEntity with ErrorResponse and 400 status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            WebRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "VALIDATION_ERROR",
                "Invalid input provided. Please check your request.",
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles FileProcessingException - thrown when file upload/processing fails.
     * Returns 400 Bad Request if file format is unsupported, 500 Internal Server Error otherwise.
     *
     * @param ex the FileProcessingException
     * @param request the web request
     * @return ResponseEntity with ErrorResponse and appropriate status
     */
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponse> handleFileProcessingException(
            FileProcessingException ex,
            WebRequest request) {
        
        log.error("File processing error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "FILE_PROCESSING_ERROR",
                "Failed to process uploaded file. Supported formats: PDF, TXT, DOCX",
                request.getDescription(false).replace("uri=", "")
        );
        
        HttpStatus status = ex.getMessage().toLowerCase().contains("unsupported") 
                ? HttpStatus.BAD_REQUEST 
                : HttpStatus.INTERNAL_SERVER_ERROR;
        
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles SkillExtractionException - thrown when skill extraction fails.
     * Returns 500 Internal Server Error with error code SKILL_EXTRACTION_ERROR.
     *
     * @param ex the SkillExtractionException
     * @param request the web request
     * @return ResponseEntity with ErrorResponse and 500 status
     */
    @ExceptionHandler(SkillExtractionException.class)
    public ResponseEntity<ErrorResponse> handleSkillExtractionException(
            SkillExtractionException ex,
            WebRequest request) {
        
        log.error("Skill extraction error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "Failed to extract skills from resume",
                "SKILL_EXTRACTION_ERROR",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles AnalysisException - thrown when resume analysis fails.
     * Returns 500 Internal Server Error with error code ANALYSIS_ERROR.
     *
     * @param ex the AnalysisException
     * @param request the web request
     * @return ResponseEntity with ErrorResponse and 500 status
     */
    @ExceptionHandler(AnalysisException.class)
    public ResponseEntity<ErrorResponse> handleAnalysisException(
            AnalysisException ex,
            WebRequest request) {
        
        log.error("Analysis error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "An error occurred during resume analysis",
                "ANALYSIS_ERROR",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles IllegalArgumentException - thrown for illegal argument values.
     * Returns 400 Bad Request with error code ILLEGAL_ARGUMENT.
     *
     * @param ex the IllegalArgumentException
     * @param request the web request
     * @return ResponseEntity with ErrorResponse and 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "ILLEGAL_ARGUMENT",
                "Invalid argument provided",
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions - catch-all for unexpected errors.
     * Returns 500 Internal Server Error with error code INTERNAL_SERVER_ERROR.
     *
     * @param ex the Exception
     * @param request the web request
     * @return ResponseEntity with ErrorResponse and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                "Please try again later or contact support",
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
