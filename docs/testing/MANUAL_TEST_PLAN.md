# Manual Testing Plan - Personal Website API

## Prerequisites

**Start Services:**
```bash
# Start PostgreSQL (if not running)
docker-compose -f src/main/resources/docker/docker-compose.yml up -d postgres

# Start the application
mvn spring-boot:run
```

**Verify Health:**
```bash
curl http://localhost:8080/api/v1/health
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
curl http://localhost:8080/api/v1/health
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
  -d '{"text": "ENC(your-encrypted-value-here)"}'
```

**Expected:**
- Status: `200 OK`
- Body: `{"status": "success", "data": {"text": "my-secret-password"}}`
- Decrypted text matches original

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
    "fullDescription": "A comprehensive personal portfolio website built with enterprise-grade patterns. Features project management, blog, and contact form.",
    "type": "PERSONAL",
    "status": "IN_PROGRESS",
    "difficultyLevel": "ADVANCED",
    "githubUrl": "https://github.com/caseythecoder90/personal-website",
    "demoUrl": "https://caseyquinn.com",
    "documentationUrl": "https://docs.caseyquinn.com",
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

**Test Case B: Invalid URL**
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Bad URL Project",
    "shortDescription": "Testing invalid URLs",
    "type": "PERSONAL",
    "githubUrl": "not-a-valid-url"
  }'
```

**Expected:**
- Status: `400 Bad Request`
- Error: `githubUrl: "Must be a valid URL"`

**Test Case C: Duplicate name**
```bash
# Try to create another project with same name as project from 3.1
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
# Use the ID from test 3.1
curl http://localhost:8080/api/v1/projects/1
```

**Expected:**
- Status: `200 OK`
- Body contains all project fields
- `images` array is present (empty for now)

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
- Array contains all projects created so far

---

### 3.6 Get Projects Paginated (Public)
**Purpose:** Test pagination - no auth required

```bash
curl "http://localhost:8080/api/v1/projects/paginated?page=0&size=10&sort=createdAt,desc"
```

**Expected:**
- Status: `200 OK`
- Body includes: `content`, `totalElements`, `totalPages`, `size`, `number`
- Projects sorted by creation date (newest first)

---

### 3.7 Update Project
**Purpose:** Modify existing project

```bash
# Use ID from test 3.1
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
- `shortDescription` updated
- `status` updated to `IN_PROGRESS`
- `published` updated to `true`

**Verify update persisted:**
```bash
curl http://localhost:8080/api/v1/projects/1
```

---

### 3.8 Delete Project (Should Fail - Published)
**Purpose:** Test business rule: cannot delete published projects

```bash
# Try to delete the published project from 3.7
curl -X DELETE http://localhost:8080/api/v1/projects/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `400 Bad Request`
- Error: `{"status": "error", "errorCode": "DELETE_PUBLISHED", "message": "Cannot delete published projects. Unpublish first."}`

---

### 3.9 Delete Project (Should Succeed)
**Purpose:** Successfully delete unpublished project

**First unpublish the project:**
```bash
curl -X PUT http://localhost:8080/api/v1/projects/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Project Alpha",
    "shortDescription": "Updated description with new info",
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
- Body: `{"status": "success", "message": "Project deleted successfully"}`

**Verify deletion:**
```bash
curl http://localhost:8080/api/v1/projects/1
```
- Status: `404 Not Found`

---

## 4. Project Image Endpoints

**Note:** POST, PUT, and DELETE endpoints require JWT authentication.

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

### 4.1 Upload Image
**Purpose:** Upload image with metadata

**Prepare a test image file (JPEG, PNG, GIF, or WebP, under 10MB)**

```bash
# Replace {projectId} with the ID from setup
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

**Save the image ID for subsequent tests!**

---

### 4.2 Upload Multiple Images
**Purpose:** Test multiple image uploads

```bash
# Upload 2nd image
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/another-image.png" \
  -F 'imageType=DIAGRAM' \
  -F 'altText=Architecture diagram' \
  -F 'displayOrder=1'

# Upload 3rd image
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/third-image.png" \
  -F 'imageType=LOGO' \
  -F 'altText=Project logo' \
  -F 'displayOrder=2'
```

**Expected:**
- Both succeed with `201 Created`
- First image remains `isPrimary: true`
- Other images have `isPrimary: false`

---

### 4.3 Get Project Images (Public)
**Purpose:** List all images for a project - no auth required

```bash
curl http://localhost:8080/api/v1/projects/{projectId}/images
```

**Expected:**
- Status: `200 OK`
- Array with 3 images
- Sorted by `displayOrder` ascending (0, 1, 2)
- First image has `isPrimary: true`

---

### 4.4 Get Single Image (Public)
**Purpose:** Retrieve specific image - no auth required

```bash
curl http://localhost:8080/api/v1/projects/{projectId}/images/{imageId}
```

**Expected:**
- Status: `200 OK`
- Image details match upload

---

### 4.5 Update Image Metadata
**Purpose:** Update metadata without re-uploading file

```bash
curl -X PUT http://localhost:8080/api/v1/projects/{projectId}/images/{imageId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "altText": "Updated alt text for accessibility",
    "caption": "New caption describing the image",
    "displayOrder": 10
  }'
