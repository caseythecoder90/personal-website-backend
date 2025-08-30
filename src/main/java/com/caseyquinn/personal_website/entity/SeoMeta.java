package com.caseyquinn.personal_website.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "seo_meta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoMeta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Column(name = "meta_title")
    private String metaTitle;
    
    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;
    
    @Column(name = "og_title")
    private String ogTitle;
    
    @Column(name = "og_description", columnDefinition = "TEXT")
    private String ogDescription;
    
    @Column(name = "og_image", length = 500)
    private String ogImage;
    
    @Column(name = "canonical_url", length = 500)
    private String canonicalUrl;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "structured_data", columnDefinition = "jsonb")
    private Map<String, Object> structuredData;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}