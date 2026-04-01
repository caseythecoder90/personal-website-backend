# Implementation Planning Checklist

## Overview
Comprehensive checklist for implementing all features of the Personal Website Portfolio system based on documented requirements and API flows.

---

## Phase 1: Portfolio Foundation (Done)

### Project & Technology CRUD
- [x] **Project Entity & Repository**
  - [x] Project entity with all metadata fields
  - [x] ProjectRepository with Spring Data JPA
  - [x] ProjectDao + ProjectDaoImpl with @RetryableDataAccess

- [x] **Technology Entity & Repository**
  - [x] Technology entity with proficiency tracking
  - [x] TechnologyRepository
  - [x] TechnologyDao + TechnologyDaoImpl with @RetryableDataAccess

- [x] **Service & Controller Layer**
  - [x] ProjectService with business rules
  - [x] TechnologyService with all CRUD + filtering (category, proficiency, featured, most-used)
  - [x] ProjectController with full CRUD + pagination
  - [x] TechnologyController with full CRUD + filtering endpoints
  - [x] DTO/Mapper layer (MapStruct)

- [x] **Exception Handling**
  - [x] ErrorCode enum for all error types
  - [x] BusinessException / ValidationException / DuplicateResourceException
  - [x] NotFoundException / ForbiddenException / RateLimitExceededException
  - [x] GlobalExceptionHandler with proper HTTP status mapping
  - [x] MissingServletRequestParameterException handling (400 not 500)
  - [x] @RetryableDataAccess annotation for transient failures

- [x] **Database & Migration**
  - [x] spring-boot-starter-flyway dependency
  - [x] V1__initial_schema.sql — all tables, enums, indexes, constraints
  - [x] V2-V8 migrations (cloudinary, users, project_links, certifications, blog images, resumes)
  - [x] ddl-auto set to validate

- [x] **Configuration & Logging**
  - [x] application.yml with PostgreSQL + Redis config (all secrets via env vars)
  - [x] application-production.yml profile overrides
  - [x] Logback compact console pattern
  - [x] OpenAPI/Swagger configuration with Bearer token security scheme

---

## Phase 2: Portfolio Enhancement (Done)

### Project Image Management
- [x] **Image Upload Endpoints**
  - [x] POST /api/v1/projects/{id}/images — multipart upload
  - [x] GET /api/v1/projects/{id}/images — list images
  - [x] GET /api/v1/projects/{id}/images/{imageId} — get single image
  - [x] PUT /api/v1/projects/{id}/images/{imageId} — update metadata
  - [x] DELETE /api/v1/projects/{id}/images/{imageId} — remove image
  - [x] PUT /api/v1/projects/{id}/images/{imageId}/set-primary — set primary

- [x] **Image Processing**
  - [x] File validation (format, size, magic bytes verification)
  - [x] Cloudinary integration for cloud storage
  - [x] ImageProperties config class for limits and allowed types
  - [x] FileValidationService with PDF and image validation

### Project Links
- [x] **Project Link CRUD**
  - [x] ProjectLink entity with LinkType enum (GITHUB, LIVE, DEMO, etc.)
  - [x] Full CRUD endpoints under /api/v1/projects/{id}/links
  - [x] V4 + V5 migrations (create links table, remove legacy URL columns)

### Caching
- [x] **Redis Cache Implementation**
  - [x] @Cacheable on project, technology, certification, blog, resume endpoints
  - [x] @CacheEvict on create/update/delete with cross-cache eviction
  - [x] TTL configuration per cache type (CacheConfig + CacheConstants)
  - [x] Redis JSON serialization with Jackson (fixed deserialization issues)

### Rate Limiting
- [x] **Bucket4j Rate Limiting**
  - [x] Per-IP token bucket rate limiting
  - [x] Tiered limits: public (60/min), login (5/min), admin (30/min)
  - [x] RateLimitFilter with configurable RateLimitProperties
  - [x] Retry-After header on 429 responses

---

## Phase 3: Blog System (Done)

### Blog Post CRUD Operations
- [x] **Blog Post Entities & DTOs**
  - [x] BlogPost entity with publish/unpublish workflow
  - [x] CreateBlogPostRequest / UpdateBlogPostRequest DTOs
  - [x] BlogPostResponse with categories, tags, images
  - [x] BlogPostMapper (MapStruct)

