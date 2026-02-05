# Personal Website Portfolio - System Design Documentation

## Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Patterns](#architecture-patterns)
3. [Domain Model](#domain-model)
4. [API Design](#api-design)
5. [User Journeys](#user-journeys)
6. [Data Flow](#data-flow)
7. [Security Model](#security-model)
8. [Performance Considerations](#performance-considerations)
9. [Future Roadmap](#future-roadmap)

## System Overview

### Vision Statement
A professional portfolio website showcasing technical projects, skills, and professional experience. Built using enterprise-grade patterns to demonstrate advanced Spring Boot capabilities.

### Core Objectives
- **Professional Showcase**: Display projects with rich metadata, images, and technology associations
- **Content Management**: Blog system for technical articles and insights
- **Visitor Engagement**: Contact forms for professional inquiries
- **Skills Demonstration**: Advanced Spring Boot patterns and architecture

### Technology Stack
```
Backend: Spring Boot 3.5+ with Java 21
Database: PostgreSQL with Flyway migrations
Caching: Redis for session and performance
Documentation: OpenAPI/Swagger
Testing: JUnit 5
Deployment: Docker, potentially Railway
```

## Architecture Patterns

### Layered Architecture
```
┌─────────────────┐
│   Controllers   │ ← REST API Layer
├─────────────────┤
│   Services      │ ← Business Logic Layer
├─────────────────┤
│     DAOs        │ ← Data Access Layer
├─────────────────┤
│  Repositories   │ ← JPA/Hibernate Layer
├─────────────────┤
│   Database      │ ← PostgreSQL + Redis
└─────────────────┘
```

### Key Patterns Implemented
- **DAO Pattern**: Clean abstraction over JPA repositories
- **Exception Translation**: Separate business and data exceptions with ErrorCode enum
- **DTO Pattern**: Request/Response objects with validation
- **Mapper Pattern**: MapStruct for type-safe conversions
- **Builder Pattern**: Lombok for clean object construction
- **Spring Retry**: Automatic retry on transient data access failures

### Package Structure
```
com.caseyquinn.personal_website/
├── config/          # Configuration classes
├── controller/      # REST endpoints
├── dao/            # Data access objects
│   └── impl/       # DAO implementations
├── dto/            # Data transfer objects
│   ├── request/    # API request DTOs
│   └── response/   # API response DTOs
├── entity/         # JPA entities
│   └── enums/      # Enumeration types
├── exception/      # Custom exceptions
│   ├── business/   # Business rule violations
│   └── data/       # Data access errors
├── mapper/         # MapStruct mappers
├── repository/     # JPA repositories
└── service/        # Business logic services
```

## Domain Model

### Core Entities Overview

#### **Portfolio Domain** (Active — fully wired)
- `Project` - Central entity for portfolio projects
- `Technology` - Skills and tech stack with proficiency tracking
- `ProjectImage` - Visual gallery with type classification

#### **Blog Domain** (Entities defined — implementation next)
- `BlogPost` - Technical articles and insights
- `BlogCategory` - Content organization
- `BlogTag` - Flexible content tagging

#### **Contact Domain** (Entity defined — implementation next)
- `ContactSubmission` - Professional inquiries

### Entity Relationships Map

```
┌─────────────┐       ┌─────────────────┐       ┌─────────────┐
│   Project   │◄─────►│ ProjectTechnology│◄─────►│ Technology  │
│             │       │   (Join Table)   │       │             │
└──────┬──────┘       └─────────────────┘       └─────────────┘
       │
       └──► ProjectImage (1:N)

┌─────────────┐       ┌──────────────────┐      ┌─────────────┐
│  BlogPost   │◄─────►│ BlogPostCategory │◄────►│BlogCategory │
│             │       │   (Join Table)    │      │             │
└──────┬──────┘       └──────────────────┘      └─────────────┘
       │
       │              ┌──────────────────┐      ┌─────────────┐
       └─────────────►│  BlogPostTag     │◄────►│  BlogTag    │
                      │   (Join Table)    │      │             │
                      └──────────────────┘      └─────────────┘

┌─────────────────┐
│ ContactSubmission│  (Standalone)
│                 │
└─────────────────┘
```

### Enumeration Types
```java
// Project Classification
ProjectType: PERSONAL, PROFESSIONAL, OPEN_SOURCE, LEARNING, FREELANCE
ProjectStatus: PLANNING, IN_PROGRESS, COMPLETED, MAINTAINED, ARCHIVED
DifficultyLevel: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

// Technology & Skills
TechnologyCategory: LANGUAGE, FRAMEWORK, LIBRARY, DATABASE, TOOL, CLOUD, DEPLOYMENT, TESTING
ProficiencyLevel: LEARNING, FAMILIAR, PROFICIENT, EXPERT

// Contact
InquiryType: GENERAL, PROJECT, COLLABORATION, HIRING, FREELANCE
SubmissionStatus: NEW, READ, REPLIED, ARCHIVED

// Media
ImageType: THUMBNAIL, SCREENSHOT, ARCHITECTURE_DIAGRAM, UI_MOCKUP, LOGO
```

## API Design

### REST Endpoint Structure
```
/api/v1/
├── /projects              # Portfolio project management
│   ├── GET    /           # List all projects (paginated)
│   ├── POST   /           # Create new project (metadata only)
│   ├── GET    /{id}       # Get project by ID
│   ├── PUT    /{id}       # Update project
│   ├── DELETE /{id}       # Delete project
│   ├── GET    /featured   # Get featured projects
│   ├── GET    /published  # Get published projects
│   └── /{id}/images       # Project image management
│       ├── GET    /       # List project images
│       ├── POST   /       # Upload new images (multipart/form-data)
│       ├── PUT    /{imageId}  # Update image metadata
│       ├── DELETE /{imageId}  # Remove image
│       └── PUT    /{imageId}/primary  # Set as primary image
│
├── /technologies          # Skills and tech stack
│   ├── GET    /           # List all technologies
│   ├── POST   /           # Add new technology
│   ├── GET    /{id}       # Get technology details
│   ├── PUT    /{id}       # Update technology
│   └── GET    /featured   # Get featured technologies
│
├── /contact              # Professional inquiries
│   └── POST  /           # Submit contact form
│
├── /blog                 # Content management (upcoming)
│   ├── /posts
│   │   ├── GET    /      # List published blog posts
│   │   ├── POST   /      # Create new post
│   │   ├── GET    /{id}  # Get post by ID
│   │   └── PUT    /{id}  # Update post
│   ├── /categories
│   │   ├── GET    /      # List categories
│   │   └── POST   /      # Create category
│   └── /tags
│       ├── GET    /      # List tags
│       └── POST   /      # Create tag
│
└── /resume               # Resume download (upcoming)
    └── GET /download     # Download current resume PDF
```

### API Response Structure
```json
{
  "success": true,
  "data": { /* response payload */ },
  "metadata": {
    "timestamp": "2024-01-15T10:30:00Z",
    "pagination": {
      "page": 1,
      "size": 10,
      "totalElements": 25,
      "totalPages": 3
    }
  },
  "errors": null
}
```

### Error Response Structure
```json
{
  "success": false,
  "data": null,
  "errors": [
    {
      "code": "VALIDATION_ERROR",
      "message": "Project name must be unique",
      "field": "name",
      "timestamp": "2024-01-15T10:30:00Z"
    }
  ]
}
```

## User Journeys

### 1. Visitor Exploring Portfolio
```
Visitor → Homepage → View Featured Projects → Click Project →
View Project Details → Check Tech Stack → Visit GitHub →
Browse related projects by technology
```

### 2. Potential Employer Journey
```
Employer → Portfolio → Filter by Technology → Review Projects →
Read Blog Posts → Contact Form → Submit Inquiry →
Owner receives email notification
```

### 3. Blog Reader Journey
```
Reader → Blog Section → Browse Categories → Read Article →
Related Articles → Share →
[Discover related projects via shared tags]
```

## Data Flow

### Project Creation Flow (Separate Endpoints)
```
1. API client creates project via POST (metadata only)
2. Service validates business rules
3. DAO saves project to database
4. Technologies linked via junction table
5. Cache invalidated for public endpoints
6. [Separate Step] ProjectImages uploaded via dedicated endpoint
   - POST /api/v1/projects/{id}/images
   - Image validation (size, format, dimensions)
   - Auto-generate thumbnails if needed
   - Set primary image and display order
   - Store metadata (alt text, captions)
   - Update project cache
```

### Contact Submission Flow
```
1. Visitor submits contact form
2. Validation (email format, required fields)
3. Spam detection (IP rate limiting)
4. Save to database with status=NEW
5. Email notification to owner
6. Auto-response to visitor
```

## Security Model

### Input Validation & Sanitization
- **Bean Validation** on all DTOs
- **SQL Injection prevention** via JPA/Hibernate parameterized queries
- **XSS protection** on content fields
- **File upload validation** for images

### Privacy & Data Protection
- **IP address logging** in contact submissions for spam detection
- **GDPR-aware** contact data handling
- **Secure credential management** via environment variables

## Performance Considerations

### Caching Strategy
```
Redis Cache Layers:
├── Application Cache
│   ├── Featured Projects (30min TTL)
│   ├── Technology List (1hr TTL)
│   └── Blog Posts (15min TTL)
└── Rate Limiting
    ├── Contact Form (5/hr per IP)
    └── API Endpoints (100/min per IP)
```

### Database Optimization
- **Flyway migrations** for versioned, repeatable schema management
- **Proper indexing** on frequently queried fields (slugs, status, featured, timestamps)
- **Pagination** for large result sets
- **Eager/Lazy loading** optimization
- **Connection pooling** via HikariCP

### Monitoring & Observability
- **Application metrics** via Spring Actuator
- **Health check** endpoints
- **Structured logging** via Logback with compact console pattern

## Future Roadmap

### Portfolio Phase (Current)
- [x] Project CRUD with DAO pattern
- [x] Technology management
- [x] Flyway database migrations
- [ ] ProjectImage upload endpoints
- [ ] Redis caching implementation

### Blog Phase (Next)
- [ ] Blog post CRUD endpoints
- [ ] Category and tag management
- [ ] Content filtering and search

### Contact & Resume Phase
- [ ] Contact form processing with email notifications
- [ ] Resume upload and download
- [ ] Rate limiting implementation

### Production Phase
- [ ] CI/CD pipeline setup
- [ ] Railway/Vercel deployment
- [ ] CDN for image optimization

---

This system design serves as the foundation for implementation decisions and provides clear documentation for future development phases.
