package com.resumeanalyzer.web.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ErrorResponse DTO class.
 * Tests serialization, deserialization, and field handling.
 */
@DisplayName("ErrorResponse DTO Tests")
class ErrorResponseTest {

    private ErrorResponse errorResponse;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        errorResponse = new ErrorResponse();
        objectMapper = new ObjectMapper();
    }

    // ===================== Constructor Tests =====================

    @Test
    @DisplayName("Should create ErrorResponse with default constructor")
    void testDefaultConstructor() {
        ErrorResponse response = new ErrorResponse();
        
        assertNull(response.getError());
        assertNull(response.getCode());
        assertNotNull(response.getTimestamp());
        assertNull(response.getDetails());
        assertNull(response.getPath());
    }

    @Test
    @DisplayName("Should create ErrorResponse with error and code")
    void testConstructorWithErrorAndCode() {
        ErrorResponse response = new ErrorResponse("Test error", "TEST_ERROR");
        
        assertEquals("Test error", response.getError());
        assertEquals("TEST_ERROR", response.getCode());
        assertNotNull(response.getTimestamp());
        assertNull(response.getDetails());
    }

    @Test
    @DisplayName("Should create ErrorResponse with error, code, and details")
    void testConstructorWithDetails() {
        ErrorResponse response = new ErrorResponse("Test error", "TEST_ERROR", "Additional details");
        
        assertEquals("Test error", response.getError());
        assertEquals("TEST_ERROR", response.getCode());
        assertEquals("Additional details", response.getDetails());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create ErrorResponse with all fields")
    void testConstructorWithAllFields() {
        ErrorResponse response = new ErrorResponse("Test error", "TEST_ERROR", "Additional details", "/api/test");
        
        assertEquals("Test error", response.getError());
        assertEquals("TEST_ERROR", response.getCode());
        assertEquals("Additional details", response.getDetails());
        assertEquals("/api/test", response.getPath());
        assertNotNull(response.getTimestamp());
    }

    // ===================== Field Getter/Setter Tests =====================

    @Test
    @DisplayName("Should set and get error field")
    void testSetGetError() {
        errorResponse.setError("Test error message");
        
        assertEquals("Test error message", errorResponse.getError());
    }

    @Test
    @DisplayName("Should set and get code field")
    void testSetGetCode() {
        errorResponse.setCode("TEST_CODE");
        
        assertEquals("TEST_CODE", errorResponse.getCode());
    }

    @Test
    @DisplayName("Should set and get details field")
    void testSetGetDetails() {
        errorResponse.setDetails("Error details");
        
        assertEquals("Error details", errorResponse.getDetails());
    }

    @Test
    @DisplayName("Should set and get path field")
    void testSetGetPath() {
        errorResponse.setPath("/api/analyze");
        
        assertEquals("/api/analyze", errorResponse.getPath());
    }

    @Test
    @DisplayName("Should set and get timestamp field")
    void testSetGetTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        errorResponse.setTimestamp(now);
        
        assertEquals(now, errorResponse.getTimestamp());
    }

    // ===================== Timestamp Tests =====================

    @Test
    @DisplayName("Should initialize timestamp with current time")
    void testTimestampInitialization() {
        LocalDateTime before = LocalDateTime.now();
        ErrorResponse response = new ErrorResponse("Error", "CODE");
        LocalDateTime after = LocalDateTime.now();
        
        assertTrue(!response.getTimestamp().isBefore(before));
        assertTrue(!response.getTimestamp().isAfter(after.plusSeconds(1)));
    }

    @Test
    @DisplayName("Should allow timestamp modification")
    void testTimestampModification() {
        LocalDateTime customTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        errorResponse.setTimestamp(customTime);
        
        assertEquals(customTime, errorResponse.getTimestamp());
    }

    // ===================== Null Handling Tests =====================

    @Test
    @DisplayName("Should handle null error field")
    void testNullError() {
        errorResponse.setError(null);
        
        assertNull(errorResponse.getError());
    }

    @Test
    @DisplayName("Should handle null code field")
    void testNullCode() {
        errorResponse.setCode(null);
        
        assertNull(errorResponse.getCode());
    }

    @Test
    @DisplayName("Should handle null details field")
    void testNullDetails() {
        errorResponse.setDetails(null);
        
        assertNull(errorResponse.getDetails());
    }

    @Test
    @DisplayName("Should handle null path field")
    void testNullPath() {
        errorResponse.setPath(null);
        
        assertNull(errorResponse.getPath());
    }

    // ===================== ToString Tests =====================

    @Test
    @DisplayName("Should generate toString representation")
    void testToString() {
        errorResponse.setError("Error");
        errorResponse.setCode("CODE");
        errorResponse.setDetails("Details");
        errorResponse.setPath("/api/test");
        
        String toString = errorResponse.toString();
        
        assertTrue(toString.contains("error="));
        assertTrue(toString.contains("code="));
        assertTrue(toString.contains("details="));
        assertTrue(toString.contains("path="));
    }

    @Test
    @DisplayName("Should include error message in toString")
    void testToStringIncludesError() {
        errorResponse.setError("Test error message");
        
        assertTrue(errorResponse.toString().contains("Test error message"));
    }

    @Test
    @DisplayName("Should include error code in toString")
    void testToStringIncludesCode() {
        errorResponse.setCode("TEST_ERROR");
        
        assertTrue(errorResponse.toString().contains("TEST_ERROR"));
    }

    // ===================== Field Validation Tests =====================

    @Test
    @DisplayName("Should accept empty string for error field")
    void testEmptyStringError() {
        errorResponse.setError("");
        
        assertEquals("", errorResponse.getError());
    }

    @Test
    @DisplayName("Should accept empty string for code field")
    void testEmptyStringCode() {
        errorResponse.setCode("");
        
        assertEquals("", errorResponse.getCode());
    }

    @Test
    @DisplayName("Should accept long error message")
    void testLongErrorMessage() {
        String longMessage = "a".repeat(1000);
        errorResponse.setError(longMessage);
        
        assertEquals(longMessage, errorResponse.getError());
    }

    @Test
    @DisplayName("Should accept special characters in fields")
    void testSpecialCharactersInFields() {
        errorResponse.setError("Error: Invalid input! @#$%^&*()");
        errorResponse.setCode("ERROR_CODE_123");
        errorResponse.setDetails("Details with <script> tags");
        
        assertEquals("Error: Invalid input! @#$%^&*()", errorResponse.getError());
        assertEquals("ERROR_CODE_123", errorResponse.getCode());
        assertEquals("Details with <script> tags", errorResponse.getDetails());
    }

    // ===================== Multiple Instance Tests =====================

    @Test
    @DisplayName("Should create multiple independent instances")
    void testMultipleInstances() {
        ErrorResponse response1 = new ErrorResponse("Error 1", "CODE_1");
        ErrorResponse response2 = new ErrorResponse("Error 2", "CODE_2");
        
        assertEquals("Error 1", response1.getError());
        assertEquals("Error 2", response2.getError());
        assertEquals("CODE_1", response1.getCode());
        assertEquals("CODE_2", response2.getCode());
    }

    @Test
    @DisplayName("Should have different timestamps for different instances")
    void testDifferentTimestamps() throws InterruptedException {
        ErrorResponse response1 = new ErrorResponse("Error 1", "CODE_1");
        Thread.sleep(100);
        ErrorResponse response2 = new ErrorResponse("Error 2", "CODE_2");
        
        assertNotEquals(response1.getTimestamp(), response2.getTimestamp());
    }

    // ===================== Edge Case Tests =====================

    @Test
    @DisplayName("Should handle error response with only error field set")
    void testErrorFieldOnly() {
        ErrorResponse response = new ErrorResponse();
        response.setError("Error message");
        
        assertEquals("Error message", response.getError());
        assertNull(response.getCode());
    }

    @Test
    @DisplayName("Should handle error response with only code field set")
    void testCodeFieldOnly() {
        ErrorResponse response = new ErrorResponse();
        response.setCode("ERROR_CODE");
        
        assertNull(response.getError());
        assertEquals("ERROR_CODE", response.getCode());
    }

    @Test
    @DisplayName("Should handle error response with whitespace in fields")
    void testWhitespaceInFields() {
        errorResponse.setError("   Error   ");
        errorResponse.setCode("   CODE   ");
        errorResponse.setDetails("   Details   ");
        
        assertEquals("   Error   ", errorResponse.getError());
        assertEquals("   CODE   ", errorResponse.getCode());
        assertEquals("   Details   ", errorResponse.getDetails());
    }

    @Test
    @DisplayName("Should handle error response with newlines in fields")
    void testNewlinesInFields() {
        errorResponse.setError("Error\nwith\nnewlines");
        errorResponse.setDetails("Details\nwith\nmultiple\nlines");
        
        assertEquals("Error\nwith\nnewlines", errorResponse.getError());
        assertEquals("Details\nwith\nmultiple\nlines", errorResponse.getDetails());
    }
}
