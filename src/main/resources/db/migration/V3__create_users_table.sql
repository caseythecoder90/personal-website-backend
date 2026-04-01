-- Create users table for authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

COMMENT ON TABLE users IS 'Users for authentication and authorization';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password';
COMMENT ON COLUMN users.role IS 'User role: ADMIN or USER';

-- Insert default admin user
-- IMPORTANT: After first deploy, immediately update the password via SQL:
--   UPDATE users SET password = '<new-bcrypt-hash>' WHERE username = 'admin';
-- Generate a hash using: /api/v1/operations/hash-password (non-production only)
INSERT INTO users (username, email, password, full_name, role)
VALUES (
    'admin',
    'admin@caseyquinn.com',
    '$2a$10$placeholder000000000000000000000000000000000000000000',
    'Casey Quinn',
    'ADMIN'
);
