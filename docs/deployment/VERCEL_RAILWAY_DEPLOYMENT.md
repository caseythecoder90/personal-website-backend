# Vercel + Railway Deployment Architecture

## Overview
This deployment strategy uses **Vercel** for the frontend and **Railway** for the backend, providing a scalable, cost-effective solution for a professional portfolio website.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        VERCEL PLATFORM                             │
├─────────────────────────────────────────────────────────────────────┤
│ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────────────┐ │
│ │   Portfolio     │ │  Admin Panel    │ │      CDN/Edge           │ │
│ │   Website       │ │   Dashboard     │ │    Functions            │ │
│ │  (Next.js/      │ │  (React SPA)    │ │  - Static Assets        │ │
│ │   React)        │ │                 │ │  - Image Optimization   │ │
│ │                 │ │                 │ │  - Global Distribution  │ │
│ └─────────────────┘ └─────────────────┘ └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ HTTPS API Calls
                                    │ (CORS enabled)
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       RAILWAY PLATFORM                             │
├─────────────────────────────────────────────────────────────────────┤
│ ┌─────────────────────────────────────────────────────────────────┐ │
│ │                Spring Boot Application                          │ │
│ │ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │ │
│ │ │     API     │ │    Auth     │ │   File      │ │  Analytics  │ │ │
│ │ │  Gateway    │ │   Service   │ │  Storage    │ │ Processing  │ │ │
│ │ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘ │ │
│ └─────────────────────────────────────────────────────────────────┘ │
│ ┌─────────────────┐              ┌─────────────────────────────────┐ │
│ │   PostgreSQL    │              │             Redis              │ │
│ │  (Primary DB)   │              │  ┌─────────┐ ┌─────────────────┐ │ │
│ │  - Projects     │              │  │  Cache  │ │   Rate Limit    │ │ │
│ │  - Blog Posts   │              │  │  Layer  │ │   + Sessions    │ │ │
│ │  - Analytics    │              │  └─────────┘ └─────────────────┘ │ │
│ │  - User Data    │              │  ┌─────────────────────────────┐ │ │
│ └─────────────────┘              │  │      Event Queue           │ │ │
│                                  │  │   (Analytics Events)       │ │ │
│                                  │  └─────────────────────────────┘ │ │
│                                  └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ External Integrations
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    EXTERNAL SERVICES                               │
├─────────────────────────────────────────────────────────────────────┤
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐ │
│ │   Resend    │ │ Cloudinary  │ │ reCAPTCHA   │ │   GitHub API    │ │
│ │   (Email    │ │  (Image     │ │  (Spam      │ │  (Repository    │ │
│ │ Delivery)   │ │  Storage)   │ │Protection)  │ │   Stats)        │ │
│ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

## Domain Configuration

### Primary Domains
```
Production Frontend:  https://caseyquinn.dev
Admin Dashboard:      https://admin.caseyquinn.dev
API Backend:          https://api.caseyquinn.dev
```

### Vercel Configuration
```javascript
// vercel.json
{
  "version": 2,
  "domains": ["caseyquinn.dev", "www.caseyquinn.dev"],
  "routes": [
    {
      "src": "/admin/(.*)",
      "dest": "/admin/$1"
    },
    {
      "src": "/(.*)",
      "dest": "/$1"
    }
  ],
  "env": {
    "NEXT_PUBLIC_API_URL": "https://api.caseyquinn.dev",
    "NEXT_PUBLIC_RECAPTCHA_SITE_KEY": "@recaptcha-site-key"
  },
  "headers": [
    {
      "source": "/api/(.*)",
      "headers": [
        {
          "key": "Access-Control-Allow-Origin",
          "value": "https://caseyquinn.dev"
        }
      ]
    }
  ]
}
```

## Railway Backend Deployment

