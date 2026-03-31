package com.caseyquinn.personal_website.constants;

/**
 * Constants for Redis cache names and key prefixes.
 * Cache names align with TTL configurations in CacheConfig.
 */
public final class CacheConstants {

    private CacheConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ── Cache Names ──────────────────────────────────────────────────────

    public static final String CACHE_PROJECTS = "projects";
    public static final String CACHE_TECHNOLOGIES = "technologies";
    public static final String CACHE_CERTIFICATIONS = "certifications";
    public static final String CACHE_BLOG_POSTS = "blog_posts";
    public static final String CACHE_BLOG_CATEGORIES = "blog_categories";
    public static final String CACHE_BLOG_TAGS = "blog_tags";
    public static final String CACHE_RESUME = "resume";

    // ── Key Prefixes ─────────────────────────────────────────────────────

    public static final String KEY_ALL = "'all'";
    public static final String KEY_PUBLISHED = "'published'";
    public static final String KEY_FEATURED = "'featured'";
    public static final String KEY_POPULAR = "'popular'";
    public static final String KEY_ACTIVE = "'active'";
    public static final String KEY_BY_ID = "'id:' + #id";
    public static final String KEY_BY_SLUG = "'slug:' + #slug";
    public static final String KEY_BY_CATEGORY = "'category:' + #slug";
    public static final String KEY_BY_TAG = "'tag:' + #slug";
    public static final String KEY_PUBLISHED_PAGINATED = "'published:page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize";
}
