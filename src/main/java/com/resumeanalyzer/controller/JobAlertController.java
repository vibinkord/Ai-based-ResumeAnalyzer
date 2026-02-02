package com.resumeanalyzer.controller;

import com.resumeanalyzer.model.dto.JobAlertRequest;
import com.resumeanalyzer.model.dto.JobAlertResponse;
import com.resumeanalyzer.service.JobAlertService;
import com.resumeanalyzer.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * JobAlertController - REST endpoints for job alert management
 * Provides CRUD operations for job alerts
 */
@RestController
@RequestMapping("/api/job-alerts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Job Alerts", description = "Job alert management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('USER', 'PREMIUM', 'ANALYST')")
public class JobAlertController {

    private final JobAlertService jobAlertService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Get current user ID from JWT token
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    /**
     * Create a new job alert
     *
     * @param request the job alert request
     * @param httpRequest the HTTP request
     * @return created job alert response
     */
    @PostMapping
    @Operation(summary = "Create job alert", description = "Create a new job alert for the current user")
    public ResponseEntity<JobAlertResponse> createJobAlert(
            @Valid @RequestBody JobAlertRequest request,
            HttpServletRequest httpRequest) {
        log.info("Creating job alert");
        Long userId = getCurrentUserId(httpRequest);

        try {
            JobAlertResponse response = jobAlertService.createJobAlert(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating job alert", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get all job alerts for current user
     *
     * @param httpRequest the HTTP request
     * @return list of job alert responses
     */
    @GetMapping
    @Operation(summary = "Get all job alerts", description = "Retrieve all job alerts for the current user")
    public ResponseEntity<List<JobAlertResponse>> getAllJobAlerts(HttpServletRequest httpRequest) {
        log.info("Fetching all job alerts for current user");
        Long userId = getCurrentUserId(httpRequest);

        List<JobAlertResponse> alerts = jobAlertService.getAlertsByUser(userId);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get job alerts with pagination
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @param httpRequest the HTTP request
     * @return paginated job alert responses
     */
    @GetMapping("/paginated")
    @Operation(summary = "Get job alerts paginated", description = "Retrieve job alerts with pagination")
    public ResponseEntity<Page<JobAlertResponse>> getJobAlertsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        log.info("Fetching job alerts with pagination - page: {}, size: {}", page, size);
        Long userId = getCurrentUserId(httpRequest);

        Pageable pageable = PageRequest.of(page, size);
        Page<JobAlertResponse> alerts = jobAlertService.getAlertsByUser(userId, pageable);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get a specific job alert by ID
     *
     * @param id the alert ID
     * @param httpRequest the HTTP request
     * @return the job alert response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get job alert by ID", description = "Retrieve a specific job alert")
    public ResponseEntity<JobAlertResponse> getJobAlert(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("Fetching job alert with ID: {}", id);
        Long userId = getCurrentUserId(httpRequest);

        return jobAlertService.getJobAlertById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search job alerts by job title
     *
     * @param keyword the search keyword
     * @param httpRequest the HTTP request
     * @return list of matching job alert responses
     */
    @GetMapping("/search")
    @Operation(summary = "Search job alerts", description = "Search job alerts by job title")
    public ResponseEntity<List<JobAlertResponse>> searchJobAlerts(
            @RequestParam String keyword,
            HttpServletRequest httpRequest) {
        log.info("Searching job alerts with keyword: {}", keyword);
        Long userId = getCurrentUserId(httpRequest);

        List<JobAlertResponse> results = jobAlertService.searchAlertsByJobTitle(userId, keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * Update a job alert
     *
     * @param id the alert ID
     * @param request the update request
     * @param httpRequest the HTTP request
     * @return updated job alert response
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update job alert", description = "Update an existing job alert")
    public ResponseEntity<JobAlertResponse> updateJobAlert(
            @PathVariable Long id,
            @Valid @RequestBody JobAlertRequest request,
            HttpServletRequest httpRequest) {
        log.info("Updating job alert with ID: {}", id);
        Long userId = getCurrentUserId(httpRequest);

        try {
            JobAlertResponse response = jobAlertService.updateJobAlert(id, userId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error updating job alert", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete a job alert
     *
     * @param id the alert ID
     * @param httpRequest the HTTP request
     * @return response entity
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job alert", description = "Delete a job alert")
    public ResponseEntity<Void> deleteJobAlert(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("Deleting job alert with ID: {}", id);
        Long userId = getCurrentUserId(httpRequest);

        try {
            jobAlertService.deleteJobAlert(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting job alert", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deactivate a job alert
     *
     * @param id the alert ID
     * @param httpRequest the HTTP request
     * @return response entity
     */
    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate job alert", description = "Deactivate a job alert (soft delete)")
    public ResponseEntity<Map<String, String>> deactivateJobAlert(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("Deactivating job alert with ID: {}", id);
        Long userId = getCurrentUserId(httpRequest);

        try {
            jobAlertService.deactivateJobAlert(id, userId);
            return ResponseEntity.ok(Map.of("message", "Alert deactivated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error deactivating job alert", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to deactivate alert"));
        }
    }

    /**
     * Reactivate a job alert
     *
     * @param id the alert ID
     * @param httpRequest the HTTP request
     * @return response entity
     */
    @PostMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate job alert", description = "Reactivate a deactivated job alert")
    public ResponseEntity<Map<String, String>> reactivateJobAlert(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        log.info("Reactivating job alert with ID: {}", id);
        Long userId = getCurrentUserId(httpRequest);

        try {
            jobAlertService.reactivateJobAlert(id, userId);
            return ResponseEntity.ok(Map.of("message", "Alert reactivated successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Error reactivating job alert", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to reactivate alert"));
        }
    }

    /**
     * Get statistics for current user's job alerts
     *
     * @param httpRequest the HTTP request
     * @return alert statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get alert statistics", description = "Get job alert statistics for current user")
    public ResponseEntity<Map<String, Object>> getAlertStatistics(HttpServletRequest httpRequest) {
        log.info("Fetching alert statistics");
        Long userId = getCurrentUserId(httpRequest);

        long activeCount = jobAlertService.getActiveAlertCount(userId);
        long totalCount = jobAlertService.getTotalAlertCount(userId);

        return ResponseEntity.ok(Map.of(
                "activeAlerts", activeCount,
                "totalAlerts", totalCount,
                "inactiveAlerts", totalCount - activeCount
        ));
    }
}