- [x] **Blog Post API**
  - [x] GET /api/v1/blog/posts — all posts
  - [x] GET /api/v1/blog/posts/published — published only
  - [x] GET /api/v1/blog/posts/published/paginated — paginated published posts
  - [x] GET /api/v1/blog/posts/{id} — by ID
  - [x] GET /api/v1/blog/posts/slug/{slug} — by slug (increments view count)
  - [x] GET /api/v1/blog/posts/category/{slug} — by category
  - [x] GET /api/v1/blog/posts/tag/{slug} — by tag
  - [x] GET /api/v1/blog/posts/search?q= — full text search
  - [x] POST /api/v1/blog/posts — create
  - [x] PUT /api/v1/blog/posts/{id} — update
  - [x] DELETE /api/v1/blog/posts/{id} — delete (unpublish first)
  - [x] PUT /api/v1/blog/posts/{id}/publish — publish
  - [x] PUT /api/v1/blog/posts/{id}/unpublish — unpublish
  - [x] POST/DELETE /api/v1/blog/posts/{id}/categories/{categoryId} — link/unlink
  - [x] POST/DELETE /api/v1/blog/posts/{id}/tags/{tagId} — link/unlink

- [x] **Blog Category Management**
  - [x] Full CRUD endpoints
  - [x] Slug-based lookup

- [x] **Blog Tag Management**
  - [x] Full CRUD endpoints
  - [x] Popular tags endpoint (top 10 by usage count)
  - [x] Usage count tracking (increment/decrement on link/unlink)

- [x] **Blog Post Images**
  - [x] Full CRUD + set primary endpoint
  - [x] Cloudinary integration
  - [x] Image types: FEATURED, INLINE, GALLERY, THUMBNAIL

### Blog DAO Layer
- [x] BlogPostDao + BlogPostDaoImpl
- [x] BlogCategoryDao + BlogCategoryDaoImpl
- [x] BlogTagDao + BlogTagDaoImpl
- [x] @RetryableDataAccess on all implementations

---

## Phase 4: Certifications (Done)

- [x] **Certification CRUD**
  - [x] Certification entity with CertificationStatus enum (EARNED, IN_PROGRESS, EXPIRED)
  - [x] Full CRUD endpoints with slug, status, organization, published, featured filtering
  - [x] Many-to-many relationship with Technology (link/unlink endpoints)
  - [x] V6 migration (certifications table + join table)

---

## Phase 5: Contact & Communication (Done)

### Contact Submission System
- [x] **Contact Form Processing**
  - [x] ContactSubmissionDao + ContactSubmissionDaoImpl
  - [x] ContactSubmissionService with validation
  - [x] ContactSubmissionController
  - [x] POST /api/v1/contact — public submission endpoint
  - [x] IP address and User-Agent capture

- [x] **Spam Protection**
  - [x] Bucket4j rate limiting by IP (shared with global rate limiter)
  - [x] Message length validation (20-2000 chars)

- [x] **Contact Management via API**
  - [x] GET /api/v1/contact — list all submissions (ADMIN)
  - [x] GET /api/v1/contact/{id} — single submission (ADMIN)
  - [x] GET /api/v1/contact/status/{status} — filter by status (ADMIN)
  - [x] GET /api/v1/contact/inquiry-type/{type} — filter by inquiry type (ADMIN)
  - [x] PUT /api/v1/contact/{id}/status — update status (ADMIN)
  - [x] DELETE /api/v1/contact/{id} — delete (ADMIN)

### Email Notifications
- [x] **Resend Integration**
  - [x] Contact confirmation email to visitor (async)
  - [x] New submission notification to site owner (async)
  - [x] Thymeleaf HTML email templates
  - [x] EmailConstants for template names, variables, subjects

---

## Phase 6: Resume Management (Done)

### Resume System
- [x] **Resume API**
  - [x] GET /api/v1/resume — active resume metadata (public)
  - [x] GET /api/v1/resume/download — 302 redirect to Cloudinary URL (public)
  - [x] POST /api/v1/resume — upload PDF (ADMIN, replaces existing)
  - [x] DELETE /api/v1/resume — delete active resume (ADMIN)

- [x] **Resume Storage**
  - [x] Cloudinary raw file upload for PDFs
  - [x] Single active resume model (upload replaces previous)
  - [x] PDF validation (content type + magic bytes)
  - [x] 5MB file size limit
  - [x] V8 migration (resumes table)

---

## Phase 7: Security & Authentication (Done)

- [x] **JWT Authentication**
  - [x] Stateless JWT with 24-hour expiration, 7-day refresh
  - [x] JwtTokenProvider, JwtAuthenticationFilter
  - [x] Login endpoint: POST /api/v1/auth/login
  - [x] V3 migration (users table)

- [x] **Spring Security Configuration**
  - [x] Public GET endpoints, ADMIN-only write operations
  - [x] CORS configured for frontend origins
  - [x] Stateless session management