### Railway Configuration
```toml
# railway.toml
[build]
builder = "NIXPACKS"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 300
restartPolicyType = "ON_FAILURE"
restartPolicyMaxRetries = 3

[[services]]
name = "personal-website-api"
source = "."

[services.variables]
RAILWAY_STATIC_URL = "https://api.caseyquinn.dev"
PORT = "8080"
```

### Environment Variables (Railway)
```bash
# Database (Railway PostgreSQL Add-on)
DATABASE_URL=postgresql://postgres:password@containers-us-west-xxx.railway.app:5432/railway

# Redis (Railway Redis Add-on)  
REDIS_URL=redis://default:password@containers-us-west-xxx.railway.app:6379

# JWT Configuration
JWT_SECRET=your-super-secure-jwt-secret-key-min-256-bits
JWT_EXPIRATION=86400000

# External API Keys
RESEND_API_KEY=re_your_resend_api_key_here
RECAPTCHA_SECRET_KEY=your_google_recaptcha_secret_key
CLOUDINARY_URL=cloudinary://api_key:secret@cloud_name

# Application Configuration
FRONTEND_URL=https://caseyquinn.dev
ADMIN_DASHBOARD_URL=https://admin.caseyquinn.dev
ADMIN_EMAIL=admin@caseyquinn.dev

# Spring Profiles
SPRING_PROFILES_ACTIVE=production

# File Upload Limits
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=5MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=25MB
```

### Production Spring Configuration
```yaml
# application-production.yml
server:
  port: ${PORT:8080}
  forward-headers-strategy: framework

spring:
  datasource:
    url: ${DATABASE_URL}
    hikari:
      maximum-pool-size: 5
      minimum-idle: 2
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      
  redis:
    url: ${REDIS_URL}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        
  jpa:
    hibernate:
      ddl-auto: validate  # Use Flyway for migrations
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    
  cache:
    type: redis
    redis:
      time-to-live: 900000  # 15 minutes
      
logging:
  level:
    com.caseyquinn.personal_website: INFO
    org.springframework.security: WARN
    org.hibernate: WARN
    
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
      
security:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION}
    
# Custom application properties
app:
  frontend-url: ${FRONTEND_URL}
  admin-dashboard-url: ${ADMIN_DASHBOARD_URL}
  admin-email: ${ADMIN_EMAIL}
  cors:
    allowed-origins: ${FRONTEND_URL},${ADMIN_DASHBOARD_URL}
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
    
resend:
  api-key: ${RESEND_API_KEY}
  from-email: "noreply@caseyquinn.dev"
  
recaptcha:
  secret-key: ${RECAPTCHA_SECRET_KEY}
  
file-upload:
  max-size: 5MB
  allowed-types: image/jpeg,image/png,image/webp,image/gif,application/pdf
  storage-path: ./uploads
```

## Database Migration Strategy

### Flyway Migration Files
```sql
-- db/migration/V1__Initial_Schema.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'VIEWER',
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    last_login TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Projects table
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    short_description VARCHAR(500),
    full_description TEXT,
    github_url VARCHAR(500),
    live_url VARCHAR(500),
    docker_url VARCHAR(500),
    documentation_url VARCHAR(500),
    project_type VARCHAR(20) NOT NULL DEFAULT 'PERSONAL',
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNING',
    difficulty_level VARCHAR(20) NOT NULL DEFAULT 'BEGINNER',
    start_date TIMESTAMPTZ,
    completion_date TIMESTAMPTZ,
    estimated_hours INTEGER,
    display_order INTEGER DEFAULT 0,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    view_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Continue with other tables...
```

## Monitoring and Observability

### Health Checks
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Check database connectivity
        // Check Redis connectivity  
        // Check external service availability
        return Health.up()
            .withDetail("database", "Connected")
            .withDetail("redis", "Connected")
            .withDetail("external-services", "Available")
            .build();
    }
}
```

### Logging Configuration
```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="production">
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <includeContext>true</includeContext>
                <includeMdc>true</includeMdc>
            </encoder>
        </appender>
        
        <logger name="com.caseyquinn.personal_website" level="INFO"/>
        <logger name="org.springframework.web.filter.CommonsRequestLoggingFilter" level="DEBUG"/>
        
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
        </root>
    </springProfile>
