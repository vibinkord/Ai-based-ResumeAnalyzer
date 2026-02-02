package com.resumeanalyzer.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Swagger/OpenAPI annotations in controllers and DTOs.
 * 
 * Verifies that all API endpoints and data transfer objects have proper
 * documentation annotations for OpenAPI/Swagger documentation generation.
 */
@DisplayName("Swagger Annotations Tests")
@SpringJUnitConfig
class SwaggerAnnotationsTest {

    @Test
    @DisplayName("ResumeAnalysisController should have @Tag annotation")
    void testResumeAnalysisControllerHasTagAnnotation() {
        Tag tag = ResumeAnalysisController.class.getAnnotation(Tag.class);
        assertNotNull(tag, "ResumeAnalysisController should have @Tag annotation");
        assertEquals("Resume Analysis", tag.name());
        assertTrue(tag.description().contains("resume") || tag.description().contains("Resume"));
    }

    @Test
    @DisplayName("UtilityController should have @Tag annotation")
    void testUtilityControllerHasTagAnnotation() {
        Tag tag = UtilityController.class.getAnnotation(Tag.class);
        assertNotNull(tag, "UtilityController should have @Tag annotation");
        assertEquals("Utility", tag.name());
        assertTrue(tag.description().contains("utility") || tag.description().contains("health"));
    }

    @Test
    @DisplayName("ResumeAnalysisController.analyze() should have @Operation annotation")
    void testAnalyzeMethodHasOperationAnnotation() {
        try {
            Method analyzeMethod = ResumeAnalysisController.class.getDeclaredMethod(
                "analyze", com.resumeanalyzer.web.dto.ResumeAnalysisRequest.class);
            
            Operation operation = analyzeMethod.getAnnotation(Operation.class);
            assertNotNull(operation, "analyze() method should have @Operation annotation");
            assertNotNull(operation.summary(), "Operation should have a summary");
            assertNotNull(operation.description(), "Operation should have a description");
        } catch (NoSuchMethodException e) {
            fail("analyze() method not found in ResumeAnalysisController");
        }
    }

    @Test
    @DisplayName("ResumeAnalysisController.analyze() should have @ApiResponses annotation")
    void testAnalyzeMethodHasApiResponsesAnnotation() {
        try {
            Method analyzeMethod = ResumeAnalysisController.class.getDeclaredMethod(
                "analyze", com.resumeanalyzer.web.dto.ResumeAnalysisRequest.class);
            
            ApiResponse[] responses = analyzeMethod.getAnnotationsByType(ApiResponse.class);
            assertTrue(responses.length > 0, "analyze() method should have @ApiResponse annotations");
        } catch (NoSuchMethodException e) {
            fail("analyze() method not found in ResumeAnalysisController");
        }
    }

