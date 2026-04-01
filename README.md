# Personal Website Backend

Spring Boot backend powering my personal portfolio site. Handles project showcasing, blog, certifications, contact form, and resume management through a RESTful API with JWT authentication and Redis caching.

Built with Java 21, Spring Boot 3.5, PostgreSQL, and Redis.

## Architecture

Layered architecture with clean separation of concerns:

```
Controller → Service → DAO → Repository → PostgreSQL
                                    ↕
                                  Redis Cache
```

Each layer has a single responsibility:
- **Controllers** handle HTTP and delegate to services
- **Services** contain business logic and validation
- **DAOs** abstract data access with retry logic for transient failures
- **Repositories** are Spring Data JPA interfaces

### Package Structure
```
src/main/java/com/caseyquinn/personal_website/
├── annotations/        # Custom API documentation annotations
├── config/             # Security, cache, rate limiting config
├── controller/         # REST endpoints (13 controllers)
├── dao/                # Data access interfaces + implementations
├── dto/                # Request and response objects
├── entity/             # JPA entities (12 entities)
├── exception/          # Exception hierarchy with ErrorCode enum
├── mapper/             # MapStruct entity-DTO mappers
├── repository/         # Spring Data JPA repositories
├── security/           # JWT filter, rate limit filter
└── service/            # Business logic (16 services)
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.4 |
| Security | Spring Security + JWT (jjwt 0.12.3) |
| Database | PostgreSQL |
| Caching | Redis + Spring Cache |
| Rate Limiting | Bucket4j (token bucket, per-IP) |
| ORM | Hibernate / Spring Data JPA |
| Migrations | Flyway (V1-V8) |
| Object Mapping | MapStruct |
| Email | Resend + Thymeleaf templates |
| Image Storage | Cloudinary |
| Config Encryption | Jasypt |
| API Docs | OpenAPI / Swagger |
| Build | Maven |

## API Endpoints

All GET endpoints are public. Write operations require JWT authentication with ADMIN role.

### Authentication
| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/v1/auth/login` | Public |

