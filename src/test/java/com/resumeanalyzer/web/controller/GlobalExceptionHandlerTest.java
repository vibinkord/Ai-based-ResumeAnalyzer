package com.resumeanalyzer.web.controller;

import com.resumeanalyzer.exception.AnalysisException;
import com.resumeanalyzer.exception.FileProcessingException;
import com.resumeanalyzer.exception.SkillExtractionException;
import com.resumeanalyzer.exception.ValidationException;
import com.resumeanalyzer.web.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler class.
 * Tests exception handling and error response generation.
 */
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest mockWebRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockWebRequest = mock(WebRequest.class);
        when(mockWebRequest.getDescription(false)).thenReturn("uri=/api/analyze");
    }

    // ===================== ValidationException Tests =====================

    @Test
    @DisplayName("Should handle ValidationException with 400 Bad Request")
    void testHandleValidationException_ReturnsBadRequest() {
        ValidationException exception = new ValidationException("Resume text cannot be empty");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, mockWebRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resume text cannot be empty", response.getBody().getError());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
    }

    @Test
    @DisplayName("Should include timestamp in ValidationException response")
    void testHandleValidationException_IncludesTimestamp() {
        ValidationException exception = new ValidationException("Invalid input");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, mockWebRequest);
        
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Should include request path in ValidationException response")
    void testHandleValidationException_IncludesPath() {
        ValidationException exception = new ValidationException("Invalid input");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, mockWebRequest);
        
        assertNotNull(response.getBody().getPath());
    }

    // ===================== FileProcessingException Tests =====================

    @Test
    @DisplayName("Should handle FileProcessingException with unsupported format as 400")
    void testHandleFileProcessingException_UnsupportedFormat_ReturnsBadRequest() {
        FileProcessingException exception = new FileProcessingException("Unsupported file format: XLSX");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileProcessingException(exception, mockWebRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("FILE_PROCESSING_ERROR", response.getBody().getCode());
    }

    @Test
    @DisplayName("Should handle FileProcessingException with other errors as 500")
    void testHandleFileProcessingException_OtherError_ReturnsInternalServerError() {
        FileProcessingException exception = new FileProcessingException("Failed to read file");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileProcessingException(exception, mockWebRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("FILE_PROCESSING_ERROR", response.getBody().getCode());
    }

    @Test
    @DisplayName("Should include helpful message in FileProcessingException response")
    void testHandleFileProcessingException_IncludesHelpfulMessage() {
        FileProcessingException exception = new FileProcessingException("Failed to process file");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileProcessingException(exception, mockWebRequest);
        
        assertNotNull(response.getBody().getDetails());
        assertTrue(response.getBody().getDetails().contains("PDF"));
    }

    // ===================== SkillExtractionException Tests =====================

    @Test
    @DisplayName("Should handle SkillExtractionException with 500 Internal Server Error")
    void testHandleSkillExtractionException_ReturnsInternalServerError() {
        SkillExtractionException exception = new SkillExtractionException("Failed to extract skills");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSkillExtractionException(exception, mockWebRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("SKILL_EXTRACTION_ERROR", response.getBody().getCode());
    }

    @Test
    @DisplayName("Should include error details in SkillExtractionException response")
    void testHandleSkillExtractionException_IncludesDetails() {
        SkillExtractionException exception = new SkillExtractionException("Regex compilation failed");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSkillExtractionException(exception, mockWebRequest);
        
        assertEquals("Regex compilation failed", response.getBody().getDetails());
    }

    // ===================== AnalysisException Tests =====================

    @Test
    @DisplayName("Should handle AnalysisException with 500 Internal Server Error")
    void testHandleAnalysisException_ReturnsInternalServerError() {
        AnalysisException exception = new AnalysisException("Analysis failed");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAnalysisException(exception, mockWebRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ANALYSIS_ERROR", response.getBody().getCode());
    }

    @Test
    @DisplayName("Should provide generic message for AnalysisException")
    void testHandleAnalysisException_GenericMessage() {
        AnalysisException exception = new AnalysisException("Some internal error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAnalysisException(exception, mockWebRequest);
        
        assertEquals("An error occurred during resume analysis", response.getBody().getError());
        assertEquals("Some internal error", response.getBody().getDetails());
    }

    // ===================== IllegalArgumentException Tests =====================

    @Test
    @DisplayName("Should handle IllegalArgumentException with 400 Bad Request")
    void testHandleIllegalArgumentException_ReturnsBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Job description is required");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, mockWebRequest);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ILLEGAL_ARGUMENT", response.getBody().getCode());
    }

    @Test
    @DisplayName("Should include exception message in IllegalArgumentException response")
    void testHandleIllegalArgumentException_IncludesMessage() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument value");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, mockWebRequest);
        
        assertEquals("Invalid argument value", response.getBody().getError());
    }

    // ===================== Generic Exception Tests =====================

    @Test
    @DisplayName("Should handle generic Exception with 500 Internal Server Error")
    void testHandleGlobalException_ReturnsInternalServerError() {
        Exception exception = new Exception("Unexpected error occurred");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, mockWebRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
    }

    @Test
    @DisplayName("Should provide generic message for unexpected exceptions")
    void testHandleGlobalException_GenericMessage() {
        Exception exception = new Exception("Some unknown error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, mockWebRequest);
        
        assertEquals("An unexpected error occurred", response.getBody().getError());
        assertEquals("Please try again later or contact support", response.getBody().getDetails());
    }

    @Test
    @DisplayName("Should set timestamp for generic exception response")
    void testHandleGlobalException_IncludesTimestamp() {
        Exception exception = new Exception("Error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, mockWebRequest);
        
        assertNotNull(response.getBody().getTimestamp());
    }

    // ===================== Error Response Structure Tests =====================

    @Test
    @DisplayName("Should always include error field in response")
    void testErrorResponseStructure_HasErrorField() {
        ValidationException exception = new ValidationException("Test error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, mockWebRequest);
        
        assertNotNull(response.getBody().getError());
    }

    @Test
    @DisplayName("Should always include code field in response")
    void testErrorResponseStructure_HasCodeField() {
        ValidationException exception = new ValidationException("Test error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, mockWebRequest);
        
        assertNotNull(response.getBody().getCode());
    }

    @Test
    @DisplayName("Should always include timestamp field in response")
    void testErrorResponseStructure_HasTimestampField() {
        ValidationException exception = new ValidationException("Test error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, mockWebRequest);
        
        assertNotNull(response.getBody().getTimestamp());
    }

    // ===================== HTTP Status Code Tests =====================

    @Test
    @DisplayName("Should return 400 for client errors")
    void testHttpStatusCodes_ClientError() {
        ValidationException exception = new ValidationException("Invalid input");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, mockWebRequest);
        
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("Should return 500 for server errors")
    void testHttpStatusCodes_ServerError() {
        AnalysisException exception = new AnalysisException("Server error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAnalysisException(exception, mockWebRequest);
        
        assertTrue(response.getStatusCode().is5xxServerError());
    }

    // ===================== Error Code Consistency Tests =====================

    @Test
    @DisplayName("Each exception type should have unique error code")
    void testErrorCodes_AreUnique() {
        String validationCode = exceptionHandler.handleValidationException(
                new ValidationException("test"), mockWebRequest).getBody().getCode();
        String fileCode = exceptionHandler.handleFileProcessingException(
                new FileProcessingException("test"), mockWebRequest).getBody().getCode();
        String skillCode = exceptionHandler.handleSkillExtractionException(
                new SkillExtractionException("test"), mockWebRequest).getBody().getCode();
        String analysisCode = exceptionHandler.handleAnalysisException(
                new AnalysisException("test"), mockWebRequest).getBody().getCode();
        
        // All codes should be different
        long uniqueCodes = java.util.Set.of(validationCode, fileCode, skillCode, analysisCode).size();
        assertEquals(4, uniqueCodes);
    }
}