</configuration>
```

## CI/CD Pipeline Configuration

### GitHub Actions for Backend (Railway)
```yaml
# .github/workflows/deploy-backend.yml
name: Deploy Backend to Railway

on:
  push:
    branches: [main]
    paths: ['backend/**']

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout
        uses: actions/checkout@v4
        
      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          
      - name: Run Tests
        run: |
          cd backend/personal-website
          ./mvnw clean test
          
      - name: Build Application
        run: |
          cd backend/personal-website  
          ./mvnw clean package -DskipTests
          
      - name: Deploy to Railway
        uses: bervProject/railway-deploy@v1.0.0
        with:
          railway_token: ${{ secrets.RAILWAY_TOKEN }}
          service: 'personal-website-api'
```

### Vercel Configuration for Frontend
```yaml
# .github/workflows/deploy-frontend.yml  
name: Deploy Frontend to Vercel

on:
  push:
    branches: [main]
    paths: ['frontend/**']

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout
        uses: actions/checkout@v4
        
      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          
      - name: Install Dependencies
        run: |
          cd frontend
          npm ci
          
      - name: Build Application
        run: |
          cd frontend
          npm run build
          
      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v25
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
          working-directory: ./frontend
```

## Performance Optimization

### CDN and Caching Strategy
```
Vercel Edge Network:
├── Static Assets (JS, CSS, Images) → Cached globally
├── API Responses (GET endpoints) → Cached at edge (5 min TTL)
└── Dynamic Content → Fresh from Railway

Railway Backend Caching:
├── Redis Application Cache → 15 min TTL
├── Database Connection Pool → Optimized for Railway limits
└── HTTP Response Cache → Conditional based on content type
```

### Database Performance
```sql
-- Production Indexes
CREATE INDEX CONCURRENTLY idx_projects_published_featured ON projects (published, featured);
CREATE INDEX CONCURRENTLY idx_projects_slug ON projects (slug);
CREATE INDEX CONCURRENTLY idx_project_images_project_primary ON project_images (project_id, is_primary);
CREATE INDEX CONCURRENTLY idx_page_views_created_path ON page_views (created_at, page_path);
CREATE INDEX CONCURRENTLY idx_contact_submissions_status_created ON contact_submissions (status, created_at);
```

## Security Configuration

### CORS Configuration
```java
@Configuration
@EnableWebSecurity
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://caseyquinn.dev",
            "https://*.caseyquinn.dev",
            "https://*.vercel.app"  // For preview deployments
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### Security Headers
```java
@Configuration
public class SecurityHeadersConfig {
    
    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityHeadersFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}

public class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        chain.doFilter(request, response);
    }
}
```

## Cost Optimization

### Railway Pricing Optimization
```
Estimated Monthly Costs:
├── Railway Pro Plan: $5/month
├── PostgreSQL Add-on: $5/month  
├── Redis Add-on: $5/month
└── Total: ~$15/month

Resource Limits:
├── RAM: 512MB (sufficient for Spring Boot)
├── CPU: Shared vCPU 
├── Storage: 1GB (logs + temporary files)
└── Bandwidth: 100GB/month
```

### Vercel Pricing
```
Hobby Plan: $0/month
├── 100GB Bandwidth
├── 1000 Serverless Function Invocations/day
├── Custom Domain
└── Automatic HTTPS

Pro Plan: $20/month (if needed)
├── 1TB Bandwidth  
├── Unlimited Serverless Functions
├── Analytics
└── Team Collaboration
```

This deployment architecture provides a professional, scalable foundation for your portfolio website with clear separation of concerns, proper security measures, and cost-effective resource utilization.