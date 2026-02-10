# Personal Website Project - Development Context

## Project Vision & Learning Goals

This is Casey Quinn's personal website backend - a comprehensive learning project focused on mastering enterprise-grade Spring Boot development patterns. The project serves dual purposes:

1. **Professional Portfolio**: Demonstrate advanced technical skills with real-world application
2. **Learning Exercise**: Master enterprise patterns used in Fortune 500 companies

## Current Development Phase

**Phase 3: Feature Expansion & Security**
- ✅ DAO pattern implementation with @RetryableDataAccess
- ✅ Service layer with clean business logic separation
- ✅ RESTful API endpoints with proper HTTP semantics
- ✅ Swagger/OpenAPI documentation integration
- ✅ Flyway database migrations (V1–V8)
- ✅ Logback compact logging pattern
- ✅ ProjectImage upload endpoints with Cloudinary integration
- ✅ Project Links CRUD (replaced legacy URL columns)
- ✅ JWT Authentication with Spring Security
- ✅ Jasypt encryption for sensitive configuration
- ✅ Certifications system with technology linking
- ✅ Blog system (full stack: posts, categories, tags, images)
- ✅ Technology Controller (all CRUD + filtering endpoints)
- ✅ Contact submission with Resend email notifications + Thymeleaf templates
- ✅ Resume upload/download via Cloudinary (single active resume)
- Next: Redis caching, testing

## Architecture Decisions & Rationale

### Why DAO Pattern Over Direct Repository Usage?
**Decision**: Implement DAO layer between Service and Repository
**Rationale**:
- **Exception Translation**: Database exceptions handled in DAO, not business logic
- **Technology Independence**: Easy to switch from JPA to MyBatis/MongoDB
- **Enterprise Standard**: Common pattern in large-scale applications
- **Testability**: Service layer can be tested with mocked DAOs

### Why JWT Authentication?
**Decision**: Stateless JWT-based authentication with role-based access control
**Rationale**:
- GET endpoints are public (portfolio viewing)
- POST/PUT/DELETE endpoints require ADMIN role
- Stateless sessions scale horizontally without shared session storage
- Jasypt encrypts sensitive config values (DB passwords, API secrets)

### Exception Hierarchy Strategy
**Decision**: Separate Business and Data Access exceptions with ErrorCode enum
**Rationale**:
- Business exceptions → 400 responses
- Not Found → 404 responses
- Forbidden → 403 responses
- Data access exceptions → 500 responses (with retry on transient failures)
- GlobalExceptionHandler centralizes all error formatting

### Database Migration Strategy
**Decision**: Flyway manages all schema changes; Hibernate set to `validate` only
**Rationale**:
- Schema is version-controlled and repeatable
- No surprise DDL changes on application restart
- Production-safe pattern

### Technology Choices
- **Java 21**: Latest LTS with modern language features
- **Spring Boot 3.5+**: Latest enterprise framework
- **Spring Security**: JWT authentication with role-based access control
- **PostgreSQL**: Robust relational database for portfolio data
- **Redis**: Caching and rate limiting (planned)
- **Flyway**: Versioned database migrations
- **MapStruct**: Compile-time mapping for performance
- **OpenAPI**: Industry standard API documentation
- **Spring Retry**: `@RetryableDataAccess` for transient DB failures
- **Jasypt**: Encryption for sensitive configuration properties
- **Cloudinary**: Cloud storage for project images and resume PDFs
- **jjwt (0.12.3)**: JWT token generation and validation
- **Resend (4.11.0)**: Transactional email notifications
- **Thymeleaf**: Email template engine (not used as web view resolver)
- **Apache Commons**: Lang3, Collections4, IO for utility operations

## Active Entities

### Portfolio (Fully Wired)
- `Project` — central portfolio entity
- `Technology` — skills with proficiency tracking
- `ProjectImage` — image galleries per project (Cloudinary)
- `ProjectLink` — external links per project (GitHub, demo, docs, etc.)

### Certifications (Fully Wired)
- `Certification` — professional credentials with status tracking
  - Many-to-many relationship with `Technology`
  - Statuses: EARNED, IN_PROGRESS, EXPIRED

