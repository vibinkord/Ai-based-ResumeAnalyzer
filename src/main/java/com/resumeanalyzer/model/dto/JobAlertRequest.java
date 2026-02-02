package com.resumeanalyzer.model.dto;

import com.resumeanalyzer.model.entity.JobAlert;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * JobAlertRequest DTO - Request body for creating/updating job alerts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAlertRequest {

    @NotBlank(message = "Job title is required")
    @Size(min = 2, max = 255, message = "Job title must be between 2 and 255 characters")
    private String jobTitle;

    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String company;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Size(max = 2000, message = "Required skills must not exceed 2000 characters")
    private String requiredSkills; // Comma-separated skills

    @Min(value = 0, message = "Minimum salary must be non-negative")
    private Double salaryMin;

    @Min(value = 0, message = "Maximum salary must be non-negative")
    private Double salaryMax;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Size(max = 2000, message = "Job URL must not exceed 2000 characters")
    private String jobUrl;

    @NotNull(message = "Frequency is required")
    private JobAlert.AlertFrequency frequency;

    @NotNull(message = "Match threshold is required")
    @Min(value = 0, message = "Match threshold must be at least 0")
    @Max(value = 100, message = "Match threshold must not exceed 100")
    private Double matchThreshold;

    @NotNull(message = "Send email notification preference is required")
    private Boolean sendEmailNotification;

    /**
     * Validate that salaryMax >= salaryMin if both are provided
     */
    @AssertTrue(message = "Maximum salary must be greater than or equal to minimum salary")
    private boolean isSalaryRangeValid() {
        if (salaryMin != null && salaryMax != null) {
            return salaryMax >= salaryMin;
        }
        return true;
    }

    /**
     * Convert DTO to entity
     */
    public JobAlert toEntity() {
        return JobAlert.builder()
                .jobTitle(this.jobTitle)
                .company(this.company)
                .description(this.description)
                .requiredSkills(this.requiredSkills)
                .salaryMin(this.salaryMin)
                .salaryMax(this.salaryMax)
                .location(this.location)
                .jobUrl(this.jobUrl)
                .frequency(this.frequency)
                .matchThreshold(this.matchThreshold)
                .sendEmailNotification(this.sendEmailNotification)
                .build();
    }
}
