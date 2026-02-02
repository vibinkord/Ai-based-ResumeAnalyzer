package com.resumeanalyzer.report;

import com.resumeanalyzer.analysis.SkillMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResumeReportGenerator class.
 * Tests report generation formatting and structure.
 */
@DisplayName("ResumeReportGenerator Tests")
class ResumeReportGeneratorTest {

    private ResumeReportGenerator reportGenerator;

    @BeforeEach
    void setUp() {
        reportGenerator = new ResumeReportGenerator();
    }

    @Test
    @DisplayName("Should generate non-null report")
    void testGenerateReportNotNull() {
        Set<String> matchedSkills = Set.of("Java", "SQL");
        Set<String> missingSkills = Set.of("Spring");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 66.67);
        List<String> suggestions = Arrays.asList("Suggestion 1", "Suggestion 2");

        String report = reportGenerator.generateReport(result, suggestions);

        assertNotNull(report, "Report should not be null");
        assertTrue(report.length() > 0, "Report should not be empty");
    }

    @Test
    @DisplayName("Should include match percentage in report")
    void testReportIncludesMatchPercentage() {
        Set<String> matchedSkills = Set.of("Java");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 75.5);
        List<String> suggestions = List.of();

        String report = reportGenerator.generateReport(result, suggestions);

        assertTrue(report.contains("75"), "Report should contain match percentage");
    }

    @Test
    @DisplayName("Should include matched skills section")
    void testReportIncludesMatchedSkills() {
        Set<String> matchedSkills = Set.of("Java", "SQL", "REST");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 100.0);
        List<String> suggestions = List.of();

        String report = reportGenerator.generateReport(result, suggestions);

        assertTrue(report.toLowerCase().contains("match"), "Report should contain matched skills section");
        assertTrue(report.contains("Java"), "Report should contain matched skill Java");
        assertTrue(report.contains("SQL"), "Report should contain matched skill SQL");
    }

    @Test
    @DisplayName("Should include missing skills section")
    void testReportIncludesMissingSkills() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = Set.of("Spring", "Docker", "Kubernetes");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 0.0);
        List<String> suggestions = List.of();

        String report = reportGenerator.generateReport(result, suggestions);

        assertTrue(report.toLowerCase().contains("miss"), "Report should contain missing skills section");
        assertTrue(report.contains("Spring"), "Report should contain missing skill Spring");
        assertTrue(report.contains("Docker"), "Report should contain missing skill Docker");
    }

    @Test
    @DisplayName("Should include suggestions section")
    void testReportIncludesSuggestions() {
        Set<String> matchedSkills = Set.of("Java");
        Set<String> missingSkills = Set.of("Spring");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 50.0);
        List<String> suggestions = Arrays.asList(
                "Add Spring experience",
                "Improve project descriptions"
        );

        String report = reportGenerator.generateReport(result, suggestions);

        assertTrue(report.toLowerCase().contains("suggestion"), "Report should contain suggestions section");
        assertTrue(report.contains("Add Spring experience"), "Report should contain first suggestion");
        assertTrue(report.contains("Improve project descriptions"), "Report should contain second suggestion");
    }

    @Test
    @DisplayName("Should handle empty matched skills")
    void testReportWithEmptyMatchedSkills() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = Set.of("Java", "SQL");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 0.0);
        List<String> suggestions = List.of();

        String report = reportGenerator.generateReport(result, suggestions);

        assertNotNull(report, "Report should still be generated");
        assertTrue(report.length() > 0, "Report should not be empty");
    }

    @Test
    @DisplayName("Should handle empty missing skills")
    void testReportWithEmptyMissingSkills() {
        Set<String> matchedSkills = Set.of("Java", "SQL");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 100.0);
        List<String> suggestions = List.of();

        String report = reportGenerator.generateReport(result, suggestions);

        assertNotNull(report, "Report should still be generated");
        assertTrue(report.length() > 0, "Report should not be empty");
    }

    @Test
    @DisplayName("Should handle empty suggestions list")
    void testReportWithEmptySuggestions() {
        Set<String> matchedSkills = Set.of("Java");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 100.0);
        List<String> suggestions = List.of();

        String report = reportGenerator.generateReport(result, suggestions);

        assertNotNull(report, "Report should still be generated");
        assertTrue(report.length() > 0, "Report should not be empty");
    }

    @Test
    @DisplayName("Should have professional formatting")
    void testReportFormatting() {
        Set<String> matchedSkills = Set.of("Java", "SQL");
        Set<String> missingSkills = Set.of("Spring");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 66.67);
        List<String> suggestions = Arrays.asList("Suggestion 1", "Suggestion 2");

        String report = reportGenerator.generateReport(result, suggestions);

        // Check for professional formatting
        assertTrue(report.contains("=") || report.contains("-") || report.contains("*"),
                "Report should have some formatting characters");
    }

    @Test
    @DisplayName("Should include all matched skills in report")
    void testAllMatchedSkillsIncluded() {
        Set<String> matchedSkills = Set.of("Java", "SQL", "REST", "Spring", "Docker");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 100.0);
        List<String> suggestions = List.of();

        String report = reportGenerator.generateReport(result, suggestions);

        for (String skill : matchedSkills) {
            assertTrue(report.contains(skill), "Report should contain matched skill: " + skill);
        }
    }

    @Test
    @DisplayName("Should include all missing skills in report")
    void testAllMissingSkillsIncluded() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = Set.of("Kubernetes", "Docker", "CI/CD", "Terraform", "AWS");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 0.0);
        List<String> suggestions = List.of();

        String report = reportGenerator.generateReport(result, suggestions);

        for (String skill : missingSkills) {
            assertTrue(report.contains(skill), "Report should contain missing skill: " + skill);
        }
    }

    @Test
    @DisplayName("Should include all suggestions in report")
    void testAllSuggestionsIncluded() {
        Set<String> matchedSkills = Set.of("Java");
        Set<String> missingSkills = Set.of("Spring");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 50.0);
        List<String> suggestions = Arrays.asList(
                "First suggestion",
                "Second suggestion",
                "Third suggestion",
                "Fourth suggestion"
        );

        String report = reportGenerator.generateReport(result, suggestions);

        for (String suggestion : suggestions) {
            assertTrue(report.contains(suggestion), "Report should contain suggestion: " + suggestion);
        }
    }

    @Test
    @DisplayName("Should handle very high match percentage")
    void testVeryHighMatchPercentage() {
        Set<String> matchedSkills = Set.of("Java", "SQL", "Spring");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 100.0);
        List<String> suggestions = List.of("Strong match!");

        String report = reportGenerator.generateReport(result, suggestions);

        assertTrue(report.contains("100"), "Report should contain 100% match");
    }

    @Test
    @DisplayName("Should handle zero match percentage")
    void testZeroMatchPercentage() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = Set.of("Java", "SQL", "Spring");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 0.0);
        List<String> suggestions = Arrays.asList("Major restructuring needed");

        String report = reportGenerator.generateReport(result, suggestions);

        assertTrue(report.contains("0"), "Report should contain 0% match");
    }

    @Test
    @DisplayName("Should handle large number of skills")
    void testLargeNumberOfSkills() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = new HashSet<>();

        // Create 20 matched and 20 missing skills
        for (int i = 0; i < 20; i++) {
            matchedSkills.add("MatchedSkill" + i);
            missingSkills.add("MissingSkill" + i);
        }

        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 50.0);
        List<String> suggestions = List.of("Work on missing skills");

        String report = reportGenerator.generateReport(result, suggestions);

        assertNotNull(report, "Report should be generated for large skill sets");
        assertTrue(report.length() > 0, "Report should not be empty");
        // Verify some skills are included
        assertTrue(report.contains("MatchedSkill0"), "Report should contain at least first matched skill");
        assertTrue(report.contains("MissingSkill0"), "Report should contain at least first missing skill");
    }

    @Test
    @DisplayName("Should generate readable report text")
    void testReportReadability() {
        Set<String> matchedSkills = Set.of("Java");
        Set<String> missingSkills = Set.of("Spring");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 50.0);
        List<String> suggestions = Arrays.asList("Suggestion");

        String report = reportGenerator.generateReport(result, suggestions);

        // Check that report contains natural language
        assertTrue(report.toLowerCase().contains("resume") || 
                   report.toLowerCase().contains("analysis") ||
                   report.toLowerCase().contains("match") ||
                   report.toLowerCase().contains("skill"),
                "Report should contain meaningful content");
    }

}
