package com.caseyquinn.personal_website.entity;

import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
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
@Table(name = "technologies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "projects")
@ToString(exclude = "projects")
public class Technology {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // "Java", "Spring Boot", "React"
    
    private String version; // "21", "3.2", "18"
    
    @Enumerated(EnumType.STRING)
    private TechnologyCategory category; // LANGUAGE, FRAMEWORK, DATABASE, TOOL, CLOUD
    
    private String iconUrl; // URL to technology icon/logo
    private String color; // Hex color for UI display
    private String documentationUrl;
    
    @Column(name = "proficiency_level")
    @Enumerated(EnumType.STRING)
    private ProficiencyLevel proficiencyLevel; // LEARNING, FAMILIAR, PROFICIENT, EXPERT
    
    @Column(name = "years_experience")
    private Double yearsExperience;
    
    @Builder.Default
    private Boolean featured = false; // Show in skills section
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Bidirectional relationship
    @ManyToMany(mappedBy = "technologies", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Project> projects = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}