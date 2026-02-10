-- V8: Create resume table for PDF storage
CREATE TABLE resumes (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    file_name            VARCHAR(255) NOT NULL,
    file_url             VARCHAR(500) NOT NULL,
    cloudinary_public_id VARCHAR(255) NOT NULL,
    file_size            BIGINT NOT NULL,
    content_type         VARCHAR(100) NOT NULL DEFAULT 'application/pdf',
    active               BOOLEAN NOT NULL DEFAULT TRUE,
    uploaded_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_resumes_active ON resumes(active);
