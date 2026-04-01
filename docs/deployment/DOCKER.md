# Docker Setup

## What Is Docker?

Docker packages your application and all its dependencies into a **container** — a lightweight, isolated environment that runs the same way on any machine. Instead of "it works on my machine," Docker guarantees "it works in this container, everywhere."

Key concepts:
- **Image** — a read-only blueprint (like a class in Java). Built from a `Dockerfile`
- **Container** — a running instance of an image (like an object). Has its own filesystem, network, and processes
- **Registry** — a place to store and share images (GitHub Container Registry, Docker Hub, etc.)

---

## Dockerfile: Building the Application Image

Our Dockerfile uses a **multi-stage build** — two separate stages that produce a smaller, more secure final image.

### Stage 1: Build

```dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copy dependency definitions first for layer caching
COPY pom.xml .
COPY .mvn/ .mvn/
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src/ src/
RUN mvn clean package -DskipTests -B
```

**Line by line:**

| Line | What It Does |
|------|-------------|
| `FROM maven:3.9-eclipse-temurin-21-alpine AS build` | Starts from a Maven + JDK 21 base image. `alpine` is a minimal Linux distro (~5MB vs ~100MB for Ubuntu). `AS build` names this stage so we can reference it later |
| `WORKDIR /build` | Sets the working directory inside the container. All subsequent commands run from here |
| `COPY pom.xml .` | Copies just the Maven config file first |
| `COPY .mvn/ .mvn/` | Copies Maven wrapper config |
| `RUN mvn dependency:go-offline -B` | Downloads all dependencies. This is done **before** copying source code so Docker can cache this layer. If only your Java code changes (not dependencies), Docker skips this step on rebuild — saving minutes |
| `COPY src/ src/` | Now copies the actual source code |
| `RUN mvn clean package -DskipTests -B` | Compiles the code and builds the JAR. `-DskipTests` skips tests (they ran in CI already). `-B` is batch mode (no interactive prompts) |

**Why two separate COPY steps for pom.xml and src/?**

Docker builds in **layers**. Each instruction creates a layer that's cached. If a layer hasn't changed, Docker reuses the cached version. By copying `pom.xml` and downloading dependencies first, Docker only re-downloads dependencies when `pom.xml` changes — not every time you change a Java file. This is called **layer caching** and it makes rebuilds much faster.

### Stage 2: Runtime

```dockerfile
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=build /build/target/personal-website-*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=production"]
```

**Line by line:**

| Line | What It Does |
|------|-------------|
| `FROM eclipse-temurin:21-jre-alpine` | Starts a **new** image with just the JRE (no JDK, no Maven). This is the key to multi-stage builds — the build tools are discarded, resulting in a much smaller image (~200MB vs ~800MB) |
| `RUN addgroup -S appgroup && adduser -S appuser -G appgroup` | Creates a non-root user. `-S` means system user (no home directory, no password). Running as root inside a container is a security risk — if an attacker breaks in, they'd have root access |
| `WORKDIR /app` | Sets working directory for the runtime container |
| `COPY --from=build /build/target/personal-website-*.jar app.jar` | Copies **only** the built JAR from the build stage. All the source code, Maven cache, and build tools are left behind |
| `RUN chown appuser:appgroup app.jar` | Makes the non-root user the owner of the JAR file |
| `USER appuser` | All subsequent commands (and the running app) execute as this non-root user |
| `EXPOSE 8080` | Documents that the app listens on port 8080. This doesn't actually publish the port — it's metadata for humans and tools |
| `ENV JAVA_OPTS="-Xmx512m -Xms256m"` | Sets default JVM memory: 256MB initial heap, 512MB max. Can be overridden at runtime |
| `ENTRYPOINT [...]` | The command that runs when the container starts. Activates the `production` Spring profile |

### Why Multi-Stage Builds?

| | Single Stage | Multi-Stage (Ours) |
|---|---|---|
| Final image contains | JDK + Maven + source code + JAR | JRE + JAR only |
| Image size | ~800MB | ~200MB |
| Attack surface | Large (build tools present) | Minimal (runtime only) |
| Build secrets exposure | Possible | Not possible (discarded) |

---

## .dockerignore: What NOT to Include

```
.git
.idea
.vscode
.claude
target/
docs/
*.iml
*.iws
*.ipr
.env
*.env
nul
temp-*.json
```

