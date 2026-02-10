# Manual Testing Plan - Personal Website API

## Prerequisites

**Start Services:**
```bash
# Start PostgreSQL and Redis (if not running)
docker-compose -f src/main/resources/docker/docker-compose.yml up -d postgres redis

# Start the application
JASYPT_ENCRYPTOR_PASSWORD=your-secret-key mvn spring-boot:run
```

**Verify Health:**
```bash
curl http://localhost:8080/api/v1/operations/health
```

Expected: `{"status":"success","data":{"status":"UP",...}}`

---

## Test Plan Structure

Each test should verify:
- **Status Code** - Correct HTTP response
- **Response Body** - Expected data structure
- **Database State** - Data persisted correctly
- **Error Handling** - Proper error messages

---

## 1. Authentication Endpoints

### 1.1 Login - Valid Credentials
**Purpose:** Authenticate and receive JWT token

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected:**
- Status: `200 OK`
- Body contains: `token`, `tokenType: "Bearer"`, `expiresIn`, `username: "admin"`, `role: "ADMIN"`

**IMPORTANT: Save the token for all subsequent protected endpoint tests!**

```bash
# Set as environment variable for convenience
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### 1.2 Login - Invalid Username
**Purpose:** Test invalid username rejection

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "wronguser",
    "password": "admin123"
  }'
```

**Expected:**
- Status: `401 Unauthorized`
- Error message about bad credentials

---

### 1.3 Login - Invalid Password
**Purpose:** Test invalid password rejection

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrongpassword"
  }'
```

**Expected:**
- Status: `401 Unauthorized`
- Error message about bad credentials

---

### 1.4 Login - Missing Fields
**Purpose:** Test validation on login request

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin"
  }'
```

**Expected:**
- Status: `400 Bad Request`
- Validation error for missing password

---

### 1.5 Access Protected Endpoint Without Token
**Purpose:** Verify protected endpoints require authentication

```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Project",
    "shortDescription": "No token test",
    "type": "PERSONAL"
  }'
```

**Expected:**
- Status: `403 Forbidden`
- Error message about authentication required

---

### 1.6 Access Protected Endpoint With Invalid Token
**Purpose:** Verify invalid tokens are rejected

```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid.token.here" \
  -d '{
    "name": "Test Project",
    "shortDescription": "Invalid token test",
    "type": "PERSONAL"
  }'
```

**Expected:**
- Status: `403 Forbidden`
- Error about invalid token

---

### 1.7 Access Public Endpoint Without Token
**Purpose:** Verify public endpoints work without authentication

```bash
curl http://localhost:8080/api/v1/projects
```

**Expected:**
- Status: `200 OK`
- Projects list returned (may be empty)

---

## 2. Operations Endpoints

### 2.1 Health Check
**Purpose:** Verify service is running

```bash
curl http://localhost:8080/api/v1/operations/health
```

**Expected:**
- Status: `200 OK`
- Body contains: `status: "UP"`, `service`, `version`, `environment`

---

### 2.2 Encrypt Text
**Purpose:** Encrypt sensitive values for application.yml

```bash
curl -X POST http://localhost:8080/api/v1/operations/encrypt \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"text": "my-secret-password"}'
```

**Expected:**
- Status: `200 OK`
- Body: `{"status": "success", "data": {"text": "ENC(...)"}}`
- Encrypted value starts with `ENC(`

**Save the encrypted value for next test!**

---

### 2.3 Decrypt Text
**Purpose:** Verify encryption/decryption round-trip

```bash
# Use the encrypted value from previous test
curl -X POST http://localhost:8080/api/v1/operations/decrypt \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"text": "ENC(your-encrypted-value-here)"}'
```

**Expected:**
- Status: `200 OK`
- Body: `{"status": "success", "data": {"text": "my-secret-password"}}`
- Decrypted text matches original

---

### 2.4 Hash Password
**Purpose:** Generate BCrypt password hash

```bash
curl -X POST http://localhost:8080/api/v1/operations/hash-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"text": "newpassword123"}'
```

**Expected:**
- Status: `200 OK`
- Body: `{"status": "success", "data": {"text": "$2a$10$..."}}`
- Hash starts with `$2a$`

---

## 3. Project Endpoints

**Note:** POST, PUT, and DELETE endpoints require JWT authentication. Include the header:
```
-H "Authorization: Bearer $TOKEN"
```

### 3.1 Create Project (Minimal)
**Purpose:** Create project with only required fields

```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Project Alpha",
    "shortDescription": "A minimal test project",
    "type": "PERSONAL"
  }'
```

**Expected:**
- Status: `201 Created`
- Body: `{"status": "success", "data": {...}}`
- Response includes: `id`, `slug` (auto-generated as "test-project-alpha")
- `published` defaults to `false`
- `featured` defaults to `false`
- `status` defaults to `PLANNING`

**Save the project ID for subsequent tests!**

---

### 3.2 Create Project (Full)
**Purpose:** Create project with all optional fields

```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Personal Website Portfolio",
    "slug": "personal-website",
    "shortDescription": "Full-stack personal website showcasing projects",
    "fullDescription": "A comprehensive personal portfolio website built with enterprise-grade patterns.",
    "type": "PERSONAL",
    "status": "IN_PROGRESS",
    "difficultyLevel": "ADVANCED",
    "startDate": "2024-01-01T00:00:00",
    "published": true,
    "featured": true,
    "displayOrder": 1
  }'
```

**Expected:**
- Status: `201 Created`
- All fields populated as specified
- `images` array is empty `[]`

---

### 3.3 Create Project (Validation Errors)
**Purpose:** Test validation rules

**Test Case A: Missing required fields**
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "type": "PERSONAL"
  }'
```

**Expected:**
- Status: `400 Bad Request`
- Body: `{"status": "error", "errorCode": "VALIDATION_FAILED", ...}`
- Errors include: `name: "must not be blank"`, `shortDescription: "must not be blank"`

**Test Case B: Duplicate name**
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Project Alpha",
    "shortDescription": "Duplicate name",
    "type": "PERSONAL"
  }'
```

