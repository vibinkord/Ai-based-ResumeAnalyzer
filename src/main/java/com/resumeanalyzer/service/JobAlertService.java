package com.resumeanalyzer.service;

import com.resumeanalyzer.model.dto.JobAlertRequest;
import com.resumeanalyzer.model.dto.JobAlertResponse;
import com.resumeanalyzer.model.entity.JobAlert;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.repository.JobAlertRepository;
import com.resumeanalyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JobAlertService - Manages job alert subscriptions
 * Handles CRUD operations for job alerts and processes alert matching
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class JobAlertService {

    private final JobAlertRepository jobAlertRepository;
    private final UserRepository userRepository;

    /**
     * Create a new job alert for a user
     *
     * @param userId  the user's ID
     * @param request the job alert request
     * @return the created job alert response
     * @throws IllegalArgumentException if user not found or invalid request
     */
    @Transactional
    public JobAlertResponse createJobAlert(Long userId, JobAlertRequest request) {
        log.info("Creating job alert for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        JobAlert jobAlert = request.toEntity();
        jobAlert.setUser(user);
        jobAlert.setIsActive(true);

        JobAlert savedAlert = jobAlertRepository.save(jobAlert);
        log.info("Job alert created with ID: {} for user: {}", savedAlert.getId(), userId);

        return JobAlertResponse.fromEntity(savedAlert);
    }

    /**
     * Get a job alert by ID
     *
     * @param alertId the alert ID
     * @return Optional containing the job alert response
     */
    public Optional<JobAlertResponse> getJobAlertById(Long alertId) {
        return jobAlertRepository.findById(alertId)
                .map(JobAlertResponse::fromEntity);
    }

    /**
     * Get job alert with user verification
     *
     * @param alertId the alert ID
     * @param userId  the user ID (for verification)
     * @return Optional containing the job alert response
     */
    public Optional<JobAlertResponse> getJobAlertById(Long alertId, Long userId) {
        Optional<JobAlert> alert = jobAlertRepository.findById(alertId);
        if (alert.isPresent() && alert.get().getUser().getId().equals(userId)) {
            return alert.map(JobAlertResponse::fromEntity);
        }
        return Optional.empty();
    }

    /**
     * Get all active job alerts for a user
     *
     * @param userId the user's ID
     * @return list of job alert responses
     */
    public List<JobAlertResponse> getAlertsByUser(Long userId) {
        log.info("Fetching job alerts for user ID: {}", userId);
        List<JobAlert> alerts = jobAlertRepository.findByUserIdAndIsActiveTrue(userId);
        return alerts.stream()
                .map(JobAlertResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all job alerts for a user with pagination
     *
     * @param userId   the user's ID
     * @param pageable pagination information
     * @return page of job alert responses
     */
    public Page<JobAlertResponse> getAlertsByUser(Long userId, Pageable pageable) {
        log.info("Fetching job alerts for user ID: {} with pagination", userId);
        Page<JobAlert> alerts = jobAlertRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return alerts.map(JobAlertResponse::fromEntity);
    }

    /**
     * Search alerts by job title
     *
     * @param userId    the user's ID
     * @param jobTitle  the job title to search
     * @return list of matching job alert responses
     */
    public List<JobAlertResponse> searchAlertsByJobTitle(Long userId, String jobTitle) {
        log.info("Searching alerts for user ID: {} with job title containing: {}", userId, jobTitle);
        List<JobAlert> alerts = jobAlertRepository.findByUserIdAndJobTitleContaining(userId, jobTitle);
        return alerts.stream()
                .map(JobAlertResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update a job alert
     *
     * @param alertId the alert ID
     * @param userId  the user ID (for verification)
     * @param request the update request
     * @return the updated job alert response
     * @throws IllegalArgumentException if alert not found or user unauthorized
     */
    @Transactional
    public JobAlertResponse updateJobAlert(Long alertId, Long userId, JobAlertRequest request) {
        log.info("Updating job alert ID: {} for user: {}", alertId, userId);

        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Job alert not found with ID: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this alert");
        }

        alert.setJobTitle(request.getJobTitle());
        alert.setCompany(request.getCompany());
        alert.setDescription(request.getDescription());
        alert.setRequiredSkills(request.getRequiredSkills());
        alert.setSalaryMin(request.getSalaryMin());
        alert.setSalaryMax(request.getSalaryMax());
        alert.setLocation(request.getLocation());
        alert.setJobUrl(request.getJobUrl());
        alert.setFrequency(request.getFrequency());
        alert.setMatchThreshold(request.getMatchThreshold());
        alert.setSendEmailNotification(request.getSendEmailNotification());

        JobAlert updatedAlert = jobAlertRepository.save(alert);
        log.info("Job alert updated successfully with ID: {}", alertId);

        return JobAlertResponse.fromEntity(updatedAlert);
    }

    /**
     * Delete a job alert
     *
     * @param alertId the alert ID
     * @param userId  the user ID (for verification)
     * @throws IllegalArgumentException if alert not found or user unauthorized
     */
    @Transactional
    public void deleteJobAlert(Long alertId, Long userId) {
        log.info("Deleting job alert ID: {} for user: {}", alertId, userId);

        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Job alert not found with ID: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this alert");
        }

        jobAlertRepository.deleteById(alertId);
        log.info("Job alert deleted successfully with ID: {}", alertId);
    }

    /**
     * Deactivate a job alert (soft delete)
     *
     * @param alertId the alert ID
     * @param userId  the user ID (for verification)
     * @throws IllegalArgumentException if alert not found or user unauthorized
     */
    @Transactional
    public void deactivateJobAlert(Long alertId, Long userId) {
        log.info("Deactivating job alert ID: {} for user: {}", alertId, userId);

        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Job alert not found with ID: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to deactivate this alert");
        }

        alert.setIsActive(false);
        jobAlertRepository.save(alert);
        log.info("Job alert deactivated successfully with ID: {}", alertId);
    }

    /**
     * Reactivate a job alert
     *
     * @param alertId the alert ID
     * @param userId  the user ID (for verification)
     * @throws IllegalArgumentException if alert not found or user unauthorized
     */
    @Transactional
    public void reactivateJobAlert(Long alertId, Long userId) {
        log.info("Reactivating job alert ID: {} for user: {}", alertId, userId);

        JobAlert alert = jobAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Job alert not found with ID: " + alertId));

        if (!alert.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to reactivate this alert");
        }

        alert.setIsActive(true);
        jobAlertRepository.save(alert);
        log.info("Job alert reactivated successfully with ID: {}", alertId);
    }

    /**
     * Get alerts that need processing based on frequency
     *
     * @return list of job alerts to process
     */
    public List<JobAlert> getAlertsToProcess() {
        log.debug("Fetching alerts that need processing");
        List<JobAlert> alerts = jobAlertRepository.findAll();
        return alerts.stream()
                .filter(JobAlert::shouldProcessAlert)
                .collect(Collectors.toList());
    }

    /**
     * Mark an alert as processed
     *
     * @param alertId the alert ID
     */
    @Transactional
    public void markAlertAsProcessed(Long alertId) {
        jobAlertRepository.findById(alertId).ifPresent(alert -> {
            alert.markAsSent();
            jobAlertRepository.save(alert);
            log.debug("Alert ID: {} marked as processed", alertId);
        });
    }

    /**
     * Get the count of active alerts for a user
     *
     * @param userId the user's ID
     * @return count of active alerts
     */
    public long getActiveAlertCount(Long userId) {
        return jobAlertRepository.countByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Get the count of all alerts for a user
     *
     * @param userId the user's ID
     * @return total count of alerts
     */
    public long getTotalAlertCount(Long userId) {
        return jobAlertRepository.countByUserId(userId);
    }

    /**
     * Check if user has active alerts
     *
     * @param userId the user's ID
     * @return true if user has at least one active alert
     */
    public boolean hasActiveAlerts(Long userId) {
        return getActiveAlertCount(userId) > 0;
    }
}
