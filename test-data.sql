-- Personal Website Portfolio Test Data
-- Run this script against the local PostgreSQL database to populate test data
-- Requires V1__initial_schema.sql to have been applied first (via Flyway or manually)

-- Clear existing data in dependency order
DELETE FROM blog_post_tags;
DELETE FROM blog_post_categories;
DELETE FROM blog_posts;
DELETE FROM blog_tags;
DELETE FROM blog_categories;
DELETE FROM contact_submissions;
DELETE FROM project_images;
DELETE FROM project_technologies;
DELETE FROM projects;
DELETE FROM technologies;

-- Reset sequences
ALTER SEQUENCE technologies_id_seq RESTART WITH 1;
ALTER SEQUENCE projects_id_seq RESTART WITH 1;
ALTER SEQUENCE project_images_id_seq RESTART WITH 1;
ALTER SEQUENCE blog_categories_id_seq RESTART WITH 1;
ALTER SEQUENCE blog_tags_id_seq RESTART WITH 1;
ALTER SEQUENCE blog_posts_id_seq RESTART WITH 1;
ALTER SEQUENCE contact_submissions_id_seq RESTART WITH 1;

-- ================================
-- Insert Technologies
-- ================================
INSERT INTO technologies (name, version, category, proficiency_level, years_experience, color, icon_url, documentation_url, featured, created_at, updated_at) VALUES
-- Programming Languages
('Java', '21', 'LANGUAGE', 'EXPERT', 5.0, '#f89820', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg', 'https://docs.oracle.com/en/java/', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('JavaScript', 'ES2023', 'LANGUAGE', 'PROFICIENT', 4.0, '#f7df1e', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/javascript/javascript-original.svg', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TypeScript', '5.0', 'LANGUAGE', 'PROFICIENT', 3.0, '#3178c6', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/typescript/typescript-original.svg', 'https://www.typescriptlang.org/docs/', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Python', '3.11', 'LANGUAGE', 'FAMILIAR', 2.0, '#3776ab', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/python/python-original.svg', 'https://docs.python.org/3/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Frameworks
('Spring Boot', '3.2', 'FRAMEWORK', 'EXPERT', 4.0, '#6db33f', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg', 'https://docs.spring.io/spring-boot/docs/current/reference/html/', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('React', '18', 'FRAMEWORK', 'PROFICIENT', 3.0, '#61dafb', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg', 'https://react.dev/learn', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('FastAPI', '0.104', 'FRAMEWORK', 'FAMILIAR', 1.0, '#009688', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/fastapi/fastapi-original.svg', 'https://fastapi.tiangolo.com/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Databases
('PostgreSQL', '15', 'DATABASE', 'PROFICIENT', 3.0, '#336791', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg', 'https://www.postgresql.org/docs/', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Redis', '7', 'DATABASE', 'FAMILIAR', 2.0, '#dc382d', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/redis/redis-original.svg', 'https://redis.io/docs/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Tools
('Docker', '24', 'TOOL', 'PROFICIENT', 3.0, '#2496ed', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg', 'https://docs.docker.com/', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Maven', '3.9', 'TOOL', 'PROFICIENT', 4.0, '#c71a36', null, 'https://maven.apache.org/guides/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Git', '2.42', 'TOOL', 'EXPERT', 5.0, '#f05032', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/git/git-original.svg', 'https://git-scm.com/docs', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('IntelliJ IDEA', '2023.3', 'TOOL', 'EXPERT', 5.0, '#000000', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/intellij/intellij-original.svg', 'https://www.jetbrains.com/help/idea/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Libraries
('Lombok', '1.18', 'LIBRARY', 'EXPERT', 4.0, '#b32629', null, 'https://projectlombok.org/features/all', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MapStruct', '1.5', 'LIBRARY', 'PROFICIENT', 2.0, '#ff6b35', null, 'https://mapstruct.org/documentation/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Tailwind CSS', '3.3', 'LIBRARY', 'PROFICIENT', 2.0, '#06b6d4', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/tailwindcss/tailwindcss-plain.svg', 'https://tailwindcss.com/docs', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Vite', '5.0', 'TOOL', 'FAMILIAR', 1.0, '#646cff', null, 'https://vitejs.dev/guide/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cloud & Deployment
('AWS', 'N/A', 'CLOUD', 'LEARNING', 0.5, '#ff9900', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/amazonwebservices/amazonwebservices-original.svg', 'https://docs.aws.amazon.com/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kubernetes', '1.28', 'DEPLOYMENT', 'LEARNING', 0.5, '#326ce5', 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kubernetes/kubernetes-plain.svg', 'https://kubernetes.io/docs/', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================
-- Insert Projects
-- ================================
INSERT INTO projects (name, slug, short_description, full_description, github_url, live_url, docker_url, documentation_url, project_type, status, difficulty_level, start_date, completion_date, estimated_hours, display_order, featured, published, view_count, created_at, updated_at) VALUES
('Personal Website Portfolio', 'personal-website-portfolio',
 'Full-stack personal website showcasing projects and technical skills',
 'A comprehensive personal portfolio website built with enterprise-grade patterns and modern technologies. Features include project management, blog system, and contact management.',
 'https://github.com/caseyquinn/personal-website',
 'https://caseyquinn.com',
 'https://hub.docker.com/r/caseyquinn/personal-website',
 'https://caseyquinn.com/docs/api',
 'PERSONAL', 'IN_PROGRESS', 'ADVANCED',
 '2024-01-01'::timestamp, NULL, 120, 1, true, true, 245,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('E-Commerce Platform', 'ecommerce-platform',
 'Scalable microservices-based e-commerce solution',
 'Built a full-featured e-commerce platform using Spring Boot microservices, React frontend, and PostgreSQL. Includes product catalog, shopping cart, and payment processing.',
 'https://github.com/caseyquinn/ecommerce-platform',
 'https://demo-shop.caseyquinn.com',
 'https://hub.docker.com/r/caseyquinn/ecommerce-platform',
 NULL,
 'PROFESSIONAL', 'COMPLETED', 'EXPERT',
 '2023-06-01'::timestamp, '2023-12-15'::timestamp, 280, 2, true, true, 189,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Task Management API', 'task-management-api',
 'RESTful API for project and task management',
 'A comprehensive task management system with project organization, real-time notifications, and advanced filtering. Built with Spring Boot.',
 'https://github.com/caseyquinn/task-api',
 NULL,
 NULL,
 'https://api.caseyquinn.com/docs',
 'LEARNING', 'COMPLETED', 'INTERMEDIATE',
 '2023-03-01'::timestamp, '2023-05-20'::timestamp, 85, 3, false, true, 67,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Weather Dashboard', 'weather-dashboard',
 'Real-time weather monitoring with data visualization',
 'Interactive weather dashboard built with React and TypeScript. Features include location-based forecasts, historical data charts, and severe weather alerts.',
 'https://github.com/caseyquinn/weather-dashboard',
 'https://weather.caseyquinn.com',
 NULL,
 NULL,
 'PERSONAL', 'MAINTAINED', 'INTERMEDIATE',
 '2023-01-15'::timestamp, '2023-02-28'::timestamp, 45, 4, false, true, 123,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================
-- Link Projects to Technologies
-- ================================
INSERT INTO project_technologies (project_id, technology_id) VALUES
-- Personal Website Portfolio (Java, Spring Boot, React, TypeScript, PostgreSQL, Docker)
(1, 1), (1, 5), (1, 6), (1, 3), (1, 8), (1, 10),

-- E-Commerce Platform (Java, Spring Boot, React, PostgreSQL, Redis, Docker, AWS)
(2, 1), (2, 5), (2, 6), (2, 8), (2, 9), (2, 10), (2, 18),

-- Task Management API (Java, Spring Boot, PostgreSQL, Maven)
(3, 1), (3, 5), (3, 8), (3, 11),

-- Weather Dashboard (React, TypeScript, JavaScript, Tailwind CSS)
(4, 6), (4, 3), (4, 2), (4, 16);

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

-- Verify data insertion
SELECT 'Technologies' as table_name, COUNT(*) as count FROM technologies
UNION ALL SELECT 'Projects', COUNT(*) FROM projects
UNION ALL SELECT 'Project Technologies', COUNT(*) FROM project_technologies
UNION ALL SELECT 'Project Images', COUNT(*) FROM project_images
UNION ALL SELECT 'Blog Categories', COUNT(*) FROM blog_categories
UNION ALL SELECT 'Blog Tags', COUNT(*) FROM blog_tags
UNION ALL SELECT 'Contact Submissions', COUNT(*) FROM contact_submissions
ORDER BY table_name;
