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
A comprehensive personal portfolio website showcasing technical projects, skills, and professional experience. Built using enterprise-grade patterns to demonstrate advanced Spring Boot capabilities.

### Core Objectives
- **Professional Showcase**: Display projects with rich metadata and analytics
- **Content Management**: Blog system for technical articles and insights
- **Visitor Engagement**: Contact forms, analytics, and SEO optimization
- **Skills Demonstration**: Advanced Spring Boot patterns and architecture

### Technology Stack
```
Backend: Spring Boot 3.5+ with Java 21
Database: PostgreSQL 15 with JSONB support
Caching: Redis for session and performance
Documentation: OpenAPI/Swagger
Testing: JUnit 5, TestContainers
Deployment: Docker, potentially AWS
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
- **Exception Translation**: Separate business and data exceptions
- **DTO Pattern**: Request/Response objects with validation
- **Mapper Pattern**: MapStruct for type-safe conversions
- **Builder Pattern**: Lombok for clean object construction

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

#### **Project Management Domain**
- `Project` - Central entity for portfolio projects
- `Technology` - Skills and tech stack with proficiency tracking
- `ProjectImage` - Visual gallery with type classification
- `LearningOutcome` - Skills/knowledge gained from projects

#### **Content Management Domain** 
- `BlogPost` - Technical articles and insights
- `BlogCategory` - Content organization
- `BlogTag` - Flexible content tagging

#### **User Interaction Domain**
- `ContactSubmission` - Professional inquiries
- `User` - Admin/content management
- `PageView` - General analytics
- `ProjectAnalytics` - Project-specific tracking

#### **SEO & Optimization Domain**
- `SeoMeta` - Search engine optimization metadata

### Entity Relationships Map

```
┌─────────────┐       ┌─────────────────┐       ┌─────────────┐
│   Project   │◄─────►│ ProjectTechnology│◄─────►│ Technology  │
│             │       │   (Join Table)   │       │             │
└──────┬──────┘       └─────────────────┘       └─────────────┘
       │                                                      
       ├──► ProjectImage (1:N)                               
       ├──► LearningOutcome (1:N)                            
       ├──► ProjectAnalytics (1:N)                           
       └──► SeoMeta (1:1)                                    

┌─────────────┐       ┌──────────────────┐      ┌─────────────┐
│  BlogPost   │◄─────►│ BlogPostCategory │◄────►│BlogCategory │
│             │       │   (Join Table)    │      │             │
└──────┬──────┘       └──────────────────┘      └─────────────┘
       │              
       │              ┌──────────────────┐      ┌─────────────┐
       └─────────────►│  BlogPostTag     │◄────►│  BlogTag    │
                      │   (Join Table)    │      │             │
                      └──────────────────┘      └─────────────┘

┌─────────────────┐    ┌─────────────────┐
│ ContactSubmission│    │    PageView     │
│                 │    │                 │  
└─────────────────┘    └─────────────────┘

┌─────────────┐
│    User     │ (Admin/Content Management)
│             │
└─────────────┘
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
SkillCategory: TECHNICAL, SOFT_SKILL, TOOL, METHODOLOGY, ARCHITECTURE

// User Management
UserRole: ADMIN, EDITOR, VIEWER
InquiryType: GENERAL, PROJECT, COLLABORATION, HIRING, FREELANCE
SubmissionStatus: NEW, READ, REPLIED, ARCHIVED

