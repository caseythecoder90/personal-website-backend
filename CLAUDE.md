# Personal Website Project - Development Context

## Project Vision & Learning Goals

This is Casey Quinn's personal website backend - a comprehensive learning project focused on mastering enterprise-grade Spring Boot development patterns. The project serves dual purposes:

1. **Professional Portfolio**: Demonstrate advanced technical skills with real-world application
2. **Learning Exercise**: Master enterprise patterns used in Fortune 500 companies

## Current Development Phase

**Phase 2: Core Backend Features (Week 3-4)**
- ✅ DAO pattern implementation with comprehensive exception handling
- ✅ Service layer with clean business logic separation
- ✅ RESTful API endpoints with proper HTTP semantics
- ✅ Swagger/OpenAPI documentation integration
- 🔄 Comprehensive testing strategy implementation

## Architecture Decisions & Rationale

### Why DAO Pattern Over Direct Repository Usage?
**Decision**: Implement DAO layer between Service and Repository
**Rationale**: 
- **Exception Translation**: Database exceptions handled in DAO, not business logic
- **Technology Independence**: Easy to switch from JPA to MyBatis/MongoDB
- **Enterprise Standard**: Common pattern in large-scale applications
- **Testability**: Service layer can be tested with mocked DAOs

### Exception Hierarchy Strategy
**Decision**: Separate Business and Data Access exceptions
**Rationale**:
- **Client Communication**: Business exceptions return 400, data exceptions return 500
- **Error Handling**: Different recovery strategies for different error types
- **Debugging**: Clear separation between business rule violations and technical failures
- **Enterprise Pattern**: Standard approach in professional applications

### Technology Choices
- **Java 21**: Latest LTS with modern language features
- **Spring Boot 3.2+**: Latest enterprise framework with virtual threads
- **PostgreSQL**: Robust relational database for portfolio data
- **Redis**: Caching and session management
- **MapStruct**: Compile-time mapping for performance
- **OpenAPI**: Industry standard API documentation

## Key Learning Objectives

### 1. Enterprise Architecture Patterns
- **Layered Architecture**: Controller → Service → DAO → Repository
- **Dependency Injection**: Spring IoC best practices
- **Transaction Management**: Proper @Transactional boundaries
- **Exception Handling**: Global handlers with proper HTTP status codes

### 2. Professional API Development
- **RESTful Design**: Proper HTTP semantics and resource modeling
- **Validation Strategy**: Bean validation with custom error responses
- **Documentation**: Auto-generated, interactive API documentation
- **Error Responses**: Consistent, helpful error message formats

### 3. Data Access Best Practices
- **DAO Pattern**: Clean abstraction over repository layer
- **Exception Translation**: Database errors properly abstracted
- **Query Optimization**: Efficient data retrieval patterns
- **Transaction Safety**: Proper rollback and consistency handling

## Development Commands & Workflow

### Essential Commands
```bash
# Start development environment
docker-compose up -d postgres redis

# Compile and run application
mvn clean compile
mvn spring-boot:run

# Run tests
mvn test
mvn integration-test

# Check code quality
mvn checkstyle:check
mvn spotbugs:check
```

### Access Points
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/api/v1/health
- **Actuator**: http://localhost:8080/actuator/health

### Testing Endpoints
```bash
# Health check
curl http://localhost:8080/api/v1/health

# Create project
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Project",
    "description": "A test project for API validation",
    "techStack": "Java, Spring Boot, PostgreSQL"
  }'

# Get all projects
curl http://localhost:8080/api/v1/projects
```

## Code Quality Standards

### Architecture Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Easy to extend without modification
- **Dependency Inversion**: Depend on abstractions, not concretions
- **Separation of Concerns**: Business logic separate from data access

### Coding Standards
- **No Wildcard Imports**: Always use explicit imports, never import com.example.*
- **Exception Safety**: All methods handle errors appropriately
- **Null Safety**: Proper null checks and Optional usage
- **Resource Management**: Proper cleanup and connection handling
- **Documentation**: Self-documenting code with clear naming

### Testing Strategy
- **Unit Tests**: Business logic in service layer
- **Integration Tests**: DAO layer with real database (TestContainers)
- **Contract Tests**: API endpoints with MockMvc
- **Exception Tests**: Error handling scenarios

## Current Implementation Status

### ✅ Completed Features
- **Project Structure**: Enterprise-grade package organization
- **Exception Hierarchy**: BaseException → Business/Data exceptions
- **DAO Implementation**: ProjectDao with comprehensive error handling
- **Service Layer**: Clean business logic with validation rules
- **REST Controllers**: Full CRUD operations with proper HTTP codes
- **DTO Pattern**: Request/Response objects with validation
- **MapStruct Integration**: Type-safe entity-DTO mapping
- **OpenAPI Documentation**: Auto-generated API docs
- **Global Exception Handler**: Centralized error management
- **Configuration**: Production-ready application.yml

### 🔄 Next Priority Items
1. **Testing Strategy**: Unit and integration test implementation
2. **Validation Enhancement**: Custom validators and business rules
3. **Caching Layer**: Redis integration for performance
4. **Authentication**: JWT-based security implementation
5. **Database Migrations**: Flyway for version control

## Business Logic & Validation Rules

### Project Management Rules
- **Uniqueness**: Project names must be unique
- **Limits**: Maximum 10 active projects per user
- **Deletion**: Published projects cannot be deleted directly
- **GitHub URLs**: Must follow proper GitHub URL format
- **Tech Stack**: Maximum 200 characters for technology descriptions

### API Behavior
- **Pagination**: Default page size of 10 items
- **Sorting**: Projects sorted by creation date (newest first)
- **Filtering**: Technology-based filtering with case-insensitive search
- **Error Responses**: Detailed validation errors with field-specific messages

## Development Context for AI Assistant

When working on this project:

1. **Maintain Enterprise Standards**: Focus on patterns used in professional environments
2. **Preserve Architecture**: Keep DAO → Service → Controller separation clean
3. **Exception Safety**: All new code should handle errors appropriately
4. **Testing**: Include tests for any new functionality
5. **Documentation**: Update OpenAPI docs for new endpoints
6. **Performance**: Consider caching and optimization opportunities

### Common Tasks
- **New Endpoints**: Follow existing patterns for validation and error handling
- **Business Rules**: Implement in service layer, not DAO or controller
- **Database Changes**: Use proper migration strategies
- **Error Handling**: Leverage existing exception hierarchy
- **Testing**: Write tests that verify both success and failure scenarios

## Learning Resources & References

### Spring Boot Best Practices
- Official Spring Boot documentation
- Enterprise Integration Patterns (Hohpe & Woolf)
- Clean Architecture (Robert Martin)
- Effective Java (Joshua Bloch)

### Professional Development Patterns
- DAO vs Repository patterns in enterprise applications
- Exception handling strategies in distributed systems
- API design guidelines (REST maturity model)
- Testing strategies for layered architectures

---

**This file serves as context for development sessions and should be updated as the project evolves.**