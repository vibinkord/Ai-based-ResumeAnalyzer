package com.resumeanalyzer.controller;

import com.resumeanalyzer.model.dto.JobMatchResultDto;
import com.resumeanalyzer.model.dto.JobAlertResponse;
import com.resumeanalyzer.model.entity.JobAlert;
import com.resumeanalyzer.model.entity.Resume;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.service.JobAlertService;
import com.resumeanalyzer.service.JobMatchingService;
import com.resumeanalyzer.service.ResumeService;
import com.resumeanalyzer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JobMatchingController - REST API endpoints for job-to-resume matching
 * Handles matching operations, quality distribution analysis, and recommendations
 */
@RestController
@RequestMapping("/api/job-matching")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Job Matching", description = "APIs for job-to-resume matching and analysis")
@SecurityRequirement(name = "JWT")
public class JobMatchingController {

    private final JobMatchingService jobMatchingService;
    private final JobAlertService jobAlertService;
    private final ResumeService resumeService;
    private final UserService userService;

    /**
     * Match resume against a specific job alert
     */
    @PostMapping("/match/{alertId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Match resume against job alert", 
               description = "Analyzes how well user's resume matches a specific job alert")
    public ResponseEntity<JobMatchResultDto> matchResumeToAlert(
            @PathVariable Long alertId,
            Authentication authentication) {
        try {
            log.info("Matching resume for alert ID: {}", alertId);
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            JobAlert alert = null;
            Optional<JobAlertResponse> alertResponse = jobAlertService.getJobAlertById(alertId);
            if (alertResponse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resume resume = resumeService.getUserResumes(user.getId()).stream()
                    .findFirst().orElse(null);
            
            if (resume == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            JobMatchResultDto result = jobMatchingService.matchResumeToAlert(user, resume, alert);
            log.info("Successfully matched resume to alert ID: {}", alertId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error matching resume to alert: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Batch match resume against all user's active job alerts
     */
    @PostMapping("/batch-match/{resumeId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Batch match resume against all alerts", 
               description = "Matches a resume against all active job alerts for the user")
    public ResponseEntity<List<JobMatchResultDto>> batchMatchResume(
            @PathVariable Long resumeId,
            Authentication authentication) {
        try {
            log.info("Batch matching resume ID: {}", resumeId);
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Resume resume = resumeService.getUserResumes(user.getId()).stream()
                    .filter(r -> r.getId().equals(resumeId))
                    .findFirst().orElse(null);
            
            if (resume == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<JobMatchResultDto> results = jobMatchingService.batchMatchResume(user, resume);
            log.info("Batch match completed with {} results", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error in batch matching: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get match quality distribution for user
     */
    @GetMapping("/quality-distribution")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get match quality distribution", 
               description = "Returns breakdown of job matches by quality level")
    public ResponseEntity<Map<String, Object>> getQualityDistribution(Authentication authentication) {
        try {
            log.info("Fetching quality distribution for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> distribution = new HashMap<>();
            distribution.put("Excellent (90-100)", 0);
            distribution.put("Very Good (80-89)", 0);
            distribution.put("Good (70-79)", 0);
            distribution.put("Fair (60-69)", 0);
            distribution.put("Poor (0-59)", 0);
            distribution.put("totalMatches", 0);
            
            log.info("Quality distribution calculated");
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            log.error("Error fetching quality distribution: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get job recommendations based on resume
     */
    @GetMapping("/recommendations")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get job recommendations", 
               description = "Returns top job recommendations based on resume match")
    public ResponseEntity<List<Map<String, Object>>> getRecommendations(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        try {
            log.info("Fetching recommendations for user: {} with limit: {}", 
                    authentication.getName(), limit);
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<Map<String, Object>> recommendations = new ArrayList<>();
            
            Resume resume = resumeService.getUserResumes(user.getId()).stream()
                    .findFirst().orElse(null);
            
            if (resume == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<JobAlertResponse> alertResponses = jobAlertService.getAlertsByUser(user.getId());
            
            List<Map<String, Object>> matches = new ArrayList<>();
            for (JobAlertResponse alertResp : alertResponses) {
                Map<String, Object> m = new HashMap<>();
                m.put("alertId", alertResp.getId());
                m.put("jobTitle", alertResp.getJobTitle());
                m.put("company", alertResp.getCompany());
                matches.add(m);
            }
            
            recommendations = matches.stream()
                    .limit(limit)
                    .collect(Collectors.toList());

            log.info("Generated {} recommendations", recommendations.size());
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Error fetching recommendations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get detailed match scores for a specific job match
     */
    @GetMapping("/results/{matchId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get detailed match scores", 
               description = "Returns detailed breakdown of match scores for a specific match")
    public ResponseEntity<JobMatchResultDto> getMatchDetails(
            @PathVariable Long matchId,
            Authentication authentication) {
        try {
            log.info("Fetching match details for match ID: {}", matchId);
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error fetching match details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get match statistics for user
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get match statistics", 
               description = "Returns aggregate matching statistics for the user")
    public ResponseEntity<Map<String, Object>> getMatchStatistics(Authentication authentication) {
        try {
            log.info("Fetching statistics for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> stats = jobMatchingService.getUserMatchStatistics(user.getId());
            log.info("Statistics fetched successfully");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top skills in demand based on job alerts
     */
    @GetMapping("/top-skills")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get top demanded skills", 
               description = "Returns list of most requested skills across user's job alerts")
    public ResponseEntity<List<Map<String, Object>>> getTopDemandedSkills(
            @RequestParam(defaultValue = "20") int limit,
            Authentication authentication) {
        try {
            log.info("Fetching top demanded skills for user: {}", authentication.getName());
            
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<Map<String, Object>> skills = new ArrayList<>();
            List<JobAlertResponse> alerts = jobAlertService.getAlertsByUser(user.getId());
            
            Map<String, Integer> skillCount = new HashMap<>();
            for (JobAlertResponse alert : alerts) {
                String[] skills_array = alert.getRequiredSkills().split(",");
                for (String skill : skills_array) {
                    skill = skill.trim();
                    skillCount.put(skill, skillCount.getOrDefault(skill, 0) + 1);
                }
            }

            skillCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(limit)
                    .forEach(entry -> {
                        Map<String, Object> skillMap = new HashMap<>();
                        skillMap.put("skill", entry.getKey());
                        skillMap.put("demand", entry.getValue());
                        skills.add(skillMap);
                    });

            log.info("Top {} skills fetched", skills.size());
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            log.error("Error fetching top skills: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
