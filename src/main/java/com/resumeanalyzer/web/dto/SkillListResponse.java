package com.resumeanalyzer.web.dto;

import java.util.List;

/**
 * Response DTO for skills list endpoint.
 * Contains all known skills in the system.
 */
public class SkillListResponse {
    private List<String> skills;
    private int totalCount;
    private String description;

    public SkillListResponse(List<String> skills, int totalCount, String description) {
        this.skills = skills;
        this.totalCount = totalCount;
        this.description = description;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
