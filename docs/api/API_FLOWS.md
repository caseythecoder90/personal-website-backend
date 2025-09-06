# API Flow Diagrams & User Journeys

## Mermaid Diagrams for System Flows

Copy these diagrams into any Mermaid-compatible tool (GitHub, Notion, etc.) or use the Mermaid Live Editor.

### 1. Project Creation Flow

```mermaid
sequenceDiagram
    participant A as Admin
    participant C as Controller
    participant S as Service
    participant D as DAO
    participant R as Repository
    participant DB as Database
    participant IS as ImageService
    participant FS as FileStorage
    participant Cache as Redis

    A->>C: POST /api/v1/projects (with images)
    C->>C: Validate DTO
    C->>S: createProject(request)
    S->>S: Validate business rules
    S->>S: Check name uniqueness
    S->>D: save(project)
    D->>R: save(entity)
    R->>DB: INSERT project
    DB-->>R: Return saved entity
    R-->>D: Return project
    D-->>S: Return project
    S->>S: Link technologies
    
    alt If images provided
        S->>IS: processProjectImages(projectId, images)
        IS->>IS: Validate image files
        IS->>FS: Upload to storage
        FS-->>IS: Return image URLs
        IS->>D: saveProjectImages(images)
        D->>DB: INSERT project_images
    end
    
    S->>Cache: Invalidate project cache
    S-->>C: Return created project
    C-->>A: 201 Created + ProjectResponse
```

### 2. Portfolio Visitor Journey

```mermaid
flowchart TD
    A[Visitor arrives at homepage] --> B{Browse featured projects?}
    B -->|Yes| C[View featured projects list]
    B -->|No| D[Browse all projects]
    
    C --> E[Click on project]
    D --> E
    
    E --> F[View project details]
    F --> G{Interested in technology?}
    
    G -->|Yes| H[Click technology tag]
    G -->|No| I{Want to see code?}
    
    H --> J[Filter projects by technology]
    J --> K[Discover similar projects]
    
    I -->|Yes| L[Click GitHub link]
    I -->|No| M{Try live demo?}
    
    L --> N[Track GitHub click event]
    M -->|Yes| O[Click live demo]
    M -->|No| P{Contact developer?}
    
    O --> Q[Track demo click event]
    P -->|Yes| R[Go to contact form]
    P -->|No| S[Continue browsing]
    
    R --> T[Fill contact form]
    T --> U[Submit inquiry]
    U --> V[Thank you page]
    
    style A fill:#e1f5fe
    style V fill:#c8e6c9
    style N fill:#fff3e0
    style Q fill:#fff3e0
```

### 3. Contact Form Submission Flow

```mermaid
sequenceDiagram
    participant V as Visitor
    participant F as Frontend
    participant C as Controller
    participant S as Service
    participant D as DAO
    participant DB as Database
    participant E as Email Service
    participant A as Admin

    V->>F: Fill contact form
    F->>C: POST /api/v1/contact
    C->>C: Validate input
    C->>S: processContactSubmission()
    S->>S: Rate limit check
    S->>S: Spam detection
    S->>D: save(submission)
    D->>DB: INSERT contact_submission
    DB-->>D: Return saved submission
    D-->>S: Return submission
    
    par Email Notifications
        S->>E: Send confirmation to visitor
        and
        S->>E: Notify admin of new inquiry
    end
    
    E-->>V: Confirmation email sent
    E-->>A: New inquiry notification
    
    S-->>C: Return success response
    C-->>F: 201 Created
    F-->>V: Thank you message
    
    Note over A: Admin sees new submission in dashboard
```

### 4. Blog Post Publishing Flow

```mermaid
flowchart LR
    A[Admin creates draft] --> B[Add categories]
    B --> C[Add tags] 
    C --> D[Write content]
    D --> E{Preview satisfactory?}
    
    E -->|No| D
    E -->|Yes| F[Generate SEO metadata]
    
    F --> G[Set publish date]
    G --> H[Publish post]
    
    H --> I[Update blog indexes]
    I --> J[Invalidate cache]
    J --> K[Generate sitemap]
    K --> L[Notify search engines]
    
    L --> M[Post live on website]
    
    subgraph "Automatic Processes"
        I
        J 
        K
        L
    end
    
    style A fill:#e3f2fd
    style M fill:#c8e6c9
```