This works like `.gitignore` but for Docker builds. When Docker runs `COPY`, it sends the entire directory (the "build context") to the Docker daemon. `.dockerignore` excludes files from this context:

| Pattern | Why Excluded |
|---------|-------------|
| `.git` | Repository history is huge and not needed to build |
| `.idea`, `.vscode`, `.claude` | IDE and tool config |
| `target/` | Local build artifacts — Docker builds fresh inside the container |
| `docs/` | Documentation isn't needed in the runtime image |
| `.env`, `*.env` | **Critical** — prevents secrets from being baked into the image |
| `nul`, `temp-*.json` | Local artifacts |

---

## Docker Compose: Orchestrating All Services

Docker Compose defines and runs multi-container applications. Instead of starting 5 containers manually with long `docker run` commands, you declare everything in a YAML file and run `docker compose up`.

### The App Service

```yaml
app:
  image: ghcr.io/caseythecoder90/personal-website-backend:latest
  container_name: personal-website-app
  env_file: .env
  depends_on:
    postgres:
      condition: service_healthy
    redis:
      condition: service_healthy
  restart: unless-stopped
  healthcheck:
    test: ["CMD-SHELL", "wget -qO- http://localhost:8080/actuator/health || exit 1"]
    interval: 30s
    timeout: 10s
    retries: 3
    start_period: 60s
  networks:
    - app-network
```

| Property | What It Does |
|----------|-------------|
| `image` | Pulls the pre-built image from GitHub Container Registry (built by CI/CD pipeline) |
| `container_name` | Gives the container a fixed name instead of a random one |
| `env_file: .env` | Loads environment variables from `.env` file. This is how secrets (DB password, JWT secret, API keys) are injected without being in the image |
| `depends_on` with `condition: service_healthy` | Won't start the app until PostgreSQL and Redis are **healthy** (not just started). This prevents the app from crashing because the database isn't ready yet |
| `restart: unless-stopped` | Automatically restarts if the container crashes. Only stops if you explicitly run `docker compose stop` |
| `healthcheck` | Docker periodically runs `wget` against the Spring Boot health endpoint. If it fails 3 times in a row, the container is marked unhealthy. `start_period: 60s` gives the app time to boot before health checks begin |
| `networks` | Connects to the shared `app-network` so it can reach PostgreSQL, Redis, and be reached by Nginx |

### The PostgreSQL Service

```yaml
postgres:
  image: postgres:16-alpine
  container_name: personal-website-postgres
  environment:
    POSTGRES_DB: ${POSTGRES_DB}
    POSTGRES_USER: ${POSTGRES_USER}
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
  volumes:
    - postgres_data:/var/lib/postgresql/data
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}"]
    interval: 10s
    timeout: 5s
    retries: 5
  restart: unless-stopped
  networks:
    - app-network
```

| Property | What It Does |
|----------|-------------|
| `image: postgres:16-alpine` | PostgreSQL 16 on Alpine Linux (minimal footprint) |
| `environment` | The official Postgres image reads these variables on first start to create the database and user automatically. `${POSTGRES_DB}` references values from the `.env` file |
| `volumes: postgres_data:/var/lib/postgresql/data` | A **named volume** that persists data outside the container. Without this, all data would be lost when the container restarts. Docker manages the volume's location on disk |
| `healthcheck` | Uses `pg_isready` (built into Postgres) to check if the database is accepting connections. This is what the app's `depends_on: condition: service_healthy` waits for |
| No `ports` mapping | PostgreSQL is **not exposed** to the internet. It's only accessible within the Docker network. This is a security best practice — the database should never be directly reachable from outside |

### The Redis Service

```yaml
redis:
  image: redis:7-alpine
  container_name: personal-website-redis
  command: redis-server --requirepass ${REDIS_PASSWORD}
  volumes:
    - redis_data:/data
  healthcheck:
    test: ["CMD", "redis-cli", "-a", "${REDIS_PASSWORD}", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
  restart: unless-stopped
  networks:
    - app-network
```

| Property | What It Does |
|----------|-------------|
| `command` | Overrides the default Redis startup command to require password authentication. By default, Redis has no password |
| `volumes: redis_data:/data` | Persists Redis data (cache entries) across container restarts |
| `healthcheck` | Sends a `PING` command to Redis with authentication. Redis responds with `PONG` if healthy |
| No `ports` mapping | Same as PostgreSQL — Redis is only accessible within the Docker network |

