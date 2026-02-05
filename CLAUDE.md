# Personal Website Project - Development Context

## Project Vision & Learning Goals

This is Casey Quinn's personal website backend - a comprehensive learning project focused on mastering enterprise-grade Spring Boot development patterns. The project serves dual purposes:

1. **Professional Portfolio**: Demonstrate advanced technical skills with real-world application
2. **Learning Exercise**: Master enterprise patterns used in Fortune 500 companies

## Current Development Phase

**Phase 2: Portfolio Enhancement**
- ✅ DAO pattern implementation with @RetryableDataAccess
- ✅ Service layer with clean business logic separation
- ✅ RESTful API endpoints with proper HTTP semantics
- ✅ Swagger/OpenAPI documentation integration
- ✅ Flyway database migrations (V1__initial_schema.sql)
- ✅ Logback compact logging pattern
- ✅ Dead entities/enums removed (User, LearningOutcome, PageView, ProjectAnalytics, SeoMeta)
- ✅ All documentation updated for API-first, no admin UI approach
- 🔄 ProjectImage upload endpoints
- 🔄 Redis caching layer
- Next: Blog system, then contact form

## Architecture Decisions & Rationale

### Why DAO Pattern Over Direct Repository Usage?
**Decision**: Implement DAO layer between Service and Repository
**Rationale**:
- **Exception Translation**: Database exceptions handled in DAO, not business logic
- **Technology Independence**: Easy to switch from JPA to MyBatis/MongoDB
- **Enterprise Standard**: Common pattern in large-scale applications
- **Testability**: Service layer can be tested with mocked DAOs

### Why API-First (No Admin UI)?
**Decision**: All content management done via API calls, no admin dashboard
**Rationale**:
- Simpler architecture — no auth system, no frontend admin routes
- AI tools (curl, Postman) make it easy to create/manage content
- Focus stays on portfolio quality rather than admin tooling
- Content posted via: portfolio, blog, contact, resume — all via REST

### Exception Hierarchy Strategy
**Decision**: Separate Business and Data Access exceptions with ErrorCode enum
**Rationale**:
- Business exceptions → 400 responses
- Not Found → 404 responses
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
- **PostgreSQL**: Robust relational database for portfolio data
- **Redis**: Caching and rate limiting (planned)
- **Flyway**: Versioned database migrations
- **MapStruct**: Compile-time mapping for performance
- **OpenAPI**: Industry standard API documentation
- **Spring Retry**: `@RetryableDataAccess` for transient DB failures

## Active Entities

### Portfolio (Fully Wired)
- `Project` — central portfolio entity
- `Technology` — skills with proficiency tracking
- `ProjectImage` — image galleries per project

### Blog (Entities Exist — Implementation Pending)
- `BlogPost`, `BlogCategory`, `BlogTag`

### Contact (Entity Exists — Implementation Pending)
- `ContactSubmission`

### Enums in Use
- `ProjectType`, `ProjectStatus`, `DifficultyLevel`
- `TechnologyCategory`, `ProficiencyLevel`
- `ImageType`
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

### 3. Data Access Best Practices
- **DAO Pattern**: Clean abstraction over repository layer
- **Flyway Migrations**: Version-controlled schema management
- **Exception Translation**: Database errors properly abstracted
- **Transaction Safety**: Proper rollback and consistency handling

## Development Commands & Workflow

### Essential Commands
```bash
# Start development environment
docker-compose -f src/main/resources/docker/docker-compose.yml up -d postgres redis

# Compile and run application
mvn clean compile
mvn spring-boot:run

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

# Create project
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Project",
    "shortDescription": "A test project",
    "techStack": "Java, Spring Boot"
  }'

# Get all projects
curl http://localhost:8080/api/v1/projects
```

## Code Quality Standards

### Coding Standards
- **No Wildcard Imports**: Always use explicit imports
- **Exception Safety**: All methods handle errors appropriately
- **Null Safety**: Proper null checks and Optional usage
- **Resource Management**: Proper cleanup and connection handling

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
- DAO pattern with @RetryableDataAccess on both impls
- Exception hierarchy: BaseException → BusinessException/NotFoundException + GlobalExceptionHandler
- ErrorCode enum for structured error responses
- Flyway migration: V1__initial_schema.sql (10 tables, all enums, indexes, FK constraints)
- Logback compact console logging
- OpenAPI/Swagger documentation
- MapStruct mappers for Project and Technology
- test-data.sql cleaned up for current schema
- All docs updated: DIAGRAMS, SYSTEM_DESIGN, TECHNICAL_ARCHITECTURE, API_FLOWS, FEATURE_SPECIFICATIONS, IMPLEMENTATION_CHECKLIST, database-schema.dbml
- Dead code removed: User, LearningOutcome, PageView, ProjectAnalytics, SeoMeta entities + related enums + ADMIN_UI_FLOWS.md

### 🔄 Next Priority Items
1. **ProjectImage endpoints**: Upload, list, update, delete, set primary
2. **Redis caching**: @Cacheable on list endpoints, @CacheEvict on mutations
3. **Blog system**: BlogPost/Category/Tag DAO → Service → Controller
4. **Contact form**: ContactSubmission DAO → Service → Controller + email notifications
5. **Resume**: Upload/download endpoints
6. **Testing**: Unit and integration tests for all services

## Business Logic & Validation Rules

### Project Management Rules
- **Uniqueness**: Project names and slugs must be unique
- **Limits**: Maximum 6 featured projects
- **Deletion**: Published projects cannot be deleted (archive only)
- **GitHub URLs**: Must follow proper GitHub URL format
- **Slugs**: Auto-generated from name on creation

### API Behavior
- **Pagination**: Default page size of 10 items
- **Sorting**: Projects sorted by creation date (newest first)
- **Filtering**: Technology-based filtering with case-insensitive search
- **Error Responses**: Detailed validation errors with field-specific messages

## Development Context for AI Assistant

When working on this project:

1. **Maintain Enterprise Standards**: Follow existing DAO → Service → Controller pattern
2. **Preserve Architecture**: Keep layered separation clean
3. **Exception Safety**: Use existing exception hierarchy and ErrorCode enum
4. **No Admin UI**: All content management is via API (curl/Postman/AI)
5. **Flyway for Schema**: Never change ddl-auto; add new V2, V3... migrations for schema changes
6. **Testing**: Write tests that verify both success and failure scenarios
7. **No H2**: Use local PostgreSQL for all testing

### Common Tasks
- **New Endpoints**: Follow existing patterns (see ProjectController + ProjectService)
- **Business Rules**: Implement in service layer only
- **Database Changes**: New Flyway migration file (V2__, V3__, etc.)
- **Error Handling**: Use ErrorCode enum + BusinessException/ValidationException
- **New Entities**: Add entity → repository → DAO interface → DAO impl → service → controller

---

**This file serves as context for development sessions and should be updated as the project evolves.**