**Expected:**
- Status: `409 Conflict`
- Error: `{"status": "error", "errorCode": "DUPLICATE_RESOURCE"}`

---

### 3.4 Get Project by ID (Public)
**Purpose:** Retrieve single project - no auth required

```bash
curl http://localhost:8080/api/v1/projects/1
```

**Expected:**
- Status: `200 OK`
- Body contains all project fields

**Test Case: Non-existent ID**
```bash
curl http://localhost:8080/api/v1/projects/99999
```

**Expected:**
- Status: `404 Not Found`
- Error: `{"status": "error", "errorCode": "NOT_FOUND"}`

---

### 3.5 Get All Projects (Public)
**Purpose:** List all projects - no auth required

```bash
curl http://localhost:8080/api/v1/projects
```

**Expected:**
- Status: `200 OK`
- Body: `{"status": "success", "data": [...]}`

---

### 3.6 Get Projects Paginated (Public)
**Purpose:** Test pagination

```bash
curl "http://localhost:8080/api/v1/projects/paginated?page=0&size=10&sort=createdAt,desc"
```

**Expected:**
- Status: `200 OK`
- Body includes: `content`, `totalElements`, `totalPages`, `size`, `number`

---

### 3.7 Get Projects by Technology (Public)
**Purpose:** Filter projects by technology name

```bash
curl http://localhost:8080/api/v1/projects/technology/Java
```

**Expected:**
- Status: `200 OK`
- Only projects using "Java" technology returned

---

### 3.8 Update Project
**Purpose:** Modify existing project

```bash
curl -X PUT http://localhost:8080/api/v1/projects/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Project Alpha",
    "shortDescription": "Updated description with new info",
    "type": "PERSONAL",
    "status": "IN_PROGRESS",
    "published": true
  }'
```

**Expected:**
- Status: `200 OK`
- Fields updated accordingly

---

### 3.9 Delete Project (Should Fail - Published)
**Purpose:** Test business rule: cannot delete published projects

```bash
curl -X DELETE http://localhost:8080/api/v1/projects/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `400 Bad Request`
- Error: `{"errorCode": "DELETE_PUBLISHED"}`

---

### 3.10 Delete Project (Should Succeed)
**Purpose:** Successfully delete unpublished project

**First unpublish:**
```bash
curl -X PUT http://localhost:8080/api/v1/projects/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Project Alpha",
    "shortDescription": "Updated description",
    "type": "PERSONAL",
    "published": false
  }'
```

**Then delete:**
```bash
curl -X DELETE http://localhost:8080/api/v1/projects/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Verify: `GET /api/v1/projects/1` returns `404`

---

## 4. Project Link Endpoints

### Setup: Create a Test Project
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Link Test Project",
    "shortDescription": "Project for testing links",
    "type": "PERSONAL"
  }'
```
**Save the project ID!**

---

### 4.1 Create Project Link
```bash
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/links \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "url": "https://github.com/caseythecoder90/example",
    "linkType": "GITHUB",
    "displayOrder": 0
  }'
```

**Expected:**
- Status: `201 Created`
- Response includes: `id`, `url`, `linkType: "GITHUB"`

---

### 4.2 Create Multiple Links
```bash
# Demo link
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/links \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "url": "https://demo.caseyquinn.com",
    "linkType": "LIVE",
    "displayOrder": 1
  }'

# Documentation link
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/links \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "url": "https://docs.caseyquinn.com",
    "linkType": "DOCUMENTATION",
    "displayOrder": 2
  }'
```

---

### 4.3 Get All Project Links (Public)
```bash
curl http://localhost:8080/api/v1/projects/{projectId}/links
```

**Expected:**
- Status: `200 OK`
- Array with all links for the project

---

### 4.4 Get Single Link (Public)
```bash
curl http://localhost:8080/api/v1/projects/{projectId}/links/{linkId}
```

**Expected:**
- Status: `200 OK`

---

### 4.5 Update Link
```bash
curl -X PUT http://localhost:8080/api/v1/projects/{projectId}/links/{linkId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "url": "https://github.com/caseythecoder90/updated-repo",
    "linkType": "GITHUB",
    "displayOrder": 0
  }'
```

**Expected:**
- Status: `200 OK`
- URL updated

---

### 4.6 Delete Link
```bash
curl -X DELETE http://localhost:8080/api/v1/projects/{projectId}/links/{linkId} \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`

---

## 5. Project Image Endpoints

### Setup: Create a Test Project
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Image Test Project",
    "shortDescription": "Project for testing image uploads",
    "type": "PERSONAL"
  }'
```
**Save the project ID!**

---

### 5.1 Upload Image
**Prepare a test image file (JPEG, PNG, GIF, or WebP, under 10MB)**

```bash
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/your/test-image.jpg" \
  -F 'imageType=SCREENSHOT' \
  -F 'altText=Homepage screenshot' \
  -F 'caption=Main landing page view' \
  -F 'isPrimary=true' \
  -F 'displayOrder=0'
```

**Expected:**
- Status: `201 Created`
- Response includes: `id`, `url` (Cloudinary CDN URL), `cloudinaryPublicId`, `isPrimary: true`

---

### 5.2 Get Project Images (Public)
```bash
curl http://localhost:8080/api/v1/projects/{projectId}/images
```

**Expected:**
- Status: `200 OK`
- Array of images sorted by `displayOrder`

---

### 5.3 Update Image Metadata
```bash
curl -X PUT http://localhost:8080/api/v1/projects/{projectId}/images/{imageId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "altText": "Updated alt text",
    "caption": "New caption",
    "displayOrder": 10
  }'
```

**Expected:**
- Status: `200 OK`
- Metadata updated, `url` unchanged

---

### 5.4 Set Primary Image
```bash
curl -X PUT http://localhost:8080/api/v1/projects/{projectId}/images/{imageId}/set-primary \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Only one image has `isPrimary: true`