### Blog (Fully Wired)
- `BlogPost` — blog articles with publish/unpublish workflow
- `BlogCategory` — post categorization (many-to-many with BlogPost)
- `BlogTag` — post tagging (many-to-many with BlogPost)
- `BlogPostImage` — image galleries per blog post (Cloudinary)

### Contact (Fully Wired)
- `ContactSubmission` — visitor inquiries with status tracking
  - Statuses: NEW, READ, REPLIED, ARCHIVED
  - Email notifications via Resend + Thymeleaf templates

### Resume (Fully Wired)
- `Resume` — PDF resume uploaded to Cloudinary (single active resume)

### Authentication (Fully Wired)
- `User` — admin user for write operations

### Enums in Use
- `ProjectType`, `ProjectStatus`, `DifficultyLevel`
- `TechnologyCategory`, `ProficiencyLevel`
- `ImageType`, `BlogImageType`
- `LinkType`
- `CertificationStatus`
- `UserRole`
- `InquiryType`, `SubmissionStatus`

## Key Learning Objectives

### 1. Enterprise Architecture Patterns
- **Layered Architecture**: Controller → Service → DAO → Repository
- **Dependency Injection**: Spring IoC best practices
- **Transaction Management**: Proper @Transactional boundaries
- **Exception Handling**: Global handlers with proper HTTP status codes
- **Retry Logic**: Automatic retry on transient data access failures

### 2. Professional API Development
- **RESTful Design**: Proper HTTP semantics and resource modeling
- **Validation Strategy**: Bean validation with custom error responses
- **Documentation**: Auto-generated, interactive API documentation
- **Error Responses**: Consistent, helpful error message formats

### 3. Security
- **JWT Authentication**: Stateless token-based auth
- **Role-Based Access Control**: Public reads, ADMIN-only writes
- **Configuration Encryption**: Jasypt for sensitive properties
- **CORS Configuration**: Controlled cross-origin access

### 4. Data Access Best Practices
- **DAO Pattern**: Clean abstraction over repository layer
- **Flyway Migrations**: Version-controlled schema management
- **Exception Translation**: Database errors properly abstracted
- **Transaction Safety**: Proper rollback and consistency handling

## Flyway Migrations

| Version | Description |
|---------|-------------|
| V1 | Initial schema (projects, technologies, project_images, blog tables, contact) |
| V2 | Add cloudinary_public_id to project_images |
| V3 | Create users table |
| V4 | Create project_links table |
| V5 | Remove legacy URL columns from projects |
| V6 | Create certifications table and certification_technologies join table |
| V7 | Create blog_post_images table with Cloudinary support |
| V8 | Create resumes table for PDF storage |

## API Endpoints

### Authentication
| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/v1/auth/login` | Public |

### Projects
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/projects` | Public |
| GET | `/api/v1/projects/paginated` | Public |
| GET | `/api/v1/projects/{id}` | Public |
| POST | `/api/v1/projects` | ADMIN |
| PUT | `/api/v1/projects/{id}` | ADMIN |
| DELETE | `/api/v1/projects/{id}` | ADMIN |
| GET | `/api/v1/projects/technology/{technology}` | Public |

### Technologies
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/technologies` | Public |
| GET | `/api/v1/technologies/paginated` | Public |
| GET | `/api/v1/technologies/{id}` | Public |
| GET | `/api/v1/technologies/name/{name}` | Public |
| GET | `/api/v1/technologies/category/{category}` | Public |
| GET | `/api/v1/technologies/proficiency/{level}` | Public |
| GET | `/api/v1/technologies/featured` | Public |
| GET | `/api/v1/technologies/most-used` | Public |
| POST | `/api/v1/technologies` | ADMIN |
| PUT | `/api/v1/technologies/{id}` | ADMIN |
| DELETE | `/api/v1/technologies/{id}` | ADMIN |

### Project Links
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/projects/{projectId}/links` | Public |
| GET | `/api/v1/projects/{projectId}/links/{linkId}` | Public |
| POST | `/api/v1/projects/{projectId}/links` | ADMIN |
| PUT | `/api/v1/projects/{projectId}/links/{linkId}` | ADMIN |
| DELETE | `/api/v1/projects/{projectId}/links/{linkId}` | ADMIN |

