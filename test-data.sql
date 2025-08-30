-- Personal Website Portfolio Test Data
-- Run this script to populate the database with test data for development

-- Clear existing data in dependency order
DELETE FROM project_analytics;
DELETE FROM page_views;
DELETE FROM seo_meta;
DELETE FROM blog_post_tags;
DELETE FROM blog_post_categories;
DELETE FROM blog_posts;
DELETE FROM blog_tags;
DELETE FROM blog_categories;
DELETE FROM contact_submissions;
DELETE FROM learning_outcomes;
DELETE FROM project_images;
DELETE FROM project_technologies;
DELETE FROM projects;
DELETE FROM technologies;
DELETE FROM users;

-- Reset sequences
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE projects_id_seq RESTART WITH 1;
ALTER SEQUENCE technologies_id_seq RESTART WITH 1;
ALTER SEQUENCE project_images_id_seq RESTART WITH 1;
ALTER SEQUENCE learning_outcomes_id_seq RESTART WITH 1;
ALTER SEQUENCE contact_submissions_id_seq RESTART WITH 1;
ALTER SEQUENCE blog_categories_id_seq RESTART WITH 1;
ALTER SEQUENCE blog_tags_id_seq RESTART WITH 1;
ALTER SEQUENCE blog_posts_id_seq RESTART WITH 1;
ALTER SEQUENCE page_views_id_seq RESTART WITH 1;
ALTER SEQUENCE project_analytics_id_seq RESTART WITH 1;
ALTER SEQUENCE seo_meta_id_seq RESTART WITH 1;

