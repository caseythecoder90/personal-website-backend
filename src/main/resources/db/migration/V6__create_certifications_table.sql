-- V6__create_certifications_table.sql
-- Creates certifications table with enum type, join table for technology associations, and indexes

-- ================================
-- ENUM TYPE
-- ================================

CREATE TYPE certification_status AS ENUM (
    'EARNED', 'IN_PROGRESS', 'EXPIRED'
);

-- ================================
-- CERTIFICATIONS TABLE
-- ================================

CREATE TABLE certifications (
    id                     BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(255)  NOT NULL UNIQUE,
    slug                   VARCHAR(255)  NOT NULL UNIQUE,
    issuing_organization   VARCHAR(255)  NOT NULL,
    credential_id          VARCHAR(255),
    credential_url         VARCHAR(500),
    issue_date             DATE,
    expiration_date        DATE,
    status                 certification_status NOT NULL DEFAULT 'EARNED',
    description            TEXT,
    badge_url              VARCHAR(500),
    published              BOOLEAN       NOT NULL DEFAULT FALSE,
    featured               BOOLEAN       NOT NULL DEFAULT FALSE,
    display_order          INTEGER,
    created_at             TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_certifications_slug         ON certifications (slug);
CREATE INDEX idx_certifications_status       ON certifications (status);
CREATE INDEX idx_certifications_issuing_org  ON certifications (issuing_organization);
CREATE INDEX idx_certifications_published    ON certifications (published);
CREATE INDEX idx_certifications_featured     ON certifications (featured);
CREATE INDEX idx_certifications_display_order ON certifications (display_order);

-- ================================
-- JOIN TABLE: certification_technologies
-- ================================

CREATE TABLE certification_technologies (
    certification_id BIGINT NOT NULL REFERENCES certifications (id) ON DELETE CASCADE,
    technology_id    BIGINT NOT NULL REFERENCES technologies (id) ON DELETE CASCADE,
    PRIMARY KEY (certification_id, technology_id)
);

CREATE INDEX idx_certification_technologies_tech ON certification_technologies (technology_id);
