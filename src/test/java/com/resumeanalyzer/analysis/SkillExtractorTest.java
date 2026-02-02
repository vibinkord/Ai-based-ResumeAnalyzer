package com.resumeanalyzer.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SkillExtractor class.
 * Tests skill extraction from text with various edge cases and scenarios.
 */
@DisplayName("SkillExtractor Tests")
class SkillExtractorTest {

    private SkillExtractor skillExtractor;

    @BeforeEach
    void setUp() {
        skillExtractor = new SkillExtractor();
    }

    @Test
    @DisplayName("Should extract skills from text with multiple skills")
    void testExtractMultipleSkills() {
        String text = "I am a Java developer with experience in SQL and REST API development";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("Java"), "Should extract Java skill");
        assertTrue(skills.contains("SQL"), "Should extract SQL skill");
        assertTrue(skills.contains("REST"), "Should extract REST skill");
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void testExtractSkillsFromNullInput() {
        Set<String> skills = skillExtractor.extractSkills(null);
        
        assertNotNull(skills, "Should return empty set, not null");
        assertTrue(skills.isEmpty(), "Should return empty set for null input");
    }

    @Test
    @DisplayName("Should handle empty string input")
    void testExtractSkillsFromEmptyString() {
        Set<String> skills = skillExtractor.extractSkills("");
        
        assertNotNull(skills, "Should return empty set");
        assertTrue(skills.isEmpty(), "Should return empty set for empty string");
    }

    @Test
    @DisplayName("Should be case insensitive")
    void testCaseInsensitiveExtraction() {
        String textLowerCase = "java sql rest";
        String textUpperCase = "JAVA SQL REST";
        String textMixedCase = "Java SQL Rest";

        Set<String> skillsLower = skillExtractor.extractSkills(textLowerCase);
        Set<String> skillsUpper = skillExtractor.extractSkills(textUpperCase);
        Set<String> skillsMixed = skillExtractor.extractSkills(textMixedCase);

        assertEquals(skillsLower, skillsUpper, "Should treat lowercase and uppercase equally");
        assertEquals(skillsUpper, skillsMixed, "Should treat mixed case equally");
    }

    @Test
    @DisplayName("Should handle special characters in text")
    void testHandleSpecialCharacters() {
        String text = "Java@2024, SQL-Database, REST/API, JSON-Processing";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("Java"), "Should extract Java despite special chars");
        assertTrue(skills.contains("SQL"), "Should extract SQL despite special chars");
        assertTrue(skills.contains("REST"), "Should extract REST despite special chars");
        assertTrue(skills.contains("JSON"), "Should extract JSON despite special chars");
    }

    @Test
    @DisplayName("Should extract from single skill text")
    void testExtractSingleSkill() {
        String text = "Python programming language";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("Python"), "Should extract Python");
        assertEquals(1, skills.size(), "Should only extract one skill");
    }

    @Test
    @DisplayName("Should not extract skills not in known list")
    void testDoesNotExtractUnknownSkills() {
        String text = "I know COBOL and FORTRAN programming";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertFalse(skills.contains("COBOL"), "Should not extract unknown skill COBOL");
        assertFalse(skills.contains("FORTRAN"), "Should not extract unknown skill FORTRAN");
    }

    @Test
    @DisplayName("Should extract OOP as skill")
    void testExtractOOP() {
        String text = "Strong understanding of OOP principles";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("OOP"), "Should extract OOP");
    }

    @Test
    @DisplayName("Should extract Git as skill")
    void testExtractGit() {
        String text = "Version control with Git and GitHub";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("Git"), "Should extract Git");
    }

    @Test
    @DisplayName("Should extract Spring as skill")
    void testExtractSpring() {
        String text = "Experience with Spring Framework";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("Spring"), "Should extract Spring");
    }

    @Test
    @DisplayName("Should extract Multithreading as skill")
    void testExtractMultithreading() {
        String text = "Proficient in Multithreading and concurrent programming";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("Multithreading"), "Should extract Multithreading");
    }

    @Test
    @DisplayName("Should extract ExecutorService as skill")
    void testExtractExecutorService() {
        String text = "Using ExecutorService for thread pool management";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("ExecutorService"), "Should extract ExecutorService");
    }

    @Test
    @DisplayName("Should extract NIO as skill")
    void testExtractNIO() {
        String text = "File I/O using NIO for efficient processing";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("NIO"), "Should extract NIO");
    }

    @Test
    @DisplayName("Should extract collection types as skills")
    void testExtractCollectionTypes() {
        String text = "Data structures: ArrayList, HashMap, HashSet";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("ArrayList"), "Should extract ArrayList");
        assertTrue(skills.contains("HashMap"), "Should extract HashMap");
        assertTrue(skills.contains("HashSet"), "Should extract HashSet");
    }

    @Test
    @DisplayName("Should return a set that contains extracted skills")
    void testReturnedSetContainsSkills() {
        String text = "Java and SQL";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertNotNull(skills, "Returned set should not be null");
        assertTrue(skills.size() > 0, "Returned set should contain skills");
    }

    @Test
    @DisplayName("Should handle text with extra whitespace")
    void testHandleExtraWhitespace() {
        String text = "Java     SQL     REST     API";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("Java"), "Should extract Java");
        assertTrue(skills.contains("SQL"), "Should extract SQL");
        assertTrue(skills.contains("REST"), "Should extract REST");
    }

    @Test
    @DisplayName("Should extract multiple skill instances only once")
    void testNoDuplicateSkills() {
        String text = "Java developer using Java with Java frameworks";
        Set<String> skills = skillExtractor.extractSkills(text);

        long javaCount = skills.stream().filter(s -> s.equals("Java")).count();
        assertEquals(1, javaCount, "Should not have duplicate Java in set");
    }

    @Test
    @DisplayName("Should extract JavaScript as skill")
    void testExtractJavaScript() {
        String text = "Frontend development with JavaScript and HTML";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("JavaScript"), "Should extract JavaScript");
    }

    @Test
    @DisplayName("Should extract CSS as skill")
    void testExtractCSS() {
        String text = "Styling with CSS and Bootstrap";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("CSS"), "Should extract CSS");
    }

    @Test
    @DisplayName("Should extract HTML as skill")
    void testExtractHTML() {
        String text = "HTML markup and semantic elements";
        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("HTML"), "Should extract HTML");
    }

    @Test
    @DisplayName("Should extract complex resume text")
    void testExtractFromComplexResume() {
        String text = "Senior Java Developer with 5+ years experience. " +
                "Skilled in Spring Boot, REST APIs, SQL databases, and OOP design patterns. " +
                "Proficient in Git version control, JSON processing, and multithreading. " +
                "Collections like ArrayList and HashMap. ExecutorService for concurrency.";

        Set<String> skills = skillExtractor.extractSkills(text);

        assertTrue(skills.contains("Java"), "Should extract Java");
        assertTrue(skills.contains("Spring"), "Should extract Spring");
        assertTrue(skills.contains("REST"), "Should extract REST");
        assertTrue(skills.contains("SQL"), "Should extract SQL");
        assertTrue(skills.contains("OOP"), "Should extract OOP");
        assertTrue(skills.contains("Git"), "Should extract Git");
        assertTrue(skills.contains("JSON"), "Should extract JSON");
        assertTrue(skills.contains("Multithreading"), "Should extract Multithreading");
        assertTrue(skills.contains("ArrayList"), "Should extract ArrayList");
        assertTrue(skills.contains("HashMap"), "Should extract HashMap");
        assertTrue(skills.contains("ExecutorService"), "Should extract ExecutorService");
    }

}