-- ================================
-- Insert Users
-- ================================
INSERT INTO users (username, email, password_hash, role, first_name, last_name, active, created_at, updated_at) VALUES
('admin', 'casey@caseyquinn.com', '$2a$10$hash', 'ADMIN', 'Casey', 'Quinn', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('editor', 'editor@caseyquinn.com', '$2a$10$hash', 'EDITOR', 'Test', 'Editor', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================
-- Insert Technologies
-- ================================
INSERT INTO technologies (name, version, category, proficiency_level, years_experience, color, featured, created_at) VALUES
-- Programming Languages
('Java', '21', 'LANGUAGE', 'EXPERT', 5.0, '#f89820', true, CURRENT_TIMESTAMP),
('JavaScript', 'ES2023', 'LANGUAGE', 'PROFICIENT', 4.0, '#f7df1e', true, CURRENT_TIMESTAMP),
('TypeScript', '5.0', 'LANGUAGE', 'PROFICIENT', 3.0, '#3178c6', true, CURRENT_TIMESTAMP),
('Python', '3.11', 'LANGUAGE', 'FAMILIAR', 2.0, '#3776ab', false, CURRENT_TIMESTAMP),

-- Frameworks
('Spring Boot', '3.2', 'FRAMEWORK', 'EXPERT', 4.0, '#6db33f', true, CURRENT_TIMESTAMP),
('React', '18', 'FRAMEWORK', 'PROFICIENT', 3.0, '#61dafb', true, CURRENT_TIMESTAMP),
('FastAPI', '0.104', 'FRAMEWORK', 'FAMILIAR', 1.0, '#009688', false, CURRENT_TIMESTAMP),

-- Databases
('PostgreSQL', '15', 'DATABASE', 'PROFICIENT', 3.0, '#336791', true, CURRENT_TIMESTAMP),
('Redis', '7', 'DATABASE', 'FAMILIAR', 2.0, '#dc382d', false, CURRENT_TIMESTAMP),

-- Tools
('Docker', '24', 'TOOL', 'PROFICIENT', 3.0, '#2496ed', true, CURRENT_TIMESTAMP),
('Maven', '3.9', 'TOOL', 'PROFICIENT', 4.0, '#c71a36', false, CURRENT_TIMESTAMP),
('Git', '2.42', 'TOOL', 'EXPERT', 5.0, '#f05032', true, CURRENT_TIMESTAMP),
('IntelliJ IDEA', '2023.3', 'TOOL', 'EXPERT', 5.0, '#000000', false, CURRENT_TIMESTAMP),

-- Libraries
('Lombok', '1.18', 'LIBRARY', 'EXPERT', 4.0, '#b32629', false, CURRENT_TIMESTAMP),
('MapStruct', '1.5', 'LIBRARY', 'PROFICIENT', 2.0, '#ff6b35', false, CURRENT_TIMESTAMP),
('Tailwind CSS', '3.3', 'LIBRARY', 'PROFICIENT', 2.0, '#06b6d4', true, CURRENT_TIMESTAMP),
('Vite', '5.0', 'TOOL', 'FAMILIAR', 1.0, '#646cff', false, CURRENT_TIMESTAMP),

-- Cloud & Deployment
('AWS', 'N/A', 'CLOUD', 'LEARNING', 0.5, '#ff9900', false, CURRENT_TIMESTAMP),
('Kubernetes', '1.28', 'DEPLOYMENT', 'LEARNING', 0.5, '#326ce5', false, CURRENT_TIMESTAMP);

-- ================================
-- Insert Projects
-- ================================
INSERT INTO projects (name, slug, short_description, full_description, github_url, live_url, project_type, status, difficulty_level, start_date, completion_date, estimated_hours, display_order, featured, published, view_count, created_at, updated_at) VALUES
('Personal Website Portfolio', 'personal-website-portfolio', 
 'Full-stack personal website showcasing projects and technical skills', 
 'A comprehensive personal portfolio website built with enterprise-grade patterns and modern technologies. Features include project management, blog system, analytics tracking, and SEO optimization.',
 'https://github.com/caseyquinn/personal-website',
 'https://caseyquinn.com',
 'PERSONAL', 'IN_PROGRESS', 'ADVANCED',
 '2024-01-01'::timestamp, NULL, 120, 1, true, true, 245,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('E-Commerce Platform', 'ecommerce-platform',
 'Scalable microservices-based e-commerce solution',
 'Built a full-featured e-commerce platform using Spring Boot microservices, React frontend, and PostgreSQL. Includes user management, product catalog, shopping cart, and payment processing.',
 'https://github.com/caseyquinn/ecommerce-platform',
 'https://demo-shop.caseyquinn.com',
 'PROFESSIONAL', 'COMPLETED', 'EXPERT',
 '2023-06-01'::timestamp, '2023-12-15'::timestamp, 280, 2, true, true, 189,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Task Management API', 'task-management-api',
 'RESTful API for project and task management',
 'A comprehensive task management system with user authentication, project organization, real-time notifications, and advanced filtering. Built with Spring Boot and secured with JWT.',
 'https://github.com/caseyquinn/task-api',
 NULL,
 'LEARNING', 'COMPLETED', 'INTERMEDIATE',
 '2023-03-01'::timestamp, '2023-05-20'::timestamp, 85, 3, false, true, 67,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Weather Dashboard', 'weather-dashboard',
 'Real-time weather monitoring with data visualization',
 'Interactive weather dashboard built with React and TypeScript. Features include location-based forecasts, historical data charts, and severe weather alerts.',
 'https://github.com/caseyquinn/weather-dashboard',
 'https://weather.caseyquinn.com',
 'PERSONAL', 'MAINTAINED', 'INTERMEDIATE',
 '2023-01-15'::timestamp, '2023-02-28'::timestamp, 45, 4, false, true, 123,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================
-- Link Projects to Technologies
-- ================================
INSERT INTO project_technologies (project_id, technology_id, created_at) VALUES
-- Personal Website Portfolio (Java, Spring Boot, React, TypeScript, PostgreSQL, Docker)
(1, 1, CURRENT_TIMESTAMP), (1, 5, CURRENT_TIMESTAMP), (1, 6, CURRENT_TIMESTAMP), 
(1, 3, CURRENT_TIMESTAMP), (1, 8, CURRENT_TIMESTAMP), (1, 10, CURRENT_TIMESTAMP),

-- E-Commerce Platform (Java, Spring Boot, React, PostgreSQL, Redis, Docker, AWS)
(2, 1, CURRENT_TIMESTAMP), (2, 5, CURRENT_TIMESTAMP), (2, 6, CURRENT_TIMESTAMP),
(2, 8, CURRENT_TIMESTAMP), (2, 9, CURRENT_TIMESTAMP), (2, 10, CURRENT_TIMESTAMP), (2, 18, CURRENT_TIMESTAMP),

-- Task Management API (Java, Spring Boot, PostgreSQL, Maven)
(3, 1, CURRENT_TIMESTAMP), (3, 5, CURRENT_TIMESTAMP), (3, 8, CURRENT_TIMESTAMP), (3, 11, CURRENT_TIMESTAMP),

-- Weather Dashboard (React, TypeScript, JavaScript, Tailwind CSS)
(4, 6, CURRENT_TIMESTAMP), (4, 3, CURRENT_TIMESTAMP), (4, 2, CURRENT_TIMESTAMP), (4, 16, CURRENT_TIMESTAMP);

-- ================================
-- Insert Project Images
-- ================================
INSERT INTO project_images (project_id, url, alt_text, caption, image_type, display_order, is_primary, created_at) VALUES
(1, 'https://example.com/images/portfolio-home.png', 'Portfolio homepage screenshot', 'Clean, modern homepage design', 'SCREENSHOT', 1, true, CURRENT_TIMESTAMP),
(1, 'https://example.com/images/portfolio-projects.png', 'Projects gallery page', 'Interactive projects showcase', 'SCREENSHOT', 2, false, CURRENT_TIMESTAMP),
(1, 'https://example.com/images/portfolio-architecture.png', 'System architecture diagram', 'High-level system architecture', 'ARCHITECTURE_DIAGRAM', 3, false, CURRENT_TIMESTAMP),

(2, 'https://example.com/images/ecommerce-home.png', 'E-commerce homepage', 'Product catalog with search', 'SCREENSHOT', 1, true, CURRENT_TIMESTAMP),
(2, 'https://example.com/images/ecommerce-cart.png', 'Shopping cart interface', 'Intuitive shopping cart design', 'UI_MOCKUP', 2, false, CURRENT_TIMESTAMP);

-- ================================
-- Insert Learning Outcomes
-- ================================
INSERT INTO learning_outcomes (project_id, title, description, category, difficulty_level, created_at) VALUES
(1, 'Enterprise Architecture Patterns', 'Mastered layered architecture with DAO pattern, dependency injection, and proper separation of concerns', 'ARCHITECTURE', 'ADVANCED', CURRENT_TIMESTAMP),
(1, 'Advanced Spring Boot Features', 'Implemented comprehensive exception handling, validation, and OpenAPI documentation', 'TECHNICAL', 'ADVANCED', CURRENT_TIMESTAMP),
(1, 'Database Design & Optimization', 'Designed normalized schema with proper indexing and constraint management', 'TECHNICAL', 'INTERMEDIATE', CURRENT_TIMESTAMP),

(2, 'Microservices Architecture', 'Built scalable microservices with proper service boundaries and communication patterns', 'ARCHITECTURE', 'EXPERT', CURRENT_TIMESTAMP),
(2, 'Payment Integration', 'Integrated secure payment processing with Stripe API and webhook handling', 'TECHNICAL', 'ADVANCED', CURRENT_TIMESTAMP),
(2, 'Performance Optimization', 'Implemented caching strategies and database optimization for high-traffic scenarios', 'TECHNICAL', 'ADVANCED', CURRENT_TIMESTAMP);

-- ================================
-- Insert Blog Categories
-- ================================
INSERT INTO blog_categories (name, slug, description, color, created_at) VALUES
('Software Development', 'software-development', 'Articles about programming, architecture, and best practices', '#2563eb', CURRENT_TIMESTAMP),
('Career & Growth', 'career-growth', 'Professional development and career insights', '#10b981', CURRENT_TIMESTAMP),
('Technology Reviews', 'tech-reviews', 'Reviews and comparisons of tools and technologies', '#f59e0b', CURRENT_TIMESTAMP),
('Tutorials', 'tutorials', 'Step-by-step guides and how-to articles', '#8b5cf6', CURRENT_TIMESTAMP);

-- ================================
-- Insert Blog Tags
-- ================================
INSERT INTO blog_tags (name, slug, usage_count, created_at) VALUES
('Java', 'java', 5, CURRENT_TIMESTAMP),
('Spring Boot', 'spring-boot', 4, CURRENT_TIMESTAMP),
('React', 'react', 3, CURRENT_TIMESTAMP),
('PostgreSQL', 'postgresql', 2, CURRENT_TIMESTAMP),
('Architecture', 'architecture', 3, CURRENT_TIMESTAMP),
('Best Practices', 'best-practices', 4, CURRENT_TIMESTAMP),
('Tutorial', 'tutorial', 2, CURRENT_TIMESTAMP);

-- ================================
-- Insert Contact Submissions
-- ================================
INSERT INTO contact_submissions (name, email, subject, message, inquiry_type, status, ip_address, created_at) VALUES
('John Smith', 'john.smith@example.com', 'Collaboration Opportunity', 'Hi Casey, I would like to discuss a potential collaboration on a Spring Boot project.', 'COLLABORATION', 'NEW', '192.168.1.100', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('Sarah Johnson', 'sarah.j@techcorp.com', 'Job Opportunity', 'We have an exciting backend developer position that might interest you.', 'HIRING', 'READ', '10.0.0.50', CURRENT_TIMESTAMP - INTERVAL '1 week'),
('Mike Brown', 'mike@startup.io', 'Freelance Project', 'Looking for a Java developer for a 3-month project. Are you available?', 'FREELANCE', 'REPLIED', '172.16.0.25', CURRENT_TIMESTAMP - INTERVAL '3 days');

-- ================================
-- Insert Page Views (sample analytics)
-- ================================
INSERT INTO page_views (page_path, referrer, user_agent, ip_address, country, device_type, created_at) VALUES
('/', 'https://google.com', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '203.0.113.1', 'US', 'DESKTOP', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
('/projects', 'https://linkedin.com', 'Mozilla/5.0 (iPhone; CPU iPhone OS 16_0)', '203.0.113.2', 'CA', 'MOBILE', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
('/projects/personal-website-portfolio', '/', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', '203.0.113.3', 'UK', 'DESKTOP', CURRENT_TIMESTAMP - INTERVAL '30 minutes');

-- ================================
-- Insert Project Analytics
-- ================================
INSERT INTO project_analytics (project_id, event_type, referrer, user_agent, ip_address, created_at) VALUES
(1, 'VIEW', 'https://google.com', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '203.0.113.1', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(1, 'GITHUB_CLICK', '/projects/personal-website-portfolio', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', '203.0.113.1', CURRENT_TIMESTAMP - INTERVAL '45 minutes'),
(2, 'VIEW', 'https://linkedin.com', 'Mozilla/5.0 (iPhone; CPU iPhone OS 16_0)', '203.0.113.2', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(2, 'DEMO_CLICK', '/projects/ecommerce-platform', 'Mozilla/5.0 (iPhone; CPU iPhone OS 16_0)', '203.0.113.2', CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes');

-- ================================
-- Insert SEO Metadata
-- ================================
INSERT INTO seo_meta (entity_type, entity_id, meta_title, meta_description, og_title, og_description, canonical_url, updated_at) VALUES
('PROJECT', 1, 'Personal Website Portfolio | Casey Quinn', 'Enterprise-grade personal portfolio built with Spring Boot, React, and PostgreSQL. Showcasing modern development practices and clean architecture.', 'Casey Quinn - Personal Website Portfolio', 'Full-stack personal website showcasing projects and technical skills', 'https://caseyquinn.com/projects/personal-website-portfolio', CURRENT_TIMESTAMP),
('PROJECT', 2, 'E-Commerce Platform | Casey Quinn', 'Scalable microservices e-commerce platform built with Spring Boot. Features user management, product catalog, and secure payment processing.', 'E-Commerce Platform - Casey Quinn Portfolio', 'Scalable microservices-based e-commerce solution', 'https://caseyquinn.com/projects/ecommerce-platform', CURRENT_TIMESTAMP);

-- Verify data insertion
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL SELECT 'Technologies', COUNT(*) FROM technologies
UNION ALL SELECT 'Projects', COUNT(*) FROM projects
UNION ALL SELECT 'Project Technologies', COUNT(*) FROM project_technologies
UNION ALL SELECT 'Project Images', COUNT(*) FROM project_images
UNION ALL SELECT 'Learning Outcomes', COUNT(*) FROM learning_outcomes
UNION ALL SELECT 'Blog Categories', COUNT(*) FROM blog_categories
UNION ALL SELECT 'Blog Tags', COUNT(*) FROM blog_tags
UNION ALL SELECT 'Contact Submissions', COUNT(*) FROM contact_submissions
UNION ALL SELECT 'Page Views', COUNT(*) FROM page_views
UNION ALL SELECT 'Project Analytics', COUNT(*) FROM project_analytics
UNION ALL SELECT 'SEO Meta', COUNT(*) FROM seo_meta
ORDER BY table_name;