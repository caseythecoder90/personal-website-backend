# JWT Authentication Guide

## Overview

Your API now has JWT (JSON Web Token) authentication configured:
- ✅ **Public endpoints** - No authentication required (GET operations)
- ✅ **Admin endpoints** - JWT token required (POST, PUT, DELETE)
- ✅ **Stateless** - No session storage
- ✅ **CORS enabled** - React/Angular frontend ready

---

## Quick Start

### 1. Start the Application

```bash
mvn spring-boot:run
```

The V3 migration will automatically create the `users` table and insert a default admin user.

---

## Default Admin Credentials

**Username:** `admin`
**Password:** `admin123`
**Email:** `admin@caseyquinn.com`
**Role:** `ADMIN`

⚠️ **IMPORTANT:** Change this password after first login!

---

## Authentication Flow

### Step 1: Login

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "status": "success",
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY0...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "username": "admin",
    "role": "ADMIN"
  },
  "timestamp": "2026-02-06T17:45:00"
}
```

**Copy the token!** It expires in 24 hours.

---

### Step 2: Use Token for Protected Endpoints

**With Token (Admin Operations):**
```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "name": "New Project",
    "shortDescription": "Created with JWT auth",
    "type": "PERSONAL"
  }'
```

**Without Token (Public Read):**
```bash
# This still works - no token needed
curl http://localhost:8080/api/v1/projects
```

---

## Endpoint Security Rules

### ✅ Public Endpoints (No Auth Required)

**Authentication:**
- `POST /api/v1/auth/login`

**Health & Operations:**
- `GET /api/v1/health`
- `POST /api/v1/operations/encrypt`
- `POST /api/v1/operations/decrypt`

**Public Read (GET only):**
- `GET /api/v1/projects`
- `GET /api/v1/projects/{id}`
- `GET /api/v1/projects/{id}/images`
- `GET /api/v1/technologies/**`
- `GET /api/v1/blog/**`

**Documentation:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

---

### 🔒 Protected Endpoints (JWT Required)

**Projects:**
- `POST /api/v1/projects` - Create project (ADMIN)
- `PUT /api/v1/projects/{id}` - Update project (ADMIN)
- `DELETE /api/v1/projects/{id}` - Delete project (ADMIN)

**Project Images:**
- `POST /api/v1/projects/{id}/images` - Upload image (ADMIN)
- `PUT /api/v1/projects/{id}/images/{imageId}` - Update image (ADMIN)
- `DELETE /api/v1/projects/{id}/images/{imageId}` - Delete image (ADMIN)

**Technologies:**
- `POST /api/v1/technologies` - Create technology (ADMIN)
- `PUT /api/v1/technologies/{id}` - Update technology (ADMIN)
- `DELETE /api/v1/technologies/{id}` - Delete technology (ADMIN)

---

## Postman Setup

### Option 1: Manual Header
1. Login and copy token
2. Add header to each request:
   - **Key:** `Authorization`
   - **Value:** `Bearer YOUR_TOKEN_HERE`

### Option 2: Postman Variable (Recommended)
1. **Login Request** → Add to "Tests" tab:
```javascript
var jsonData = pm.response.json();
pm.environment.set("jwt_token", jsonData.data.token);
```

2. **Protected Requests** → Add to "Authorization" tab:
   - **Type:** `Bearer Token`
   - **Token:** `{{jwt_token}}`

Now the token auto-updates when you login!

---

## React/Angular Integration

### Example: React with Axios

**Login:**
```javascript
// Login and store token
const login = async (username, password) => {
  const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
    username,
    password
  });

  const token = response.data.data.token;
  localStorage.setItem('jwt_token', token);
  return token;
};
```

**API Calls with Token:**
```javascript
// Create Axios instance with interceptor
const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1'
});

api.interceptors.request.use(config => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Use it
const createProject = async (projectData) => {
  const response = await api.post('/projects', projectData);
  return response.data;
};
```

**Logout:**
```javascript
const logout = () => {
  localStorage.removeItem('jwt_token');
};
```

---

## Security Configuration

### CORS Settings
Currently allows:
- `http://localhost:3000` (React dev)
- `http://localhost:4200` (Angular dev)
- `https://caseyquinn.com` (Production)

**To add more origins:** Edit `SecurityConfig.java` → `corsConfigurationSource()`

### JWT Secret
**Default:** `your-256-bit-secret-key-change-this-in-production-minimum-32-characters`

**For Production:** Set environment variable:
```bash
export JWT_SECRET="your-secure-random-256-bit-secret-key-here"
```

Generate secure secret:
```bash
openssl rand -base64 32
```

### Token Expiration
- **Access Token:** 24 hours (86400000 ms)
- **Configurable in:** `application.yml` → `jwt.expiration`

---

## Error Responses

### Invalid Credentials
```json
{
  "status": "error",
  "message": "Bad credentials",
  "timestamp": "2026-02-06T17:45:00"
}
```
**Status Code:** `401 Unauthorized`

### Missing/Invalid Token
```json
{
  "status": "error",
  "message": "Full authentication is required to access this resource",
  "timestamp": "2026-02-06T17:45:00"
}
```
**Status Code:** `403 Forbidden`

### Expired Token
```json
{
  "status": "error",
  "message": "JWT token has expired",
  "timestamp": "2026-02-06T17:45:00"
}
```
**Status Code:** `401 Unauthorized`
**Action:** Login again to get new token

---

## Testing Checklist

### Basic Auth Flow
- [ ] Login with valid credentials → Receive token
- [ ] Login with invalid credentials → 401 error
- [ ] Access public endpoint without token → Success
- [ ] Access admin endpoint without token → 403 error
- [ ] Access admin endpoint with token → Success

### Token Validation
- [ ] Use valid token → Success
- [ ] Use invalid token → 403 error
- [ ] Use expired token → 401 error
- [ ] Use token after 24 hours → 401 error

### CORS
- [ ] Request from React dev server → Success
- [ ] Preflight OPTIONS request → Success

---

## Troubleshooting

### Problem: "Access Denied" on Login
- **Check:** Username and password are correct
- **Check:** User exists in database: `SELECT * FROM users WHERE username='admin';`

### Problem: Token Not Working
- **Check:** Header format: `Authorization: Bearer <token>` (note the space!)
- **Check:** Token hasn't expired (24 hours)
- **Check:** No extra spaces or newlines in token

### Problem: CORS Error in Browser
- **Check:** Frontend origin is in `corsConfigurationSource()` allowedOrigins list
- **Check:** Request includes `Authorization` header in allowed headers

### Problem: All Endpoints Return 403
- **Check:** Spring Security configuration loaded correctly
- **Check:** JWT secret is set properly
- **Check:** Application logs for errors

---

## Production Checklist

Before deploying to production:

- [ ] Change default admin password
- [ ] Set strong JWT_SECRET environment variable
- [ ] Update CORS allowed origins (remove localhost)
- [ ] Consider shorter token expiration
- [ ] Add refresh token endpoint (optional)
- [ ] Enable HTTPS only
- [ ] Add rate limiting on login endpoint
- [ ] Set up password reset flow
- [ ] Add audit logging for admin operations

---

## Next Steps

1. **Test login** with Postman
2. **Verify** public endpoints work without token
3. **Verify** admin endpoints require token
4. **Update** Postman collection with auth variables
5. **Build** React/Angular login page
6. **Add** more users if needed

---

## Additional Features (Optional)

Want to add:
- **Refresh Tokens** - Get new token without re-login
- **Password Reset** - Email-based password recovery
- **Register Endpoint** - Allow new user signups
- **User Profile** - View/update user details
- **Role-based Access** - Add more roles (EDITOR, VIEWER, etc.)

Let me know if you want any of these implemented!
