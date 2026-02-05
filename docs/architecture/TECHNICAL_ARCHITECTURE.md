# Technical Architecture & Infrastructure Design

## Deployment Architecture (Vercel + Railway)

### Overview
```
┌─────────────────────────────────────────────────────────────┐
│                     VERCEL (Frontend)                       │
│  ┌─────────────────┐  ┌─────────────────────────────────┐   │
│  │   React App     │  │          Blog (Content)         │   │
│  │   (Portfolio)   │  │                                 │   │
│  └─────────────────┘  └─────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                                │
                        HTTPS API Calls
                                │
┌─────────────────────────────────────────────────────────────┐
│                    RAILWAY (Backend)                         │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Spring Boot Application                │    │
│  │  ┌─────────────┐  ┌─────────────────────────────┐   │    │
│  │  │     API     │  │   File Serving (Resume)     │   │    │
│  │  │  Endpoints  │  │                             │   │    │
│  │  └─────────────┘  └─────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────┐                    ┌─────────────────┐     │
│  │ PostgreSQL  │                    │      Redis      │     │
│  │ (Primary DB)│                    │     (Cache)     │     │
│  └─────────────┘                    └─────────────────┘     │
└─────────────────────────────────────────────────────────────┘
                                │
                        External Services
                                │
┌─────────────────────────────────────────────────────────────┐
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐    │
│  │   Resend    │ │  Cloudinary │ │      reCAPTCHA      │    │
│  │  (Emails)   │ │  (Images)   │ │   (Spam Protection) │    │
│  └─────────────┘ └─────────────┘ └─────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

## Database & Migration Strategy

### Flyway Migrations
All schema changes are version-controlled via Flyway. Migration files live at `src/main/resources/db/migration/`.

```
db/migration/
└── V1__initial_schema.sql   # All tables, enums, indexes, FK constraints
```

`application.yml` sets `ddl-auto: validate` so Hibernate only validates against the schema — Flyway owns all DDL.

### Current Tables
- `technologies` — skills with proficiency and category tracking
- `projects` — portfolio projects with full metadata
- `project_technologies` — M:N join table
- `project_images` — image galleries per project
- `blog_categories` / `blog_tags` / `blog_posts` — blog system
- `blog_post_categories` / `blog_post_tags` — blog M:N joins
- `contact_submissions` — contact form submissions

## Rate Limiting & Spam Protection

### Redis-Based Rate Limiting (Planned)
```java
// Rate Limiting Rules
@Configuration
public class RateLimitConfig {
    // Contact form: 5 submissions per hour per IP
    public static final String CONTACT_RATE_LIMIT = "contact:rate_limit";

    // API calls: 100 requests per minute per IP
    public static final String API_RATE_LIMIT = "api:rate_limit";
}
```

### Spam Detection Layers (Planned)
```java
// Multi-layer spam detection
@Service
public class SpamDetectionService {

    // Layer 1: Rate limiting by IP
    public boolean checkIPRateLimit(String ipAddress);

    // Layer 2: Content pattern analysis
    public boolean checkContentPatterns(String message);

    // Layer 3: reCAPTCHA verification
    public boolean verifyRecaptcha(String token, String ipAddress);
}
```

## Caching Strategy

### Redis Cache Architecture
```
Redis Cache Layers:
├── L1: Application Cache
│   ├── /api/v1/projects (15 min TTL)
│   ├── /api/v1/technologies (1 hour TTL)
│   └── /api/v1/blog/posts (30 min TTL)
├── L2: Data Layer Cache
│   ├── Project entities (1 hour TTL)
│   ├── Featured projects (30 min TTL)
│   └── Blog post content (1 hour TTL)
└── L3: Rate Limiting
    ├── IP rate limit counters (1 hour TTL)
    └── Spam detection cache (1 hour TTL)
```

### Cache Implementation Pattern
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

### Resend.com Integration (Planned)
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

    // Owner notification for new contact
    public void notifyOwnerNewContact(ContactSubmission submission) {
        Email email = Email.builder()
            .from("notifications@caseyquinn.dev")
            .to("casey@caseyquinn.dev")
            .subject("New Contact Form Submission")
            .html(loadTemplate("contact-notification", submission))
            .build();

        resendClient.emails().send(email);
    }
}
```

### Email Templates
```html
<!-- contact-confirmation.html -->
<html>
<body>
    <h2>Hi {{name}},</h2>
    <p>Thanks for your message! I'll get back to you within 24 hours.</p>
    <p><strong>Your message:</strong></p>
    <blockquote>{{message}}</blockquote>
    <p>Best regards,<br>Casey Quinn</p>
</body>
</html>

<!-- contact-notification.html -->
<html>
<body>
    <h2>New Contact Form Submission</h2>
    <p><strong>From:</strong> {{name}} ({{email}})</p>
    <p><strong>Type:</strong> {{inquiryType}}</p>
    <p><strong>Message:</strong></p>
    <blockquote>{{message}}</blockquote>
</body>
</html>
```

## Resume Hosting & Download System

### Resume Management API (Planned)
```java
@RestController
@RequestMapping("/api/v1/resume")
public class ResumeController {

    // Public download endpoint
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadResume() {
        Resource resume = resumeService.getCurrentResume();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=Casey-Quinn-Resume.pdf")
            .header("Content-Type", "application/pdf")
            .body(resume);
    }

    // Upload endpoint (via API, no admin UI needed)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file) {
        resumeService.updateResume(file);
        return ResponseEntity.ok().build();
    }

    // Get resume metadata
    @GetMapping("/info")
    public ResumeInfo getResumeInfo() {
        return resumeService.getResumeInfo();
    }
}
```

### Resume Storage Strategy
```java
@Service
public class ResumeService {
    private final Path resumeStoragePath = Paths.get("uploads/resume/");

    public Resource getCurrentResume() {
        Path latestResume = findLatestResumeFile();
        return new UrlResource(latestResume.toUri());
    }

    public void updateResume(MultipartFile file) {
        validateResumeFile(file);
        archiveCurrentResume();

        String filename = "Casey-Quinn-Resume-" +
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) +
                         ".pdf";

        Path newResumePath = resumeStoragePath.resolve(filename);
        Files.copy(file.getInputStream(), newResumePath);
        updateResumeMetadata(filename);
        cacheManager.getCache("resume").clear();
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

# Email (Resend)
RESEND_API_KEY=re_your-resend-api-key
OWNER_EMAIL=casey@caseyquinn.dev

# External Services
RECAPTCHA_SECRET_KEY=your-recaptcha-secret
CLOUDINARY_URL=cloudinary://api_key:secret@cloud_name

# Application
FRONTEND_URL=https://your-vercel-app.vercel.app
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

resend:
  api-key: ${RESEND_API_KEY}

recaptcha:
  secret-key: ${RECAPTCHA_SECRET_KEY}

app:
  frontend-url: ${FRONTEND_URL}
  owner-email: ${OWNER_EMAIL}
  resume:
    storage-path: ./uploads/resume/
    max-file-size: 10MB
```

This technical architecture provides a robust, scalable foundation for the portfolio website with clear separation of concerns and industry-standard practices for each component.