---

### 5.5 Delete Image
```bash
curl -X DELETE http://localhost:8080/api/v1/projects/{projectId}/images/{imageId} \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Image removed from Cloudinary and database

---

### 5.6 File Validation Tests

**Test Case A: File too large (>10MB)** → Expected: `400`, `errorCode: "FILE_TOO_LARGE"`
**Test Case B: Invalid file type (e.g., .txt)** → Expected: `400`, `errorCode: "INVALID_FILE"`
**Test Case C: Empty file** → Expected: `400`
**Test Case D: Max 20 images limit** → Expected: `400`, `errorCode: "MAX_IMAGES"`

---

## 6. Technology Endpoints

### 6.1 Create Technology
```bash
curl -X POST http://localhost:8080/api/v1/technologies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Java",
    "category": "LANGUAGE",
    "proficiencyLevel": "EXPERT",
    "yearsExperience": 8.5
  }'
```

**Expected:**
- Status: `201 Created`
- Response includes `id`, `name`, `category`, `proficiencyLevel`

**Create additional technologies for filtering tests:**
```bash
# Spring Boot
curl -X POST http://localhost:8080/api/v1/technologies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Spring Boot",
    "category": "FRAMEWORK",
    "proficiencyLevel": "PROFICIENT",
    "yearsExperience": 5.0,
    "featured": true
  }'

# PostgreSQL
curl -X POST http://localhost:8080/api/v1/technologies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "PostgreSQL",
    "category": "DATABASE",
    "proficiencyLevel": "PROFICIENT",
    "yearsExperience": 4.0
  }'
```

---

### 6.2 Get All Technologies (Public)
```bash
curl http://localhost:8080/api/v1/technologies
```

**Expected:**
- Status: `200 OK`
- Array of all technologies

---

### 6.3 Get Technologies Paginated (Public)
```bash
curl "http://localhost:8080/api/v1/technologies/paginated?page=0&size=10"
```

**Expected:**
- Status: `200 OK`
- Paginated response with `content`, `totalElements`, `totalPages`

---

### 6.4 Get Technology by ID (Public)
```bash
curl http://localhost:8080/api/v1/technologies/1
```

**Expected:**
- Status: `200 OK`

**Test Case: Non-existent ID**
```bash
curl http://localhost:8080/api/v1/technologies/99999
```
- Expected: `404 Not Found`

---

### 6.5 Get Technology by Name (Public)
```bash
curl http://localhost:8080/api/v1/technologies/name/Java
```

**Expected:**
- Status: `200 OK`
- Returns the Java technology

---

### 6.6 Get Technologies by Category (Public)
```bash
curl http://localhost:8080/api/v1/technologies/category/LANGUAGE
```

**Expected:**
- Status: `200 OK`
- Only technologies with `category: "LANGUAGE"`

---

### 6.7 Get Technologies by Proficiency (Public)
```bash
curl http://localhost:8080/api/v1/technologies/proficiency/EXPERT
```

**Expected:**
- Status: `200 OK`
- Only technologies with `proficiencyLevel: "EXPERT"`

---

### 6.8 Get Featured Technologies (Public)
```bash
curl http://localhost:8080/api/v1/technologies/featured
```

**Expected:**
- Status: `200 OK`
- Only technologies with `featured: true`

---

### 6.9 Get Most Used Technologies (Public)
```bash
curl http://localhost:8080/api/v1/technologies/most-used
```

**Expected:**
- Status: `200 OK`
- Technologies sorted by usage count

---

### 6.10 Update Technology
```bash
curl -X PUT http://localhost:8080/api/v1/technologies/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Java",
    "category": "LANGUAGE",
    "proficiencyLevel": "EXPERT",
    "yearsExperience": 9.0,
    "featured": true
  }'
```

**Expected:**
- Status: `200 OK`
- `yearsExperience` updated to 9.0

---

### 6.11 Delete Technology
```bash
curl -X DELETE http://localhost:8080/api/v1/technologies/3 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`

**Test Case: Delete technology in use**
- Expected: `400`, `errorCode: "TECH_IN_USE"`

---

## 7. Certification Endpoints

### 7.1 Create Certification
```bash
curl -X POST http://localhost:8080/api/v1/certifications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "AWS Solutions Architect Associate",
    "issuingOrganization": "Amazon Web Services",
    "description": "Cloud architecture certification",
    "certificationStatus": "EARNED",
    "issueDate": "2024-06-15",
    "expirationDate": "2027-06-15",
    "credentialUrl": "https://aws.amazon.com/verification/12345",
    "published": true,
    "featured": true
  }'
```

**Expected:**
- Status: `201 Created`
- `slug` auto-generated: "aws-solutions-architect-associate"

---

### 7.2 Create Another Certification
```bash
curl -X POST http://localhost:8080/api/v1/certifications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Spring Professional",
    "issuingOrganization": "VMware",
    "description": "Spring Framework certification",
    "certificationStatus": "IN_PROGRESS",
    "published": false
  }'
```

---

### 7.3 Get All Certifications (Public)
```bash
curl http://localhost:8080/api/v1/certifications
```

**Expected:**
- Status: `200 OK`
- Array of all certifications

---

### 7.4 Get Certification by ID (Public)
```bash
curl http://localhost:8080/api/v1/certifications/1
```

---

### 7.5 Get Certification by Slug (Public)
```bash
curl http://localhost:8080/api/v1/certifications/slug/aws-solutions-architect-associate
```

---

### 7.6 Get by Status (Public)
```bash
curl http://localhost:8080/api/v1/certifications/status/EARNED
```

**Expected:**
- Only certifications with `certificationStatus: "EARNED"`

---

### 7.7 Get by Organization (Public)
```bash
curl "http://localhost:8080/api/v1/certifications/organization/Amazon%20Web%20Services"
```

---

### 7.8 Get Published (Public)
```bash
curl http://localhost:8080/api/v1/certifications/published
```

