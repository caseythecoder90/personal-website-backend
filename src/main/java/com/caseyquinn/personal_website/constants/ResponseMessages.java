package com.caseyquinn.personal_website.constants;

/**
 * Constants for API response messages used in controllers.
 * Organized by domain to follow domain-driven design principles.
 */
public final class ResponseMessages {

    private ResponseMessages() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ── Auth ──────────────────────────────────────────────────────────────

    public static final String LOGIN_SUCCESSFUL = "Login successful";

    // ── Projects ─────────────────────────────────────────────────────────

    public static final String PROJECTS_RETRIEVED = "Projects retrieved successfully";
    public static final String PROJECT_RETRIEVED = "Project retrieved successfully";
    public static final String PROJECT_CREATED = "Project created successfully";
    public static final String PROJECT_UPDATED = "Project updated successfully";
    public static final String PROJECT_DELETED = "Project deleted successfully";

    // ── Project Images ───────────────────────────────────────────────────

    public static final String IMAGE_UPLOADED = "Image uploaded successfully";
    public static final String IMAGES_RETRIEVED = "Images retrieved successfully";
    public static final String IMAGE_RETRIEVED = "Image retrieved successfully";
    public static final String IMAGE_METADATA_UPDATED = "Image metadata updated successfully";
    public static final String IMAGE_DELETED = "Image deleted successfully";
    public static final String PRIMARY_IMAGE_SET = "Primary image set successfully";

    // ── Project Links ────────────────────────────────────────────────────

    public static final String LINK_CREATED = "Link created successfully";
    public static final String LINKS_RETRIEVED = "Links retrieved successfully";
    public static final String LINK_RETRIEVED = "Link retrieved successfully";
    public static final String LINK_UPDATED = "Link updated successfully";
    public static final String LINK_DELETED = "Link deleted successfully";

    // ── Technologies ─────────────────────────────────────────────────────

    public static final String TECHNOLOGIES_RETRIEVED = "Technologies retrieved successfully";
    public static final String TECHNOLOGY_RETRIEVED = "Technology retrieved successfully";
    public static final String TECHNOLOGY_CREATED = "Technology created successfully";
    public static final String TECHNOLOGY_UPDATED = "Technology updated successfully";
    public static final String TECHNOLOGY_DELETED = "Technology deleted successfully";
    public static final String FEATURED_TECHNOLOGIES_RETRIEVED = "Featured technologies retrieved successfully";
    public static final String MOST_USED_TECHNOLOGIES_RETRIEVED = "Most used technologies retrieved successfully";

    // ── Certifications ───────────────────────────────────────────────────

    public static final String CERTIFICATIONS_RETRIEVED = "Certifications retrieved successfully";
    public static final String CERTIFICATION_RETRIEVED = "Certification retrieved successfully";
    public static final String CERTIFICATION_CREATED = "Certification created successfully";
    public static final String CERTIFICATION_UPDATED = "Certification updated successfully";
    public static final String CERTIFICATION_DELETED = "Certification deleted successfully";
    public static final String PUBLISHED_CERTIFICATIONS_RETRIEVED = "Published certifications retrieved successfully";
    public static final String FEATURED_CERTIFICATIONS_RETRIEVED = "Featured certifications retrieved successfully";
    public static final String CERT_TECHNOLOGY_ADDED = "Technology added to certification successfully";
    public static final String CERT_TECHNOLOGY_REMOVED = "Technology removed from certification successfully";

    // ── Blog Posts ───────────────────────────────────────────────────────

    public static final String BLOG_POSTS_RETRIEVED = "Blog posts retrieved successfully";
    public static final String PUBLISHED_BLOG_POSTS_RETRIEVED = "Published blog posts retrieved successfully";
    public static final String BLOG_POST_RETRIEVED = "Blog post retrieved successfully";
    public static final String BLOG_POST_CREATED = "Blog post created successfully";
    public static final String BLOG_POST_UPDATED = "Blog post updated successfully";
    public static final String BLOG_POST_DELETED = "Blog post deleted successfully";
    public static final String BLOG_POST_PUBLISHED = "Blog post published successfully";
    public static final String BLOG_POST_UNPUBLISHED = "Blog post unpublished successfully";
    public static final String BLOG_POST_SEARCH_RESULTS = "Search results retrieved successfully";
    public static final String BLOG_POST_CATEGORY_ADDED = "Category added to blog post successfully";
    public static final String BLOG_POST_CATEGORY_REMOVED = "Category removed from blog post successfully";
    public static final String BLOG_POST_TAG_ADDED = "Tag added to blog post successfully";
    public static final String BLOG_POST_TAG_REMOVED = "Tag removed from blog post successfully";

    // ── Blog Categories ──────────────────────────────────────────────────

    public static final String BLOG_CATEGORIES_RETRIEVED = "Blog categories retrieved successfully";
    public static final String BLOG_CATEGORY_RETRIEVED = "Blog category retrieved successfully";
    public static final String BLOG_CATEGORY_CREATED = "Blog category created successfully";
    public static final String BLOG_CATEGORY_UPDATED = "Blog category updated successfully";
    public static final String BLOG_CATEGORY_DELETED = "Blog category deleted successfully";

    // ── Blog Tags ────────────────────────────────────────────────────────

    public static final String BLOG_TAGS_RETRIEVED = "Blog tags retrieved successfully";
    public static final String POPULAR_BLOG_TAGS_RETRIEVED = "Popular blog tags retrieved successfully";
    public static final String BLOG_TAG_RETRIEVED = "Blog tag retrieved successfully";
    public static final String BLOG_TAG_CREATED = "Blog tag created successfully";
    public static final String BLOG_TAG_UPDATED = "Blog tag updated successfully";
    public static final String BLOG_TAG_DELETED = "Blog tag deleted successfully";

    // ── Blog Post Images ─────────────────────────────────────────────────

    public static final String BLOG_IMAGE_UPLOADED = "Blog post image uploaded successfully";
    public static final String BLOG_IMAGES_RETRIEVED = "Blog post images retrieved successfully";
    public static final String BLOG_IMAGE_RETRIEVED = "Blog post image retrieved successfully";
    public static final String BLOG_IMAGE_UPDATED = "Blog post image updated successfully";
    public static final String BLOG_IMAGE_DELETED = "Blog post image deleted successfully";
    public static final String BLOG_IMAGE_PRIMARY_SET = "Image set as primary successfully";

    // ── Contact Submissions ──────────────────────────────────────────────

    public static final String CONTACT_SUBMITTED = "Contact form submitted successfully";
    public static final String SUBMISSIONS_RETRIEVED = "Submissions retrieved successfully";
    public static final String SUBMISSION_RETRIEVED = "Submission retrieved successfully";
    public static final String SUBMISSION_STATUS_UPDATED = "Submission status updated successfully";
    public static final String SUBMISSION_DELETED = "Submission deleted successfully";

    // ── Resume ───────────────────────────────────────────────────────────

    public static final String RESUME_RETRIEVED = "Resume retrieved successfully";
    public static final String RESUME_UPLOADED = "Resume uploaded successfully";
    public static final String RESUME_DELETED = "Resume deleted successfully";

    // ── Operations ───────────────────────────────────────────────────────

    public static final String SERVICE_HEALTHY = "Service is healthy";
    public static final String TEXT_ENCRYPTED = "Text encrypted successfully";
    public static final String TEXT_DECRYPTED = "Text decrypted successfully";
    public static final String PASSWORD_HASHED = "Password hashed successfully";
}
