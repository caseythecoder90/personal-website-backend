package com.caseyquinn.personal_website.entity;

import com.caseyquinn.personal_website.entity.enums.ProficiencyLevel;
import com.caseyquinn.personal_website.entity.enums.TechnologyCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
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
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 50)
    private String version;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "technology_category")
    private TechnologyCategory category;

    @Column(length = 500)
    private String iconUrl;
    @Column(length = 7)
    private String color;
    @Column(length = 500)
    private String documentationUrl;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "proficiency_level", columnDefinition = "proficiency_level")
    private ProficiencyLevel proficiencyLevel;
    
    @Column(name = "years_experience", precision = 3, scale = 1)
    private BigDecimal yearsExperience;
    
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