-- Create link_type enum for categorizing project links
CREATE TYPE link_type AS ENUM (
    'GITHUB',
    'LIVE',
    'DEMO',
    'STAGING',
    'DOCUMENTATION',
    'DOCKER',
    'NPM',
    'MAVEN',
    'API',
    'OTHER'
);

-- Create project_links table for multiple URLs per project
CREATE TABLE project_links (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    url VARCHAR(1000) NOT NULL,
    link_type link_type NOT NULL,
    label VARCHAR(100),
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for efficient lookups by project
CREATE INDEX idx_project_links_project_id ON project_links(project_id);

-- Index for filtering by link type
CREATE INDEX idx_project_links_type ON project_links(link_type);

COMMENT ON TABLE project_links IS 'Stores multiple URLs/links for each project (GitHub repos, demos, docs, etc.)';
COMMENT ON COLUMN project_links.label IS 'Optional label like "Backend", "Frontend", "Staging"';
COMMENT ON COLUMN project_links.display_order IS 'Order for displaying links of the same type';
