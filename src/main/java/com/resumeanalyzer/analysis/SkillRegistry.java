package com.resumeanalyzer.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Registry for managing technical skills.
 * Loads skills from a configuration source (JSON file) and provides access to skill information.
 * 
 * This class uses a lazy singleton pattern to ensure the skill data is only loaded once.
 */
public class SkillRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(SkillRegistry.class);
    private static final String SKILLS_FILE = "/skills.json";
    
    private static volatile SkillRegistry instance;
    private final Set<String> displayNames;
    private final Map<String, String> normalizedToDisplay;
    private final Map<String, SkillInfo> skillDetails;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private SkillRegistry() {
        this.displayNames = new HashSet<>();
        this.normalizedToDisplay = new HashMap<>();
        this.skillDetails = new HashMap<>();
        loadSkills();
    }

    /**
     * Gets the singleton instance of SkillRegistry.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * @return the SkillRegistry instance
     */
    public static SkillRegistry getInstance() {
        if (instance == null) {
            synchronized (SkillRegistry.class) {
                if (instance == null) {
                    instance = new SkillRegistry();
                }
            }
        }
        return instance;
    }

    /**
     * Loads skills from the JSON configuration file.
     * Falls back to hardcoded skills if file cannot be loaded.
     */
    private void loadSkills() {
        try {
            loadSkillsFromJson();
            log.info("Successfully loaded {} skills from JSON configuration", displayNames.size());
        } catch (Exception e) {
            log.warn("Failed to load skills from JSON file, using fallback skill set", e);
            loadFallbackSkills();
        }
    }

    /**
     * Loads skills from the skills.json resource file.
     * 
     * @throws Exception if the file cannot be read or parsed
     */
    private void loadSkillsFromJson() throws Exception {
        log.debug("Loading skills from JSON resource file: {}", SKILLS_FILE);
        
        try (InputStream inputStream = getClass().getResourceAsStream(SKILLS_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Skills JSON file not found: " + SKILLS_FILE);
            }
            
            // Parse JSON manually (without external library dependency)
            String content = new String(inputStream.readAllBytes());
            parseJsonSkills(content);
        }
    }

    /**
     * Parses the JSON content and extracts skill information.
     * Uses simple string parsing to avoid external JSON library dependency.
     *
     * @param jsonContent the JSON content string
     */
    private void parseJsonSkills(String jsonContent) {
        log.debug("Parsing skills from JSON content");
        
        // Simple parsing: find all "name": "..." entries
        String skillsArray = jsonContent.substring(jsonContent.indexOf("["), jsonContent.lastIndexOf("]") + 1);
        
        int start = 0;
        while ((start = skillsArray.indexOf("\"name\": \"", start)) != -1) {
            start += "\"name\": \"".length();
            int end = skillsArray.indexOf("\"", start);
            String skillName = skillsArray.substring(start, end);
            
            // Also parse category if available
            String category = parseField(skillsArray, start, "category");
            
            addSkill(skillName, category != null ? category : "Uncategorized");
            start = end;
        }
        
        if (displayNames.isEmpty()) {
            log.warn("No skills parsed from JSON content, using fallback skills");
            loadFallbackSkills();
        }
    }

    /**
     * Helper method to parse a field value from JSON string content.
     *
     * @param content the JSON string content
     * @param startPos the position to start searching from
     * @param fieldName the field name to find
     * @return the field value or null if not found
     */
    private String parseField(String content, int startPos, String fieldName) {
        int fieldPos = content.indexOf("\"" + fieldName + "\": \"", startPos);
        if (fieldPos == -1 || fieldPos > startPos + 200) { // Arbitrary limit to stay in current object
            return null;
        }
        
        int valueStart = fieldPos + ("\"" + fieldName + "\": \"").length();
        int valueEnd = content.indexOf("\"", valueStart);
        
        if (valueEnd == -1) {
            return null;
        }
        
        return content.substring(valueStart, valueEnd);
    }

    /**
     * Adds a skill to the registry.
     *
     * @param skillName the display name of the skill
     * @param category the category of the skill
     */
    private void addSkill(String skillName, String category) {
        if (skillName == null || skillName.trim().isEmpty()) {
            return;
        }
        
        String trimmed = skillName.trim();
        displayNames.add(trimmed);
        String normalized = normalizeToken(trimmed);
        normalizedToDisplay.put(normalized, trimmed);
        skillDetails.put(trimmed, new SkillInfo(trimmed, category));
    }

    /**
     * Loads hardcoded fallback skills.
     * Used when JSON file cannot be loaded.
     */
    private void loadFallbackSkills() {
        log.debug("Loading fallback skills");
        
        String[] fallbackSkills = {
                "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust",
                "SQL", "MongoDB", "PostgreSQL", "MySQL", "Redis",
                "Spring", "Spring Boot", "Hibernate", "React", "Angular", "Node.js", "Express",
                "HTML", "CSS", "REST", "GraphQL", "JSON", "XML",
                "Docker", "Kubernetes", "AWS", "Azure", "GCP",
                "Git", "GitHub", "GitLab", "Maven", "Gradle",
                "JUnit", "Mockito", "Jest", "Pytest",
                "Agile", "Scrum", "TDD", "Linux", "Windows",
                "OOP", "Microservices", "Design Patterns", "SOLID"
        };
        
        for (String skill : fallbackSkills) {
            addSkill(skill, "General");
        }
    }

    /**
     * Gets the display name for a normalized skill token.
     * Automatically normalizes the input token.
     *
     * @param token the skill token (will be normalized)
     * @return the display name, or null if not found
     */
    public String getDisplayName(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String normalized = normalizeToken(token);
        return normalizedToDisplay.get(normalized);
    }

    /**
     * Gets information about a skill.
     *
     * @param skillName the skill name (display name)
     * @return the skill information, or null if not found
     */
    public SkillInfo getSkillInfo(String skillName) {
        return skillDetails.get(skillName);
    }

    /**
     * Gets all known skill display names.
     *
     * @return a set of all known skill names
     */
    public Set<String> getAllSkills() {
        return new HashSet<>(displayNames);
    }

    /**
     * Gets the normalized-to-display mapping.
     *
     * @return a map of normalized tokens to display names
     */
    public Map<String, String> getNormalizedToDisplayMap() {
        return new HashMap<>(normalizedToDisplay);
    }

    /**
     * Gets the count of known skills.
     *
     * @return the number of known skills
     */
    public int getSkillCount() {
        return displayNames.size();
    }

    /**
     * Normalizes a single token (skill name) for comparison.
     *
     * @param token the token to normalize
     * @return the normalized token
     */
    private String normalizeToken(String token) {
        if (token == null) {
            return "";
        }
        String lower = token.toLowerCase();
        return lower.replaceAll("[^a-z0-9]", "");
    }

    /**
     * Represents detailed information about a skill.
     */
    public static class SkillInfo {
        public final String name;
        public final String category;

        /**
         * Creates a SkillInfo instance.
         *
         * @param name the skill name
         * @param category the skill category
         */
        public SkillInfo(String name, String category) {
            this.name = name;
            this.category = category;
        }

        @Override
        public String toString() {
            return String.format("SkillInfo{name='%s', category='%s'}", name, category);
        }
    }
}
