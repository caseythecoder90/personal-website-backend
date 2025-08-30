# Personal Website Backend - Enterprise Spring Boot Application

## Overview

A professional enterprise-grade backend service for Casey Quinn's personal website, built with Spring Boot 3.2+ and Java 21. This project demonstrates modern enterprise development patterns, clean architecture principles, and industry best practices.

## 🎯 Learning Goals & Enterprise Patterns

This project focuses on mastering professional development patterns used in Fortune 500 companies:

### Core Enterprise Patterns Implemented
- **Layered Architecture**: Controller → Service → DAO → Repository
- **DAO Pattern**: Professional data access abstraction with exception handling
- **DTO Pattern**: Clean separation between entities and API contracts
- **Exception Hierarchy**: Business vs Data access exception separation
- **Global Exception Handling**: Centralized error management
- **Bean Validation**: Request/response validation with custom validators

### Technology Stack
- **Java 21** with modern language features
- **Spring Boot 3.2+** for enterprise application framework
- **PostgreSQL** for relational data persistence
- **Redis** for caching and session management
- **MapStruct** for entity-DTO mapping
- **OpenAPI/Swagger** for API documentation
- **Maven** for dependency management

## 🏗️ Architecture

### Package Structure
```
src/main/java/com/caseyquinn/personal_website/
├── controller/          # REST Controllers (API Layer)
├── service/             # Business Logic Layer
├── dao/                 # Data Access Objects
│   └── impl/           # DAO Implementations
├── repository/          # Spring Data JPA Repositories
├── entity/              # JPA Entities
├── dto/                 # Data Transfer Objects
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
├── mapper/              # MapStruct Entity ↔ DTO Mappers
├── exception/           # Custom Exception Hierarchy
│   ├── business/        # Business Logic Exceptions
│   └── data/           # Data Access Exceptions
├── config/              # Configuration Classes
└── util/                # Utility Classes
```

### Exception Hierarchy
```
BaseException (abstract)
├── BusinessException
│   ├── ProjectValidationException
│   ├── DuplicateProjectException
│   └── ProjectBusinessRuleException
└── DataAccessException
    ├── EntityNotFoundException
    ├── DatabaseConnectionException
    └── DataIntegrityException
```

## 🚀 Features

### Portfolio Management
- **Project CRUD Operations**: Full lifecycle management of portfolio projects
- **Technology Filtering**: Search projects by technology stack
- **Validation**: Comprehensive request validation with custom rules
- **Business Rules**: Duplicate prevention, project limits, publication controls

### Professional API Design
- **RESTful Endpoints**: Proper HTTP methods and status codes
- **Pagination Support**: Efficient data retrieval for large datasets
- **Consistent Responses**: Standardized API response wrapper
- **Error Handling**: Detailed error messages with proper HTTP codes

### Enterprise Features
- **Health Checks**: Application monitoring and status endpoints
- **API Documentation**: Auto-generated Swagger/OpenAPI documentation
- **Exception Translation**: Database exceptions properly abstracted
- **Transaction Management**: Proper @Transactional boundaries

## 📋 API Endpoints

### Core Endpoints
- `GET /api/v1/health` - Service health check
- `GET /api/v1/projects` - Get all projects
- `GET /api/v1/projects/paginated` - Paginated project list
- `GET /api/v1/projects/{id}` - Get project by ID
- `POST /api/v1/projects` - Create new project
- `PUT /api/v1/projects/{id}` - Update existing project
- `DELETE /api/v1/projects/{id}` - Delete project
- `GET /api/v1/projects/technology/{tech}` - Filter by technology

### API Documentation
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/api-docs`

## 🛠️ Development Setup

### Prerequisites
- Java 21 (managed with jenv)
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL (via Docker)
- Redis (via Docker)

### Environment Setup
```bash
# Start databases
docker-compose up -d postgres redis

# Compile and run
mvn clean compile
mvn spring-boot:run
```

### Configuration
Database and Redis configurations are in `application.yml`:
- **PostgreSQL**: `localhost:5432/casquinn_personal_website`
- **Redis**: `localhost:6379`
- **Server**: `localhost:8080`

## 🧪 Testing

### Test Strategy
- **Unit Tests**: Service layer business logic
- **Integration Tests**: DAO layer with TestContainers
- **API Tests**: Controller layer with MockMvc
- **Exception Tests**: Custom exception handling

### Running Tests
```bash
mvn test                    # Unit tests
mvn integration-test        # Integration tests
mvn verify                  # Full test suite
```

## 📊 Monitoring & Operations

### Health Checks
- **Application Health**: `/actuator/health`
- **Custom Health**: `/api/v1/health`
- **Metrics**: `/actuator/metrics`

### Logging
- **Structured Logging**: SLF4J with Logback
- **Debug Mode**: Enabled for development
- **SQL Logging**: Hibernate SQL queries logged

## 🔧 Development Practices

### Code Quality
- **Clean Architecture**: Proper separation of concerns
- **SOLID Principles**: Applied throughout the codebase
- **Exception Safety**: Comprehensive error handling
- **Documentation**: Self-documenting code with Swagger

### Enterprise Patterns
- **Dependency Injection**: Spring IoC container
- **Transaction Management**: Declarative transactions
- **Validation**: Bean Validation (JSR-303)
- **Mapping**: MapStruct for type-safe conversions

## 📈 Future Enhancements

### Planned Features
- **Authentication**: JWT-based security
- **Caching**: Redis-based response caching
- **Rate Limiting**: API throttling and protection
- **Audit Logging**: Entity change tracking
- **Database Migrations**: Flyway integration

### Performance Optimizations
- **Connection Pooling**: HikariCP configuration
- **Query Optimization**: JPA query tuning
- **Caching Strategy**: Multi-level caching
- **Monitoring**: APM integration

## 🤝 Contributing

This is a learning project focused on enterprise development patterns. Code reviews and suggestions for improvements are welcome.

### Development Workflow
1. Feature branch creation
2. Implementation with tests
3. Code review and feedback
4. Integration and deployment

## 📝 License

This project is for educational and portfolio purposes.

---

**Built with ❤️ by Casey Quinn**  
*Demonstrating enterprise Java development patterns and modern Spring Boot practices*