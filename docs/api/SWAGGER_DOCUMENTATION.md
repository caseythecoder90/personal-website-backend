# OpenAPI & Swagger Documentation Guide

## What is OpenAPI?

**OpenAPI Specification (OAS)** is a specification for describing REST APIs. It's a standardized format that allows developers to:

- **Document APIs**: Describe endpoints, request/response formats, authentication methods
- **Generate Code**: Auto-generate client SDKs and server stubs
- **Validate Requests**: Ensure API requests conform to the specification
- **Test APIs**: Provide interactive testing interfaces

### OpenAPI vs Swagger - Key Differences

| Aspect | OpenAPI | Swagger |
|--------|---------|---------|
| **Definition** | The specification itself (JSON/YAML format) | Tools for working with OpenAPI specs |
| **Current Version** | OpenAPI 3.x (latest standard) | Swagger 2.x (legacy, now called OpenAPI 2.0) |
| **Ownership** | Linux Foundation (open standard) | SmartBear (commercial tools) |
| **Usage** | The spec format | Tools like Swagger UI, Swagger Editor |

## What is Swagger?

**Swagger** is a suite of tools for working with OpenAPI specifications:

### Core Swagger Tools

1. **Swagger UI** - Interactive API documentation interface
2. **Swagger Editor** - Design and edit OpenAPI specs
3. **Swagger Codegen** - Generate client/server code from specs
4. **Swagger Hub** - Collaborative API design platform

### Swagger UI Features

- **Interactive Testing**: Execute API calls directly from the browser
- **Request/Response Examples**: See sample data formats
- **Authentication Support**: Test with various auth methods
- **Schema Validation**: Validate request payloads
- **Multiple Format Support**: JSON, XML, form data

## Spring Boot OpenAPI Configuration

### 1. Dependencies Required

```xml
<!-- SpringDoc OpenAPI (replaces older Swagger dependencies) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### 2. Configuration Class

```java
@Configuration
public class OpenApiConfig {
    
    @Value("${app.version:1.0.0}")
    private String version;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Website API")
                        .version(version)
                        .description("REST API for Casey Quinn's personal website")
                        .contact(new Contact()
                                .name("Casey Quinn")
                                .email("casey@caseyquinn.com")
                                .url("https://caseyquinn.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
```

### 3. Application Properties

```yaml
# OpenAPI/Swagger configuration
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    try-it-out-enabled: true
    operations-sorter: method
    tags-sorter: alpha
  show-actuator: false
```

## Controller Annotations for Documentation

### Basic Endpoint Documentation

```java
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Projects", description = "Project management APIs")
public class ProjectController {
    
    @Operation(
        summary = "Create new project",
        description = "Creates a new project with validation"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Project created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "409", description = "Project name already exists")
    })
    @PostMapping("/projects")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        // Implementation
    }
}
```

### DTO Documentation

```java
@Schema(description = "Request to create a new project")
public class CreateProjectRequest {
    
    @NotBlank(message = "Project name is required")
    @Size(min = 2, max = 100)
    @Schema(
        description = "Project name", 
        example = "Personal Website",
        required = true,
        minLength = 2,
        maxLength = 100
    )
    private String name;
    
    @Schema(
        description = "GitHub repository URL", 
        example = "https://github.com/user/repo",
        pattern = "^https://github\\.com/.*"
    )
    private String githubUrl;
}
```

## Access URLs & Endpoints

### Default URLs (Spring Boot)
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

### Custom URLs (with configuration)
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **API Docs**: `http://localhost:8080/api-docs`

## Common Configuration Issues & Solutions

### 1. Swagger UI Not Loading

**Symptoms**: 
- 404 errors when accessing Swagger UI
- Connection refused errors

**Solutions**:
```yaml
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    # Alternative path
    # path: /swagger-ui/index.html
```

### 2. API Docs Not Generated

**Symptoms**:
- Empty API documentation
- Controllers not appearing in Swagger

**Solutions**:
```java
// Ensure controllers are in component scan path
@SpringBootApplication
@ComponentScan(basePackages = "com.caseyquinn.personal_website")
public class PersonalWebsiteApplication {
    // ...
}

// Add @RestController annotation
@RestController // Required for SpringDoc to detect
@RequestMapping("/api/v1")
public class ProjectController {
    // ...
}
```

### 3. CORS Issues

**Symptoms**:
- Browser console CORS errors
- Failed to fetch API documentation

**Solutions**:
```java
@CrossOrigin(origins = "*") // For development only
@RestController
public class ProjectController {
    // ...
}

// Or global CORS configuration
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
```

### 4. SpringDoc vs Swagger Dependencies

**Old Swagger (Deprecated)**:
```xml
<!-- Don't use these -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
</dependency>
```

**New SpringDoc (Recommended)**:
```xml
<!-- Use this instead -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

## Advanced Features

### 1. Security Documentation

```java
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@OpenAPIDefinition(
    info = @Info(title = "Personal Website API", version = "1.0"),
    security = {@SecurityRequirement(name = "bearerAuth")}
)
public class OpenApiConfig {
    // ...
}
```

### 2. Custom Response Examples

```java
@Operation(summary = "Get project by ID")
@ApiResponse(
    responseCode = "200",
    description = "Project found",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = ProjectResponse.class),
        examples = @ExampleObject(
            name = "Project Example",
            value = """
                {
                    "id": 1,
                    "name": "Personal Website",
                    "description": "Full-stack portfolio website",
                    "techStack": "Java, Spring Boot, React"
                }
                """
        )
    )
)
```

### 3. Grouping and Organization

```yaml
springdoc:
  group-configs:
    - group: 'public'
      paths-to-match: '/api/v1/public/**'
      packages-to-scan: com.caseyquinn.personal_website.controller.public
    - group: 'admin'
      paths-to-match: '/api/v1/admin/**'
      packages-to-scan: com.caseyquinn.personal_website.controller.admin
```

## Troubleshooting Your Current Issue

Based on the error you're seeing, it appears to be a connection issue. Let's check:

### 1. Verify SpringDoc Configuration

Check your current configuration matches the expected format:

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
```

### 2. Check Application Startup

Ensure the application starts without errors and SpringDoc beans are created.

### 3. Verify URLs

Try these URLs in order:
1. `http://localhost:8080/swagger-ui.html`
2. `http://localhost:8080/swagger-ui/index.html`
3. `http://localhost:8080/api-docs`

### 4. Check Dependencies

Ensure you have the correct SpringDoc dependency and no conflicting Swagger dependencies.

## Best Practices

### 1. Documentation Standards
- **Descriptive Summaries**: Clear, concise endpoint descriptions
- **Example Values**: Provide realistic example data
- **Error Documentation**: Document all possible error responses
- **Schema Descriptions**: Explain complex data structures

### 2. Security Considerations
- **Hide Internal APIs**: Don't expose admin endpoints in public docs
- **Sanitize Examples**: Don't include real credentials in examples
- **Rate Limiting**: Document API rate limits

### 3. Maintenance
- **Version Management**: Keep API documentation in sync with code
- **Testing**: Regularly test API endpoints through Swagger UI
- **Feedback**: Use documentation for stakeholder review

---

This documentation should help you understand and configure OpenAPI/Swagger properly. Let me know what specific error you're seeing and I can help diagnose the connection issue!