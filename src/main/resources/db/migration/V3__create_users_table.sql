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

-- Insert default admin user (password: admin123 - CHANGE THIS!)
-- Password is BCrypt hash of 'admin123'
INSERT INTO users (username, email, password, full_name, role)
VALUES (
    'admin',
    'admin@caseyquinn.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye1J5Yc8nJe6pqQhW.xGSvIKHYjYi3HWK',
    'Casey Quinn',
    'ADMIN'
);
