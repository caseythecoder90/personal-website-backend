# API Flow Diagrams & User Journeys

## Mermaid Diagrams for System Flows

Copy these diagrams into any Mermaid-compatible tool (GitHub, Notion, etc.) or use the Mermaid Live Editor.

### 1. Project Creation Flow

```mermaid
sequenceDiagram
    participant C as API Client
    participant Ctrl as Controller
    participant S as Service
    participant D as DAO
    participant R as Repository
    participant DB as Database
    participant Cache as Redis

    C->>Ctrl: POST /api/v1/projects (project data only)
    Ctrl->>Ctrl: Validate DTO
    Ctrl->>S: createProject(request)
    S->>S: Validate business rules
    S->>S: Check name uniqueness
    S->>D: save(project)
    D->>R: save(entity)
    R->>DB: INSERT project
    DB-->>R: Return saved entity
    R-->>D: Return project
    D-->>S: Return project
    S->>S: Link technologies
    S->>Cache: Invalidate project cache
    S-->>Ctrl: Return created project
    Ctrl-->>C: 201 Created + ProjectResponse

    Note over C,Ctrl: Images uploaded separately via POST /api/v1/projects/{id}/images
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

    L --> N[Visit GitHub repo]
    M -->|Yes| O[Click live demo]
    M -->|No| P{Contact developer?}

    O --> Q[View live application]
    P -->|Yes| R[Go to contact form]
    P -->|No| S[Continue browsing]

    R --> T[Fill contact form]
    T --> U[Submit inquiry]
    U --> V[Thank you page]

    style A fill:#e1f5fe
    style V fill:#c8e6c9
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
        S->>E: Notify owner of new inquiry
    end

    E-->>V: Confirmation email sent

    S-->>C: Return success response
    C-->>F: 201 Created
    F-->>V: Thank you message
```

### 4. Blog Post Publishing Flow

```mermaid
flowchart LR
    A[API Client creates draft] --> B[Add categories]
    B --> C[Add tags]
    C --> D[Write content]
    D --> E{Preview satisfactory?}

    E -->|No| D
    E -->|Yes| F[Set publish date]

    F --> G[Publish post via API]

    G --> H[Update blog indexes]
    H --> I[Invalidate cache]

    I --> J[Post live on website]

    subgraph "Automatic Processes"
        H
        I
    end

    style A fill:#e3f2fd
    style J fill:#c8e6c9
```

### 5. Technology Proficiency Tracking

```mermaid
flowchart LR
    A[API Client updates technology] --> B{Change in proficiency?}
    B -->|No| C[Update basic info]
    B -->|Yes| D[Update proficiency level]

    C --> E[Save to database]
    D --> F[Update years experience]
    F --> G[Recalculate skill metrics]
    G --> H[Update featured status]
    H --> E

    E --> I[Update project associations]
    I --> J[Invalidate caches]
    J --> K[Update portfolio display]

    subgraph "Skill Portfolio Impact"
        I
        J
        K
    end

    style A fill:#e3f2fd
    style K fill:#c8e6c9
```

## User Journey Maps

### Journey 1: Technical Recruiter
```
Goal: Evaluate technical skills for Java position

Persona: Senior Technical Recruiter
Duration: 15-20 minutes
Device: Desktop (work computer)

Journey Steps:
1. Discovery (LinkedIn/Google) → Land on homepage
2. Visual Overview → Browse project thumbnails for quick assessment
3. Skills Assessment → Filter projects by "Java", "Spring Boot"
4. Project Deep-dive → Click project → View screenshots/demos
5. Code Quality Review → Click GitHub links, review commits
6. Architecture Understanding → View architecture diagrams
7. Communication Skills → Read blog posts about technical topics
8. Contact Decision → Submit hiring inquiry via contact form

Pain Points:
- Need quick skill verification
- Want to see actual project interfaces, not just descriptions
- Looking for recent activity and continuous learning

Solutions:
- Featured technologies on homepage
- High-quality project thumbnails in grid view
- Technology filtering on projects
- Screenshot galleries showing UI/UX work
- Architecture diagrams for technical depth
- Direct GitHub links
- Recent blog posts showing current knowledge
```

### Journey 2: Potential Client
```
Goal: Hire for freelance project development

Persona: Startup Founder
Duration: 10-15 minutes
Device: Mobile (evening browsing)

Journey Steps:
1. Referral → From networking contact
2. Portfolio Review → Browse recent projects
3. Capability Check → Look for similar project types
4. Budget Estimation → Check project complexity and timelines
5. Initial Contact → Submit freelance inquiry

Pain Points:
- Mobile-first browsing experience
- Need to understand project scope and timelines
- Want to see client work examples

Solutions:
- Mobile-responsive design
- Project difficulty and timeline indicators
- Professional vs personal project categorization
- Clear contact form with inquiry type selection
```