    @Test
    @DisplayName("ResumeAnalysisResponse should have @Schema annotation")
    void testResumeAnalysisResponseHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.ResumeAnalysisResponse.class.getAnnotation(Schema.class);
        assertNotNull(schema, "ResumeAnalysisResponse should have @Schema annotation");
        assertEquals("ResumeAnalysisResponse", schema.name());
    }

    @Test
    @DisplayName("ResumeAnalysisRequest should have @Schema annotation")
    void testResumeAnalysisRequestHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.ResumeAnalysisRequest.class.getAnnotation(Schema.class);
        assertNotNull(schema, "ResumeAnalysisRequest should have @Schema annotation");
        assertEquals("ResumeAnalysisRequest", schema.name());
    }

    @Test
    @DisplayName("HealthResponse should have @Schema annotation")
    void testHealthResponseHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.HealthResponse.class.getAnnotation(Schema.class);
        assertNotNull(schema, "HealthResponse should have @Schema annotation");
        assertEquals("HealthResponse", schema.name());
    }

    @Test
    @DisplayName("ErrorResponse should have @Schema annotation")
    void testErrorResponseHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.ErrorResponse.class.getAnnotation(Schema.class);
        assertNotNull(schema, "ErrorResponse should have @Schema annotation");
        assertEquals("ErrorResponse", schema.name());
    }

    @Test
    @DisplayName("SkillListResponse should have @Schema annotation")
    void testSkillListResponseHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.SkillListResponse.class.getAnnotation(Schema.class);
        assertNotNull(schema, "SkillListResponse should have @Schema annotation");
        assertEquals("SkillListResponse", schema.name());
    }

    @Test
    @DisplayName("ComparisonRequest should have @Schema annotation")
    void testComparisonRequestHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.ComparisonRequest.class.getAnnotation(Schema.class);
        assertNotNull(schema, "ComparisonRequest should have @Schema annotation");
        assertEquals("ComparisonRequest", schema.name());
    }

    @Test
    @DisplayName("ComparisonResponse should have @Schema annotation")
    void testComparisonResponseHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.ComparisonResponse.class.getAnnotation(Schema.class);
        assertNotNull(schema, "ComparisonResponse should have @Schema annotation");
        assertEquals("ComparisonResponse", schema.name());
    }

    @Test
    @DisplayName("BatchAnalysisRequest should have @Schema annotation")
    void testBatchAnalysisRequestHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.BatchAnalysisRequest.class.getAnnotation(Schema.class);
        assertNotNull(schema, "BatchAnalysisRequest should have @Schema annotation");
        assertEquals("BatchAnalysisRequest", schema.name());
    }

    @Test
    @DisplayName("BatchAnalysisResponse should have @Schema annotation")
    void testBatchAnalysisResponseHasSchemaAnnotation() {
        Schema schema = com.resumeanalyzer.web.dto.BatchAnalysisResponse.class.getAnnotation(Schema.class);
        assertNotNull(schema, "BatchAnalysisResponse should have @Schema annotation");
        assertEquals("BatchAnalysisResponse", schema.name());
    }

    @Test
    @DisplayName("UtilityController.healthCheck() should have @Operation annotation")
    void testHealthCheckMethodHasOperationAnnotation() {
        try {
            Method healthMethod = UtilityController.class.getDeclaredMethod("healthCheck");
            
            Operation operation = healthMethod.getAnnotation(Operation.class);
            assertNotNull(operation, "healthCheck() method should have @Operation annotation");
            assertNotNull(operation.summary(), "Operation should have a summary");
        } catch (NoSuchMethodException e) {
            fail("healthCheck() method not found in UtilityController");
        }
    }

    @Test
    @DisplayName("UtilityController.getSkills() should have @Operation annotation")
    void testGetSkillsMethodHasOperationAnnotation() {
        try {
            Method skillsMethod = UtilityController.class.getDeclaredMethod("getSkills");
            
            Operation operation = skillsMethod.getAnnotation(Operation.class);
            assertNotNull(operation, "getSkills() method should have @Operation annotation");
            assertNotNull(operation.summary(), "Operation should have a summary");
        } catch (NoSuchMethodException e) {
            fail("getSkills() method not found in UtilityController");
        }
    }

    @Test
    @DisplayName("UtilityController.compareResumes() should have @Operation annotation")
    void testCompareResumesMethodHasOperationAnnotation() {
        try {
            Method compareMethod = UtilityController.class.getDeclaredMethod(
                "compareResumes", com.resumeanalyzer.web.dto.ComparisonRequest.class);
            
            Operation operation = compareMethod.getAnnotation(Operation.class);
            assertNotNull(operation, "compareResumes() method should have @Operation annotation");
            assertNotNull(operation.summary(), "Operation should have a summary");
        } catch (NoSuchMethodException e) {
            fail("compareResumes() method not found in UtilityController");
        }
    }

    @Test
    @DisplayName("UtilityController.batchAnalyze() should have @Operation annotation")
    void testBatchAnalyzeMethodHasOperationAnnotation() {
        try {
            Method batchMethod = UtilityController.class.getDeclaredMethod(
                "batchAnalyze", com.resumeanalyzer.web.dto.BatchAnalysisRequest.class);
            
            Operation operation = batchMethod.getAnnotation(Operation.class);
            assertNotNull(operation, "batchAnalyze() method should have @Operation annotation");
            assertNotNull(operation.summary(), "Operation should have a summary");
        } catch (NoSuchMethodException e) {
            fail("batchAnalyze() method not found in UtilityController");
        }
    }

    @Test
    @DisplayName("Swagger Config class should be created")
    void testSwaggerConfigExists() {
        try {
            Class<?> swaggerConfig = Class.forName("com.resumeanalyzer.config.SwaggerConfig");
            assertNotNull(swaggerConfig);
            
            // Check for openAPI bean method
            Method[] methods = swaggerConfig.getDeclaredMethods();
            boolean hasOpenAPIBean = false;
            for (Method method : methods) {
                if ("customOpenAPI".equals(method.getName())) {
                    hasOpenAPIBean = true;
                    break;
                }
            }
            assertTrue(hasOpenAPIBean, "SwaggerConfig should have customOpenAPI() method");
        } catch (ClassNotFoundException e) {
            fail("SwaggerConfig class not found");
        }
    }
}
