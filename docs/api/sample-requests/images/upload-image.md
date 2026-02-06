# Upload Project Image

Upload a new image to a project with metadata.

## Endpoint
```
POST /api/v1/projects/{projectId}/images
```

## Request

### Headers
```
Content-Type: multipart/form-data
```

### Parameters
- `projectId` (path): The ID of the project

### Form Data
- `file` (required): The image file to upload
  - Allowed types: image/jpeg, image/png, image/gif, image/webp
  - Max size: 10MB
- `imageType` (required): Type of image (SCREENSHOT, DIAGRAM, LOGO)
- `altText` (optional): Alt text for accessibility
- `caption` (optional): Image caption
- `displayOrder` (optional): Display order (default: 0)
- `isPrimary` (optional): Set as primary image (default: false)

## Example: cURL

```bash
curl -X POST http://localhost:8080/api/v1/projects/1/images \
  -F "file=@/path/to/screenshot.png" \
  -F "imageType=SCREENSHOT" \
  -F "altText=Homepage screenshot showing main features" \
  -F "caption=Main landing page" \
  -F "displayOrder=0" \
  -F "isPrimary=true"
```

## Example: cURL with JSON metadata

```bash
curl -X POST http://localhost:8080/api/v1/projects/1/images \
  -F "file=@screenshot.png" \
  -F 'imageType=SCREENSHOT' \
  -F 'altText=Homepage screenshot' \
  -F 'isPrimary=true'
```

## Example: Postman

1. Set method to `POST`
2. Set URL to `http://localhost:8080/api/v1/projects/1/images`
3. Go to Body tab → form-data
4. Add key `file`, change type to `File`, select your image
5. Add other keys (text type):
   - `imageType`: `SCREENSHOT`
   - `altText`: `Homepage screenshot`
   - `isPrimary`: `true`

## Response (201 Created)

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "projectId": 1,
    "projectName": "E-Commerce Platform",
    "url": "https://res.cloudinary.com/dwdaehpml/image/upload/v1234567890/portfolio-images/e-commerce-platform/screenshot.png",
    "cloudinaryPublicId": "portfolio-images/e-commerce-platform/screenshot",
    "altText": "Homepage screenshot showing main features",
    "caption": "Main landing page",
    "imageType": "SCREENSHOT",
    "displayOrder": 0,
    "isPrimary": true,
    "createdAt": "2024-01-15T10:30:00"
  },
  "message": "Image uploaded successfully",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Error Responses

### 400 Bad Request - File too large
```json
{
  "status": "error",
  "errorCode": "FILE_TOO_LARGE",
  "message": "File size (11534336 bytes) exceeds maximum allowed (10485760 bytes)",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 400 Bad Request - Invalid file type
```json
{
  "status": "error",
  "errorCode": "INVALID_FILE",
  "message": "Invalid file type: application/pdf. Allowed types: [image/jpeg, image/png, image/gif, image/webp]",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 400 Bad Request - Max images exceeded
```json
{
  "status": "error",
  "errorCode": "MAX_IMAGES",
  "message": "Project already has maximum allowed images (20)",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 404 Not Found - Project not found
```json
{
  "status": "error",
  "errorCode": "NOT_FOUND",
  "message": "Project not found with id: 999",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Notes

- Images are stored in Cloudinary CDN under `portfolio-images/{project-slug}/`
- If `isPrimary` is true, any existing primary image will be unset
- Maximum 20 images per project
- Supported formats: JPEG, PNG, GIF, WebP
- Maximum file size: 10MB
