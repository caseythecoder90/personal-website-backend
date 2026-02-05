-- V1__initial_schema.sql
-- Initial database schema for Personal Website Portfolio
-- Creates all tables, enums, indexes, and foreign key constraints

-- ================================
-- ENUMERATIONS (PostgreSQL types)
-- ================================

CREATE TYPE project_type AS ENUM (
    'PERSONAL', 'PROFESSIONAL', 'OPEN_SOURCE', 'LEARNING', 'FREELANCE'
);

CREATE TYPE project_status AS ENUM (
    'PLANNING', 'IN_PROGRESS', 'COMPLETED', 'MAINTAINED', 'ARCHIVED'
);

CREATE TYPE difficulty_level AS ENUM (
    'BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'
);

CREATE TYPE technology_category AS ENUM (
    'LANGUAGE', 'FRAMEWORK', 'LIBRARY', 'DATABASE', 'TOOL', 'CLOUD', 'DEPLOYMENT', 'TESTING'
);

CREATE TYPE proficiency_level AS ENUM (
    'LEARNING', 'FAMILIAR', 'PROFICIENT', 'EXPERT'
);

CREATE TYPE image_type AS ENUM (
    'THUMBNAIL', 'SCREENSHOT', 'ARCHITECTURE_DIAGRAM', 'UI_MOCKUP', 'LOGO'
);

CREATE TYPE inquiry_type AS ENUM (
    'GENERAL', 'PROJECT', 'COLLABORATION', 'HIRING', 'FREELANCE'
);

CREATE TYPE submission_status AS ENUM (
    'NEW', 'READ', 'REPLIED', 'ARCHIVED'
);

-- ================================
-- PORTFOLIO TABLES
-- ================================

