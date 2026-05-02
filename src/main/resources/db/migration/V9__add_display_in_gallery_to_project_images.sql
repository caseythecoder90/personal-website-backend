-- Add display_in_gallery flag so the UI can distinguish inline-in-markdown
-- images (false) from gallery thumbnails (true). Defaults true so existing
-- rows keep current behavior.
ALTER TABLE project_images
ADD COLUMN display_in_gallery BOOLEAN NOT NULL DEFAULT true;

COMMENT ON COLUMN project_images.display_in_gallery IS 'When false, the image is intended for markdown embedding only and should not appear in the project gallery';
