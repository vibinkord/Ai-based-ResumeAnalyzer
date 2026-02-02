package com.resumeanalyzer.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extracts technical skills from raw text using pattern matching and a configurable skill registry.
 * Uses the SkillRegistry to maintain an extensible, configurable list of known skills.
 * Case-insensitive and duplicate-safe via HashSet.
 */
public class SkillExtractor {
    
    private static final Logger log = LoggerFactory.getLogger(SkillExtractor.class);
    
    private final SkillRegistry skillRegistry;
    private final Map<String, String> normalizedToDisplay;

    /**
     * Creates a SkillExtractor using the default SkillRegistry.
     */
    public SkillExtractor() {
        this.skillRegistry = SkillRegistry.getInstance();
        this.normalizedToDisplay = skillRegistry.getNormalizedToDisplayMap();
        log.debug("SkillExtractor initialized with {} known skills", skillRegistry.getSkillCount());
    }

    /**
     * Extracts skills present in the given text.
     * 
     * @param rawText input text (resume or job description)
     * @return set of detected skill display names
     */
    public Set<String> extractSkills(String rawText) {
        Set<String> detected = new HashSet<>();
        
        if (rawText == null || rawText.isEmpty()) {
            log.debug("Raw text is null or empty, returning empty skill set");
            return detected;
        }
        
        log.debug("Starting skill extraction from text of {} characters", rawText.length());
        String normalizedText = normalizeText(rawText);

        // For each known skill (normalized), check presence via word-boundary match
        for (Map.Entry<String, String> entry : normalizedToDisplay.entrySet()) {
            String normalizedSkill = entry.getKey();
            String display = entry.getValue();
            
            if (containsWord(normalizedText, normalizedSkill)) {
                detected.add(display);
                log.debug("Detected skill: {}", display);
            }
        }
        
        log.info("Skill extraction completed: found {} skills", detected.size());
        return detected;
    }

    /**
     * Gets information about a specific skill.
     *
     * @param skillName the skill name
     * @return the skill information, or null if not found
     */
    public SkillRegistry.SkillInfo getSkillInfo(String skillName) {
        return skillRegistry.getSkillInfo(skillName);
    }

    /**
     * Gets all known skills in the registry.
     *
     * @return a set of all known skill names
     */
    public Set<String> getKnownSkills() {
        return skillRegistry.getAllSkills();
    }

    /**
     * Gets the number of known skills.
     *
     * @return the count of known skills
     */
    public int getSkillCount() {
        return skillRegistry.getSkillCount();
    }

    // --- Helpers ---

    /**
     * Normalizes the entire text: lowercase, remove special characters, compress whitespace.
     *
     * @param text the text to normalize
     * @return the normalized text
     */
    private String normalizeText(String text) {
        String lower = text.toLowerCase();
        String cleaned = lower.replaceAll("[^a-z0-9\\s]", " ");
        return cleaned.replaceAll("\\s+", " ").trim();
    }

    /**
     * Checks if the normalized text contains the skill token via token-based matching.
     * Ensures whole-word matching to avoid partial matches.
     *
     * @param normalizedText the normalized text to search in
     * @param normalizedSkill the normalized skill token to find
     * @return true if the skill is found as a whole word, false otherwise
     */
    private boolean containsWord(String normalizedText, String normalizedSkill) {
        if (normalizedSkill == null || normalizedSkill.isEmpty()) {
            return false;
        }
        
        // normalizedText is already compressed to single spaces; split into tokens
        String[] tokens = normalizedText.split(" ");
        for (String token : tokens) {
            if (token.equals(normalizedSkill)) {
                return true;
            }
        }
        return false;
    }
}
