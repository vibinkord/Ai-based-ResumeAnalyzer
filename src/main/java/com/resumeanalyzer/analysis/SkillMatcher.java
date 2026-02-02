package com.resumeanalyzer.analysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Matches resume skills against job description skills and computes a simple score.
 * Core Java only: uses HashSet for intersection and difference, no I/O.
 */
public class SkillMatcher {

    /**
     * Immutable result of skill matching.
     */
    public static final class Result {
        private final Set<String> matchedSkills;
        private final Set<String> missingSkills;
        private final double matchPercentage;

        public Result(Set<String> matchedSkills, Set<String> missingSkills, double matchPercentage) {
            this.matchedSkills = Collections.unmodifiableSet(matchedSkills);
            this.missingSkills = Collections.unmodifiableSet(missingSkills);
            this.matchPercentage = matchPercentage;
        }

        /** Returns the intersection: skills present in both resume and job. */
        public Set<String> getMatchedSkills() { return matchedSkills; }
        /** Returns the difference: job skills not present in resume. */
        public Set<String> getMissingSkills() { return missingSkills; }
        /** Returns percentage: matched / total job skills * 100.0 (0 if job is empty). */
        public double getMatchPercentage() { return matchPercentage; }
    }

    /**
     * Computes matched and missing skills plus a simple percentage score.
     * @param resumeSkills skills extracted from the resume (may be null)
     * @param jobSkills skills extracted from the job description (may be null)
     * @return immutable Result with matched, missing and matchPercentage
     */
    public Result match(Set<String> resumeSkills, Set<String> jobSkills) {
        Set<String> resume = resumeSkills != null ? new HashSet<>(resumeSkills) : new HashSet<>();
        Set<String> job = jobSkills != null ? new HashSet<>(jobSkills) : new HashSet<>();

        // Intersection for matched
        Set<String> matched = new HashSet<>(resume);
        matched.retainAll(job);

        // Difference for missing (job - resume)
        Set<String> missing = new HashSet<>(job);
        missing.removeAll(resume);

        // Percentage calculation with safe edge-case handling
        double percentage = 0.0;
        int total = job.size();
        if (total > 0) {
            percentage = (matched.size() * 100.0) / total;
        }

        return new Result(matched, missing, percentage);
    }
}
