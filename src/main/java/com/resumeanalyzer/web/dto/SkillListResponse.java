package com.resumeanalyzer.web.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for skills list endpoint.
 * Contains all known skills in the system.
 */
@Schema(
    name = "SkillListResponse",
    description = "List of all known technical skills in the system"
)
public class SkillListResponse {
    @Schema(
        description = "List of all recognized technical skills",
        example = "[\"Java\", \"Python\", \"Spring Boot\", \"AWS\", \"Docker\"]"
    )
    private List<String> skills;

    @Schema(
        description = "Total count of known skills",
        example = "115",
        minimum = "0"
    )
    private int totalCount;

    @Schema(
        description = "Description of the skill list",
        example = "All recognized technical skills in the system"
    )
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