```

**Expected:**
- Status: `200 OK`
- Metadata updated
- `url` and `cloudinaryPublicId` unchanged

---

### 4.6 Set Primary Image
**Purpose:** Change which image is primary

```bash
# Set the 2nd image as primary
curl -X PUT http://localhost:8080/api/v1/projects/{projectId}/images/{imageId2}/set-primary \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Selected image now has `isPrimary: true`

**Verify:**
```bash
curl http://localhost:8080/api/v1/projects/{projectId}/images
```
- Only one image has `isPrimary: true` (the 2nd one)
- First image now has `isPrimary: false`

---

### 4.7 Delete Image
**Purpose:** Remove image from project

```bash
curl -X DELETE http://localhost:8080/api/v1/projects/{projectId}/images/{imageId} \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:**
- Status: `200 OK`
- Message: "Image deleted successfully"

**Verify:**
```bash
curl http://localhost:8080/api/v1/projects/{projectId}/images
```
- Only 2 images remain
- Deleted image not in list

---

### 4.8 File Validation Tests

**Test Case A: File too large (>10MB)**
```bash
# Create or use a file > 10MB
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/large-file.jpg" \
  -F 'imageType=SCREENSHOT' \
  -F 'altText=Large file test'
```

**Expected:**
- Status: `400 Bad Request`
- Error: `{"errorCode": "FILE_TOO_LARGE"}`

**Test Case B: Invalid file type**
```bash
# Try uploading a text file
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/document.txt" \
  -F 'imageType=SCREENSHOT' \
  -F 'altText=Invalid file'
```

**Expected:**
- Status: `400 Bad Request`
- Error: `{"errorCode": "INVALID_FILE"}`

**Test Case C: Empty file**
```bash
# Create empty file: touch empty.jpg
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@empty.jpg" \
  -F 'imageType=SCREENSHOT'
```

**Expected:**
- Status: `400 Bad Request`
- Error about empty file

**Test Case D: Max images limit (20)**
```bash
# Upload 17 more images (you already have 3)
# On the 21st upload:
curl -X POST http://localhost:8080/api/v1/projects/{projectId}/images \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@test.jpg" \
  -F 'imageType=SCREENSHOT'
```

**Expected:**
- Status: `400 Bad Request`
- Error: `{"errorCode": "MAX_IMAGES"}`

---

### 4.9 Image Ownership Validation
**Purpose:** Ensure images can only be accessed via their parent project

**Setup:** Create a second project and note its ID

```bash
# Try to access image from project A using project B's ID
curl http://localhost:8080/api/v1/projects/{projectBId}/images/{imageFromProjectA}
```

**Expected:**
- Status: `400 Bad Request` or `404 Not Found`
- Cannot access image from wrong project

---

## 5. Technology Endpoints

**Note:** POST, PUT, and DELETE endpoints require JWT authentication.

### 5.1 Create Technology
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

### 5.2 Get All Technologies (Public)
```bash
curl http://localhost:8080/api/v1/technologies
```

### 5.3 Associate Technology with Project
```bash
# When creating project, include technologyIds
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Spring Boot API",
    "shortDescription": "RESTful API built with Spring Boot",
    "type": "PROFESSIONAL",
    "technologyIds": [1, 2, 3]
  }'
```

---

## 6. End-to-End Scenario

**Goal:** Create a complete project with images using authentication

1. **Login** and save the token
2. **Create project** (save ID)
3. **Upload 3 images** with authentication
4. **Set primary image**
5. **Get project by ID** (public) - verify images array populated
6. **Update project metadata** with authentication
7. **Delete one image** with authentication
8. **Get project again** - verify 2 images remain

---

## Checklist

Use this checklist to track your testing:

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

### Projects - Happy Path
- [ ] Create minimal project (with auth)
- [ ] Create full project (with auth)
- [ ] Get by ID (public)
- [ ] Get all projects (public)
- [ ] Get paginated projects (public)
- [ ] Update project (with auth)
- [ ] Delete unpublished project (with auth)

### Projects - Error Cases
- [ ] Validation errors (missing fields)
- [ ] Duplicate name rejected
- [ ] Invalid URL rejected
- [ ] Cannot delete published project
- [ ] 404 for non-existent project

### Images - Happy Path
- [ ] Upload image successfully (with auth)
- [ ] Upload multiple images (with auth)
- [ ] Get all images for project (public)
- [ ] Get single image (public)
- [ ] Update image metadata (with auth)
- [ ] Set primary image (with auth)
- [ ] Delete image (with auth)

### Images - Error Cases
- [ ] File too large rejected
- [ ] Invalid file type rejected
- [ ] Empty file rejected
- [ ] Max 20 images enforced
- [ ] Image ownership validated

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

---

## Notes

- **Default Admin Credentials:** username: `admin`, password: `admin123`
- **Token Expiration:** 24 hours - login again if expired
- **Postman Collection:** Already exists at `docs/api/postman/personal-website-api.postman_collection.json`
- **Test Images:** Use real image files (JPEG/PNG/GIF/WebP) under 10MB
- **Database State:** After each test run, database retains data - use DELETE endpoints or restart if needed
- **Logs:** Check console output for detailed error messages

---

## Next Steps After Manual Testing

1. Document any bugs found
2. Verify all Cloudinary URLs work in browser
3. Check database state with SQL queries
4. Test with real project data
5. Once APIs confirmed working → write unit tests based on actual behavior
