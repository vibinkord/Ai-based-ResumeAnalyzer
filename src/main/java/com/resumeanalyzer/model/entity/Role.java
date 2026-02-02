package com.resumeanalyzer.model.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * Role enum representing user roles in the application.
 * Used for role-based access control (RBAC) with Spring Security.
 */
public enum Role implements GrantedAuthority {
    /**
     * User role - standard user with basic access
     */
    ROLE_USER,

    /**
     * Admin role - elevated privileges, can manage users and access all features
     */
    ROLE_ADMIN,

    /**
     * Premium role - user with premium features unlocked
     */
    ROLE_PREMIUM,

    /**
     * Analyst role - dedicated role for advanced analytics
     */
    ROLE_ANALYST;

    @Override
    public String getAuthority() {
        return name();
    }
}
