package com.resumeanalyzer.suggestions;

import com.resumeanalyzer.analysis.SkillMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Generates AI-like, rule-based resume improvement suggestions.
 * Uses deterministic logic based on missing skills and match percentage.
 * Core Java only, no APIs.
 */
public class ResumeSuggestionEngine {

    /**
     * Generates resume improvement suggestions based on skill matching results.
     * @param result SkillMatcher.Result containing matched, missing skills and percentage
     * @return list of human-readable suggestion strings
     */
    public List<String> generateSuggestions(SkillMatcher.Result result) {
        List<String> suggestions = new ArrayList<>();

        if (result == null) {
            return suggestions;
        }

        // Rule 1: For each missing skill, suggest gaining experience
        Set<String> missingSkills = result.getMissingSkills();
        if (!missingSkills.isEmpty()) {
            for (String skill : missingSkills) {
                suggestions.add("Add hands-on experience with " + skill + " to your resume.");
            }
        }

        // Rules 2–4: Score-based suggestions
        double percentage = result.getMatchPercentage();

        if (percentage < 50.0) {
            // Rule 2: Major restructuring needed
            suggestions.add("Your resume needs major alignment with job requirements. " +
                    "Restructure to highlight required skills more prominently.");
            suggestions.add("Consider adding a 'Core Competencies' section to emphasize missing technical areas.");
        } else if (percentage < 80.0) {
            // Rule 3: Improve descriptions and keywords
            suggestions.add("Improve project descriptions to better highlight relevant skills mentioned in the job posting.");
            suggestions.add("Optimize resume keywords to match ATS scanning patterns used by recruiters.");
        } else {
            // Rule 4: Minor refinements
            suggestions.add("Your resume has strong alignment. Refine formatting and ATS keyword optimization.");
            suggestions.add("Consider adding quantifiable results (e.g., performance improvements) to strengthen impact.");
        }

        return suggestions;
    }
}
