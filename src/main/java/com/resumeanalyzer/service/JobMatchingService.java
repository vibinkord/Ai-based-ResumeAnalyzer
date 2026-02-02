package com.resumeanalyzer.service;

import com.resumeanalyzer.model.dto.JobMatchResultDto;
import com.resumeanalyzer.model.entity.*;
import com.resumeanalyzer.repository.JobMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JobMatchingService - Advanced algorithm for matching jobs to user alerts
 * Implements multi-factor matching with skill analysis, salary matching, and more
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobMatchingService {

    private final JobMatchRepository jobMatchRepository;

    /**
     * Match a resume against a job alert
     * Uses multi-factor scoring algorithm
     */
    public JobMatchResultDto matchResumeToAlert(User user, Resume resume, JobAlert alert) {
        log.info("Matching resume ID: {} to alert ID: {}", resume.getId(), alert.getId());

        JobMatchResultDto result = new JobMatchResultDto();
        result.setAlertId(alert.getId());
        result.setUserId(user.getId());

        // Extract skills from resume and alert
        Set<String> resumeSkills = extractSkillsFromResume(resume);
        Set<String> alertSkills = alert.getRequiredSkillsSet();

        // Calculate skill match score
        double skillScore = calculateSkillMatchScore(resumeSkills, alertSkills);
        
        // Calculate salary match score if salary info available
        double salaryScore = calculateSalaryScore(resume, alert);
        
        // Calculate experience match score
        double experienceScore = calculateExperienceScore(resume);
        
        // Calculate location score (if applicable)
        double locationScore = calculateLocationScore(resume, alert);

        // Combined weighted score
        double finalScore = weightedScore(skillScore, salaryScore, experienceScore, locationScore);
        
        result.setMatchScore(finalScore);
        result.setSkillScore(skillScore);
        result.setSalaryScore(salaryScore);
        result.setExperienceScore(experienceScore);
        result.setLocationScore(locationScore);

        // Extract matched and missing skills
        Set<String> matchedSkills = resumeSkills.stream()
                .filter(alertSkills::contains)
                .collect(Collectors.toSet());
        
        Set<String> missingSkills = alertSkills.stream()
                .filter(s -> !matchedSkills.contains(s))
                .collect(Collectors.toSet());

        result.setMatchedSkills(String.join(",", matchedSkills));
        result.setMissingSkills(String.join(",", missingSkills));
        result.setMatchPercentage((int) finalScore);
        result.setTimestamp(LocalDateTime.now());
        result.setMatched(finalScore >= alert.getMatchThreshold());

        log.info("Match score: {} for alert ID: {}", finalScore, alert.getId());
        return result;
    }

    /**
     * Calculate skill match score (0-100)
     * Based on percentage of required skills found in resume
     */
    private double calculateSkillMatchScore(Set<String> resumeSkills, Set<String> alertSkills) {
        if (alertSkills.isEmpty()) {
            return 100.0;
        }

        long matchedCount = resumeSkills.stream()
                .filter(alertSkills::contains)
                .count();

        return (matchedCount * 100.0) / alertSkills.size();
    }

    /**
     * Calculate salary match score
     * Returns 100 if resume salary expectation matches job salary range
     */
    private double calculateSalaryScore(Resume resume, JobAlert alert) {
        // If no salary info, return neutral score
        if (alert.getSalaryMin() == null || alert.getSalaryMax() == null) {
            return 75.0; // Neutral score
        }

        // Extract salary from resume if available
        // This is a placeholder - actual implementation would parse resume
        Double resumeSalary = extractSalaryFromResume(resume);
        
        if (resumeSalary == null) {
            return 75.0; // Neutral score
        }

        // Check if resume salary is within alert range
        if (resumeSalary >= alert.getSalaryMin() && resumeSalary <= alert.getSalaryMax()) {
            return 100.0;
        }

        // Calculate how far outside the range
        if (resumeSalary < alert.getSalaryMin()) {
            double gap = alert.getSalaryMin() - resumeSalary;
            double tolerance = alert.getSalaryMin() * 0.1; // 10% tolerance
            return Math.max(50.0, 100.0 - (gap / tolerance * 25));
        } else {
            double gap = resumeSalary - alert.getSalaryMax();
            double tolerance = alert.getSalaryMax() * 0.1; // 10% tolerance
            return Math.max(50.0, 100.0 - (gap / tolerance * 25));
        }
    }

    /**
     * Calculate experience match score
     * Based on years of experience mentioned in resume
     */
    private double calculateExperienceScore(Resume resume) {
        // Extract years of experience from resume
        int yearsOfExperience = extractExperienceYears(resume);
        
        // Ideal experience is 3-10 years
        if (yearsOfExperience >= 3 && yearsOfExperience <= 10) {
            return 100.0;
        }
        
        // Less than 3 years: reduce score
        if (yearsOfExperience < 3) {
            return 50.0 + (yearsOfExperience * 16.67);
        }
        
        // More than 10 years: still good but maybe overqualified
        return Math.max(70.0, 100.0 - ((yearsOfExperience - 10) * 5));
    }

    /**
     * Calculate location match score
     * Returns 100 if location matches, 80 if not specified
     */
    private double calculateLocationScore(Resume resume, JobAlert alert) {
        if (alert.getLocation() == null || alert.getLocation().isEmpty()) {
            return 80.0; // Neutral if not specified
        }

        String resumeLocation = extractLocationFromResume(resume);
        if (resumeLocation == null || resumeLocation.isEmpty()) {
            return 80.0; // Neutral if not available
        }

        // Simple location matching
        if (resumeLocation.toLowerCase().contains(alert.getLocation().toLowerCase()) ||
                alert.getLocation().toLowerCase().contains(resumeLocation.toLowerCase())) {
            return 100.0;
        }

        return 60.0; // Partial match
    }

    /**
     * Calculate weighted final score
     * Weighting: Skills 50%, Salary 25%, Experience 15%, Location 10%
     */
    private double weightedScore(double skill, double salary, double experience, double location) {
        return (skill * 0.50) + (salary * 0.25) + (experience * 0.15) + (location * 0.10);
    }

    /**
     * Extract skills from resume content
     */
    private Set<String> extractSkillsFromResume(Resume resume) {
        Set<String> skills = new HashSet<>();
        String content = resume.getContent() != null ? resume.getContent() : "";
        
        // Common programming languages and frameworks
        String[] commonSkills = {
            "Java", "Python", "JavaScript", "TypeScript", "C#", "C++", "Go", "Rust", "PHP", "Ruby",
            "Spring", "Spring Boot", "Django", "Flask", "React", "Angular", "Vue", "Node.js",
            "SQL", "MongoDB", "PostgreSQL", "MySQL", "Redis", "Elasticsearch",
            "Docker", "Kubernetes", "AWS", "Azure", "GCP", "Git", "GitHub", "GitLab",
            "REST", "GraphQL", "Microservices", "CI/CD", "Jenkins", "Maven", "Gradle",
            "HTML", "CSS", "Sass", "Bootstrap", "Material", "Webpack", "npm", "yarn"
        };

        String contentLower = content.toLowerCase();
        for (String skill : commonSkills) {
            if (contentLower.contains(skill.toLowerCase())) {
                skills.add(skill);
            }
        }

        return skills;
    }

    /**
     * Extract salary expectation from resume
     * Returns null if not found
     */
    private Double extractSalaryFromResume(Resume resume) {
        // Placeholder - would parse resume for salary info
        return null;
    }

    /**
     * Extract years of experience from resume
     */
    private int extractExperienceYears(Resume resume) {
        // Simple heuristic: count mentions of years
        String content = resume.getContent() != null ? resume.getContent() : "";
        String[] patterns = {"1 year", "2 years", "3 years", "4 years", "5 years", 
                           "6 years", "7 years", "8 years", "9 years", "10 years"};
        
        for (String pattern : patterns) {
            if (content.toLowerCase().contains(pattern)) {
                return Integer.parseInt(pattern.split(" ")[0]);
            }
        }

        return 2; // Default assumption
    }

    /**
     * Extract location from resume
     */
    private String extractLocationFromResume(Resume resume) {
        // Placeholder - would extract location from resume
        return null;
    }

    /**
     * Batch process matches for all alerts for a user
     */
    public List<JobMatchResultDto> batchMatchResume(User user, Resume resume) {
        log.info("Batch matching resume ID: {} for user ID: {}", resume.getId(), user.getId());
        
        List<JobMatchResultDto> results = new ArrayList<>();
        
        // This would be called with actual job alerts
        // For now, returns empty list
        return results;
    }

    /**
     * Get match statistics for a user
     */
    public Map<String, Object> getUserMatchStatistics(Long userId) {
        log.info("Calculating match statistics for user ID: {}", userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        // Would query job matches and calculate stats
        long totalMatches = 0;
        long highQualityMatches = 0;
        double averageScore = 0;

        stats.put("totalMatches", totalMatches);
        stats.put("highQualityMatches", highQualityMatches);
        stats.put("averageScore", averageScore);
        stats.put("topSkillMatch", "");

        return stats;
    }
}