### Journey 3: Peer Developer
```
Goal: Learn about interesting technical approaches

Persona: Mid-level Developer
Duration: 25-30 minutes
Device: Desktop (personal learning time)

Journey Steps:
1. Content Discovery → Find blog post via search/social
2. Technical Deep-dive → Read implementation details
3. Related Content → Browse similar projects and posts
4. Code Exploration → Visit GitHub repos for examples
5. Knowledge Sharing → Share interesting findings

Pain Points:
- Want detailed technical explanations
- Need working code examples
- Looking for learning resources

Solutions:
- Detailed blog posts with code examples
- Architecture diagrams and explanations
- Related content recommendations via shared tags
```

### 6. Project Image Upload Flow (Separate Endpoint)

```mermaid
sequenceDiagram
    participant C as API Client
    participant Ctrl as ProjectController
    participant S as ProjectService
    participant IS as ImageService
    participant FS as FileStorage
    participant DB as Database
    participant Cache as Redis

    Note over C: After project is created successfully

    C->>Ctrl: POST /api/v1/projects/{id}/images
    Note over Ctrl: Content-Type: multipart/form-data
    Ctrl->>Ctrl: Validate multipart files and metadata
    Ctrl->>S: uploadProjectImages(projectId, files, metadata)

    S->>S: Verify project exists

    loop For each image file
        S->>IS: processImage(file, metadata)
        IS->>IS: Validate image (format, size, dimensions)
        IS->>IS: Generate optimized versions (thumbnail, etc.)
        IS->>FS: Upload original + optimized versions
        FS-->>IS: Return storage URLs
        IS->>IS: Create ProjectImage entity with metadata
        IS->>DB: Save ProjectImage entity
    end

    S->>S: Auto-set first uploaded image as primary if none exists
    S->>S: Update display order based on upload sequence
    S->>Cache: Invalidate project cache

    S-->>Ctrl: Return created images metadata
    Ctrl-->>C: 201 Created + ProjectImageResponse[]

    Note over C: Images now visible in project gallery
```

### 7. Primary Image Management Flow

```mermaid
sequenceDiagram
    participant C as API Client
    participant Ctrl as Controller
    participant S as Service
    participant D as DAO
    participant DB as Database
    participant Cache as Redis

    C->>Ctrl: PUT /api/v1/projects/{id}/images/{imageId}/primary
    Ctrl->>S: setPrimaryImage(projectId, imageId)
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
    S-->>Ctrl: Success response
    Ctrl-->>C: 200 OK
```

### 8. Blog Post Creation Flow (via API)

```mermaid
sequenceDiagram
    participant C as API Client
    participant Ctrl as BlogController
    participant S as BlogService
    participant D as BlogDAO
    participant DB as Database
    participant Cache as Redis

    C->>Ctrl: POST /api/v1/blog/posts
    Ctrl->>S: createBlogPost(request)
    S->>S: Validate content and metadata
    S->>S: Generate slug from title
    S->>D: saveBlogPost(blogPost)
    D->>DB: INSERT blog_post

    S->>S: Link categories and tags
    D->>DB: INSERT blog_post_categories
    D->>DB: INSERT blog_post_tags

    S-->>Ctrl: Return created blog post
    Ctrl-->>C: 201 Created + BlogPostResponse

    alt If publishing immediately
        S->>S: Set published = true, set published_at
        S->>Cache: Invalidate blog cache
    end
```

### 9. Contact Submission Management Flow (via API)

```mermaid
sequenceDiagram
    participant C as API Client
    participant Ctrl as ContactController
    participant S as ContactService
    participant E as EmailService
    participant D as ContactDAO
    participant DB as Database

    C->>Ctrl: GET /api/v1/contact?status=NEW
    Ctrl->>S: getContactSubmissions(status)
    S->>D: findByStatus(NEW)
    D->>DB: SELECT from contact_submissions
    DB-->>D: Return submissions
    D-->>S: Return submissions
    S-->>Ctrl: Return contact list
    Ctrl-->>C: 200 OK + ContactSubmission[]

    Note over C: View and respond to submissions

    C->>Ctrl: PUT /api/v1/contact/{id}/status
    Ctrl->>S: updateStatus(id, READ)
    S->>D: updateStatus(id, READ)
    D->>DB: UPDATE contact_submissions SET status = 'READ'
    S-->>Ctrl: Success
    Ctrl-->>C: 200 OK

    C->>Ctrl: POST /api/v1/contact/{id}/reply
    Ctrl->>S: replyToSubmission(id, message)
    S->>E: sendReplyEmail(submission, message)
    E-->>S: Email sent confirmation
    S->>D: updateStatus(id, REPLIED)
    D->>DB: UPDATE contact_submissions SET status = 'REPLIED'
    S-->>Ctrl: Reply sent successfully
    Ctrl-->>C: 200 OK
```

This comprehensive documentation provides the foundation for understanding system behavior, data flows, and user interactions. Use these diagrams to guide implementation decisions.
