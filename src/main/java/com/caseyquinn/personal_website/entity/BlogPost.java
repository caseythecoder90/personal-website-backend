package com.caseyquinn.personal_website.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"categories", "tags"})
@ToString(exclude = {"categories", "tags"})
public class BlogPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, unique = true)
    private String slug;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String excerpt;
    
    @Builder.Default
    private Boolean published = false;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;
    
    @Column(name = "read_time_minutes")
    private Integer readTimeMinutes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "blog_post_categories",
        joinColumns = @JoinColumn(name = "blog_post_id"),
        inverseJoinColumns = @JoinColumn(name = "blog_category_id")
    )
    @Builder.Default
    private Set<BlogCategory> categories = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "blog_post_tags",
        joinColumns = @JoinColumn(name = "blog_post_id"),
        inverseJoinColumns = @JoinColumn(name = "blog_tag_id")
    )
    @Builder.Default
    private Set<BlogTag> tags = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (slug == null && title != null) {
            slug = generateSlug(title);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateSlug(String title) {
        return title.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-")
                   .trim();
    }
    
    public void addCategory(BlogCategory category) {
        categories.add(category);
        category.getBlogPosts().add(this);
    }
    
    public void removeCategory(BlogCategory category) {
        categories.remove(category);
        category.getBlogPosts().remove(this);
    }
    
    public void addTag(BlogTag tag) {
        tags.add(tag);
        tag.getBlogPosts().add(this);
    }
    
    public void removeTag(BlogTag tag) {
        tags.remove(tag);
        tag.getBlogPosts().remove(this);
    }
}