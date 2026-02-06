-- Add cloudinary_public_id column for deletion operations
ALTER TABLE project_images
ADD COLUMN cloudinary_public_id VARCHAR(500);

CREATE INDEX idx_project_images_cloudinary_id ON project_images (cloudinary_public_id);

COMMENT ON COLUMN project_images.cloudinary_public_id IS 'Cloudinary public_id for management and deletion';
