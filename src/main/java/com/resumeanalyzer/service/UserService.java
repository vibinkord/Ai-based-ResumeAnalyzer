package com.resumeanalyzer.service;

import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    /**
     * Create a new user
     *
     * @param email    the user's email
     * @param password the user's password
     * @param fullName the user's full name
     * @return the created user
     * @throws IllegalArgumentException if user already exists
     */
    @Transactional
    public User createUser(String email, String password, String fullName) {
        if (userRepository.existsByEmail(email)) {
            log.warn("User with email {} already exists", email);
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        User user = new User(email, password, fullName);
        User savedUser = userRepository.save(user);
        log.info("Created new user with email: {}", email);
        return savedUser;
    }

    /**
     * Find user by email
     *
     * @param email the user's email
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by email (case-insensitive)
     *
     * @param email the user's email
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Get user by ID
     *
     * @param userId the user's ID
     * @return Optional containing the user if found
     */
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Check if user exists with given email
     *
     * @param email the email to check
     * @return true if user exists
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Update user details
     *
     * @param user the user to update
     * @return the updated user
     */
    @Transactional
    public User updateUser(User user) {
        User savedUser = userRepository.save(user);
        log.info("Updated user with ID: {}", user.getId());
        return savedUser;
    }

    /**
     * Delete user by ID
     *
     * @param userId the user's ID
     */
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("Deleted user with ID: {}", userId);
    }

    /**
     * Get total number of users
     *
     * @return total user count
     */
    public long getTotalUserCount() {
        return userRepository.count();
    }
}
