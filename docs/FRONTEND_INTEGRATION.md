# Frontend Integration Guide

This document is the single entry point for frontend Claude Code agents integrating with the personal-website backend. Read this end-to-end; it links out to deeper references.

> **Path on disk:** `C:\Users\casey\Projects\personal-website-backend\docs\FRONTEND_INTEGRATION.md`

---

## 1. Base URLs & Environments

| Environment | Base URL | Notes |
|---|---|---|
| Local dev | `http://localhost:8080` | Spring Boot default port |
| Production | `https://api.caseyrquinn.com` | Behind nginx + certbot SSL on VPS |

All API routes are prefixed with `/api/v1`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`

## 2. CORS — Allowed Origins

Configured in `SecurityConfig.corsConfigurationSource()`:

- `http://localhost:*` (any port — Vite 5173, preview 4173, Lighthouse, etc.)
- `http://127.0.0.1:*`
- `https://caseyrquinn.com`
- `https://www.caseyrquinn.com`

Methods: `GET, POST, PUT, DELETE, OPTIONS`. Credentials allowed. Headers: `*`. Max age 3600s.

If your dev origin isn't covered, edit `SecurityConfig.java` and restart the backend.

## 3. Standard Response Envelope

Every endpoint returns a `Response<T>`:

**Success**
```json
{
  "status": "success",
  "message": "Operation completed successfully",
  "data": { /* T */ },
  "timestamp": "2026-04-30T12:34:56.789"
}
```

**Error**
```json
{
  "status": "error",
  "errorCode": "NOT_FOUND",
  "message": "Project with id 42 not found",
  "timestamp": "2026-04-30T12:34:56.789"
}
```

Common `errorCode` values: `VALIDATION_FAILED`, `NOT_FOUND`, `DUPLICATE_RESOURCE`, `FORBIDDEN`, `UNAUTHORIZED`, `MAX_IMAGES_EXCEEDED`, `INTERNAL_ERROR`. HTTP status mirrors the category (400 / 401 / 403 / 404 / 409 / 500).

## 4. Authentication (JWT)

- All `GET` endpoints are **public** (portfolio is publicly viewable).
- All `POST` / `PUT` / `DELETE` endpoints require an **ADMIN** JWT, except:
  - `POST /api/v1/contact` — public submission
  - `PUT /api/v1/projects/{id}/views` — public view-count increment

**Login**
```
POST /api/v1/auth/login
Content-Type: application/json

{ "username": "admin", "password": "..." }
```
Returns `{ data: { token, expiresIn, ... } }`. Use as:
```
Authorization: Bearer <token>
```
Token TTL: 24h. Refresh TTL: 7 days. See `docs/authentication/JWT_AUTHENTICATION_GUIDE.md`.

For the public-facing frontend you typically only need unauthenticated GETs and the contact POST. Admin write operations are for the CMS-style admin UI.

## 5. Endpoint Catalog

The full route table (paths, methods, auth requirement) lives in the project README under `CLAUDE.md` → "API Endpoints", reproduced here at:

- `C:\Users\casey\Projects\personal-website-backend\CLAUDE.md` (search "API Endpoints")

High-level resource map:

| Resource | Public reads | Admin writes |
|---|---|---|
| `/auth/login` | POST (public) | — |
| `/projects` | list, paginated, by id, by slug, by technology, featured | POST/PUT/DELETE |
| `/projects/{id}/views` | PUT (public increment) | — |
| `/projects/{id}/links` | list, by id | POST/PUT/DELETE |
| `/projects/{id}/images` | list, by id | POST (multipart), PUT, DELETE, set-primary |
| `/technologies` | list, paginated, by id, name, category, proficiency, featured, most-used | POST/PUT/DELETE |
| `/certifications` | list, by id, slug, status, organization, published, featured | POST/PUT/DELETE + link/unlink technology |
| `/blog/posts` | list, published, by id, slug, category, tag, search | POST/PUT/DELETE + publish/unpublish + link categories/tags |
| `/blog/categories` | list, by id, slug | POST/PUT/DELETE |
| `/blog/tags` | list, popular, by id, slug | POST/PUT/DELETE |
| `/blog/posts/{id}/images` | list, by id | POST (multipart), PUT, DELETE, primary |
| `/contact` | POST (public submit) | GET list, by id, by status, by inquiry-type, PUT status, DELETE |
| `/resume` | GET metadata, GET `/download` (302 → Cloudinary) | POST (multipart PDF), DELETE |
| `/operations` | health | encrypt, decrypt, hash-password |