### Project Images
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/projects/{projectId}/images` | Public |
| GET | `/api/v1/projects/{projectId}/images/{imageId}` | Public |
| POST | `/api/v1/projects/{projectId}/images` | ADMIN |
| PUT | `/api/v1/projects/{projectId}/images/{imageId}` | ADMIN |
| DELETE | `/api/v1/projects/{projectId}/images/{imageId}` | ADMIN |
| PUT | `/api/v1/projects/{projectId}/images/{imageId}/set-primary` | ADMIN |

### Certifications
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/certifications` | Public |
| GET | `/api/v1/certifications/{id}` | Public |
| GET | `/api/v1/certifications/slug/{slug}` | Public |
| GET | `/api/v1/certifications/status/{status}` | Public |
| GET | `/api/v1/certifications/organization/{org}` | Public |
| GET | `/api/v1/certifications/published` | Public |
| GET | `/api/v1/certifications/featured` | Public |
| POST | `/api/v1/certifications` | ADMIN |
| PUT | `/api/v1/certifications/{id}` | ADMIN |
| DELETE | `/api/v1/certifications/{id}` | ADMIN |
| POST | `/api/v1/certifications/{id}/technologies/{techId}` | ADMIN |
| DELETE | `/api/v1/certifications/{id}/technologies/{techId}` | ADMIN |

### Blog Posts
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/blog/posts` | Public |
| GET | `/api/v1/blog/posts/published` | Public |
| GET | `/api/v1/blog/posts/{id}` | Public |
| GET | `/api/v1/blog/posts/slug/{slug}` | Public |
| GET | `/api/v1/blog/posts/category/{slug}` | Public |
| GET | `/api/v1/blog/posts/tag/{slug}` | Public |
| GET | `/api/v1/blog/posts/search?query=` | Public |
| POST | `/api/v1/blog/posts` | ADMIN |
| PUT | `/api/v1/blog/posts/{id}` | ADMIN |
| DELETE | `/api/v1/blog/posts/{id}` | ADMIN |
| PUT | `/api/v1/blog/posts/{id}/publish` | ADMIN |
| PUT | `/api/v1/blog/posts/{id}/unpublish` | ADMIN |
| POST | `/api/v1/blog/posts/{id}/categories/{categoryId}` | ADMIN |
| DELETE | `/api/v1/blog/posts/{id}/categories/{categoryId}` | ADMIN |
| POST | `/api/v1/blog/posts/{id}/tags/{tagId}` | ADMIN |
| DELETE | `/api/v1/blog/posts/{id}/tags/{tagId}` | ADMIN |

### Blog Categories
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/blog/categories` | Public |
| GET | `/api/v1/blog/categories/{id}` | Public |
| GET | `/api/v1/blog/categories/slug/{slug}` | Public |
| POST | `/api/v1/blog/categories` | ADMIN |
| PUT | `/api/v1/blog/categories/{id}` | ADMIN |
| DELETE | `/api/v1/blog/categories/{id}` | ADMIN |

### Blog Tags
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/blog/tags` | Public |
| GET | `/api/v1/blog/tags/popular` | Public |
| GET | `/api/v1/blog/tags/{id}` | Public |
| GET | `/api/v1/blog/tags/slug/{slug}` | Public |
| POST | `/api/v1/blog/tags` | ADMIN |
| PUT | `/api/v1/blog/tags/{id}` | ADMIN |
| DELETE | `/api/v1/blog/tags/{id}` | ADMIN |

### Blog Post Images
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/blog/posts/{postId}/images` | Public |
| GET | `/api/v1/blog/posts/{postId}/images/{imageId}` | Public |
| POST | `/api/v1/blog/posts/{postId}/images` | ADMIN |
| PUT | `/api/v1/blog/posts/{postId}/images/{imageId}` | ADMIN |
| DELETE | `/api/v1/blog/posts/{postId}/images/{imageId}` | ADMIN |
| PUT | `/api/v1/blog/posts/{postId}/images/{imageId}/primary` | ADMIN |

