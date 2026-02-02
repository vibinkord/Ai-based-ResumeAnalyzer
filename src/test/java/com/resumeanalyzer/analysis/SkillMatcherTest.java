package com.resumeanalyzer.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SkillMatcher class.
 * Tests skill matching and percentage calculation logic.
 */
@DisplayName("SkillMatcher Tests")
class SkillMatcherTest {

    private SkillMatcher skillMatcher;

    @BeforeEach
    void setUp() {
        skillMatcher = new SkillMatcher();
    }

    @Test
    @DisplayName("Should calculate 100% match when all skills match")
    void testFullMatch() {
        Set<String> resumeSkills = Set.of("Java", "SQL", "REST");
        Set<String> jobSkills = Set.of("Java", "SQL", "REST");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertEquals(100.0, result.getMatchPercentage(), 0.01, "Should have 100% match");
        assertEquals(3, result.getMatchedSkills().size(), "All skills should be matched");
        assertEquals(0, result.getMissingSkills().size(), "No skills should be missing");
    }

    @Test
    @DisplayName("Should calculate 0% match when no skills match")
    void testNoMatch() {
        Set<String> resumeSkills = Set.of("Python", "Django", "PostgreSQL");
        Set<String> jobSkills = Set.of("Java", "Spring", "MySQL");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertEquals(0.0, result.getMatchPercentage(), 0.01, "Should have 0% match");
        assertEquals(0, result.getMatchedSkills().size(), "No skills should be matched");
        assertEquals(3, result.getMissingSkills().size(), "All job skills should be missing");
    }

    @Test
    @DisplayName("Should calculate 50% match for partial match")
    void testPartialMatch() {
        Set<String> resumeSkills = Set.of("Java", "SQL");
        Set<String> jobSkills = Set.of("Java", "SQL", "Spring", "Docker");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertEquals(50.0, result.getMatchPercentage(), 0.01, "Should have 50% match");
        assertEquals(2, result.getMatchedSkills().size(), "Should have 2 matched skills");
        assertEquals(2, result.getMissingSkills().size(), "Should have 2 missing skills");
    }

    @Test
    @DisplayName("Should handle null resume skills")
    void testNullResumeSkills() {
        Set<String> jobSkills = Set.of("Java", "SQL");

        SkillMatcher.Result result = skillMatcher.match(null, jobSkills);

        assertEquals(0.0, result.getMatchPercentage(), 0.01, "Should have 0% match");
        assertEquals(0, result.getMatchedSkills().size(), "No matched skills");
        assertEquals(2, result.getMissingSkills().size(), "All job skills missing");
    }

    @Test
    @DisplayName("Should handle null job skills")
    void testNullJobSkills() {
        Set<String> resumeSkills = Set.of("Java", "SQL");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, null);

