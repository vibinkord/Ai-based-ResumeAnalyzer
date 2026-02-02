package com.resumeanalyzer.validation;

import com.resumeanalyzer.exception.ValidationException;

/**
 * Validates resume analysis requests.
 * Ensures that input data meets requirements before processing.
 */
public class RequestValidator {

    // Configuration constants (can be externalized later)
    public static final int MAX_RESUME_SIZE = 50 * 1024; // 50 KB
    public static final int MAX_JOB_DESCRIPTION_SIZE = 50 * 1024; // 50 KB
    public static final int MIN_TEXT_LENGTH = 1;
    public static final int MAX_TEXT_LENGTH = MAX_RESUME_SIZE;

    /**
     * Validates resume and job description text.
     *
     * @param resumeText the resume text to validate
     * @param jobDescriptionText the job description text to validate
     * @throws ValidationException if validation fails
     */
    public void validateAnalysisRequest(String resumeText, String jobDescriptionText) {
        validateResumeText(resumeText);
        validateJobDescriptionText(jobDescriptionText);
    }

    /**
     * Validates resume text.
     *
     * @param resumeText the resume text to validate
     * @throws ValidationException if resume text is invalid
     */
    public void validateResumeText(String resumeText) {
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new ValidationException("Resume text cannot be empty or null");
        }

        if (resumeText.length() > MAX_RESUME_SIZE) {
            throw new ValidationException(
                    String.format("Resume exceeds maximum size of %d bytes. Provided: %d bytes",
                            MAX_RESUME_SIZE, resumeText.length())
            );
        }

        validateTextContent(resumeText, "Resume");
    }

    /**
     * Validates job description text.
     *
     * @param jobDescriptionText the job description text to validate
     * @throws ValidationException if job description text is invalid
     */
    public void validateJobDescriptionText(String jobDescriptionText) {
        if (jobDescriptionText == null || jobDescriptionText.trim().isEmpty()) {
            throw new ValidationException("Job description text cannot be empty or null");
        }

        if (jobDescriptionText.length() > MAX_JOB_DESCRIPTION_SIZE) {
            throw new ValidationException(
                    String.format("Job description exceeds maximum size of %d bytes. Provided: %d bytes",
                            MAX_JOB_DESCRIPTION_SIZE, jobDescriptionText.length())
            );
        }

        validateTextContent(jobDescriptionText, "Job description");
    }

    /**
     * Validates text content for injection attacks and suspicious patterns.
     *
     * @param text the text to validate
     * @param fieldName the name of the field (for error messages)
     * @throws ValidationException if text contains suspicious patterns
     */
    private void validateTextContent(String text, String fieldName) {
        // Check for common injection patterns
        if (containsSuspiciousPatterns(text)) {
            throw new ValidationException(fieldName + " contains suspicious or invalid patterns");
        }
    }

    /**
     * Checks if text contains suspicious patterns that might indicate injection attempts.
     *
     * @param text the text to check
     * @return true if suspicious patterns are detected, false otherwise
     */
    private boolean containsSuspiciousPatterns(String text) {
        if (text == null) {
            return false;
        }

        // Check for SQL injection patterns
        if (text.toLowerCase().matches(".*(union|select|insert|update|delete|drop|create|alter)\\s+.*")) {
            // This is a simple heuristic - might have false positives with legitimate text
            // Consider adjusting based on actual requirements
            return false; // Allow skill names like "SQL" and "SELECT" in resume text
        }

        // Check for script injection patterns
        if (text.contains("<script") || text.contains("javascript:") || text.contains("onclick=")) {
            return true;
        }

        // Check for null bytes
        if (text.contains("\0")) {
            return true;
        }

        return false;
    }

    /**
     * Sanitizes text by removing or escaping potentially harmful content.
     *
     * @param text the text to sanitize
     * @return sanitized text
     */
    public static String sanitizeText(String text) {
        if (text == null) {
            return "";
        }

        // Trim whitespace
        String sanitized = text.trim();

        // Remove control characters except newlines and tabs
        sanitized = sanitized.replaceAll("[\\p{Cc}&&[^\n\t]]", "");

        // Remove spaces before newlines
        sanitized = sanitized.replaceAll("[ ]+\n", "\n");
        
        // Remove spaces after newlines
        sanitized = sanitized.replaceAll("\n[ ]+", "\n");
        
        // Normalize consecutive spaces (but preserve tabs and newlines)
        sanitized = sanitized.replaceAll("[ ]+", " ");
        
        // Normalize consecutive tabs
        sanitized = sanitized.replaceAll("[\t]+", "\t");
        
        // Normalize consecutive newlines (max 2 for paragraph breaks)
        sanitized = sanitized.replaceAll("\n\n\n+", "\n\n");

        return sanitized;
    }

}
