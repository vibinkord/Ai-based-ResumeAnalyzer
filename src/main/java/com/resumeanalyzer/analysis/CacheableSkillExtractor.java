package com.resumeanalyzer.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Cacheable wrapper around SkillExtractor.
 * 
 * This service provides caching capabilities for skill extraction operations.
 * Identical resume/job description text will return cached results,
 * significantly improving performance for repeated analyses.
 */
@Service
public class CacheableSkillExtractor {
    private static final Logger log = LoggerFactory.getLogger(CacheableSkillExtractor.class);
    
    private final SkillExtractor skillExtractor;

    public CacheableSkillExtractor() {
        this.skillExtractor = new SkillExtractor();
        log.info("CacheableSkillExtractor initialized with {} known skills", 
                 skillExtractor.getSkillCount());
    }

    /**
     * Extracts skills with caching.
     * Results are cached based on the hash of the input text.
     */
    @Cacheable(
        value = "skills",
        key = "#text.hashCode()",
        unless = "#result == null || #result.isEmpty()"
    )
    public Set<String> extractSkills(String text) {
        log.debug("Extracting skills from text ({}B)", text.length());
        return skillExtractor.extractSkills(text);
    }

    /**
     * Get skill information (not cached, fast operation).
     */
    public SkillRegistry.SkillInfo getSkillInfo(String skillName) {
        return skillExtractor.getSkillInfo(skillName);
    }

    /**
     * Get all known skills (cached separately).
     */
    @Cacheable(
        value = "all-skills",
        key = "'all'",
        unless = "#result == null || #result.isEmpty()"
    )
    public Set<String> getKnownSkills() {
        return skillExtractor.getKnownSkills();
    }

    /**
     * Get skill count (cached).
     */
    @Cacheable(
        value = "skill-count",
        key = "'count'"
    )
    public int getSkillCount() {
        return skillExtractor.getSkillCount();
    }
}
