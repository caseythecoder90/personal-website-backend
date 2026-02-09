-- V7: Create blog_post_images table for managing blog post images

-- Create blog_image_type enum
CREATE TYPE blog_image_type AS ENUM ('FEATURED', 'INLINE', 'GALLERY', 'THUMBNAIL');

-- Create blog_post_images table
CREATE TABLE blog_post_images (
    id BIGSERIAL PRIMARY KEY,
    blog_post_id BIGINT NOT NULL REFERENCES blog_posts(id) ON DELETE CASCADE,
    url VARCHAR(1000) NOT NULL,
    cloudinary_public_id VARCHAR(500),
    alt_text VARCHAR(255),
    caption VARCHAR(500),
    image_type blog_image_type DEFAULT 'INLINE',
    display_order INTEGER DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for efficient querying
CREATE INDEX idx_blog_post_images_post_id ON blog_post_images(blog_post_id);
CREATE INDEX idx_blog_post_images_type ON blog_post_images(image_type);
CREATE INDEX idx_blog_post_images_primary ON blog_post_images(blog_post_id, is_primary) WHERE is_primary = TRUE;
CREATE INDEX idx_blog_post_images_display_order ON blog_post_images(blog_post_id, display_order);

COMMENT ON TABLE blog_post_images IS 'Images associated with blog posts';
COMMENT ON COLUMN blog_post_images.url IS 'Full URL to the image (typically Cloudinary URL)';
COMMENT ON COLUMN blog_post_images.cloudinary_public_id IS 'Cloudinary public ID for image management and deletion';
COMMENT ON COLUMN blog_post_images.alt_text IS 'Alternative text for accessibility';
COMMENT ON COLUMN blog_post_images.caption IS 'Image caption or description';
COMMENT ON COLUMN blog_post_images.image_type IS 'Type categorization: FEATURED (header), INLINE (within content), GALLERY (gallery section), THUMBNAIL (preview)';
COMMENT ON COLUMN blog_post_images.display_order IS 'Order for displaying images within the post';
COMMENT ON COLUMN blog_post_images.is_primary IS 'Whether this is the primary/featured image for the post';
