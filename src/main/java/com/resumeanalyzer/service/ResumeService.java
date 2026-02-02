package com.resumeanalyzer.service;

import com.resumeanalyzer.model.entity.Resume;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ResumeService {
    private final ResumeRepository resumeRepository;

    /**
     * Create and save a new resume
     *
     * @param user     the user who owns the resume
     * @param filename the resume filename
     * @param content  the resume content (text)
     * @return the saved resume
     */
    @Transactional
    public Resume saveResume(User user, String filename, String content) {
        Resume resume = new Resume(user, filename, content);
        Resume savedResume = resumeRepository.save(resume);
        log.info("Saved resume '{}' for user ID: {}", filename, user.getId());
        return savedResume;
    }

    /**
     * Get resume by ID
     *
     * @param resumeId the resume's ID
     * @return Optional containing the resume if found
     */
    public Optional<Resume> getResumeById(Long resumeId) {
        return resumeRepository.findByIdWithUser(resumeId);
    }

    /**
     * Get all resumes for a specific user
     *
     * @param userId the user's ID
     * @return list of resumes (ordered by creation date, newest first)
     */
    public List<Resume> getUserResumes(Long userId) {
        return resumeRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get resume count for a user
     *
     * @param userId the user's ID
     * @return number of resumes
     */
    public long getResumeCountForUser(Long userId) {
        return resumeRepository.countByUserId(userId);
    }

    /**
     * Update resume content
     *
     * @param resumeId the resume ID
     * @param content  the new content
     * @return the updated resume
     * @throws IllegalArgumentException if resume not found
     */
    @Transactional
    public Resume updateResume(Long resumeId, String content) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
        resume.setContent(content);
        Resume updated = resumeRepository.save(resume);
        log.info("Updated resume with ID: {}", resumeId);
        return updated;
    }

    /**
     * Delete resume by ID
     *
     * @param resumeId the resume ID
     */
    @Transactional
    public void deleteResume(Long resumeId) {
        resumeRepository.deleteById(resumeId);
        log.info("Deleted resume with ID: {}", resumeId);
    }

    /**
     * Delete all resumes for a user
     *
     * @param userId the user's ID
     */
    @Transactional
    public void deleteAllResumesForUser(Long userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        resumeRepository.deleteAll(resumes);
        log.info("Deleted all resumes for user ID: {}", userId);
    }

    /**
     * Get total number of resumes
     *
     * @return total resume count
     */
    public long getTotalResumeCount() {
        return resumeRepository.count();
    }

    /**
     * Check if resume exists
     *
     * @param resumeId the resume ID
     * @return true if resume exists
     */
    public boolean resumeExists(Long resumeId) {
        return resumeRepository.existsById(resumeId);
    }
}