**Expected:**
- Only certifications with `published: true`

---

### 7.9 Get Featured (Public)
```bash
curl http://localhost:8080/api/v1/certifications/featured
```

---

### 7.10 Link Technology to Certification
```bash
curl -X POST http://localhost:8080/api/v1/certifications/1/technologies/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Certification now includes technology in its response

**Test duplicate link:**
```bash
curl -X POST http://localhost:8080/api/v1/certifications/1/technologies/1 \
  -H "Authorization: Bearer $TOKEN"
```
- Expected: `409 Conflict`, `errorCode: "DUPLICATE_CERT_TECH_ASSOC"`

---

### 7.11 Unlink Technology from Certification
```bash
curl -X DELETE http://localhost:8080/api/v1/certifications/1/technologies/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 7.12 Update Certification
```bash
curl -X PUT http://localhost:8080/api/v1/certifications/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "AWS Solutions Architect Associate",
    "issuingOrganization": "Amazon Web Services",
    "certificationStatus": "EARNED",
    "published": true,
    "featured": false
  }'
```

---

### 7.13 Delete Certification
**Test: Cannot delete published certification**
```bash
curl -X DELETE http://localhost:8080/api/v1/certifications/1 \
  -H "Authorization: Bearer $TOKEN"
```
- Expected: `400`, `errorCode: "DELETE_PUBLISHED"`

**Unpublish first, then delete:**
```bash
curl -X PUT http://localhost:8080/api/v1/certifications/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "AWS Solutions Architect Associate", "issuingOrganization": "Amazon Web Services", "published": false}'

curl -X DELETE http://localhost:8080/api/v1/certifications/1 \
  -H "Authorization: Bearer $TOKEN"
```
- Expected: `200 OK`

---

## 8. Blog Category Endpoints

### 8.1 Create Category
```bash
curl -X POST http://localhost:8080/api/v1/blog/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Software Engineering",
    "description": "Posts about software development practices"
  }'
```

**Expected:**
- Status: `201 Created`
- `slug` auto-generated: "software-engineering"

---

### 8.2 Create Additional Categories
```bash
curl -X POST http://localhost:8080/api/v1/blog/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "DevOps", "description": "CI/CD and infrastructure"}'

curl -X POST http://localhost:8080/api/v1/blog/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Career Growth", "description": "Professional development tips"}'
```

---

### 8.3 Get All Categories (Public)
```bash
curl http://localhost:8080/api/v1/blog/categories
```

---

### 8.4 Get Category by Slug (Public)
```bash
curl http://localhost:8080/api/v1/blog/categories/slug/software-engineering
```

---

### 8.5 Update Category
```bash
curl -X PUT http://localhost:8080/api/v1/blog/categories/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Software Engineering",
    "description": "Updated description for SE category"
  }'
```

---

### 8.6 Delete Category
```bash
curl -X DELETE http://localhost:8080/api/v1/blog/categories/3 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 9. Blog Tag Endpoints

### 9.1 Create Tags
```bash
curl -X POST http://localhost:8080/api/v1/blog/tags \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Spring Boot"}'

curl -X POST http://localhost:8080/api/v1/blog/tags \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Java"}'

curl -X POST http://localhost:8080/api/v1/blog/tags \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Architecture"}'
```

---

### 9.2 Get All Tags (Public)
```bash
curl http://localhost:8080/api/v1/blog/tags
```

---

### 9.3 Get Popular Tags (Public)
```bash
curl http://localhost:8080/api/v1/blog/tags/popular
```

---

### 9.4 Get Tag by Slug (Public)
```bash
curl http://localhost:8080/api/v1/blog/tags/slug/spring-boot
```

---

### 9.5 Update Tag
```bash
curl -X PUT http://localhost:8080/api/v1/blog/tags/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Spring Framework"}'
```

---

### 9.6 Delete Tag
```bash
curl -X DELETE http://localhost:8080/api/v1/blog/tags/3 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 10. Blog Post Endpoints

### 10.1 Create Blog Post
```bash
curl -X POST http://localhost:8080/api/v1/blog/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Building Enterprise APIs with Spring Boot",
    "content": "In this post, we explore best practices for building enterprise-grade REST APIs...",
    "excerpt": "A deep dive into enterprise API patterns",
    "metaDescription": "Learn enterprise Spring Boot API patterns"
  }'
```

**Expected:**
- Status: `201 Created`
- `slug` auto-generated
- `published` defaults to `false`

---

### 10.2 Get All Posts (Public)
```bash
curl http://localhost:8080/api/v1/blog/posts
```

---

### 10.3 Get Published Posts (Public)
```bash
curl http://localhost:8080/api/v1/blog/posts/published
```

**Expected:**
- Only posts with `published: true` (may be empty initially)

---

### 10.4 Publish Post
```bash
curl -X PUT http://localhost:8080/api/v1/blog/posts/1/publish \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- `published: true`, `publishedAt` set

---

### 10.5 Unpublish Post
```bash
curl -X PUT http://localhost:8080/api/v1/blog/posts/1/unpublish \
  -H "Authorization: Bearer $TOKEN"
```

---

### 10.6 Add Category to Post
```bash
curl -X POST http://localhost:8080/api/v1/blog/posts/1/categories/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Post now includes the category

**Test duplicate:**
- Expected: `409`, `errorCode: "DUPLICATE_BLOG_CAT_ASSOC"`

---

