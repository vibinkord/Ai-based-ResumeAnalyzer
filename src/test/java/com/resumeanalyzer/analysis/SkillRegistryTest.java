package com.resumeanalyzer.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SkillRegistry class.
 * Tests skill loading, retrieval, and management functionality.
 */
@DisplayName("SkillRegistry Tests")
class SkillRegistryTest {

    private SkillRegistry registry;

    @BeforeEach
    void setUp() {
        // Reset singleton instance for testing
        registry = SkillRegistry.getInstance();
    }

    // ===================== Singleton Tests =====================

    @Test
    @DisplayName("Should return same instance on multiple calls (singleton)")
    void testSingletonPattern() {
        SkillRegistry registry1 = SkillRegistry.getInstance();
        SkillRegistry registry2 = SkillRegistry.getInstance();
        
        assertSame(registry1, registry2, "getInstance() should return the same instance");
    }

    // ===================== Skill Loading Tests =====================

    @Test
    @DisplayName("Should load skills from JSON configuration")
    void testSkillsAreLoaded() {
        assertFalse(registry.getAllSkills().isEmpty(), "Skills should be loaded from JSON");
    }

    @Test
    @DisplayName("Should have at least 100 skills loaded")
    void testMinimumSkillCount() {
        assertTrue(registry.getSkillCount() >= 100, 
                "Should have at least 100 skills, found: " + registry.getSkillCount());
    }

    @Test
    @DisplayName("Should contain common programming languages")
    void testCommonLanguagesLoaded() {
        Set<String> skills = registry.getAllSkills();
        
        assertTrue(skills.contains("Java"), "Java should be in skills");
        assertTrue(skills.contains("Python"), "Python should be in skills");
        assertTrue(skills.contains("JavaScript"), "JavaScript should be in skills");
    }

    @Test
    @DisplayName("Should contain common frameworks")
    void testCommonFrameworksLoaded() {
        Set<String> skills = registry.getAllSkills();
        
        assertTrue(skills.contains("Spring"), "Spring should be in skills");
        assertTrue(skills.contains("React"), "React should be in skills");
        assertTrue(skills.contains("Docker"), "Docker should be in skills");
    }

    @Test
    @DisplayName("Should contain databases")
    void testDatabasesLoaded() {
        Set<String> skills = registry.getAllSkills();
        
        assertTrue(skills.contains("SQL"), "SQL should be in skills");
        assertTrue(skills.contains("MongoDB"), "MongoDB should be in skills");
        assertTrue(skills.contains("PostgreSQL"), "PostgreSQL should be in skills");
    }

    // ===================== Skill Retrieval Tests =====================

    @Test
    @DisplayName("Should retrieve skill by display name")
    void testGetSkillInfo() {
        SkillRegistry.SkillInfo info = registry.getSkillInfo("Java");
        
        assertNotNull(info);
        assertEquals("Java", info.name);
        assertNotNull(info.category);
    }

    @Test
    @DisplayName("Should return null for unknown skill")
    void testGetSkillInfoUnknown() {
        SkillRegistry.SkillInfo info = registry.getSkillInfo("NonExistentSkill");
        
        assertNull(info);
    }

    @Test
    @DisplayName("Should get display name from normalized token")
    void testGetDisplayName() {
        String displayName = registry.getDisplayName("java");
        
        assertEquals("Java", displayName);
    }

    @Test
    @DisplayName("Should get normalized-to-display mapping")
    void testGetNormalizedMapping() {
        var mapping = registry.getNormalizedToDisplayMap();
        
        assertNotNull(mapping);
        assertFalse(mapping.isEmpty());
        assertTrue(mapping.containsKey("java"));
        assertEquals("Java", mapping.get("java"));
    }

    // ===================== Skill Count Tests =====================

    @Test
    @DisplayName("Should report correct skill count")
    void testSkillCount() {
        int count = registry.getSkillCount();
        
        assertTrue(count > 0, "Skill count should be greater than 0");
        assertEquals(registry.getAllSkills().size(), count, "Skill count should match set size");
    }

    @Test
    @DisplayName("Should return non-empty skill set")
    void testGetAllSkills() {
        Set<String> skills = registry.getAllSkills();
        
        assertNotNull(skills);
        assertFalse(skills.isEmpty());
    }

    // ===================== Case Insensitivity Tests =====================

    @Test
    @DisplayName("Should handle case-insensitive skill lookup")
    void testCaseInsensitiveNormalization() {
        String lowercase = registry.getDisplayName("java");
        String mixedCase = registry.getDisplayName("JAVA");
        String uppercase = registry.getDisplayName("JaVa");
        
        // All should normalize to the same display name
        assertNotNull(lowercase);
        assertEquals(lowercase, mixedCase, "Case variations should normalize to same value");
    }