- [x] **Configuration Encryption**
  - [x] Jasypt for sensitive properties (ENC() format)
  - [x] Encrypt/decrypt utility endpoints (non-production only)
  - [x] Hash-password utility endpoint (non-production only)

---

## Phase 8: Code Quality & Constants (Done)

- [x] **Domain-Driven Constants**
  - [x] ResponseMessages — all controller success messages
  - [x] CacheConstants — cache names and key prefixes
  - [x] EmailConstants — template names, variables, subjects
  - [x] SecurityConstants — JWT, auth, rate limit tiers
  - [x] FileConstants — MIME types, Cloudinary subfolders
  - [x] OperationsConstants — health status, encryption prefix/suffix
  - [x] ErrorMessages — all error message strings
  - [x] CloudinaryConstants — API parameter and response keys

- [x] **Utility Classes**
  - [x] HttpRequestUtils — IP extraction, HTTP header constants

- [x] **API Documentation**
  - [x] Custom annotation classes for all controller endpoints
  - [x] Replaced deprecated @Schema required=true with requiredMode

---

## Phase 9: Production Deployment (Done)

### Docker & CI/CD
- [x] **Dockerfile** — multi-stage build (Maven build + JRE runtime)
- [x] **.dockerignore** — excludes secrets, IDE files, build artifacts
- [x] **docker-compose.prod.yml** — all 5 services (app, postgres, redis, nginx, certbot)
- [x] **.env.example** — template with placeholder values

### GitHub Actions
- [x] **CI/CD Pipeline** (.github/workflows/deploy.yml)
  - [x] Auto-build Docker image on push to main
  - [x] Push to GitHub Container Registry
  - [x] SSH deploy to VPS (pull + restart)
  - [x] Repository secrets configured (VPS_HOST, VPS_USER, VPS_SSH_KEY)

### Nginx & SSL
- [x] **Nginx reverse proxy** with SSL termination
- [x] **Let's Encrypt** certificates via Certbot (auto-renewal)
- [x] Security headers (HSTS, X-Frame-Options, X-Content-Type-Options, X-XSS-Protection)
- [x] HTTP → HTTPS redirect

### VPS Infrastructure
- [x] Hetzner CX22 VPS (Ubuntu 24.04)
- [x] Deploy user with Docker access
- [x] UFW firewall (ports 22, 80, 443 only)
- [x] PostgreSQL and Redis isolated within Docker network (no external ports)

---

## Phase 10: Frontend & Testing (Next)

### Frontend
- [ ] **React/Next.js Frontend**
  - [ ] Portfolio showcase pages
  - [ ] Blog with markdown rendering
  - [ ] Contact form
  - [ ] Resume download
  - [ ] Admin dashboard for content management

### Testing
- [ ] **Unit Tests**
  - [ ] Service layer business logic tests
  - [ ] Validation rule tests

- [ ] **Integration Tests**
  - [ ] DAO layer with local PostgreSQL (not H2)
  - [ ] Controller tests with MockMvc

- [ ] **Exception Tests**
  - [ ] Business rule violations
  - [ ] Data access error scenarios
  - [ ] Retry behavior verification

---

## Documentation

| Document | Location | Description |
|----------|----------|-------------|
| Docker Setup | `docs/deployment/DOCKER.md` | Dockerfile, docker-compose, container architecture |
| GitHub Actions CI/CD | `docs/deployment/GITHUB_ACTIONS_CICD.md` | Pipeline, secrets, monitoring, rollback |
| Nginx Reverse Proxy | `docs/deployment/NGINX_REVERSE_PROXY.md` | Full config breakdown, security headers |
| Certbot SSL | `docs/deployment/CERTBOT_SSL.md` | Let's Encrypt, certificate lifecycle |
| VPS Deployment | `docs/deployment/VPS_DEPLOYMENT.md` | Server setup, initial deployment |
| Manual Test Plan | `docs/testing/MANUAL_TEST_PLAN.md` | API endpoint testing guide |
| CLAUDE.md | `CLAUDE.md` | AI development context and coding standards |

---

## Implementation Priority Matrix

| Priority | Feature | Status |
|----------|---------|--------|
| P1 | Project CRUD + Flyway | Done |
| P1 | Image Upload Endpoints | Done |
| P1 | Redis Caching | Done |
| P1 | JWT Authentication | Done |
| P2 | Blog System | Done |
| P2 | Certifications | Done |
| P2 | Contact Form + Email | Done |
| P2 | Resume Management | Done |
| P2 | Rate Limiting | Done |
| P3 | Domain-Driven Constants | Done |
| P3 | Docker + CI/CD | Done |
| P3 | VPS Deployment + SSL | Done |
| P4 | Frontend (React/Next.js) | Next |
| P4 | Automated Testing | Next |