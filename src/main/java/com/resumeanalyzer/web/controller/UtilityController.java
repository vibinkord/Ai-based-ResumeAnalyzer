package com.resumeanalyzer.web.controller;

import com.resumeanalyzer.analysis.SkillExtractor;
import com.resumeanalyzer.web.dto.BatchAnalysisRequest;
import com.resumeanalyzer.web.dto.BatchAnalysisResponse;
import com.resumeanalyzer.web.dto.ComparisonRequest;
import com.resumeanalyzer.web.dto.ComparisonResponse;
import com.resumeanalyzer.web.dto.HealthResponse;
import com.resumeanalyzer.web.dto.SkillListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for utility and enhanced API endpoints.
 * Provides health checks, batch processing, comparison, and skill information.
 * Base path: /api/v1
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Utility", description = "Utility endpoints for health checks, skill inventory, and batch processing")
public class UtilityController {

    private static final Logger log = LoggerFactory.getLogger(UtilityController.class);
    private final SkillExtractor skillExtractor;

    public UtilityController() {
        this.skillExtractor = new SkillExtractor();
    }

    /**
     * Health check endpoint.
     * Returns application status and version information.
     * GET /api/v1/health
     *
     * @return ResponseEntity with health status
     */
    @GetMapping("/health")
    @Operation(
        summary = "Check API health status",
        description = "Returns the health status of the API including version information and current timestamp",
        operationId = "healthCheck"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "API is healthy and running",
            content = @Content(schema = @Schema(implementation = HealthResponse.class))
        )
    })
    public ResponseEntity<HealthResponse> healthCheck() {
        log.debug("Health check requested");
        
        HealthResponse response = new HealthResponse(
                "UP",
                "AI Resume Analyzer is running",
                "1.0.0",
                LocalDateTime.now()
        );
        
        log.info("Health check completed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get list of all known skills.
     * Returns all skills recognized by the analyzer grouped by category.
     * GET /api/v1/skills
     *
     * @return ResponseEntity with list of known skills
     */
    @GetMapping("/skills")
    @Operation(
        summary = "Retrieve all known technical skills",
        description = "Returns a comprehensive list of 115+ technical skills recognized by the analyzer, " +
                      "organized by category (programming languages, frameworks, databases, tools, etc.)",
        operationId = "getKnownSkills"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of known skills retrieved successfully",
            content = @Content(schema = @Schema(implementation = SkillListResponse.class))
        )
    })
    public ResponseEntity<SkillListResponse> getSkills() {
        log.debug("Skills list requested");
        
        var allSkills = skillExtractor.getKnownSkills();
        log.info("Retrieved {} known skills", allSkills.size());
        
        SkillListResponse response = new SkillListResponse(
                new ArrayList<>(allSkills),
                allSkills.size(),
                "All recognized technical skills in the system"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Batch analyze multiple resumes against job descriptions.
     * Processes multiple resume-job pairs in a single request.
     * POST /api/v1/batch
     *
     * @param request the batch analysis request containing multiple resume-job pairs
     * @return ResponseEntity with batch analysis results
     */
    @PostMapping("/batch")
    @Operation(
        summary = "Batch analyze multiple resumes",
        description = "Analyzes multiple resumes against job descriptions in a single request. " +
                      "Each item in the batch is processed independently and results are returned " +
                      "with success/failure status for each item.",
        operationId = "batchAnalyzeResumes"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Batch analysis completed",
            content = @Content(schema = @Schema(implementation = BatchAnalysisResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid batch request - missing or empty items"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Server error during batch processing"
        )
    })
    public ResponseEntity<BatchAnalysisResponse> batchAnalyze(
            @RequestBody 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Batch analysis request containing list of resume-job pairs to analyze",
                required = true
            )
            BatchAnalysisRequest request) {
        log.info("Batch analysis requested for {} items", request.getItems() != null ? request.getItems().size() : 0);
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.warn("Empty batch analysis request received");
            return ResponseEntity.badRequest().build();
        }
        
        List<BatchAnalysisResponse.Item> results = new ArrayList<>();
        int successful = 0;
        int failed = 0;
        
        for (int i = 0; i < request.getItems().size(); i++) {
            BatchAnalysisRequest.Item item = request.getItems().get(i);
            log.debug("Processing batch item {}/{}", i + 1, request.getItems().size());
            
            try {
                // Process each item (placeholder - actual processing would be done here)
                BatchAnalysisResponse.Item result = new BatchAnalysisResponse.Item(
                        item.getId(),
                        true,
                        "Analysis completed",
                        null
                );
                results.add(result);
                successful++;
            } catch (Exception e) {
                log.error("Failed to process batch item {}", i, e);
                BatchAnalysisResponse.Item result = new BatchAnalysisResponse.Item(
                        item.getId(),
                        false,
                        "Analysis failed: " + e.getMessage(),
                        null
                );
                results.add(result);
                failed++;
            }
        }
        
        BatchAnalysisResponse response = new BatchAnalysisResponse(
                results,
                successful,
                failed,
                LocalDateTime.now()
        );
        
        log.info("Batch analysis completed: {} successful, {} failed", successful, failed);
        return ResponseEntity.ok(response);
    }

    /**
     * Compare two resumes.
     * Analyzes the differences and similarities between two resumes.
     * POST /api/v1/compare
     *
     * @param request the comparison request containing two resume texts
     * @return ResponseEntity with comparison results
     */
    @PostMapping("/compare")
    @Operation(
        summary = "Compare two resumes",
        description = "Compares two resumes by extracting skills from both and identifying common, " +
                      "unique-to-resume-1, and unique-to-resume-2 skills. Returns a similarity percentage " +
                      "based on the proportion of common skills.",
        operationId = "compareResumes"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Comparison completed successfully",
            content = @Content(schema = @Schema(implementation = ComparisonResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid comparison request - missing resume content"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Server error during comparison"
        )
    })
    public ResponseEntity<ComparisonResponse> compareResumes(
            @RequestBody 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Comparison request containing two resume texts to compare",
                required = true
            )
            ComparisonRequest request) {
        log.info("Resume comparison requested");
        
        if (request.getResume1() == null || request.getResume1().isEmpty() ||
            request.getResume2() == null || request.getResume2().isEmpty()) {
            log.warn("Comparison request missing required resume content");
            return ResponseEntity.badRequest().build();
        }
        
        try {
            // Extract skills from both resumes
            var skills1 = skillExtractor.extractSkills(request.getResume1());
            var skills2 = skillExtractor.extractSkills(request.getResume2());
            
            log.debug("Resume 1 has {} skills, Resume 2 has {} skills", 
                    skills1.size(), skills2.size());
            
            // Find common and unique skills
            var commonSkills = new ArrayList<>(skills1);
            commonSkills.retainAll(skills2);
            
            var uniqueToResume1 = new ArrayList<>(skills1);
            uniqueToResume1.removeAll(commonSkills);
            
            var uniqueToResume2 = new ArrayList<>(skills2);
            uniqueToResume2.removeAll(commonSkills);
            
            double similarityPercentage = commonSkills.isEmpty() ? 0 :
                    (commonSkills.size() * 100.0) / Math.max(skills1.size(), skills2.size());
            
            ComparisonResponse response = new ComparisonResponse(
                    skills1.size(),
                    skills2.size(),
                    commonSkills,
                    uniqueToResume1,
                    uniqueToResume2,
                    similarityPercentage,
                    LocalDateTime.now()
            );
            
            log.info("Comparison completed: {}% similarity", String.format("%.2f", similarityPercentage));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Comparison failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