### 10.7 Remove Category from Post
```bash
curl -X DELETE http://localhost:8080/api/v1/blog/posts/1/categories/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 10.8 Add Tag to Post
```bash
curl -X POST http://localhost:8080/api/v1/blog/posts/1/tags/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 10.9 Remove Tag from Post
```bash
curl -X DELETE http://localhost:8080/api/v1/blog/posts/1/tags/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 10.10 Get Posts by Category (Public)
```bash
curl http://localhost:8080/api/v1/blog/posts/category/software-engineering
```

---

### 10.11 Get Posts by Tag (Public)
```bash
curl http://localhost:8080/api/v1/blog/posts/tag/spring-boot
```

---

### 10.12 Search Posts (Public)
```bash
curl "http://localhost:8080/api/v1/blog/posts/search?query=enterprise"
```

**Expected:**
- Posts matching the search query

---

### 10.13 Get Post by Slug (Public)
```bash
curl http://localhost:8080/api/v1/blog/posts/slug/building-enterprise-apis-with-spring-boot
```

---

### 10.14 Update Post
```bash
curl -X PUT http://localhost:8080/api/v1/blog/posts/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Building Enterprise APIs with Spring Boot",
    "content": "Updated content with more examples...",
    "excerpt": "Updated excerpt"
  }'
```

---

### 10.15 Delete Post
```bash
curl -X DELETE http://localhost:8080/api/v1/blog/posts/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 11. Blog Post Image Endpoints

### Setup: Create a blog post first (section 10.1)

### 11.1 Upload Blog Post Image
```bash
curl -X POST http://localhost:8080/api/v1/blog/posts/{postId}/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/blog-image.jpg" \
  -F 'imageType=SCREENSHOT' \
  -F 'altText=Blog post header image' \
  -F 'caption=Main blog illustration' \
  -F 'isPrimary=true' \
  -F 'displayOrder=0'
```

**Expected:**
- Status: `201 Created`
- Cloudinary URL returned

---

### 11.2 Get Blog Post Images (Public)
```bash
curl http://localhost:8080/api/v1/blog/posts/{postId}/images
```

---

### 11.3 Set Primary Blog Image
```bash
curl -X PUT http://localhost:8080/api/v1/blog/posts/{postId}/images/{imageId}/primary \
  -H "Authorization: Bearer $TOKEN"
```

---

### 11.4 Delete Blog Post Image
```bash
curl -X DELETE http://localhost:8080/api/v1/blog/posts/{postId}/images/{imageId} \
  -H "Authorization: Bearer $TOKEN"
```

---

## 12. Contact Submission Endpoints

### 12.1 Submit Contact Form (Public - No Auth Required)
```bash
curl -X POST http://localhost:8080/api/v1/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "subject": "Project Inquiry",
    "message": "Hi Casey, I am interested in collaborating on a project. I have a Spring Boot application that needs enterprise-grade architecture improvements.",
    "inquiryType": "COLLABORATION"
  }'
```

**Expected:**
- Status: `201 Created`
- Response includes: `id`, `status: "NEW"`, `createdAt`
- Email notifications sent asynchronously (check logs)

---

### 12.2 Submit Contact - Minimal Fields
```bash
curl -X POST http://localhost:8080/api/v1/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane@example.com",
    "message": "Hello! I wanted to reach out about potential opportunities. Your portfolio is impressive and I would love to connect."
  }'
```

**Expected:**
- Status: `201 Created`
- `inquiryType` defaults to `GENERAL`
- `subject` is null

---

### 12.3 Submit Contact - Validation Errors
```bash
# Missing required fields
curl -X POST http://localhost:8080/api/v1/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test"
  }'
```

**Expected:**
- Status: `400 Bad Request`
- Errors for: `email`, `message`

```bash
# Invalid email
curl -X POST http://localhost:8080/api/v1/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "email": "not-an-email",
    "message": "This message should fail validation because of the email."
  }'
```

**Expected:**
- Status: `400 Bad Request`
- Error for invalid email format

```bash
# Message too short (min 20 chars)
curl -X POST http://localhost:8080/api/v1/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "email": "test@example.com",
    "message": "Too short"
  }'
```

**Expected:**
- Status: `400 Bad Request`
- Error about message length

---

### 12.4 Get All Submissions (Admin Only)
```bash
curl http://localhost:8080/api/v1/contact \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Array of all submissions, newest first

**Without auth:**
```bash
curl http://localhost:8080/api/v1/contact
```
- Expected: `403 Forbidden`

---

### 12.5 Get Submission by ID (Admin Only)
```bash
curl http://localhost:8080/api/v1/contact/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 12.6 Get Submissions by Status (Admin Only)
```bash
curl http://localhost:8080/api/v1/contact/status/NEW \
  -H "Authorization: Bearer $TOKEN"
```

---

### 12.7 Get Submissions by Inquiry Type (Admin Only)
```bash
curl http://localhost:8080/api/v1/contact/inquiry-type/COLLABORATION \
  -H "Authorization: Bearer $TOKEN"
```

---

### 12.8 Update Submission Status (Admin Only)
```bash
curl -X PUT http://localhost:8080/api/v1/contact/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "status": "READ"
  }'
```

**Expected:**
- Status: `200 OK`
- `status` updated to `READ`

**Test REPLIED status:**
```bash
curl -X PUT http://localhost:8080/api/v1/contact/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "status": "REPLIED"
  }'
```

**Expected:**
- `respondedAt` timestamp set

---

### 12.9 Delete Submission (Admin Only)
```bash
curl -X DELETE http://localhost:8080/api/v1/contact/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 13. Resume Endpoints

### 13.1 Upload Resume (Admin Only)
**Prepare a PDF file under 5MB**

```bash
curl -X POST http://localhost:8080/api/v1/resume \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/resume.pdf"
```

**Expected:**
- Status: `201 Created`
- Response includes: `id`, `fileName`, `fileUrl` (Cloudinary URL), `fileSize`, `contentType: "application/pdf"`, `active: true`

---

### 13.2 Get Active Resume (Public)
```bash
curl http://localhost:8080/api/v1/resume
```

**Expected:**
- Status: `200 OK`
- Resume metadata returned

**Test when no resume exists:**
- Expected: `404 Not Found`

---

### 13.3 Download Resume (Public)
```bash
curl -v http://localhost:8080/api/v1/resume/download
```

**Expected:**
- Status: `302 Found` (redirect)
- `Location` header points to Cloudinary URL
- Following the redirect downloads the PDF

---

### 13.4 Replace Resume (Admin Only)
```bash
curl -X POST http://localhost:8080/api/v1/resume \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/updated-resume.pdf"
```

**Expected:**
- Status: `201 Created`
- Previous resume deleted from Cloudinary
- New resume is now active

**Verify only one active resume:**
```bash
curl http://localhost:8080/api/v1/resume
```
- Should return the new resume, not the old one

---

### 13.5 Delete Resume (Admin Only)
```bash
curl -X DELETE http://localhost:8080/api/v1/resume \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Resume deleted from Cloudinary and database

