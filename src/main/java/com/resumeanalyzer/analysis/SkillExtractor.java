package com.resumeanalyzer.analysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extracts technical skills from raw text using simple, efficient heuristics.
 * Core Java only, case-insensitive, and duplicate-safe via HashSet.
 */
public class SkillExtractor {
    private final Set<String> knownSkillsDisplay;          // Canonical display names (e.g., "Java", "SQL")
    private final Map<String, String> normalizedToDisplay; // normalized token -> display name

    /**
     * Creates a SkillExtractor with a predefined, extensible skill list.
     */
    public SkillExtractor() {
        this.knownSkillsDisplay = new HashSet<>(Arrays.asList(
                "Java",
                "SQL",
                "HTML",
                "CSS",
                "JavaScript",
                "Git",
                "OOP",
                "REST",
                "Spring",
                "Python",
                "JSON",
                "NIO",
                "ArrayList",
                "HashMap",
                "HashSet",
                "ExecutorService",
                "Multithreading"
        ));
        this.normalizedToDisplay = buildNormalizedIndex(knownSkillsDisplay);
    }

    /**
     * Extracts skills present in the given text.
     * @param rawText input text (resume or job description)
     * @return set of detected skill display names
     */
    public Set<String> extractSkills(String rawText) {
        Set<String> detected = new HashSet<>();
        if (rawText == null || rawText.isEmpty()) {
            return detected;
        }
        String normalizedText = normalizeText(rawText);

        // For each known skill (normalized), check presence via word-boundary match
        for (Map.Entry<String, String> entry : normalizedToDisplay.entrySet()) {
            String normalizedSkill = entry.getKey();
            String display = entry.getValue();
            if (containsWord(normalizedText, normalizedSkill)) {
                detected.add(display);
            }
        }
        return detected;
    }

    // --- Helpers ---

    private Map<String, String> buildNormalizedIndex(Set<String> displayNames) {
        Map<String, String> index = new HashMap<>();
        for (String display : displayNames) {
            String norm = normalizeToken(display);
            index.put(norm, display);
        }
        return index;
    }

    /**
     * Normalizes the entire text: lowercase, remove special characters, compress whitespace.
     */
    private String normalizeText(String text) {
        String lower = text.toLowerCase();
        String cleaned = lower.replaceAll("[^a-z0-9\\s]", " ");
        return cleaned.replaceAll("\\s+", " ").trim();
    }

    /**
     * Normalizes a single token (skill name) to align with text normalization.
     */
    private String normalizeToken(String token) {
        String lower = token.toLowerCase();
        return lower.replaceAll("[^a-z0-9]", "");
    }

    /**
     * Checks if the normalized text contains the skill token via token-based matching.
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
