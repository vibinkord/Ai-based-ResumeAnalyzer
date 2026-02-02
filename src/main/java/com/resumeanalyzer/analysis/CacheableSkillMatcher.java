package com.resumeanalyzer.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Cacheable wrapper around SkillMatcher.
 * 
 * Caches skill matching results to improve performance when comparing
 * the same resume and job description multiple times.
 */
@Service
public class CacheableSkillMatcher {
    private static final Logger log = LoggerFactory.getLogger(CacheableSkillMatcher.class);
    
    private final SkillMatcher skillMatcher;

    public CacheableSkillMatcher() {
        this.skillMatcher = new SkillMatcher();
        log.info("CacheableSkillMatcher initialized");
    }

    /**
     * Matches skills with caching based on skill sets.
     * Results are cached using a hash of the skill sets.
     */
    @Cacheable(
        value = "skill-matches",
        key = "#resumeSkills.hashCode() + '-' + #jobSkills.hashCode()",
        unless = "#result == null"
    )
    public SkillMatcher.Result match(Set<String> resumeSkills, Set<String> jobSkills) {
        log.debug("Matching {} resume skills against {} job skills",
                 resumeSkills != null ? resumeSkills.size() : 0,
                 jobSkills != null ? jobSkills.size() : 0);
        return skillMatcher.match(resumeSkills, jobSkills);
    }
}