// Analytics & Media
DeviceType: DESKTOP, MOBILE, TABLET
AnalyticsEvent: VIEW, GITHUB_CLICK, DEMO_CLICK, SHARE, DOWNLOAD  
ImageType: THUMBNAIL, SCREENSHOT, ARCHITECTURE_DIAGRAM, UI_MOCKUP, LOGO
```

## API Design

### REST Endpoint Structure
```
/api/v1/
├── /projects              # Portfolio project management
│   ├── GET    /           # List all projects (paginated)
│   ├── POST   /           # Create new project
│   ├── GET    /{id}       # Get project by ID
│   ├── PUT    /{id}       # Update project
│   ├── DELETE /{id}       # Delete project
│   ├── GET    /featured   # Get featured projects
│   ├── GET    /published  # Get published projects
│   └── POST   /{id}/view  # Track project view
│
├── /technologies          # Skills and tech stack
│   ├── GET    /           # List all technologies
│   ├── POST   /           # Add new technology
│   ├── GET    /{id}       # Get technology details
│   ├── PUT    /{id}       # Update technology
│   └── GET    /featured   # Get featured technologies
│
├── /contact              # Professional inquiries
│   ├── POST   /          # Submit contact form
│   ├── GET    /          # List submissions (admin)
│   └── PUT    /{id}      # Update submission status
│
├── /blog                 # Content management
│   ├── /posts
│   │   ├── GET    /      # List blog posts
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
├── /analytics            # Usage tracking
│   ├── GET /projects/{id}/stats  # Project analytics
│   ├── GET /dashboard             # Overall analytics
│   └── POST /track                # Track page view
│
└── /admin                # Administrative functions
    ├── GET /dashboard    # Admin dashboard
    ├── GET /analytics    # Detailed analytics
    └── GET /health       # System health check
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
[Analytics: Track views, tech interests, GitHub clicks]
```

### 2. Potential Employer Journey
```  
Employer → Portfolio → Filter by Technology → Review Projects → 
Read Blog Posts → Contact Form → Submit Inquiry →
[Admin receives notification and inquiry details]
```

### 3. Content Management Journey
```
Admin → Login → Dashboard → View Analytics → 
Create/Edit Project → Upload Images → Publish →
Monitor Contact Submissions → Respond to Inquiries
```

### 4. Blog Reader Journey
```
Reader → Blog Section → Browse Categories → Read Article → 
Related Articles → Share → Comment (future) →
[Track engagement and popular content]
```

## Data Flow

### Project Creation Flow
```
1. Admin creates project via API
2. Service validates business rules
3. DAO saves to database  
4. Technologies linked via junction table
5. Images uploaded and associated
6. SEO metadata generated
7. Analytics tracking initialized
8. Cache invalidated for public endpoints
```

### Contact Submission Flow
```  
1. Visitor submits contact form
2. Validation (email format, required fields)
3. Spam detection (IP rate limiting)
4. Save to database with status=NEW
5. Email notification to admin
6. Auto-response to visitor
7. Admin dashboard shows new submission
```

### Analytics Collection Flow
```
1. User action triggers analytics event
2. Capture: IP, User-Agent, Referrer, Timestamp
3. Async processing to avoid blocking UI
4. Store in analytics tables
5. Aggregate for dashboard displays
6. Generate insights and reports
```

## Security Model

### Authentication & Authorization
- **JWT-based authentication** for admin functions
- **Role-based access control** (ADMIN, EDITOR, VIEWER)
- **API key authentication** for external integrations

### Input Validation & Sanitization
- **Bean Validation** on all DTOs
- **SQL Injection prevention** via JPA/Hibernate
- **XSS protection** on content fields
- **File upload validation** for images

### Privacy & Data Protection
- **IP address anonymization** in analytics
- **GDPR compliance** for contact submissions
- **Data retention policies** for analytics
- **Secure password hashing** (BCrypt)

## Performance Considerations

### Caching Strategy
```
Redis Cache Layers:
├── Application Cache
│   ├── Featured Projects (30min TTL)
│   ├── Technology List (1hr TTL)  
│   └── Blog Posts (15min TTL)
├── Session Cache
│   └── Admin Sessions (24hr TTL)
└── Rate Limiting
    ├── Contact Form (5/hr per IP)
    └── API Endpoints (100/min per IP)
```

### Database Optimization
- **Proper indexing** on frequently queried fields
- **Pagination** for large result sets  
- **Eager/Lazy loading** optimization
- **Connection pooling** configuration
- **Query optimization** with JPQL

### Monitoring & Observability
- **Application metrics** via Spring Actuator
- **Database performance** monitoring
- **API response times** tracking
- **Error rate** monitoring
- **Resource utilization** tracking

## Future Roadmap

### Phase 3: Enhanced Features (Weeks 5-6)
- [ ] Advanced search and filtering
- [ ] Project comparison functionality
- [ ] Skills assessment visualization
- [ ] Interactive project timeline

### Phase 4: Integration & Deployment (Weeks 7-8)  
- [ ] CI/CD pipeline setup
- [ ] AWS deployment configuration
- [ ] CDN for image optimization
- [ ] Backup and disaster recovery

### Phase 5: Advanced Analytics (Weeks 9-10)
- [ ] Google Analytics integration
- [ ] A/B testing framework
- [ ] Conversion tracking
- [ ] Advanced reporting dashboard

### Phase 6: Performance & Scale (Weeks 11-12)
- [ ] Database partitioning
- [ ] Microservices architecture  
- [ ] Event-driven architecture
- [ ] Advanced caching strategies

---

## Next Steps for Design Documentation

1. **Create Entity Relationship Diagram** using dbdiagram.io
2. **Design API Flow Diagrams** using Draw.io  
3. **Build User Journey Maps** using Lucidchart
4. **Create Architecture Diagrams** using PlantUML
5. **Document Feature Specifications** in detail

This system design serves as the foundation for implementation decisions and provides clear documentation for future development phases.