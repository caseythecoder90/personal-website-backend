package com.caseyquinn.personal_website.entity;

import com.caseyquinn.personal_website.entity.enums.InquiryType;
import com.caseyquinn.personal_website.entity.enums.SubmissionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "inquiry_type", columnDefinition = "inquiry_type")
    @Builder.Default
    private InquiryType inquiryType = InquiryType.GENERAL;
    
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "submission_status")
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.NEW;
    
    @Column(name = "ip_address", columnDefinition = "inet")
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}