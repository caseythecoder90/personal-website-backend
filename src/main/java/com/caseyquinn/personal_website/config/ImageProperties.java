package com.caseyquinn.personal_website.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Configuration properties for image upload validation and limits.
 */
@Component
@ConfigurationProperties(prefix = "app.images")
@Getter
@Setter
public class ImageProperties {

    private int maxPerProject = 20;
    private int maxPerBlogPost = 20;
    private long maxFileSize = 10485760;
    private Set<String> allowedContentTypes = Set.of(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    );
}