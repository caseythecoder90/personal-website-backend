# Technical Architecture & Infrastructure Design

## Deployment Architecture (Vercel + Railway)

### Overview
```
┌─────────────────────────────────────────────────────────────┐
│                     VERCEL (Frontend)                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │
│  │   React App     │  │  Admin Dashboard│  │    Blog     │  │
│  │   (Portfolio)   │  │      (Admin)    │  │  (Content)  │  │
│  └─────────────────┘  └─────────────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────────────┘
                                │
                        HTTPS API Calls
                                │
┌─────────────────────────────────────────────────────────────┐
│                    RAILWAY (Backend)                       │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │              Spring Boot Application                   │  │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐   │  │
│  │  │     API     │ │   Admin     │ │   File Serving  │   │  │
│  │  │  Gateway    │ │    Auth     │ │   (Resume)      │   │  │
│  │  └─────────────┘ └─────────────┘ └─────────────────┘   │  │
│  └─────────────────────────────────────────────────────────┘  │
│  ┌─────────────┐                    ┌─────────────────────┐   │
│  │ PostgreSQL  │                    │      Redis          │   │
│  │ (Primary DB)│                    │  (Cache + Queue)    │   │
│  └─────────────┘                    └─────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                                │
                        External Services
                                │
┌─────────────────────────────────────────────────────────────┐
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐ │
│  │   Resend    │ │  Cloudinary │ │      reCAPTCHA          │ │
│  │  (Emails)   │ │  (Images)   │ │   (Spam Protection)     │ │
│  └─────────────┘ └─────────────┘ └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Authentication & Authorization System

### JWT Token Flow
```mermaid
sequenceDiagram
    participant A as Admin UI
    participant AG as API Gateway
    participant AS as Auth Service
    participant DB as Database
    participant R as Redis

    A->>AG: POST /api/v1/auth/login
    AG->>AS: Forward login request
    AS->>DB: Validate credentials
    DB-->>AS: User details + role
    AS->>AS: Generate JWT token
    AS->>R: Cache token (optional blacklist)
    AS-->>AG: Return JWT + user info
    AG-->>A: 200 OK + JWT token
    
    Note over A: Store JWT in secure storage
    
    A->>AG: POST /api/v1/projects (with JWT header)
    AG->>AG: Validate JWT signature
    AG->>AS: Verify token + extract claims
    AS->>R: Check token blacklist (if implemented)
    AS-->>AG: Token valid + user role
    AG->>AG: Check role permissions (ADMIN required)
    AG->>AG: Forward to ProjectController
```

### Security Configuration
```java
// JWT Token Structure
{
  "sub": "admin@casey.dev",
  "role": "ADMIN", 
  "iat": 1704123600,
  "exp": 1704210000,  // 24 hour expiry
  "iss": "personal-website-api"
}

