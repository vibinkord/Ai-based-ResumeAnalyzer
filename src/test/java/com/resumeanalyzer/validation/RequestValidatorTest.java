package com.resumeanalyzer.validation;

import com.resumeanalyzer.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RequestValidator class.
 * Tests input validation for resume and job description texts.
 */
@DisplayName("RequestValidator Tests")
class RequestValidatorTest {

    private RequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RequestValidator();
    }

    // ===================== Resume Text Validation Tests =====================

    @Test
    @DisplayName("Should throw ValidationException when resume text is null")
    void testValidateResumeText_NullInput() {
        assertThrows(ValidationException.class, () -> {
            validator.validateResumeText(null);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume text is empty string")
    void testValidateResumeText_EmptyInput() {
        assertThrows(ValidationException.class, () -> {
            validator.validateResumeText("");
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume text is whitespace only")
    void testValidateResumeText_WhitespaceOnlyInput() {
        assertThrows(ValidationException.class, () -> {
            validator.validateResumeText("   \n\t  ");
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume text exceeds maximum size")
    void testValidateResumeText_ExceedsMaxSize() {
        String oversizedText = "a".repeat(RequestValidator.MAX_RESUME_SIZE + 1);
        
        assertThrows(ValidationException.class, () -> {
            validator.validateResumeText(oversizedText);
        });
    }

    @Test
    @DisplayName("Should pass validation for valid resume text")
    void testValidateResumeText_ValidInput() {
        String validResume = "John Doe\n" +
                "Senior Software Engineer\n" +
                "Experience:\n" +
                "- Java, Spring Boot, Microservices\n" +
                "- REST APIs, Database Design";
        
        assertDoesNotThrow(() -> {
            validator.validateResumeText(validResume);
        });
    }

    @Test
    @DisplayName("Should pass validation for resume text at maximum size")
    void testValidateResumeText_AtMaxSize() {
        String textAtMaxSize = "a".repeat(RequestValidator.MAX_RESUME_SIZE);
        
        assertDoesNotThrow(() -> {
            validator.validateResumeText(textAtMaxSize);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume contains script injection")
    void testValidateResumeText_ScriptInjection() {
        String maliciousText = "Resume with <script>alert('xss')</script> content";
        
        assertThrows(ValidationException.class, () -> {
            validator.validateResumeText(maliciousText);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume contains onclick injection")
    void testValidateResumeText_OnClickInjection() {
        String maliciousText = "Click me <div onclick='alert(1)'>here</div>";
        
        assertThrows(ValidationException.class, () -> {
            validator.validateResumeText(maliciousText);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume contains javascript: protocol")
    void testValidateResumeText_JavascriptProtocolInjection() {
        String maliciousText = "Visit <a href='javascript:alert(1)'>my site</a>";
        
        assertThrows(ValidationException.class, () -> {
            validator.validateResumeText(maliciousText);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume contains null bytes")
    void testValidateResumeText_NullByteInjection() {
        String maliciousText = "Resume content\0hidden content";
        
        assertThrows(ValidationException.class, () -> {
            validator.validateResumeText(maliciousText);
        });
    }

    // ===================== Job Description Text Validation Tests =====================

    @Test
    @DisplayName("Should throw ValidationException when job description is null")
    void testValidateJobDescriptionText_NullInput() {
        assertThrows(ValidationException.class, () -> {
            validator.validateJobDescriptionText(null);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when job description is empty string")
    void testValidateJobDescriptionText_EmptyInput() {
        assertThrows(ValidationException.class, () -> {
            validator.validateJobDescriptionText("");
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when job description is whitespace only")
    void testValidateJobDescriptionText_WhitespaceOnlyInput() {
        assertThrows(ValidationException.class, () -> {
            validator.validateJobDescriptionText("   \n\t  ");
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when job description exceeds maximum size")
    void testValidateJobDescriptionText_ExceedsMaxSize() {
        String oversizedText = "b".repeat(RequestValidator.MAX_JOB_DESCRIPTION_SIZE + 1);
        
        assertThrows(ValidationException.class, () -> {
            validator.validateJobDescriptionText(oversizedText);
        });
    }

    @Test
    @DisplayName("Should pass validation for valid job description text")
    void testValidateJobDescriptionText_ValidInput() {
        String validJobDescription = "Senior Software Engineer\n" +
                "Required Skills:\n" +
                "- Java, Spring Boot\n" +
                "- Microservices Architecture\n" +
                "- REST API Design\n" +
                "Responsibilities:\n" +
                "- Design and implement scalable systems";
        
        assertDoesNotThrow(() -> {
            validator.validateJobDescriptionText(validJobDescription);
        });
    }

    @Test
    @DisplayName("Should pass validation for job description at maximum size")
    void testValidateJobDescriptionText_AtMaxSize() {
        String textAtMaxSize = "b".repeat(RequestValidator.MAX_JOB_DESCRIPTION_SIZE);
        
        assertDoesNotThrow(() -> {
            validator.validateJobDescriptionText(textAtMaxSize);
        });
    }

    // ===================== Combined Analysis Request Validation Tests =====================

    @Test
    @DisplayName("Should throw ValidationException when both inputs are null")
    void testValidateAnalysisRequest_BothNull() {
        assertThrows(ValidationException.class, () -> {
            validator.validateAnalysisRequest(null, null);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume is invalid in combined validation")
    void testValidateAnalysisRequest_InvalidResume() {
        String validJobDescription = "Senior Java Developer";
        
        assertThrows(ValidationException.class, () -> {
            validator.validateAnalysisRequest(null, validJobDescription);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when job description is invalid in combined validation")
    void testValidateAnalysisRequest_InvalidJobDescription() {
        String validResume = "John Doe - Senior Engineer";
        
        assertThrows(ValidationException.class, () -> {
            validator.validateAnalysisRequest(validResume, null);
        });
    }

    @Test
    @DisplayName("Should pass validation when both inputs are valid")
    void testValidateAnalysisRequest_BothValid() {
        String validResume = "John Doe\nJava, Spring Boot, Microservices";
        String validJobDescription = "Senior Java Developer\nRequired: Java, Spring, Microservices";
        
        assertDoesNotThrow(() -> {
            validator.validateAnalysisRequest(validResume, validJobDescription);
        });
    }

    @Test
    @DisplayName("Should throw ValidationException when resume contains malicious content in combined validation")
    void testValidateAnalysisRequest_ResumeMalicious() {
        String maliciousResume = "Resume with <script>alert(1)</script>";
        String validJobDescription = "Senior Java Developer";
        
        assertThrows(ValidationException.class, () -> {
            validator.validateAnalysisRequest(maliciousResume, validJobDescription);
        });
    }

    // ===================== Text Sanitization Tests =====================

    @Test
    @DisplayName("Should return empty string when sanitizing null text")
    void testSanitizeText_NullInput() {
        String result = RequestValidator.sanitizeText(null);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should trim whitespace from text")
    void testSanitizeText_TrimWhitespace() {
        String input = "   Hello World   ";
        String result = RequestValidator.sanitizeText(input);
        assertEquals("Hello World", result);
    }

    @Test
    @DisplayName("Should normalize multiple spaces to single space")
    void testSanitizeText_NormalizeSpaces() {
        String input = "Hello    world    test";
        String result = RequestValidator.sanitizeText(input);
        assertEquals("Hello world test", result);
    }

    @Test
    @DisplayName("Should remove control characters except newlines and tabs")
    void testSanitizeText_RemoveControlCharacters() {
        String input = "Hello\u0001World\u0002Test";
        String result = RequestValidator.sanitizeText(input);
        assertEquals("HelloWorldTest", result);
    }

    @Test
    @DisplayName("Should preserve newlines during sanitization")
    void testSanitizeText_PreserveNewlines() {
        String input = "Line1\nLine2\nLine3";
        String result = RequestValidator.sanitizeText(input);
        assertEquals("Line1\nLine2\nLine3", result);
    }

    @Test
    @DisplayName("Should preserve tabs during sanitization")
    void testSanitizeText_PreserveTabs() {
        String input = "Column1\tColumn2\tColumn3";
        String result = RequestValidator.sanitizeText(input);
        assertEquals("Column1\tColumn2\tColumn3", result);
    }

    @Test
    @DisplayName("Should sanitize text with mixed whitespace and control characters")
    void testSanitizeText_MixedContent() {
        String input = "  Hello\u0001  \n  World\u0002  ";
        String result = RequestValidator.sanitizeText(input);
        assertEquals("Hello\nWorld", result);
    }

    // ===================== Edge Cases Tests =====================

    @ParameterizedTest
    @ValueSource(strings = {
            "Resume with single character",
            "R",
            "1",
            "!",
            "🎓"
    })
    @DisplayName("Should accept resume text with minimum valid content")
    void testValidateResumeText_MinimumValidContent(String input) {
        assertDoesNotThrow(() -> {
            validator.validateResumeText(input);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Job description with single character",
            "J",
            "2",
            "@",
            "💼"
    })
    @DisplayName("Should accept job description with minimum valid content")
    void testValidateJobDescriptionText_MinimumValidContent(String input) {
        assertDoesNotThrow(() -> {
            validator.validateJobDescriptionText(input);
        });
    }

    @Test
    @DisplayName("Should handle resume text with special characters")
    void testValidateResumeText_SpecialCharacters() {
        String specialCharResume = "Resume: C++, C#, .NET, iOS, Node.js, AWS\n" +
                "Certifications: AWS™, Microsoft®, Oracle©";
        
        assertDoesNotThrow(() -> {
            validator.validateResumeText(specialCharResume);
        });
    }

    @Test
    @DisplayName("Should handle job description with special characters")
    void testValidateJobDescriptionText_SpecialCharacters() {
        String specialCharJobDesc = "Job: DevOps Engineer (Azure/AWS/GCP)\n" +
                "Salary: $100k-$150k\n" +
                "Location: San Francisco, CA";
        
        assertDoesNotThrow(() -> {
            validator.validateJobDescriptionText(specialCharJobDesc);
        });
    }

    @Test
    @DisplayName("Should handle resume text with unicode characters")
    void testValidateResumeText_UnicodeCharacters() {
        String unicodeResume = "姓名: 王小明\n" +
                "技能: Java, Python, 数据库\n" +
                "Expérience: 5 ans";
        
        assertDoesNotThrow(() -> {
            validator.validateResumeText(unicodeResume);
        });
    }

    @Test
    @DisplayName("Should handle job description with unicode characters")
    void testValidateJobDescriptionText_UnicodeCharacters() {
        String unicodeJobDesc = "職位: シニアエンジニア\n" +
                "必要なスキル: Java, Python\n" +
                "給与: ¥5,000,000";
        
        assertDoesNotThrow(() -> {
            validator.validateJobDescriptionText(unicodeJobDesc);
        });
    }

    @Test
    @DisplayName("Should correctly validate resume with SQL-related skills")
    void testValidateResumeText_SQLSkills() {
        // SQL skills should be allowed (not treated as injection)
        String resumeWithSQL = "Database Skills:\n" +
                "- SQL (SELECT, INSERT, UPDATE, DELETE)\n" +
                "- Stored Procedures\n" +
                "- Query Optimization";
        
        assertDoesNotThrow(() -> {
            validator.validateResumeText(resumeWithSQL);
        });
    }

    @Test
    @DisplayName("Should correctly validate job description with SQL requirements")
    void testValidateJobDescriptionText_SQLRequirements() {
        String jobWithSQL = "Required Database Skills:\n" +
                "- UNION queries\n" +
                "- Complex SELECT statements\n" +
                "- CREATE TABLE structures";
        
        assertDoesNotThrow(() -> {
            validator.validateJobDescriptionText(jobWithSQL);
        });
    }
}