**Verify deletion:**
```bash
curl http://localhost:8080/api/v1/resume
```
- Expected: `404 Not Found`

---

### 13.6 Resume Validation Tests

**Test Case A: Non-PDF file**
```bash
curl -X POST http://localhost:8080/api/v1/resume \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/image.jpg"
```
- Expected: `400`, error about invalid PDF

**Test Case B: File too large (>5MB)**
```bash
curl -X POST http://localhost:8080/api/v1/resume \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/large-file.pdf"
```
- Expected: `400`, error about file size

---

## 14. Redis Caching Tests

### Prerequisites
- Redis running on `localhost:6379`
- Connect to Redis CLI: `redis-cli -a $REDIS_PASSWORD`

### 14.1 Verify Cache Population on GET
```bash
# Clear all caches first
redis-cli -a $REDIS_PASSWORD FLUSHALL

# Fetch all projects (should hit DB)
curl http://localhost:8080/api/v1/projects

# Check Redis for cached data
redis-cli -a $REDIS_PASSWORD KEYS "*projects*"
```

**Expected:**
- First request hits database (check application logs)
- Redis now contains `projects::all` key

---

### 14.2 Verify Cache Hit on Repeated GET
```bash
# Fetch again - should be served from cache
curl http://localhost:8080/api/v1/projects
```

**Expected:**
- No database query in logs (served from Redis cache)
- Response is identical to first request

---

### 14.3 Verify Cache Eviction on Write
```bash
# Create a new project (should evict projects cache)
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Cache Test Project",
    "shortDescription": "Testing cache eviction",
    "type": "PERSONAL"
  }'

# Check Redis - projects cache should be cleared
redis-cli -a $REDIS_PASSWORD KEYS "*projects*"

# Fetch again - should hit DB and repopulate cache
curl http://localhost:8080/api/v1/projects
```

**Expected:**
- After POST, projects cache keys are gone
- Next GET hits database and repopulates cache

---

### 14.4 Verify Technology Cross-Cache Eviction
```bash
# Populate all three caches
curl http://localhost:8080/api/v1/projects
curl http://localhost:8080/api/v1/technologies
curl http://localhost:8080/api/v1/certifications

# Verify caches populated
redis-cli -a $REDIS_PASSWORD KEYS "*"

# Update a technology (should evict projects, technologies, AND certifications caches)
curl -X PUT http://localhost:8080/api/v1/technologies/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Java",
    "category": "LANGUAGE",
    "proficiencyLevel": "EXPERT",
    "yearsExperience": 10.0
  }'

# Check Redis - all three caches should be cleared
redis-cli -a $REDIS_PASSWORD KEYS "*"
```

**Expected:**
- Before update: keys for projects, technologies, certifications exist
- After update: all three cache groups are evicted

---

### 14.5 Verify Certifications Caching
```bash
redis-cli -a $REDIS_PASSWORD FLUSHALL

# Fetch certifications by different keys
curl http://localhost:8080/api/v1/certifications
curl http://localhost:8080/api/v1/certifications/published
curl http://localhost:8080/api/v1/certifications/featured

# Verify separate cache entries
redis-cli -a $REDIS_PASSWORD KEYS "*certifications*"
```

**Expected:**
- Three separate cache entries: `certifications::all`, `certifications::published`, `certifications::featured`

---

### 14.6 Verify Cache TTL
```bash
# Check remaining TTL on a cache entry
redis-cli -a $REDIS_PASSWORD TTL "certifications::all"
```

**Expected:**
- Projects: ~600 seconds (10 minutes)
- Technologies: ~1800 seconds (30 minutes)
- Certifications: ~1800 seconds (30 minutes)

---

## 15. Rate Limiting Tests

### 15.1 Verify Rate Limit Headers on Normal Request
```bash
curl -v http://localhost:8080/api/v1/projects
```

**Expected:**
- Status: `200 OK`
- Response header: `X-Rate-Limit-Remaining: 59` (or similar)

---

### 15.2 Public API Rate Limit (60 requests/minute)
```bash
# Send 61 rapid requests to a public endpoint
for i in $(seq 1 61); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/projects)
  echo "Request $i: $STATUS"
done
```

**Expected:**
- Requests 1-60: `200 OK`
- Request 61: `429 Too Many Requests`
- 429 response includes `Retry-After` header
- Body: `{"status":"error","errorCode":"RATE_LIMIT_EXCEEDED","message":"Too many requests..."}`

---

### 15.3 Login Rate Limit (5 requests/minute)
```bash
# Send 6 rapid login attempts
for i in $(seq 1 6); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/v1/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"wrong"}')
  echo "Request $i: $STATUS"
done
```

**Expected:**
- Requests 1-5: `401 Unauthorized` (bad password, but not rate limited)
- Request 6: `429 Too Many Requests`

---

### 15.4 Admin API Rate Limit (30 requests/minute)
```bash
# Send 31 rapid write requests
for i in $(seq 1 31); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:8080/api/v1/projects \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "{\"name\":\"Rate Limit Test $i\",\"shortDescription\":\"Test\",\"type\":\"PERSONAL\"}")
  echo "Request $i: $STATUS"
done
```

**Expected:**
- Requests 1-30: `201 Created` (or `409` for duplicates)
- Request 31: `429 Too Many Requests`

---

### 15.5 Swagger/Actuator Excluded from Rate Limiting
```bash
# These should never be rate limited
for i in $(seq 1 65); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui/index.html)
  echo "Request $i: $STATUS"
done
```