**Definitive sources** for request/response shapes:
- Live OpenAPI: `http://localhost:8080/v3/api-docs` (most authoritative — generated from code)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- DTOs in `src/main/java/com/caseyquinn/personal_website/dto/`
- Postman collection: `docs/api/postman/personal-website-api.postman_collection.json`
- Sample requests: `docs/api/sample-requests/{projects,technologies,images,operations}/`

## 6. Pagination, Sorting, Filtering

Paginated endpoints (`/paginated`) accept Spring's standard query params:
- `page` (0-indexed), `size` (default 10), `sort` (e.g. `createdAt,desc`)

Response is a Spring `Page<T>` shape inside `data`: `{ content: [...], totalElements, totalPages, number, size, ... }`.

Default sort for projects: `createdAt DESC` (newest first).

## 7. File Uploads

**Project images / blog post images**
```
POST /api/v1/projects/{id}/images
POST /api/v1/blog/posts/{postId}/images
Content-Type: multipart/form-data
Authorization: Bearer <admin token>

Form fields:
  file:        (image file)
  imageType:   THUMBNAIL | GALLERY | HERO | ...
  altText:     string
  caption:     string (optional)
  displayOrder: number (optional)
  isPrimary:   boolean (optional)
```

**Resume**
```
POST /api/v1/resume
Content-Type: multipart/form-data
Authorization: Bearer <admin token>

Form fields:
  file:        (PDF, max 5MB)
```
Resume download is a 302 redirect to Cloudinary — use `<a href="/api/v1/resume/download" download>` or `window.location.href = ...`. Don't `fetch().json()` it.

Limits: max 10 projects, max 20 images per blog post, single active resume.

## 8. Business Rules the Frontend Should Mirror

- Project / certification / blog post **slugs auto-generate** from name on creation — don't try to set them directly unless updating.
- Published projects/certifications **cannot be deleted** — unpublish first.
- Blog posts start **unpublished**; publish/unpublish are explicit endpoints.
- Contact submission statuses: `NEW → READ → REPLIED → ARCHIVED`.
- Contact form is public; the backend sends a confirmation email to the submitter and a notification email to the owner asynchronously via Resend.

## 9. Enums (use these literal string values)

- `ProjectType`, `ProjectStatus`, `DifficultyLevel`
- `TechnologyCategory`, `ProficiencyLevel`
- `ImageType`, `BlogImageType`
- `LinkType` (GITHUB, LIVE, DEMO, DOCS, ...)
- `CertificationStatus` (EARNED, IN_PROGRESS, EXPIRED)
- `InquiryType`, `SubmissionStatus`
- `UserRole`

For exact members, query OpenAPI or read the enum source under `src/main/java/com/caseyquinn/personal_website/entity/enums/`.

## 10. Deployment Notes for the Frontend

When deploying to production, the frontend should:
- Use `VITE_API_BASE_URL=https://api.caseyrquinn.com/api/v1` (or wherever the deployed backend lives).
- Use `withCredentials: true` only if you need cookies — JWT via `Authorization` header does not require it. (CORS allows credentials either way.)
- Treat `/api/v1/resume/download` as a redirect, not JSON.
- Never log the JWT; store it in memory or sessionStorage for admin flows.

Backend deployment reference docs (read only if you're touching infra):
- `docs/deployment/VPS_DEPLOYMENT.md`
- `docs/deployment/NGINX_REVERSE_PROXY.md`
- `docs/deployment/CERTBOT_SSL.md`
- `docs/deployment/DOCKER.md`
- `docs/deployment/GITHUB_ACTIONS_CICD.md`

## 11. Quick Smoke Test from the Frontend

```bash
# health
curl http://localhost:8080/actuator/health

# list published projects
curl http://localhost:8080/api/v1/projects

# get current resume metadata
curl http://localhost:8080/api/v1/resume
```

If CORS fails: hard-refresh (Ctrl+Shift+R), check the Network tab for `Access-Control-Allow-Origin`, and confirm your origin matches a pattern in section 2.

## 12. Where to Look Next

| If you need… | Read |
|---|---|
| Exact request/response schemas | OpenAPI at `/v3/api-docs` (live) or Swagger UI |
| Auth flow details | `docs/authentication/JWT_AUTHENTICATION_GUIDE.md` |
| Sequence diagrams | `docs/api/API_FLOWS.md`, `docs/architecture/DIAGRAMS.md` |
| System design context | `docs/architecture/SYSTEM_DESIGN.md` |
| Database schema | `docs/architecture/database-schema.dbml` |
| Sample requests | `docs/api/sample-requests/` |
| Postman collection | `docs/api/postman/personal-website-api.postman_collection.json` |
| Full endpoint table | `CLAUDE.md` (project root) |