### The Nginx Service

```yaml
nginx:
  image: nginx:alpine
  container_name: personal-website-nginx
  ports:
    - "80:80"
    - "443:443"
  volumes:
    - ./nginx/conf.d:/etc/nginx/conf.d:ro
    - ./certbot/www:/var/www/certbot:ro
    - ./certbot/conf:/etc/letsencrypt:ro
  depends_on:
    app:
      condition: service_healthy
  restart: unless-stopped
  networks:
    - app-network
```

| Property | What It Does |
|----------|-------------|
| `ports: "80:80"` and `"443:443"` | **The only ports exposed to the internet.** Format is `host:container`. This maps the VPS's port 80/443 to Nginx's port 80/443 |
| `volumes` with `:ro` | Mounts three directories as **read-only** (`:ro`). Nginx only needs to read the config and certificates, never write to them. This is a security hardening measure |
| `./nginx/conf.d` | Your Nginx configuration files (see [NGINX_REVERSE_PROXY.md](NGINX_REVERSE_PROXY.md)) |
| `./certbot/www` | SSL challenge files (see [CERTBOT_SSL.md](CERTBOT_SSL.md)) |
| `./certbot/conf` | SSL certificates |
| `depends_on: app: condition: service_healthy` | Won't start Nginx until the Spring Boot app is healthy. This prevents Nginx from returning 502 Bad Gateway during startup |

### The Certbot Service

```yaml
certbot:
  image: certbot/certbot
  container_name: personal-website-certbot
  volumes:
    - ./certbot/www:/var/www/certbot
    - ./certbot/conf:/etc/letsencrypt
  entrypoint: "/bin/sh -c 'trap exit TERM; while :; do certbot renew; sleep 12h & wait $${!}; done;'"
  networks:
    - app-network
```

Runs in a loop checking for certificate renewal every 12 hours. See [CERTBOT_SSL.md](CERTBOT_SSL.md) for full details.

Note: Certbot volumes are **not** read-only (no `:ro`) because Certbot needs to write challenge files and updated certificates.

### Volumes and Networks

```yaml
volumes:
  postgres_data:
  redis_data:

networks:
  app-network:
    driver: bridge
```

| Section | What It Does |
|---------|-------------|
| `volumes` | Declares named volumes that Docker manages. Data in these volumes survives container restarts and even `docker compose down`. Only `docker volume rm` deletes them |
| `networks: app-network: driver: bridge` | Creates an isolated network. All services on this network can reach each other by service name (e.g., `postgres`, `redis`, `app`). Docker provides built-in DNS resolution for this. The `bridge` driver is the default — it creates a private internal network on the host |

### How Container DNS Works

Inside the Docker network, each service is reachable by its service name:

```
Spring Boot connects to:    postgres:5432    (not localhost!)
Spring Boot connects to:    redis:6379       (not localhost!)
Nginx proxies to:           app:8080         (not localhost!)
```

Docker's embedded DNS server resolves these names to the correct container IPs automatically. This is why `application.yml` uses `${DB_URL}` which is set to `jdbc:postgresql://postgres:5432/...` in the `.env` file.

---

## Useful Docker Commands

```bash
# View running containers
docker compose -f docker-compose.prod.yml ps

# View logs for a specific service
docker compose -f docker-compose.prod.yml logs app
docker compose -f docker-compose.prod.yml logs -f app    # follow (real-time)

# Restart a single service
docker compose -f docker-compose.prod.yml restart app

# Pull latest images and recreate containers
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d

# Stop all services
docker compose -f docker-compose.prod.yml down

# Stop and remove volumes (DESTROYS DATA)
docker compose -f docker-compose.prod.yml down -v

# Execute a command inside a running container
docker exec -it personal-website-app sh
docker exec -it personal-website-postgres psql -U casquinn -d casquinn_personal_website

# Check disk usage
docker system df

# Clean up unused images
docker image prune -f
```

---

## References

- [Docker Documentation](https://docs.docker.com/)
- [Dockerfile Best Practices](https://docs.docker.com/build/building/best-practices/)
- [Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Docker Networking](https://docs.docker.com/network/) — how containers communicate
- [Docker Volumes](https://docs.docker.com/storage/volumes/) — persistent data storage
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/) — official Spring guide for Docker