        assertEquals(0.0, result.getMatchPercentage(), 0.01, "Should have 0% match");
        assertEquals(0, result.getMatchedSkills().size(), "No matched skills");
        assertEquals(0, result.getMissingSkills().size(), "No missing skills");
    }

    @Test
    @DisplayName("Should handle both null skills")
    void testBothNullSkills() {
        SkillMatcher.Result result = skillMatcher.match(null, null);

        assertEquals(0.0, result.getMatchPercentage(), 0.01, "Should have 0% match");
        assertEquals(0, result.getMatchedSkills().size(), "No matched skills");
        assertEquals(0, result.getMissingSkills().size(), "No missing skills");
    }

    @Test
    @DisplayName("Should handle empty skill sets")
    void testEmptySkillSets() {
        Set<String> emptySet = new HashSet<>();

        SkillMatcher.Result result = skillMatcher.match(emptySet, emptySet);

        assertEquals(0.0, result.getMatchPercentage(), 0.01, "Should have 0% match");
        assertEquals(0, result.getMatchedSkills().size(), "No matched skills");
        assertEquals(0, result.getMissingSkills().size(), "No missing skills");
    }

    @Test
    @DisplayName("Should handle empty resume with non-empty job skills")
    void testEmptyResumeSkills() {
        Set<String> resumeSkills = new HashSet<>();
        Set<String> jobSkills = Set.of("Java", "SQL", "Spring");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertEquals(0.0, result.getMatchPercentage(), 0.01, "Should have 0% match");
        assertEquals(0, result.getMatchedSkills().size(), "No matched skills");
        assertEquals(3, result.getMissingSkills().size(), "All job skills missing");
    }

    @Test
    @DisplayName("Should handle more resume skills than job skills")
    void testMoreResumeSkillsThanJobSkills() {
        Set<String> resumeSkills = Set.of("Java", "SQL", "Spring", "Docker", "Kubernetes", "Python");
        Set<String> jobSkills = Set.of("Java", "SQL");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertEquals(100.0, result.getMatchPercentage(), 0.01, "Should have 100% match");
        assertEquals(2, result.getMatchedSkills().size(), "Both job skills matched");
        assertEquals(0, result.getMissingSkills().size(), "No missing skills");
    }

    @Test
    @DisplayName("Should calculate partial match correctly")
    void testTwoThirdsMatch() {
        Set<String> resumeSkills = Set.of("Java", "SQL", "Spring");
        Set<String> jobSkills = Set.of("Java", "SQL", "Spring", "Docker", "Kubernetes");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        // 3 matched out of 5 required = 60%
        assertTrue(result.getMatchPercentage() >= 59 && result.getMatchPercentage() <= 61,
                "Should have approximately 60% match");
        assertEquals(3, result.getMatchedSkills().size(), "Should have 3 matched skills");
        assertEquals(2, result.getMissingSkills().size(), "Should have 2 missing skills");
    }

    @Test
    @DisplayName("Should return immutable sets")
    void testReturnedSetsAreImmutable() {
        Set<String> resumeSkills = Set.of("Java", "SQL");
        Set<String> jobSkills = Set.of("Java", "SQL", "Spring");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertThrows(UnsupportedOperationException.class, () -> result.getMatchedSkills().add("Python"),
                "Matched skills set should be immutable");
        assertThrows(UnsupportedOperationException.class, () -> result.getMissingSkills().add("Python"),
                "Missing skills set should be immutable");
    }

    @Test
    @DisplayName("Should correctly identify matched skills")
    void testCorrectMatchedSkillsIdentification() {
        Set<String> resumeSkills = Set.of("Java", "SQL", "Python");
        Set<String> jobSkills = Set.of("Java", "Spring", "Docker");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertTrue(result.getMatchedSkills().contains("Java"), "Java should be matched");
        assertFalse(result.getMatchedSkills().contains("SQL"), "SQL should not be matched");
        assertFalse(result.getMatchedSkills().contains("Python"), "Python should not be matched");
    }

    @Test
    @DisplayName("Should correctly identify missing skills")
    void testCorrectMissingSkillsIdentification() {
        Set<String> resumeSkills = Set.of("Java", "SQL");
        Set<String> jobSkills = Set.of("Java", "Spring", "Docker");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertTrue(result.getMissingSkills().contains("Spring"), "Spring should be missing");
        assertTrue(result.getMissingSkills().contains("Docker"), "Docker should be missing");
        assertFalse(result.getMissingSkills().contains("Java"), "Java should not be missing");
        assertFalse(result.getMissingSkills().contains("SQL"), "SQL should not be missing");
    }

    @Test
    @DisplayName("Should calculate correct percentage with single skill")
    void testSingleSkillMatching() {
        Set<String> resumeSkills = Set.of("Java");
        Set<String> jobSkills = Set.of("Java");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertEquals(100.0, result.getMatchPercentage(), 0.01, "Should have 100% match");
    }

    @Test
    @DisplayName("Should handle large skill sets")
    void testLargeSkillSets() {
        Set<String> resumeSkills = new HashSet<>();
        Set<String> jobSkills = new HashSet<>();

        // Add 30 common skills and 20 unique job skills
        for (int i = 0; i < 30; i++) {
            resumeSkills.add("Skill" + i);
            jobSkills.add("Skill" + i);
        }
        // Add 20 unique job skills
        for (int i = 30; i < 50; i++) {
            jobSkills.add("Skill" + i);
        }

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertEquals(30, result.getMatchedSkills().size(), "Should have 30 matched skills");
        assertEquals(20, result.getMissingSkills().size(), "Should have 20 missing skills");
        assertTrue(result.getMatchPercentage() > 59 && result.getMatchPercentage() < 61,
                "Should have approximately 60% match");
    }

    @Test
    @DisplayName("Result object should not be null")
    void testResultNotNull() {
        Set<String> resumeSkills = Set.of("Java");
        Set<String> jobSkills = Set.of("Java", "SQL");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getMatchedSkills(), "Matched skills should not be null");
        assertNotNull(result.getMissingSkills(), "Missing skills should not be null");
    }

    @Test
    @DisplayName("Should handle case-sensitive skill names")
    void testCaseSensitiveSkillMatching() {
        Set<String> resumeSkills = Set.of("java", "sql");
        Set<String> jobSkills = Set.of("Java", "SQL");

        SkillMatcher.Result result = skillMatcher.match(resumeSkills, jobSkills);

        // Assuming exact match (case-sensitive)
        assertEquals(0.0, result.getMatchPercentage(), 0.01, "Should have 0% match with different cases");
    }

}
