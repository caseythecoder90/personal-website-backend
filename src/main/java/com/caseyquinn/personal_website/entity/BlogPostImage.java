package com.caseyquinn.personal_website.entity;

import com.caseyquinn.personal_website.entity.enums.BlogImageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

/**
 * Entity representing an image associated with a blog post.
 * Images can be featured, inline within content, gallery images, or thumbnails.
 */
@Entity
@Table(name = "blog_post_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "blogPost")
@ToString(exclude = "blogPost")
public class BlogPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_post_id", nullable = false)
    private BlogPost blogPost;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "cloudinary_public_id", length = 500)
    private String cloudinaryPublicId;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(length = 500)
    private String caption;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "image_type", columnDefinition = "blog_image_type")
    @Builder.Default
    private BlogImageType imageType = BlogImageType.INLINE;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