CREATE TABLE technologies (
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(100)  NOT NULL UNIQUE,
    version            VARCHAR(50),
    category           technology_category,
    icon_url           VARCHAR(500),
    color              VARCHAR(7),
    documentation_url  VARCHAR(500),
    proficiency_level  proficiency_level DEFAULT 'LEARNING',
    years_experience   DECIMAL(3, 1),
    featured           BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_technologies_name             ON technologies (name);
CREATE INDEX idx_technologies_category         ON technologies (category);
CREATE INDEX idx_technologies_featured         ON technologies (featured);
CREATE INDEX idx_technologies_proficiency      ON technologies (proficiency_level);

CREATE TABLE projects (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(255) NOT NULL UNIQUE,
    slug                VARCHAR(255) NOT NULL UNIQUE,
    short_description   VARCHAR(500),
    full_description    TEXT,
    tech_stack          VARCHAR(255),
    github_url          VARCHAR(500),
    live_url            VARCHAR(500),
    docker_url          VARCHAR(500),
    documentation_url   VARCHAR(500),
    project_type        project_type     DEFAULT 'PERSONAL',
    status              project_status   DEFAULT 'PLANNING',
    difficulty_level    difficulty_level  DEFAULT 'BEGINNER',
    start_date          TIMESTAMPTZ,
    completion_date     TIMESTAMPTZ,
    estimated_hours     INTEGER,
    display_order       INTEGER,
    featured            BOOLEAN      NOT NULL DEFAULT FALSE,
    published           BOOLEAN      NOT NULL DEFAULT FALSE,
    view_count          BIGINT       NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_projects_slug            ON projects (slug);
CREATE INDEX idx_projects_status          ON projects (status);
CREATE INDEX idx_projects_type            ON projects (project_type);
CREATE INDEX idx_projects_published       ON projects (published);
CREATE INDEX idx_projects_featured        ON projects (featured);
CREATE INDEX idx_projects_display_order   ON projects (display_order);
CREATE INDEX idx_projects_created_at      ON projects (created_at);

CREATE TABLE project_technologies (
    project_id    BIGINT NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    technology_id BIGINT NOT NULL REFERENCES technologies (id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, technology_id)
);

CREATE INDEX idx_project_technologies_tech ON project_technologies (technology_id);

CREATE TABLE project_images (
    id             BIGSERIAL PRIMARY KEY,
    project_id     BIGINT     NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    url            VARCHAR(1000) NOT NULL,
    alt_text       VARCHAR(255),
    caption        VARCHAR(500),
    image_type     image_type    NOT NULL DEFAULT 'SCREENSHOT',
    display_order  INTEGER       NOT NULL DEFAULT 0,
    is_primary     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_project_images_project  ON project_images (project_id);
CREATE INDEX idx_project_images_primary  ON project_images (is_primary);
CREATE INDEX idx_project_images_order    ON project_images (display_order);

-- ================================
-- BLOG TABLES
-- ================================

CREATE TABLE blog_categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    color       VARCHAR(7),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_blog_categories_slug ON blog_categories (slug);
CREATE INDEX idx_blog_categories_name ON blog_categories (name);

CREATE TABLE blog_tags (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(50)  NOT NULL UNIQUE,
    slug         VARCHAR(50)  NOT NULL UNIQUE,
    usage_count  INTEGER      NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_blog_tags_slug       ON blog_tags (slug);
CREATE INDEX idx_blog_tags_name       ON blog_tags (name);
CREATE INDEX idx_blog_tags_usage      ON blog_tags (usage_count);

CREATE TABLE blog_posts (
    id                  BIGSERIAL PRIMARY KEY,
    title               VARCHAR(255) NOT NULL,
    slug                VARCHAR(255) NOT NULL UNIQUE,
    content             TEXT,
    excerpt             TEXT,
    published           BOOLEAN      NOT NULL DEFAULT FALSE,
    published_at        TIMESTAMPTZ,
    view_count          INTEGER      NOT NULL DEFAULT 0,
    read_time_minutes   INTEGER,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_blog_posts_slug         ON blog_posts (slug);
CREATE INDEX idx_blog_posts_published    ON blog_posts (published);
CREATE INDEX idx_blog_posts_published_at ON blog_posts (published_at);
CREATE INDEX idx_blog_posts_created_at   ON blog_posts (created_at);

CREATE TABLE blog_post_categories (
    blog_post_id      BIGINT NOT NULL REFERENCES blog_posts (id) ON DELETE CASCADE,
    blog_category_id  BIGINT NOT NULL REFERENCES blog_categories (id) ON DELETE CASCADE,
    PRIMARY KEY (blog_post_id, blog_category_id)
);

CREATE INDEX idx_blog_post_categories_category ON blog_post_categories (blog_category_id);

CREATE TABLE blog_post_tags (
    blog_post_id  BIGINT NOT NULL REFERENCES blog_posts (id) ON DELETE CASCADE,
    blog_tag_id   BIGINT NOT NULL REFERENCES blog_tags (id) ON DELETE CASCADE,
    PRIMARY KEY (blog_post_id, blog_tag_id)
);

CREATE INDEX idx_blog_post_tags_tag ON blog_post_tags (blog_tag_id);

-- ================================
-- CONTACT TABLE
-- ================================

CREATE TABLE contact_submissions (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)  NOT NULL,
    email          VARCHAR(255)  NOT NULL,
    subject        VARCHAR(255),
    message        TEXT          NOT NULL,
    inquiry_type   inquiry_type  NOT NULL DEFAULT 'GENERAL',
    status         submission_status NOT NULL DEFAULT 'NEW',
    ip_address     INET,
    user_agent     TEXT,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at   TIMESTAMPTZ
);

CREATE INDEX idx_contact_submissions_status      ON contact_submissions (status);
CREATE INDEX idx_contact_submissions_created_at  ON contact_submissions (created_at);
CREATE INDEX idx_contact_submissions_inquiry     ON contact_submissions (inquiry_type);
CREATE INDEX idx_contact_submissions_email       ON contact_submissions (email);
