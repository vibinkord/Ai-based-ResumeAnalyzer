package com.resumeanalyzer.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for resume comparison endpoint.
 * Contains comparison results including common and unique skills.
 */
@Schema(
    name = "ComparisonResponse",
    description = "Detailed comparison results between two resumes"
)
public class ComparisonResponse {
    @Schema(
        description = "Total number of unique skills in resume 1",
        example = "25",
        minimum = "0"
    )
    private int resume1SkillCount;

    @Schema(
        description = "Total number of unique skills in resume 2",
        example = "28",
        minimum = "0"
    )
    private int resume2SkillCount;

    @Schema(
        description = "Skills that are present in both resumes",
        example = "[\"Java\", \"Python\", \"Docker\"]"
    )
    private List<String> commonSkills;

    @Schema(
        description = "Skills unique to resume 1 (not in resume 2)",
        example = "[\"Spring Boot\", \"AWS\"]"
    )
    private List<String> uniqueToResume1;

    @Schema(
        description = "Skills unique to resume 2 (not in resume 1)",
        example = "[\"Django\", \"GCP\"]"
    )
    private List<String> uniqueToResume2;

    @Schema(
        description = "Similarity percentage based on common skills",
        example = "60.5",
        minimum = "0",
        maximum = "100"
    )
    private double similarityPercentage;

    @Schema(
        description = "Timestamp when comparison was performed",
        example = "2024-01-15T10:30:45.123456"
    )
    private LocalDateTime timestamp;

    public ComparisonResponse(int resume1SkillCount, int resume2SkillCount,
                             List<String> commonSkills, List<String> uniqueToResume1,
                             List<String> uniqueToResume2, double similarityPercentage,
                             LocalDateTime timestamp) {
        this.resume1SkillCount = resume1SkillCount;
        this.resume2SkillCount = resume2SkillCount;
        this.commonSkills = commonSkills;
        this.uniqueToResume1 = uniqueToResume1;
        this.uniqueToResume2 = uniqueToResume2;
        this.similarityPercentage = similarityPercentage;
        this.timestamp = timestamp;
    }

    public int getResume1SkillCount() {
        return resume1SkillCount;
    }

    public void setResume1SkillCount(int resume1SkillCount) {
        this.resume1SkillCount = resume1SkillCount;
    }

    public int getResume2SkillCount() {
        return resume2SkillCount;
    }

    public void setResume2SkillCount(int resume2SkillCount) {
        this.resume2SkillCount = resume2SkillCount;
    }

    public List<String> getCommonSkills() {
        return commonSkills;
    }

    public void setCommonSkills(List<String> commonSkills) {
        this.commonSkills = commonSkills;
    }

    public List<String> getUniqueToResume1() {
        return uniqueToResume1;
    }

    public void setUniqueToResume1(List<String> uniqueToResume1) {
        this.uniqueToResume1 = uniqueToResume1;
    }

    public List<String> getUniqueToResume2() {
        return uniqueToResume2;
    }

    public void setUniqueToResume2(List<String> uniqueToResume2) {
        this.uniqueToResume2 = uniqueToResume2;
    }

    public double getSimilarityPercentage() {
        return similarityPercentage;
    }

    public void setSimilarityPercentage(double similarityPercentage) {
        this.similarityPercentage = similarityPercentage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
