-- V3__Add_User_Roles.sql
-- Add user roles table for role-based access control (RBAC)
-- This table maintains the many-to-many relationship between users and roles

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Create index for efficient role lookups
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON user_roles(role);

-- Add default ROLE_USER to existing users (if any)
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_USER' FROM users
WHERE id NOT IN (SELECT user_id FROM user_roles)
ON CONFLICT DO NOTHING;

-- Add comment explaining the table
COMMENT ON TABLE user_roles IS 'Many-to-many relationship between users and roles for RBAC';
COMMENT ON COLUMN user_roles.user_id IS 'Reference to user.id';
COMMENT ON COLUMN user_roles.role IS 'Role name (ROLE_USER, ROLE_ADMIN, ROLE_PREMIUM, ROLE_ANALYST)';