// Protected Endpoints
/api/v1/projects/**     → ADMIN only
/api/v1/blog/**         → ADMIN only  
/api/v1/contact/admin/** → ADMIN only
/api/v1/analytics/**    → ADMIN only
/api/v1/resume/upload   → ADMIN only

// Public Endpoints  
/api/v1/projects (GET)  → Public
/api/v1/contact (POST)  → Public (rate limited)
/api/v1/resume/download → Public (tracked)
```

## Rate Limiting & Spam Protection

### Redis-Based Rate Limiting
```java
// Rate Limiting Rules
@Configuration
public class RateLimitConfig {
    // Contact form: 5 submissions per hour per IP
    public static final String CONTACT_RATE_LIMIT = "contact:rate_limit";
    
    // API calls: 100 requests per minute per IP  
    public static final String API_RATE_LIMIT = "api:rate_limit";
    
    // Admin endpoints: 1000 requests per minute (after auth)
    public static final String ADMIN_RATE_LIMIT = "admin:rate_limit";
}

// Implementation with Bucket4j + Redis
@Component
public class RateLimitService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    public boolean allowRequest(String key, int maxRequests, Duration window) {
        // Sliding window rate limiting implementation
        return bucket.tryConsume(1);
    }
}
```

### Spam Detection Layers
```java
// Multi-layer spam detection
@Service 
public class SpamDetectionService {
    
    // Layer 1: Rate limiting by IP
    public boolean checkIPRateLimit(String ipAddress);
    
    // Layer 2: Content pattern analysis
    public boolean checkContentPatterns(String message) {
        List<String> spamKeywords = Arrays.asList(
            "bitcoin", "crypto", "earn money", "click here", 
            "viagra", "casino", "lottery", "winner"
        );
        return containsSpamPattern(message, spamKeywords);
    }
    
    // Layer 3: reCAPTCHA verification
    public boolean verifyRecaptcha(String token, String ipAddress);
}
```

## Caching Strategy

### Redis Cache Architecture
```
Redis Cache Layers:
├── L1: HTTP Response Cache
│   ├── /api/v1/projects (15 min TTL)
│   ├── /api/v1/technologies (1 hour TTL)
│   └── /api/v1/blog/posts (30 min TTL)
├── L2: Data Layer Cache  
│   ├── Project entities (1 hour TTL)
│   ├── Featured projects (30 min TTL)
│   └── Blog post content (1 hour TTL)
├── L3: Session & Auth Cache
│   ├── JWT token blacklist (24 hour TTL)
│   └── Admin session data (24 hour TTL)
└── L4: Rate Limiting & Analytics
    ├── IP rate limit counters (1 hour TTL)
    ├── Analytics event queue (1 day TTL)
    └── Spam detection cache (1 hour TTL)
```

### Cache Implementation
```java
@Service
@CacheConfig(cacheNames = "projects")
public class ProjectService {
    
    @Cacheable(key = "#published")
    public List<ProjectResponse> getProjects(boolean published) {
        // Cache published projects for 15 minutes
    }
    
    @CacheEvict(allEntries = true)
    public ProjectResponse createProject(CreateProjectRequest request) {
        // Invalidate all project caches on create/update
    }
}
```

## Email Notification System

### Resend.com Integration
```java
@Service
public class EmailService {
    private final ResendClient resendClient;
    
    // Contact form confirmation
    public void sendContactConfirmation(ContactSubmission submission) {
        Email email = Email.builder()
            .from("noreply@caseyquinn.dev")
            .to(submission.getEmail())
            .subject("Thanks for reaching out!")
            .html(loadTemplate("contact-confirmation", submission))
            .build();
        
        resendClient.emails().send(email);
    }
    
    // Admin notification for new contact
    public void notifyAdminNewContact(ContactSubmission submission) {
        Email email = Email.builder()
            .from("notifications@caseyquinn.dev") 
            .to("admin@caseyquinn.dev")
            .subject("New Contact Form Submission")
            .html(loadTemplate("admin-contact-notification", submission))
            .build();
            
        resendClient.emails().send(email);
    }
}
```

### Email Templates
```html
<!-- contact-confirmation.html -->
<html>
<head><title>Thanks for reaching out!</title></head>
<body>
    <h2>Hi {{name}},</h2>
    <p>Thanks for your message! I'll get back to you within 24 hours.</p>
    <p><strong>Your message:</strong></p>
    <blockquote>{{message}}</blockquote>
    <p>Best regards,<br>Casey Quinn</p>
</body>
</html>

<!-- admin-contact-notification.html -->
<html>
<body>
    <h2>New Contact Form Submission</h2>
    <p><strong>From:</strong> {{name}} ({{email}})</p>
    <p><strong>Type:</strong> {{inquiryType}}</p>
    <p><strong>Message:</strong></p>
    <blockquote>{{message}}</blockquote>
    <p><a href="{{adminDashboardUrl}}">View in Admin Dashboard</a></p>
</body>
</html>
```

## Resume Hosting & Download System

### Resume Management API
```java
@RestController
@RequestMapping("/api/v1/resume")
public class ResumeController {
    
    // Public download endpoint with analytics
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadResume(HttpServletRequest request) {
        // Track download event
        analyticsService.trackEvent(AnalyticsEvent.RESUME_DOWNLOAD, 
                                   request.getRemoteAddr());
        
        Resource resume = resumeService.getCurrentResume();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=Casey-Quinn-Resume.pdf")
            .header("Content-Type", "application/pdf")
            .body(resume);
    }
    
    // Admin upload endpoint  
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        resumeService.updateResume(file);
        return ResponseEntity.ok().build();
    }
    
    // Get resume metadata
    @GetMapping("/info")
    public ResumeInfo getResumeInfo() {
        return resumeService.getResumeInfo(); // Last updated, download count, etc.
    }
}
```

### Resume Storage Strategy
```java
@Service
public class ResumeService {
    private final Path resumeStoragePath = Paths.get("uploads/resume/");
    
    public Resource getCurrentResume() {
        // Always serve the latest version
        Path latestResume = findLatestResumeFile();
        return new UrlResource(latestResume.toUri());
    }
    
    public void updateResume(MultipartFile file) {
        // Validate PDF file
        validateResumeFile(file);
        
        // Archive old version
        archiveCurrentResume();
        
        // Save new version with timestamp
        String filename = "Casey-Quinn-Resume-" + 
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + 
                         ".pdf";
        
        Path newResumePath = resumeStoragePath.resolve(filename);
        Files.copy(file.getInputStream(), newResumePath);
        
        // Update metadata
        updateResumeMetadata(filename);
        
        // Clear resume cache
        cacheManager.getCache("resume").clear();
    }
}
```

## Async Analytics Processing

### Queue-Based Analytics System
```java
// Simple Redis-based queue for analytics events
@Service
public class AnalyticsQueueService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ANALYTICS_QUEUE = "analytics:events";
    
    public void queueEvent(AnalyticsEvent event) {
        AnalyticsEventDto dto = AnalyticsEventDto.builder()
            .eventType(event.getType())
            .entityId(event.getEntityId())
            .ipAddress(event.getIpAddress())
            .userAgent(event.getUserAgent())
            .timestamp(LocalDateTime.now())
            .build();
            
        redisTemplate.opsForList().leftPush(ANALYTICS_QUEUE, dto);
    }
    
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void processAnalyticsEvents() {
        List<AnalyticsEventDto> events = new ArrayList<>();
        
        // Batch process up to 100 events
        for (int i = 0; i < 100; i++) {
            Object event = redisTemplate.opsForList().rightPop(ANALYTICS_QUEUE);
            if (event == null) break;
            events.add((AnalyticsEventDto) event);
        }
        
        if (!events.isEmpty()) {
            analyticsService.batchProcessEvents(events);
        }
    }
}

// Async processing service
@Service
public class AnalyticsProcessingService {
    
    @Async("analyticsTaskExecutor")
    public CompletableFuture<Void> batchProcessEvents(List<AnalyticsEventDto> events) {
        // Group events by type
        Map<String, List<AnalyticsEventDto>> eventsByType = 
            events.stream().collect(Collectors.groupingBy(AnalyticsEventDto::getEventType));
        
        // Process each type differently
        eventsByType.forEach((type, eventList) -> {
            switch (type) {
                case "PAGE_VIEW" -> processPageViews(eventList);
                case "PROJECT_VIEW" -> processProjectViews(eventList);
                case "RESUME_DOWNLOAD" -> processResumeDownloads(eventList);
                case "GITHUB_CLICK" -> processGitHubClicks(eventList);
            }
        });
        
        return CompletableFuture.completedFuture(null);
    }
}
```

### Analytics Dashboard Data Aggregation
```java
@Service 
public class AnalyticsDashboardService {
    
    @Cacheable(value = "dashboard-metrics", unless = "#result == null")
    public DashboardMetrics getDashboardMetrics(LocalDate startDate, LocalDate endDate) {
        return DashboardMetrics.builder()
            .totalPageViews(getPageViewCount(startDate, endDate))
            .uniqueVisitors(getUniqueVisitorCount(startDate, endDate))
            .topProjects(getTopProjectsByViews(startDate, endDate, 5))
            .resumeDownloads(getResumeDownloadCount(startDate, endDate))
            .contactSubmissions(getContactSubmissionCount(startDate, endDate))
            .topCountries(getTopCountriesByVisitors(startDate, endDate, 10))
            .deviceBreakdown(getDeviceBreakdown(startDate, endDate))
            .build();
    }
}
```

## Configuration & Environment Setup

### Railway Environment Variables
```bash
# Database
DATABASE_URL=postgresql://user:password@hostname:port/database

# Redis  
REDIS_URL=redis://hostname:port

# JWT
JWT_SECRET=your-super-secure-secret-key-here
JWT_EXPIRATION=86400000  # 24 hours in milliseconds

# Email (Resend)
RESEND_API_KEY=re_your-resend-api-key
ADMIN_EMAIL=admin@caseyquinn.dev

# External Services
RECAPTCHA_SECRET_KEY=your-recaptcha-secret
CLOUDINARY_URL=cloudinary://api_key:secret@cloud_name

# Application
FRONTEND_URL=https://your-vercel-app.vercel.app
ADMIN_DASHBOARD_URL=https://admin.caseyquinn.dev
```

### Spring Boot Configuration
```yaml
# application-production.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    hikari:
      maximum-pool-size: 5
      minimum-idle: 2
      
  redis:
    url: ${REDIS_URL}
    
  cache:
    type: redis
    redis:
      time-to-live: 900000  # 15 minutes default
      
security:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION}
    
resend:
  api-key: ${RESEND_API_KEY}
  
recaptcha:
  secret-key: ${RECAPTCHA_SECRET_KEY}
  
app:
  frontend-url: ${FRONTEND_URL}
  admin-email: ${ADMIN_EMAIL}
  resume:
    storage-path: ./uploads/resume/
    max-file-size: 10MB
```

This technical architecture provides a robust, scalable foundation for your portfolio website with clear separation of concerns and industry-standard practices for each component.