### Contact Submissions
| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/v1/contact` | Public |
| GET | `/api/v1/contact` | ADMIN |
| GET | `/api/v1/contact/{id}` | ADMIN |
| GET | `/api/v1/contact/status/{status}` | ADMIN |
| GET | `/api/v1/contact/inquiry-type/{type}` | ADMIN |
| PUT | `/api/v1/contact/{id}/status` | ADMIN |
| DELETE | `/api/v1/contact/{id}` | ADMIN |

### Resume
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/resume` | Public |
| GET | `/api/v1/resume/download` | Public (302 redirect) |
| POST | `/api/v1/resume` | ADMIN |
| DELETE | `/api/v1/resume` | ADMIN |

### Operations
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/operations/health` | Public |
| POST | `/api/v1/operations/encrypt` | ADMIN |
| POST | `/api/v1/operations/decrypt` | ADMIN |
| POST | `/api/v1/operations/hash-password` | ADMIN |

## Development Commands & Workflow

### Essential Commands
```bash
# Start development environment
docker-compose -f src/main/resources/docker/docker-compose.yml up -d postgres redis

# Compile and run application
mvn clean compile
mvn spring-boot:run

# Run with Jasypt encryption password
JASYPT_ENCRYPTOR_PASSWORD=your-secret-key mvn spring-boot:run

# Run tests
mvn test
mvn integration-test
```

### Access Points
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

### Testing Endpoints
```bash
# Health check
curl http://localhost:8080/actuator/health

# Login (get JWT token)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "your-password"}'

# Create project (with JWT token)
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Test Project",
    "shortDescription": "A test project",
    "type": "PERSONAL"
  }'

# Get all projects (public)
curl http://localhost:8080/api/v1/projects