**Expected:**
- All 65 requests succeed (no `429`)
- No `X-Rate-Limit-Remaining` header present

---

### 15.6 Rate Limit Recovery After Window
```bash
# Exhaust the public rate limit
for i in $(seq 1 61); do
  curl -s -o /dev/null http://localhost:8080/api/v1/projects
done

# Wait for Retry-After period, then try again
# (check Retry-After header for exact seconds)
sleep 62

curl -v http://localhost:8080/api/v1/projects
```

**Expected:**
- After waiting, request succeeds with `200 OK`
- `X-Rate-Limit-Remaining` header resets

---

## 16. End-to-End Scenarios

### Scenario 1: Complete Project Setup
1. **Login** and save token
2. **Create project** with required fields
3. **Create technologies** (Java, Spring Boot)
4. **Add links** (GitHub, Demo, Documentation)
5. **Upload 3 images** with different types
6. **Set primary image**
7. **Publish project**
8. **Verify** all data via public GET endpoints

### Scenario 2: Blog Post Workflow
1. **Create categories** (2-3)
2. **Create tags** (3-4)
3. **Create blog post**
4. **Link categories and tags** to post
5. **Upload blog images**
6. **Publish post**
7. **Search** for post by keyword
8. **Filter** by category and tag

### Scenario 3: Contact Form Flow
1. **Submit contact form** (public, no auth)
2. **Check logs** for async email notifications
3. **View submissions** as admin
4. **Filter by status** and inquiry type
5. **Update status** to READ, then REPLIED
6. **Verify respondedAt** is set

### Scenario 4: Resume Management
1. **Upload resume** PDF
2. **Get active resume** metadata (public)
3. **Download resume** via redirect (public)
4. **Upload new resume** - verify old one replaced
5. **Delete resume**
6. **Verify 404** on get active

### Scenario 5: Caching and Rate Limiting
1. **Flush Redis** caches
2. **GET projects** - verify DB hit, cache populated
3. **GET projects** again - verify cache hit (no DB query in logs)
4. **Create project** - verify cache evicted
5. **GET projects** - verify DB hit again (cache repopulated)
6. **Update technology** - verify cross-cache eviction (projects, technologies, certifications all cleared)
7. **Rapid-fire 61 GET requests** - verify 429 on request 61
8. **Check `X-Rate-Limit-Remaining`** header decrements
9. **Wait for rate limit window** - verify recovery

---

## Checklist

### Authentication
- [ ] Login with valid credentials - get token
- [ ] Login with invalid username - 401
- [ ] Login with invalid password - 401
- [ ] Login with missing fields - 400
- [ ] Protected endpoint without token - 403
- [ ] Protected endpoint with invalid token - 403
- [ ] Public endpoint without token - 200

### Operations
- [ ] Health check works
- [ ] Encrypt/decrypt round-trip successful
- [ ] Hash password returns BCrypt hash

### Projects - Happy Path
- [ ] Create minimal project (with auth)
- [ ] Create full project (with auth)
- [ ] Get by ID (public)
- [ ] Get all projects (public)
- [ ] Get paginated projects (public)
- [ ] Get by technology (public)
- [ ] Update project (with auth)
- [ ] Delete unpublished project (with auth)

### Projects - Error Cases
- [ ] Validation errors (missing fields)
- [ ] Duplicate name rejected
- [ ] Cannot delete published project
- [ ] 404 for non-existent project

### Project Links
- [ ] Create link (with auth)
- [ ] Get all links (public)
- [ ] Get single link (public)
- [ ] Update link (with auth)
- [ ] Delete link (with auth)

### Project Images - Happy Path
- [ ] Upload image successfully (with auth)
- [ ] Upload multiple images (with auth)
- [ ] Get all images for project (public)
- [ ] Get single image (public)
- [ ] Update image metadata (with auth)
- [ ] Set primary image (with auth)
- [ ] Delete image (with auth)

### Project Images - Error Cases
- [ ] File too large rejected
- [ ] Invalid file type rejected
- [ ] Empty file rejected
- [ ] Max 20 images enforced
- [ ] Image ownership validated

### Technologies
- [ ] Create technology (with auth)
- [ ] Get all technologies (public)
- [ ] Get paginated (public)
- [ ] Get by ID (public)
- [ ] Get by name (public)
- [ ] Get by category (public)
- [ ] Get by proficiency (public)
- [ ] Get featured (public)
- [ ] Get most used (public)
- [ ] Update technology (with auth)
- [ ] Delete technology (with auth)
- [ ] Cannot delete technology in use

### Certifications
- [ ] Create certification (with auth)
- [ ] Get all (public)
- [ ] Get by ID (public)
- [ ] Get by slug (public)
- [ ] Get by status (public)
- [ ] Get by organization (public)
- [ ] Get published (public)
- [ ] Get featured (public)
- [ ] Link technology (with auth)
- [ ] Duplicate technology link rejected
- [ ] Unlink technology (with auth)
- [ ] Update certification (with auth)
- [ ] Cannot delete published certification
- [ ] Delete unpublished certification (with auth)

### Blog Categories
- [ ] Create category (with auth)
- [ ] Get all categories (public)
- [ ] Get by ID (public)
- [ ] Get by slug (public)
- [ ] Update category (with auth)
- [ ] Delete category (with auth)

### Blog Tags
- [ ] Create tag (with auth)
- [ ] Get all tags (public)
- [ ] Get popular tags (public)
- [ ] Get by ID (public)
- [ ] Get by slug (public)
- [ ] Update tag (with auth)
- [ ] Delete tag (with auth)

