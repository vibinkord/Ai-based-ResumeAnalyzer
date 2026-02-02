package com.resumeanalyzer.suggestions;

import com.resumeanalyzer.analysis.SkillMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResumeSuggestionEngine class.
 * Tests suggestion generation based on skill matching results.
 */
@DisplayName("ResumeSuggestionEngine Tests")
class ResumeSuggestionEngineTest {

    private ResumeSuggestionEngine suggestionEngine;

    @BeforeEach
    void setUp() {
        suggestionEngine = new ResumeSuggestionEngine();
    }

    @Test
    @DisplayName("Should generate suggestions for missing skills")
    void testGenerateSuggestionsForMissingSkills() {
        Set<String> matchedSkills = Set.of("Java", "SQL");
        Set<String> missingSkills = Set.of("Spring", "Docker");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 50.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertNotNull(suggestions, "Suggestions list should not be null");
        assertTrue(suggestions.size() > 0, "Should generate suggestions");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("Spring")), 
                "Should suggest adding Spring experience");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("Docker")), 
                "Should suggest adding Docker experience");
    }

    @Test
    @DisplayName("Should generate major restructuring suggestion for match < 50%")
    void testMajorRestructuringSuggestion() {
        Set<String> matchedSkills = Set.of("Java");
        Set<String> missingSkills = Set.of("Spring", "Docker", "Kubernetes");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 25.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("major") && s.toLowerCase().contains("restructur")), 
                "Should suggest major restructuring for low match");
        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("core competencies")), 
                "Should suggest Core Competencies section");
    }

    @Test
    @DisplayName("Should generate improvement suggestions for 50-80% match")
    void testImprovementSuggestions() {
        Set<String> matchedSkills = Set.of("Java", "SQL", "REST");
        Set<String> missingSkills = Set.of("Spring", "Docker");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 60.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("improve") && s.toLowerCase().contains("description")), 
                "Should suggest improving descriptions");
        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("ats") || s.toLowerCase().contains("keyword")), 
                "Should suggest optimizing for ATS");
    }

    @Test
    @DisplayName("Should generate refinement suggestions for match > 80%")
    void testRefinementSuggestions() {
        Set<String> matchedSkills = Set.of("Java", "SQL", "Spring", "REST", "OOP");
        Set<String> missingSkills = Set.of("Docker");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 85.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("minor") || s.toLowerCase().contains("refine")), 
                "Should suggest minor refinements for high match");
        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("quantifiable") || s.toLowerCase().contains("impact")), 
                "Should suggest adding quantifiable results");
    }

    @Test
    @DisplayName("Should handle null result gracefully")
    void testNullResult() {
        List<String> suggestions = suggestionEngine.generateSuggestions(null);

        assertNotNull(suggestions, "Should return non-null list");
        assertTrue(suggestions.isEmpty(), "Should return empty list for null result");
    }

    @Test
    @DisplayName("Should return non-empty suggestions list")
    void testNonEmptySuggestions() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = Set.of("Spring", "Docker");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 0.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.size() > 0, "Should generate at least one suggestion");
    }

    @Test
    @DisplayName("Should generate one suggestion per missing skill")
    void testOneSuggestionPerMissingSkill() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = Set.of("Skill1", "Skill2", "Skill3");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 0.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        long missingSuggestions = suggestions.stream()
                .filter(s -> s.toLowerCase().contains("add hands-on experience"))
                .count();
        
        assertTrue(missingSuggestions >= 3, "Should have at least 3 missing skill suggestions");
    }

    @Test
    @DisplayName("Should generate different suggestions for different match percentages")
    void testDifferentSuggestionsForDifferentPercentages() {
        Set<String> matchedSkills = Set.of("Java");
        Set<String> missingSkills = Set.of("Spring");

        // Low match
        SkillMatcher.Result lowMatch = new SkillMatcher.Result(matchedSkills, missingSkills, 30.0);
        List<String> lowSuggestions = suggestionEngine.generateSuggestions(lowMatch);

        // Medium match
        SkillMatcher.Result mediumMatch = new SkillMatcher.Result(matchedSkills, missingSkills, 60.0);
        List<String> mediumSuggestions = suggestionEngine.generateSuggestions(mediumMatch);

        // High match
        SkillMatcher.Result highMatch = new SkillMatcher.Result(matchedSkills, missingSkills, 90.0);
        List<String> highSuggestions = suggestionEngine.generateSuggestions(highMatch);

        assertNotEquals(lowSuggestions, mediumSuggestions, "Low and medium match should have different suggestions");
        assertNotEquals(mediumSuggestions, highSuggestions, "Medium and high match should have different suggestions");
    }

    @Test
    @DisplayName("Should handle 0% match")
    void testZeroPercentMatch() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = Set.of("Java", "Spring");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 0.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.size() > 0, "Should generate suggestions even for 0% match");
    }

    @Test
    @DisplayName("Should handle 100% match")
    void testFullPercentMatch() {
        Set<String> matchedSkills = Set.of("Java", "Spring", "SQL");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 100.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.size() > 0, "Should generate suggestions even for perfect match");
        assertFalse(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("major")), 
                "Should not suggest major restructuring for 100% match");
    }

    @Test
    @DisplayName("Should mention missing skills in suggestions")
    void testMissingSkillMentioned() {
        Set<String> matchedSkills = new HashSet<>();
        Set<String> missingSkills = Set.of("Kubernetes");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 0.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.stream().anyMatch(s -> s.contains("Kubernetes")), 
                "Should mention Kubernetes in suggestions");
    }

    @Test
    @DisplayName("Should not include percentage threshold suggestions when no missing skills")
    void testNoMissingSkillsSuggestions() {
        Set<String> matchedSkills = Set.of("Java", "Spring", "SQL", "REST");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 100.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        // Should only have threshold-based suggestions, not missing skill suggestions
        long missingSuggestions = suggestions.stream()
                .filter(s -> s.toLowerCase().contains("add hands-on"))
                .count();
        
        assertEquals(0, missingSuggestions, "Should not have missing skill suggestions when all skills present");
    }

    @Test
    @DisplayName("Should suggest quantifiable results for high match")
    void testQuantifiableResultsSuggestion() {
        Set<String> matchedSkills = Set.of("Java", "Spring", "SQL", "REST");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 100.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("quantifiable")), 
                "Should suggest adding quantifiable results for high match");
    }

    @Test
    @DisplayName("Should suggest formatting for high match")
    void testFormattingSuggestion() {
        Set<String> matchedSkills = Set.of("Java", "Spring", "SQL");
        Set<String> missingSkills = new HashSet<>();
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 90.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("format") || s.toLowerCase().contains("alignment")), 
                "Should mention formatting or alignment for high match");
    }

    @Test
    @DisplayName("Should handle edge case: exactly 50% match")
    void testExactly50PercentMatch() {
        Set<String> matchedSkills = Set.of("Java", "SQL");
        Set<String> missingSkills = Set.of("Spring", "Docker");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 50.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        // At 50%, should get "improve" suggestions, not "major restructuring"
        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("improve")), 
                "Should suggest improvements at 50% match");
    }

    @Test
    @DisplayName("Should handle edge case: exactly 80% match")
    void testExactly80PercentMatch() {
        Set<String> matchedSkills = Set.of("Java", "SQL", "Spring", "REST");
        Set<String> missingSkills = Set.of("Docker");
        SkillMatcher.Result result = new SkillMatcher.Result(matchedSkills, missingSkills, 80.0);

        List<String> suggestions = suggestionEngine.generateSuggestions(result);

        assertTrue(suggestions.size() > 0, "Should generate suggestions at 80% match");
    }

}
