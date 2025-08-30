package com.caseyquinn.personal_website.entity;

import com.caseyquinn.personal_website.entity.enums.DeviceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
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

import java.time.LocalDateTime;

@Entity
@Table(name = "page_views")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageView {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "page_path", nullable = false, length = 500)
    private String pagePath;
    
    @Column(length = 500)
    private String referrer;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(length = 2)
    private String country;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}