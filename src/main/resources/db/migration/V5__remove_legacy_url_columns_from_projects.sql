-- Remove legacy URL columns now that project_links table exists
ALTER TABLE projects DROP COLUMN IF EXISTS github_url;
ALTER TABLE projects DROP COLUMN IF EXISTS live_url;
ALTER TABLE projects DROP COLUMN IF EXISTS docker_url;
ALTER TABLE projects DROP COLUMN IF EXISTS documentation_url;

-- Remove legacy tech_stack column now that project_technologies junction table exists
ALTER TABLE projects DROP COLUMN IF EXISTS tech_stack;

COMMENT ON TABLE projects IS 'Project URLs are now managed in project_links table. Tech stack is managed via project_technologies junction table.';