### Projects
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/projects` | Public |
| GET | `/api/v1/projects/{id}` | Public |
| GET | `/api/v1/projects/slug/{slug}` | Public |
| GET | `/api/v1/projects/paginated` | Public |
| GET | `/api/v1/projects/technology/{tech}` | Public |
| GET | `/api/v1/projects/published` | Public |
| GET | `/api/v1/projects/featured` | Public |
| POST | `/api/v1/projects` | ADMIN |
| PUT | `/api/v1/projects/{id}` | ADMIN |
| DELETE | `/api/v1/projects/{id}` | ADMIN |

### Project Links & Images
| Method | Endpoint | Auth |
|--------|----------|------|
| GET/POST/PUT/DELETE | `/api/v1/projects/{id}/links/**` | GET: Public, Write: ADMIN |
| GET/POST/PUT/DELETE | `/api/v1/projects/{id}/images/**` | GET: Public, Write: ADMIN |

### Technologies
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/technologies` | Public |
| GET | `/api/v1/technologies/{id}` | Public |
| GET | `/api/v1/technologies/category/{cat}` | Public |
| GET | `/api/v1/technologies/proficiency/{level}` | Public |
| GET | `/api/v1/technologies/featured` | Public |
| POST | `/api/v1/technologies` | ADMIN |
| PUT | `/api/v1/technologies/{id}` | ADMIN |
| DELETE | `/api/v1/technologies/{id}` | ADMIN |

### Certifications
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/certifications` | Public |
| GET | `/api/v1/certifications/{id}` | Public |
| GET | `/api/v1/certifications/slug/{slug}` | Public |
| GET | `/api/v1/certifications/status/{status}` | Public |
| GET | `/api/v1/certifications/published` | Public |
| GET | `/api/v1/certifications/featured` | Public |
| POST | `/api/v1/certifications` | ADMIN |
| PUT | `/api/v1/certifications/{id}` | ADMIN |
| DELETE | `/api/v1/certifications/{id}` | ADMIN |
| POST/DELETE | `/api/v1/certifications/{id}/technologies/{techId}` | ADMIN |

### Blog (Posts, Categories, Tags, Images)
| Method | Endpoint | Auth |
|--------|----------|------|
| GET | `/api/v1/blog/posts`, `/published`, `/published/paginated`, `/slug/{slug}`, `/category/{slug}`, `/tag/{slug}`, `/search?q=` | Public |
| POST/PUT/DELETE | `/api/v1/blog/posts/**` | ADMIN |
| GET/POST/PUT/DELETE | `/api/v1/blog/categories/**` | GET: Public, Write: ADMIN |
| GET/POST/PUT/DELETE | `/api/v1/blog/tags/**` | GET: Public, Write: ADMIN |
| GET/POST/DELETE | `/api/v1/blog/posts/{id}/images/**` | GET: Public, Write: ADMIN |

### Contact Submissions
| Method | Endpoint | Auth |
|--------|----------|------|
| POST | `/api/v1/contact` | Public |
| GET | `/api/v1/contact`, `/{id}`, `/status/{status}`, `/inquiry-type/{type}` | ADMIN |
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

## Security

- **JWT authentication** with 24-hour token expiration and 7-day refresh
- **Role-based access control**: public reads, ADMIN-only writes
- **Rate limiting** via Bucket4j: 60 req/min (public), 5 req/min (login), 30 req/min (admin)
- **Jasypt encryption** for sensitive configuration values
- **BCrypt** password encoding
- **CORS** configured for frontend origins

## Caching

Redis-backed caching with per-resource TTLs:

| Cache | TTL |
|-------|-----|
| Projects | 10 minutes |
| Technologies | 30 minutes |
| Certifications | 30 minutes |
| Blog Posts | 20 minutes |
| Blog Categories | 30 minutes |
| Blog Tags | 30 minutes |
| Resume | 60 minutes |

Write operations evict related caches. Technology changes trigger cross-cache eviction across projects, technologies, and certifications since they contain embedded technology data.

## Deployment

Deployed on a Hetzner VPS with automated CI/CD:

```
Push to main → GitHub Actions builds Docker image → Pushes to GHCR → SSHs into VPS → Pulls and restarts
```

Production stack: Nginx (SSL termination + reverse proxy) → Spring Boot → PostgreSQL + Redis, all in Docker Compose. Let's Encrypt certificates via Certbot with auto-renewal.

See `docs/deployment/` for detailed guides:
- [Docker Setup](docs/deployment/DOCKER.md)
- [GitHub Actions CI/CD](docs/deployment/GITHUB_ACTIONS_CICD.md)
- [Nginx Reverse Proxy](docs/deployment/NGINX_REVERSE_PROXY.md)
- [Certbot SSL](docs/deployment/CERTBOT_SSL.md)
- [VPS Deployment](docs/deployment/VPS_DEPLOYMENT.md)

## Development Setup

### Prerequisites
- Java 21
- Maven 3.9+
- Docker and Docker Compose
- PostgreSQL and Redis (via Docker)

### Environment Variables
```
DB_USER              # PostgreSQL username
DB_PASSWORD          # PostgreSQL password
REDIS_PASSWORD       # Redis password
CLOUD_NAME           # Cloudinary cloud name
CLOUDINARY_API_KEY   # Cloudinary API key
CLOUDINARY_API_SECRET # Cloudinary API secret
JWT_SECRET           # JWT signing key (min 32 chars)
JASYPT_ENCRYPTOR_PASSWORD # Jasypt encryption password
RESEND_API_KEY       # Resend email API key
OWNER_EMAIL          # Email for contact form notifications
```

### Running Locally
```bash
# Start databases
docker-compose -f src/main/resources/docker/docker-compose.yml up -d postgres redis

# Build and run
mvn clean compile
mvn spring-boot:run
```

### Access Points
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/api-docs
- Health check: http://localhost:8080/api/v1/operations/health

## Database Migrations

Flyway manages all schema changes. Hibernate is set to `validate` only.

| Version | Description |
|---------|-------------|
| V1 | Initial schema (projects, technologies, blog tables, contact) |
| V2 | Add cloudinary_public_id to project_images |
| V3 | Create users table |
| V4 | Create project_links table |
| V5 | Remove legacy URL columns from projects |
| V6 | Create certifications and certification_technologies |
| V7 | Blog schema refinements |
| V8 | Create resume table |

## Testing

```bash
mvn test                # Unit tests
mvn integration-test    # Integration tests
mvn verify              # Full suite with coverage
```

Manual test plan available at `docs/testing/MANUAL_TEST_PLAN.md`.

## Project Structure Highlights

**Exception handling**: Custom hierarchy with `ErrorCode` enum. Business exceptions return 4xx, data access exceptions return 5xx. `GlobalExceptionHandler` formats all error responses consistently.

**DAO pattern**: Every repository is wrapped in a DAO that handles exception translation and supports `@RetryableDataAccess` for transient failure retry.

**Entity mapping**: MapStruct generates all entity-to-DTO conversions at compile time. No runtime reflection.

**Image management**: Cloudinary integration for project images, blog post images, and resume PDFs. File validation enforced at the service layer.

**Email notifications**: Async email sending via Resend with Thymeleaf HTML templates for contact form submissions.

---

Built by Casey Quinn