### 5. Analytics Data Collection Flow

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant A as Analytics API
    participant S as Service
    participant Q as Queue
    participant P as Processor
    participant DB as Database
    participant D as Dashboard

    U->>F: Page view/interaction
    F->>A: POST /api/v1/analytics/track
    A->>S: processEvent()
    S->>S: Validate & enrich data
    S->>Q: Queue analytics event
    Q-->>S: Event queued
    S-->>A: 202 Accepted
    A-->>F: Success response
    
    Note over Q,P: Async Processing
    
    Q->>P: Process queued events
    P->>P: Batch process events
    P->>DB: Bulk insert analytics
    DB-->>P: Insert complete
    
    Note over D: Real-time dashboard updates
    
    P->>D: Update dashboard metrics
    D->>D: Refresh charts & stats
```

### 6. Admin Dashboard Data Aggregation

```mermaid
flowchart TD
    A[Admin opens dashboard] --> B[Load dashboard metrics]
    
    B --> C{Data in cache?}
    C -->|Yes| D[Return cached metrics]
    C -->|No| E[Query analytics tables]
    
    E --> F[Project view counts]
    E --> G[Contact submission stats]
    E --> H[Technology popularity]
    E --> I[Geographic data]
    E --> J[Device breakdown]
    
    F --> K[Aggregate & format data]
    G --> K
    H --> K
    I --> K
    J --> K
    
    K --> L[Cache results]
    L --> M[Return to frontend]
    
    D --> M
    M --> N[Render dashboard charts]
    
    subgraph "Performance Optimization"
        C
        L
    end
    
    style A fill:#e1f5fe
    style N fill:#c8e6c9
```

### 7. Technology Proficiency Tracking

```mermaid
flowchart LR
    A[Admin updates technology] --> B{Change in proficiency?}
    B -->|No| C[Update basic info]
    B -->|Yes| D[Update proficiency level]
    
    C --> E[Save to database]
    D --> F[Update years experience]
    F --> G[Recalculate skill metrics]
    G --> H[Update featured status]
    H --> E
    
    E --> I[Update project associations]
    I --> J[Invalidate caches]
    J --> K[Regenerate skill charts]
    K --> L[Update portfolio display]
    
    subgraph "Skill Portfolio Impact"
        I
        J
        K
        L
    end
    
    style A fill:#e3f2fd
    style L fill:#c8e6c9
```

## User Journey Maps

### Journey 1: Technical Recruiter
```
🎯 Goal: Evaluate technical skills for Java position

👤 Persona: Senior Technical Recruiter
🕐 Duration: 15-20 minutes
📱 Device: Desktop (work computer)

Journey Steps:
1. **Discovery** (LinkedIn/Google) → Land on homepage
2. **Visual Overview** → Browse project thumbnails for quick assessment
3. **Skills Assessment** → Filter projects by "Java", "Spring Boot"
4. **Project Deep-dive** → Click project → View screenshots/demos
5. **Code Quality Review** → Click GitHub links, review commits
6. **Architecture Understanding** → View architecture diagrams
7. **Communication Skills** → Read blog posts about technical topics
8. **Contact Decision** → Submit hiring inquiry via contact form

Pain Points:
- Need quick skill verification
- Want to see actual project interfaces, not just descriptions
- Looking for recent activity and continuous learning
- Need to understand project complexity quickly

Solutions:
- Featured technologies on homepage
- High-quality project thumbnails in grid view
- Technology filtering on projects
- Screenshot galleries showing UI/UX work
- Architecture diagrams for technical depth
- Direct GitHub links with contribution stats
- Recent blog posts showing current knowledge
```

### Journey 2: Potential Client
```
🎯 Goal: Hire for freelance project development

👤 Persona: Startup Founder
🕐 Duration: 10-15 minutes  
📱 Device: Mobile (evening browsing)

Journey Steps:
1. **Referral** → From networking contact
2. **Portfolio Review** → Browse recent projects
3. **Capability Check** → Look for similar project types
4. **Budget Estimation** → Check project complexity and timelines
5. **Initial Contact** → Submit freelance inquiry

Pain Points:
- Mobile-first browsing experience
- Need to understand project scope and timelines
- Want to see client work examples

Solutions:
- Mobile-responsive design
- Project difficulty and timeline indicators
- Professional vs personal project categorization
- Clear contact form with project type selection
```

### Journey 3: Peer Developer
```
🎯 Goal: Learn about interesting technical approaches

👤 Persona: Mid-level Developer
🕐 Duration: 25-30 minutes
📱 Device: Desktop (personal learning time)

Journey Steps:
1. **Content Discovery** → Find blog post via search/social
2. **Technical Deep-dive** → Read implementation details
3. **Related Content** → Browse similar projects and posts
4. **Code Exploration** → Visit GitHub repos for examples
5. **Knowledge Sharing** → Share interesting findings
6. **Future Reference** → Bookmark for later reference