# Get published certifications (public)
curl http://localhost:8080/api/v1/certifications/published
```

## Code Quality Standards

### Coding Standards
- **No Wildcard Imports**: Always use explicit imports
- **Exception Safety**: All methods handle errors appropriately
- **Null Safety**: Proper null checks and Optional usage
- **Resource Management**: Proper cleanup and connection handling

### API Documentation Standards
- **Custom Annotation Pattern**: All controller endpoints must use custom `@ApiResponses` annotation classes (e.g., `@ProjectApiResponses.GetAll`)
- **Annotation Structure**: Create a `<ResourceName>ApiResponses` class in the `annotations` package for each controller
- **Annotation Contents**: Each inner annotation interface documents the expected HTTP status codes and includes the `@Operation` annotation with summary and description
- **Response Models**: All annotations must reference specific models for success and failure responses using `@Content` and `@Schema`
- **Pattern Example**: See `ProjectApiResponses.java` as the reference implementation
- **Endpoint Documentation**: Controllers use `@<Resource>ApiResponses.<Action>` instead of inline `@ApiResponses`

### Architecture Principles
- **Single Responsibility**: Each class has one clear purpose
- **Dependency Inversion**: Depend on abstractions, not concretions
- **Separation of Concerns**: Business logic separate from data access

### Testing Strategy
- **Unit Tests**: Business logic in service layer
- **Integration Tests**: DAO layer with real PostgreSQL (local, not H2)
- **Contract Tests**: API endpoints with MockMvc
- **Exception Tests**: Error handling scenarios

## Current Implementation Status

### ✅ Completed
- Project & Technology CRUD (full stack: entity → repo → dao → service → controller)
- ProjectImage upload with Cloudinary integration
- ProjectLink CRUD with LinkType enum (GITHUB, LIVE, DEMO, etc.)
- Certification CRUD with CertificationStatus enum (EARNED, IN_PROGRESS, EXPIRED)
- Certification ↔ Technology many-to-many linking
- Blog system: Posts, Categories, Tags, Images (full stack with Cloudinary)
- Blog publish/unpublish workflow, category/tag linking, search
- Technology Controller with all CRUD + filtering endpoints
- Contact submission with public POST + admin management
- Email notifications via Resend + Thymeleaf templates (@Async)
- Resume upload/download via Cloudinary (single active resume, PDF only)
- JWT authentication with Spring Security (stateless, role-based)
- Jasypt encryption for sensitive configuration
- DAO pattern with @RetryableDataAccess on all impls
- Exception hierarchy: BaseException → BusinessException/NotFoundException/ForbiddenException + GlobalExceptionHandler
- ErrorCode enum for structured error responses
- Flyway migrations V1–V8
- Logback compact console logging
- OpenAPI/Swagger documentation with Bearer token security scheme
- MapStruct mappers for all entities (11 total)
- Security: Public GET endpoints, ADMIN-only write operations
- Documentation: JWT auth guide, manual test plan

### 🔄 Next Priority Items
1. **Redis caching**: @Cacheable on list endpoints, @CacheEvict on mutations
2. **Testing**: Unit and integration tests for all services
3. **Rate limiting**: Request throttling on public endpoints

## Business Logic & Validation Rules

### Project Management Rules
- **Uniqueness**: Project names and slugs must be unique
- **Limits**: Maximum 10 projects
- **Deletion**: Published projects cannot be deleted (unpublish first)
- **Slugs**: Auto-generated from name on creation
- **Links**: Managed via ProjectLink entity with LinkType enum

### Certification Rules
- **Uniqueness**: Certification names and slugs must be unique
- **Deletion**: Published certifications cannot be deleted (unpublish first)
- **Slugs**: Auto-generated from name on creation
- **Technology linking**: Many-to-many via certification_technologies join table
- **Status tracking**: EARNED, IN_PROGRESS, EXPIRED

### Blog Rules
- **Uniqueness**: Post titles and slugs must be unique
- **Publish workflow**: Posts start unpublished, explicit publish/unpublish actions
- **Category/Tag linking**: Many-to-many via join tables
- **Image limits**: Maximum 20 images per blog post
- **Search**: Full-text search by query parameter

### Contact Submission Rules
- **Public POST**: No authentication required for submissions
- **Admin management**: GET, PUT, DELETE require ADMIN role
- **Status tracking**: NEW → READ → REPLIED → ARCHIVED
- **Email notifications**: Confirmation to submitter, notification to owner (async)
- **IP/User-Agent capture**: Stored for reference

### Resume Rules
- **Single active resume**: Uploading replaces any existing resume
- **PDF only**: File validation enforces PDF content type
- **Max file size**: 5MB
- **Download**: 302 redirect to Cloudinary URL

### Authentication Rules
- **JWT tokens**: 24-hour expiration, 7-day refresh
- **Public endpoints**: All GET operations
- **Protected endpoints**: POST, PUT, DELETE require ADMIN role
- **Password encoding**: BCrypt

### API Behavior
- **Pagination**: Default page size of 10 items
- **Sorting**: Projects sorted by creation date (newest first)
- **Filtering**: Technology-based filtering, status-based filtering for certifications
- **Error Responses**: Detailed validation errors with field-specific messages

## Development Context for AI Assistant

When working on this project:

1. **Maintain Enterprise Standards**: Follow existing DAO → Service → Controller pattern
2. **Preserve Architecture**: Keep layered separation clean
3. **Exception Safety**: Use existing exception hierarchy and ErrorCode enum
4. **Security**: Respect JWT auth rules — GET public, write ops require ADMIN
5. **Flyway for Schema**: Never change ddl-auto; add new V9, V10... migrations for schema changes
6. **Testing**: Write tests that verify both success and failure scenarios
7. **No H2**: Use local PostgreSQL for all testing
8. **Jasypt**: Sensitive values in application.yml use `ENC(...)` format; encryption password provided via environment variable

### Common Tasks
- **New Endpoints**: Follow existing patterns (see ProjectController + ProjectService)
- **Business Rules**: Implement in service layer only
- **Database Changes**: New Flyway migration file (V9__, V10__, etc.)
- **Error Handling**: Use ErrorCode enum + BusinessException/ValidationException
- **New Entities**: Add entity → repository → DAO interface → DAO impl → service → controller
- **Security for New Endpoints**: Update SecurityConfig with GET (permitAll) and write ops (hasRole ADMIN)
- **API Documentation**: Create `<Resource>ApiResponses.java` annotation class in annotations package

---

**This file serves as context for development sessions and should be updated as the project evolves.**
