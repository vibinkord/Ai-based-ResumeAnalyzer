package com.resumeanalyzer.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for resume comparison endpoint.
 * Contains comparison results including common and unique skills.
 */
public class ComparisonResponse {
    private int resume1SkillCount;
    private int resume2SkillCount;
    private List<String> commonSkills;
    private List<String> uniqueToResume1;
    private List<String> uniqueToResume2;
    private double similarityPercentage;
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