Pain Points:
- Want detailed technical explanations
- Need working code examples
- Looking for learning resources

Solutions:
- Detailed blog posts with code examples
- Learning outcomes documented per project
- Architecture diagrams and explanations
- Related content recommendations
```

### 8. Project Image Upload Flow

```mermaid
sequenceDiagram
    participant A as Admin
    participant UI as Frontend
    participant C as Controller
    participant S as Service
    participant IS as ImageService
    participant FS as FileStorage
    participant DB as Database

    A->>UI: Select images to upload
    UI->>UI: Validate files (size, format)
    UI->>C: POST /api/v1/projects/{id}/images
    C->>C: Validate multipart files
    C->>S: uploadProjectImages(projectId, files)
    
    loop For each image
        S->>IS: processImage(file)
        IS->>IS: Validate image (format, size, dimensions)
        IS->>IS: Generate thumbnail if needed
        IS->>FS: Upload original + thumbnail
        FS-->>IS: Return URLs
        IS->>IS: Create ProjectImage entity
    end
    
    S->>DB: Save ProjectImage entities
    DB-->>S: Return saved images
    S->>S: Update display order
    S->>S: Set primary image if first upload
    S-->>C: Return image metadata
    C-->>UI: 201 Created + ImageResponse[]
    UI->>UI: Update project gallery view
```

### 9. Primary Image Management Flow

```mermaid
sequenceDiagram
    participant A as Admin
    participant C as Controller
    participant S as Service
    participant D as DAO
    participant DB as Database
    participant Cache as Redis

    A->>C: PUT /api/v1/projects/{id}/images/{imageId}/primary
    C->>S: setPrimaryImage(projectId, imageId)
    S->>D: findProjectImage(imageId)
    D->>DB: SELECT from project_images
    DB-->>D: Return image entity
    D-->>S: Return ProjectImage
    
    S->>S: Validate image belongs to project
    S->>D: updatePrimaryStatus(projectId, imageId)
    D->>DB: UPDATE project_images SET is_primary = false WHERE project_id = ?
    D->>DB: UPDATE project_images SET is_primary = true WHERE id = ?
    DB-->>D: Update complete
    
    S->>Cache: Invalidate project cache
    S-->>C: Success response
    C-->>A: 200 OK
```

## API Integration Patterns

### External Service Integration Flow
```mermaid
sequenceDiagram
    participant A as Application
    participant G as GitHub API
    participant GA as Google Analytics
    participant S as Search Console
    participant E as Email Service

    Note over A: Daily batch job runs

    A->>G: Fetch repository stats
    G-->>A: Return commit count, stars, etc.
    
    A->>A: Update project metrics
    
    A->>GA: Send usage events
    GA-->>A: Confirm receipt
    
    A->>S: Submit updated sitemap
    S-->>A: Acknowledge sitemap
    
    Note over A: Weekly reporting job
    
    A->>E: Send analytics summary
    E-->>A: Email sent confirmation
```

### 12. Blog Post Creation Flow (Admin UI)

```mermaid
sequenceDiagram
    participant A as Admin
    participant UI as Admin UI
    participant C as BlogController
    participant S as BlogService
    participant D as BlogDAO
    participant DB as Database
    participant Cache as Redis
    participant SEO as SEOService

    A->>UI: Navigate to "Create Blog Post"
    UI->>UI: Load blog creation form
    UI->>C: GET /api/v1/admin/blog/categories
    C-->>UI: Return categories list
    UI->>C: GET /api/v1/admin/blog/tags
    C-->>UI: Return tags list
    
    A->>UI: Fill blog post content
    A->>UI: Select categories and tags
    A->>UI: Click "Save Draft"
    
    UI->>C: POST /api/v1/admin/blog/posts
    C->>S: createBlogPost(request)
    S->>S: Validate content and metadata
    S->>S: Generate slug from title
    S->>D: saveBlogPost(blogPost)
    D->>DB: INSERT blog_post
    
    S->>S: Link categories and tags
    D->>DB: INSERT blog_post_categories
    D->>DB: INSERT blog_post_tags
    
    S->>SEO: generateSEOMetadata(blogPost)
    SEO->>DB: INSERT seo_meta
    
    S-->>C: Return created blog post
    C-->>UI: 201 Created + BlogPostResponse
    UI->>UI: Show success message
    
    alt If "Publish" instead of "Save Draft"
        S->>S: Set published = true
        S->>Cache: Invalidate blog cache
        S->>SEO: Generate sitemap entry
    end