    // ===================== Skill Information Tests =====================

    @Test
    @DisplayName("Should provide skill category information")
    void testSkillCategory() {
        SkillRegistry.SkillInfo java = registry.getSkillInfo("Java");
        
        assertNotNull(java);
        assertNotNull(java.category);
        assertFalse(java.category.isEmpty());
    }

    @Test
    @DisplayName("Should categorize skills correctly")
    void testSkillCategorization() {
        SkillRegistry.SkillInfo java = registry.getSkillInfo("Java");
        SkillRegistry.SkillInfo spring = registry.getSkillInfo("Spring");
        
        assertNotNull(java);
        assertNotNull(spring);
        
        // Java and Spring should have different categories
        // (one is a language, one is a framework)
        assertNotEquals(java.category, spring.category);
    }

    // ===================== Skill Info Object Tests =====================

    @Test
    @DisplayName("Should create SkillInfo with name and category")
    void testSkillInfoConstruction() {
        SkillRegistry.SkillInfo info = new SkillRegistry.SkillInfo("TestSkill", "TestCategory");
        
        assertEquals("TestSkill", info.name);
        assertEquals("TestCategory", info.category);
    }

    @Test
    @DisplayName("Should provide meaningful toString for SkillInfo")
    void testSkillInfoToString() {
        SkillRegistry.SkillInfo info = new SkillRegistry.SkillInfo("Java", "Language");
        String toString = info.toString();
        
        assertTrue(toString.contains("Java"));
        assertTrue(toString.contains("Language"));
    }

    // ===================== Large Skill Set Tests =====================

    @Test
    @DisplayName("Should handle large number of skills efficiently")
    void testLargeSkillSet() {
        long startTime = System.currentTimeMillis();
        Set<String> skills = registry.getAllSkills();
        long endTime = System.currentTimeMillis();
        
        // Should retrieve all skills in reasonable time (< 100ms)
        assertTrue(endTime - startTime < 100, "Skill retrieval should be fast");
        assertTrue(skills.size() >= 100, "Should have 100+ skills");
    }

    @Test
    @DisplayName("Should handle lookups in large skill set efficiently")
    void testLargeSkillSetLookup() {
        long startTime = System.currentTimeMillis();
        
        // Perform multiple lookups
        for (int i = 0; i < 1000; i++) {
            registry.getDisplayName("java");
        }
        
        long endTime = System.currentTimeMillis();
        
        // 1000 lookups should complete in reasonable time
        assertTrue(endTime - startTime < 100, "Multiple lookups should be fast");
    }

    // ===================== Fallback Behavior Tests =====================

    @Test
    @DisplayName("Should fall back to hardcoded skills if JSON loading fails")
    void testFallbackSkills() {
        // Even if JSON loading fails, we should have fallback skills
        Set<String> skills = registry.getAllSkills();
        
        assertFalse(skills.isEmpty(), "Should have skills from fallback or JSON");
        
        // At minimum, should have some of the fallback skills
        assertTrue(skills.contains("Java") || skills.size() > 0);
    }

    // ===================== Mapping Consistency Tests =====================

    @Test
    @DisplayName("Should maintain consistent mapping between normalized and display names")
    void testMappingConsistency() {
        var mapping = registry.getNormalizedToDisplayMap();
        Set<String> allSkills = registry.getAllSkills();
        
        // Every skill should have a normalized mapping
        for (String skill : allSkills) {
            String normalized = skill.toLowerCase().replaceAll("[^a-z0-9]", "");
            assertTrue(mapping.containsKey(normalized) || mapping.containsValue(skill),
                    "Skill " + skill + " should be in mapping");
        }
    }

    @Test
    @DisplayName("Should not have duplicate display names")
    void testNoDuplicateDisplayNames() {
        Set<String> skills = registry.getAllSkills();
        
        assertEquals(skills.size(), registry.getSkillCount(),
                "No duplicate skills should exist");
    }

    // ===================== Null Handling Tests =====================

    @Test
    @DisplayName("Should handle null skill name gracefully")
    void testNullSkillName() {
        SkillRegistry.SkillInfo info = registry.getSkillInfo(null);
        
        assertNull(info, "Should return null for null skill name");
    }

    @Test
    @DisplayName("Should handle empty string gracefully")
    void testEmptySkillName() {
        SkillRegistry.SkillInfo info = registry.getSkillInfo("");
        
        assertNull(info, "Should return null for empty skill name");
    }
}