### Blog Posts
- [ ] Create post (with auth)
- [ ] Get all posts (public)
- [ ] Get published posts (public)
- [ ] Get by ID (public)
- [ ] Get by slug (public)
- [ ] Publish post (with auth)
- [ ] Unpublish post (with auth)
- [ ] Add category to post (with auth)
- [ ] Duplicate category link rejected
- [ ] Remove category from post (with auth)
- [ ] Add tag to post (with auth)
- [ ] Duplicate tag link rejected
- [ ] Remove tag from post (with auth)
- [ ] Get posts by category (public)
- [ ] Get posts by tag (public)
- [ ] Search posts (public)
- [ ] Update post (with auth)
- [ ] Delete post (with auth)

### Blog Post Images
- [ ] Upload image (with auth)
- [ ] Get all images (public)
- [ ] Get image by ID (public)
- [ ] Update image metadata (with auth)
- [ ] Set primary image (with auth)
- [ ] Delete image (with auth)
- [ ] Max 20 images enforced

### Contact Submissions
- [ ] Submit form (public, no auth)
- [ ] Submit with minimal fields (public)
- [ ] Validation: missing email - 400
- [ ] Validation: invalid email - 400
- [ ] Validation: message too short - 400
- [ ] Get all submissions (admin only)
- [ ] Get all submissions without auth - 403
- [ ] Get by ID (admin only)
- [ ] Get by status (admin only)
- [ ] Get by inquiry type (admin only)
- [ ] Update status to READ (admin only)
- [ ] Update status to REPLIED - respondedAt set
- [ ] Delete submission (admin only)

### Resume
- [ ] Upload PDF (admin only)
- [ ] Get active resume (public)
- [ ] Download resume - 302 redirect (public)
- [ ] Replace resume - old deleted
- [ ] Delete resume (admin only)
- [ ] No active resume - 404
- [ ] Non-PDF rejected
- [ ] File too large rejected (>5MB)

### Redis Caching
- [ ] Cache populated on first GET (projects, technologies, certifications)
- [ ] Cache hit on repeated GET (no DB query in logs)
- [ ] Cache evicted on POST/PUT/DELETE
- [ ] Technology write evicts projects + certifications (cross-cache)
- [ ] Cache entries have correct TTL (projects: 10min, others: 30min)
- [ ] Multiple cache keys per resource (all, by-id, by-slug, published, featured)

### Rate Limiting
- [ ] X-Rate-Limit-Remaining header present on responses
- [ ] Public tier: 429 after 60 requests/minute
- [ ] Login tier: 429 after 5 requests/minute
- [ ] Admin tier: 429 after 30 requests/minute
- [ ] 429 response includes Retry-After header
- [ ] 429 response body has RATE_LIMIT_EXCEEDED error code
- [ ] Swagger/actuator paths excluded from rate limiting
- [ ] Rate limit recovers after window expires

---

## Common Issues & Troubleshooting

**Issue:** Connection refused
- **Fix:** Ensure app is running: `mvn spring-boot:run`

**Issue:** 403 Forbidden on protected endpoints
- **Fix:** Include `Authorization: Bearer <token>` header
- **Fix:** Verify token hasn't expired (24 hours)
- **Fix:** Check token format: `Bearer ` + token (note the space)

**Issue:** 401 Unauthorized on login
- **Fix:** Verify username is `admin` and password is `admin123`
- **Fix:** Check user exists in database: `SELECT * FROM users WHERE username='admin';`

**Issue:** 404 on all endpoints
- **Fix:** Check base URL includes `/api/v1`

**Issue:** Database errors
- **Fix:** Ensure PostgreSQL is running and Flyway migrations succeeded

**Issue:** Cloudinary upload fails
- **Fix:** Check `application.yml` has correct Cloudinary credentials

**Issue:** Token expired
- **Fix:** Login again to get a new token (tokens expire after 24 hours)

**Issue:** Email notifications not sending
- **Fix:** Check Resend API key is configured in `application.yml`
- **Fix:** Check application logs for async email errors
- **Fix:** Verify `@EnableAsync` is on the application class

**Issue:** Resume upload fails
- **Fix:** Ensure file is a valid PDF (magic bytes check)
- **Fix:** Ensure file is under 5MB

**Issue:** Thymeleaf template errors
- **Fix:** Check templates exist in `src/main/resources/templates/email/`
- **Fix:** Verify `spring.thymeleaf.enabled: false` in application.yml

**Issue:** Redis connection refused
- **Fix:** Ensure Redis is running: `docker-compose -f src/main/resources/docker/docker-compose.yml up -d redis`
- **Fix:** Check `REDIS_PASSWORD` environment variable is set
- **Fix:** Verify Redis connectivity: `redis-cli -a $REDIS_PASSWORD ping` (should return `PONG`)

**Issue:** Caching not working (every request hits DB)
- **Fix:** Verify Redis is connected (check application startup logs)
- **Fix:** Ensure `spring.cache.type: redis` is set in application.yml
- **Fix:** Check `@EnableCaching` annotation is present on `CacheConfig`

**Issue:** 429 Too Many Requests unexpectedly
- **Fix:** Wait for the rate limit window to expire (check `Retry-After` header)
- **Fix:** Rate limits are per-IP - restart the app to clear in-memory buckets
- **Fix:** Check `rate-limiting.enabled: true` in application.yml (set to `false` to disable during testing)

---

## Notes

- **Default Admin Credentials:** username: `admin`, password: `admin123`
- **Token Expiration:** 24 hours - login again if expired
- **Postman Collection:** Already exists at `docs/api/postman/personal-website-api.postman_collection.json`
- **Test Images:** Use real image files (JPEG/PNG/GIF/WebP) under 10MB
- **Test PDF:** Use a real PDF file under 5MB for resume tests
- **Database State:** After each test run, database retains data - use DELETE endpoints or restart if needed
- **Logs:** Check console output for detailed error messages
- **Email Logs:** Look for `EmailService` log entries to verify async email sending

---

## Next Steps After Manual Testing

1. Document any bugs found
2. Verify all Cloudinary URLs work in browser
3. Check database state with SQL queries
4. Test with real project data
5. Verify email templates render correctly
6. Once APIs confirmed working, write unit tests based on actual behavior