```

### 13. Blog Post Management Flow (Admin UI)

```mermaid
flowchart TD
    A[Admin Dashboard] --> B[Navigate to Blog Section]
    B --> C[Blog Post List View]
    
    C --> D{Action Selection}
    D -->|Create New| E[Create Blog Post Form]
    D -->|Edit| F[Edit Existing Post]
    D -->|Publish| G[Publish Draft Post]
    D -->|Archive| H[Archive Published Post]
    
    E --> I[Fill Content & Metadata]
    I --> J[Select Categories & Tags]
    J --> K{Save Action}
    
    F --> L[Load Existing Content]
    L --> I
    
    K -->|Save Draft| M[Save as Unpublished]
    K -->|Publish| N[Publish Immediately]
    
    G --> O[Confirm Publish Action]
    O --> P[Update Status to Published]
    
    H --> Q[Confirm Archive Action]
    Q --> R[Update Status to Archived]
    
    M --> S[Success Message]
    N --> T[Published + SEO Update]
    P --> T
    R --> U[Archived Successfully]
    
    S --> C
    T --> C
    U --> C
    
    style A fill:#e3f2fd
    style E fill:#e8f5e8
    style T fill:#c8e6c9
    style U fill:#ffecb3
```

### 14. Technology Management Flow (Admin UI)

```mermaid
sequenceDiagram
    participant A as Admin
    participant UI as Admin UI
    participant C as TechnologyController
    participant S as TechnologyService
    participant D as TechnologyDAO
    participant DB as Database
    participant Cache as Redis

    A->>UI: Navigate to "Technology Management"
    UI->>C: GET /api/v1/admin/technologies
    C->>S: getAllTechnologies()
    S->>D: findAll()
    D->>DB: SELECT * FROM technologies
    DB-->>D: Return technology list
    D-->>S: Return technologies
    S-->>C: Return technology list
    C-->>UI: 200 OK + TechnologyResponse[]
    
    A->>UI: Click "Add New Technology"
    UI->>UI: Show technology creation form
    
    A->>UI: Fill technology details
    A->>UI: Set proficiency level
    A->>UI: Set years of experience
    A->>UI: Click "Save"
    
    UI->>C: POST /api/v1/admin/technologies
    C->>S: createTechnology(request)
    S->>S: Validate technology data
    S->>S: Check name uniqueness
    S->>D: saveTechnology(technology)
    D->>DB: INSERT technology
    DB-->>D: Return saved technology
    D-->>S: Return technology
    
    S->>Cache: Invalidate technology cache
    S-->>C: Return created technology
    C-->>UI: 201 Created + TechnologyResponse
    UI->>UI: Update technology list
    
    Note over A: Technology appears in list with proficiency indicators
```

### 15. Contact Submission Admin Management Flow

```mermaid
sequenceDiagram
    participant A as Admin
    participant UI as Admin Dashboard
    participant C as ContactController
    participant S as ContactService
    participant E as EmailService
    participant D as ContactDAO
    participant DB as Database

    A->>UI: Navigate to "Contact Submissions"
    UI->>C: GET /api/v1/admin/contacts?status=NEW
    C->>S: getContactSubmissions(status)
    S->>D: findByStatus(NEW)
    D->>DB: SELECT from contact_submissions
    DB-->>D: Return new submissions
    D-->>S: Return submissions
    S-->>C: Return contact list
    C-->>UI: 200 OK + ContactSubmission[]
    
    UI->>UI: Display submissions with indicators
    
    A->>UI: Click on submission to view details
    UI->>C: GET /api/v1/admin/contacts/{id}
    C->>S: getContactSubmission(id)
    S->>D: markAsRead(id)  # Update status to READ
    D->>DB: UPDATE contact_submissions SET status = 'READ'
    S->>D: findById(id)
    D-->>S: Return full submission details
    S-->>C: Return submission
    C-->>UI: 200 OK + ContactSubmissionDetail
    
    UI->>UI: Show full submission details
    
    A->>UI: Click "Reply" button
    UI->>UI: Open reply form with pre-filled data
    A->>UI: Write response message
    A->>UI: Click "Send Reply"
    
    UI->>C: POST /api/v1/admin/contacts/{id}/reply
    C->>S: replyToSubmission(id, message)
    S->>E: sendReplyEmail(submission, message)
    E-->>S: Email sent confirmation
    S->>D: updateStatus(id, REPLIED)
    D->>DB: UPDATE contact_submissions SET status = 'replied'
    S-->>C: Reply sent successfully
    C-->>UI: 200 OK
    
    UI->>UI: Update submission status to "Replied"
    UI->>UI: Show success notification
```

This comprehensive documentation provides the foundation for understanding system behavior, data flows, and user interactions. Use these diagrams to guide implementation decisions and communicate system design to stakeholders.