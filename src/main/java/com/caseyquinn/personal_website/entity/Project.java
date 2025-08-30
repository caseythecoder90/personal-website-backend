package com.caseyquinn.personal_website.entity;

import com.caseyquinn.personal_website.entity.enums.ProjectType;
import com.caseyquinn.personal_website.entity.enums.ProjectStatus;
import com.caseyquinn.personal_website.entity.enums.DifficultyLevel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "technologies")
@ToString(exclude = "technologies")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Basic Information
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String slug; // URL-friendly version: "personal-website"
    
    @Column(length = 500)
    private String shortDescription; // For cards/previews
    
    @Column(columnDefinition = "TEXT")
    private String fullDescription; // Detailed markdown description
    
    // Legacy field - will be migrated to many-to-many relationship
    private String techStack;
    
    // Project Links
    private String githubUrl;
    private String liveUrl; // Live demo/deployment
    private String dockerUrl; // Docker Hub link if applicable
    private String documentationUrl;
    
    // Project Classification
    @Enumerated(EnumType.STRING)
    @Column(name = "project_type")
    @Builder.Default
    private ProjectType projectType = ProjectType.PERSONAL;
    
    @Enumerated(EnumType.STRING)
    private ProjectStatus status; // IN_PROGRESS, COMPLETED, MAINTAINED, ARCHIVED
    
    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED
    
    // Timeline Information
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "completion_date")
    private LocalDateTime completionDate;
    
    @Column(name = "estimated_hours")
    private Integer estimatedHours; // Time investment
    
    // Display and Sorting
    @Column(name = "display_order")
    private Integer displayOrder; // For controlling order on portfolio
    
    @Builder.Default
    private Boolean featured = false; // Show on homepage
    
    @Builder.Default
    private Boolean published = false;
    
    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;
    
    // Metadata
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "project_technologies",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    @Builder.Default
    private Set<Technology> technologies = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (slug == null && name != null) {
            slug = generateSlug(name);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateSlug(String name) {
        return name.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-")
                   .trim();
    }
    
    // Helper methods for managing relationships
    public void addTechnology(Technology technology) {
        technologies.add(technology);
        technology.getProjects().add(this);
    }
    
    public void removeTechnology(Technology technology) {
        technologies.remove(technology);
        technology.getProjects().remove(this);
    }
}