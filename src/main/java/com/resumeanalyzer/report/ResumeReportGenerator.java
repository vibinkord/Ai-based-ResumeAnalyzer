package com.resumeanalyzer.report;

import com.resumeanalyzer.analysis.SkillMatcher;
import java.util.List;
import java.util.Set;

/**
 * Generates a clean, consolidated analysis report.
 * Formats all stages (skills, matching, suggestions) into a single readable String.
 * Core Java only, no I/O.
 */
public class ResumeReportGenerator {

    /**
     * Generates a formatted analysis report.
     * @param result SkillMatcher.Result containing matched/missing skills and percentage
     * @param suggestions list of improvement suggestions from ResumeSuggestionEngine
     * @return formatted report as a single String
     */
    public String generateReport(SkillMatcher.Result result, List<String> suggestions) {
        if (result == null || suggestions == null) {
            return buildReport(new SkillMatcher.Result(Set.of(), Set.of(), 0.0), List.of());
        }
        return buildReport(result, suggestions);
    }

    private String buildReport(SkillMatcher.Result result, List<String> suggestions) {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("==============================\n");
        sb.append("AI Resume Analysis Report\n");
        sb.append("==============================\n");
        sb.append("\n");

        // Match percentage
        sb.append("Resume Match Score: ").append("%.1f%%".formatted(result.getMatchPercentage())).append("\n");
        sb.append("\n");

        // Matched skills
        sb.append("Matched Skills:\n");
        Set<String> matched = result.getMatchedSkills();
        if (matched.isEmpty()) {
            sb.append("- None\n");
        } else {
            for (String skill : matched) {
                sb.append("- ").append(skill).append("\n");
            }
        }
        sb.append("\n");

        // Missing skills
        sb.append("Missing Skills:\n");
        Set<String> missing = result.getMissingSkills();
        if (missing.isEmpty()) {
            sb.append("- None\n");
        } else {
            for (String skill : missing) {
                sb.append("- ").append(skill).append("\n");
            }
        }
        sb.append("\n");

        // Suggestions
        sb.append("Suggestions:\n");
        if (suggestions.isEmpty()) {
            sb.append("- None\n");
        } else {
            for (String suggestion : suggestions) {
                sb.append("- ").append(suggestion).append("\n");
            }
        }

        return sb.toString();
    }
}
