# System Architecture Diagrams

This file contains all Mermaid diagrams for the Personal Website Portfolio system. These will render automatically in GitHub.

## 1. Project Creation Flow

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

    Note over C,Ctrl: Project created successfully without images
    Note over C,Ctrl: Images uploaded separately via POST /api/v1/projects/{id}/images
```

## 2. Portfolio Visitor User Journey

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

## 3. Contact Form Submission Flow

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

## 4. System Architecture Overview

```mermaid
flowchart TB
    subgraph "Frontend Layer"
        UI[React Frontend]
    end

    subgraph "API Layer"
        Rate[Rate Limiting]
    end

    subgraph "Application Layer"
        PC[Project Controller]
        TC[Technology Controller]
        CC[Contact Controller]
        BC[Blog Controller]
        RC[Resume Controller]
        IC[Image Controller]
    end

    subgraph "Business Logic Layer"
        PS[Project Service]
        TS[Technology Service]
        CS[Contact Service]
        BS[Blog Service]
        RS[Resume Service]
        IS[Image Service]
    end

    subgraph "Data Access Layer"
        PD[Project DAO]
        TD[Technology DAO]
        CD[Contact DAO]
        BD[Blog DAO]
        ID[Image DAO]
    end

    subgraph "Data Layer"
        DB[(PostgreSQL Database)]
        Cache[(Redis Cache)]
        Files[File Storage]
    end

    subgraph "External Services"
        Email[Email Service]
        CDN[Content Delivery Network]
    end

    UI --> Rate

    Rate --> PC & TC & CC & BC & RC & IC

    PC --> PS
    TC --> TS
    CC --> CS
    BC --> BS
    RC --> RS
    IC --> IS

    PS --> PD
    TS --> TD
    CS --> CD
    BS --> BD
    IS --> ID

    PD --> DB
    TD --> DB
    CD --> DB
    BD --> DB
    ID --> DB

    PS --> Cache
    TS --> Cache
    BS --> Cache

    CS --> Email
    UI --> CDN
    RS --> Files

    style UI fill:#e3f2fd
    style DB fill:#fff3e0
    style Cache fill:#fce4ec
```

## 5. Blog Post Publishing Workflow

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

## 6. Database Entity Relationships

```mermaid
erDiagram
    PROJECT {
        bigserial id PK
        varchar name UK
        varchar slug UK
        text short_description
        text full_description
        varchar github_url
        varchar live_url
        project_type project_type
        project_status status
        difficulty_level difficulty_level
        timestamptz start_date
        timestamptz completion_date
        boolean featured
        boolean published
        bigint view_count
        timestamptz created_at
    }

    TECHNOLOGY {
        bigserial id PK
        varchar name UK
        varchar version
        technology_category category
        proficiency_level proficiency_level
        decimal years_experience
        boolean featured
        timestamptz created_at
    }

    PROJECT_TECHNOLOGY {
        bigint project_id PK,FK
        bigint technology_id PK,FK
    }

    PROJECT_IMAGE {
        bigserial id PK
        bigint project_id FK
        varchar url
        image_type image_type
        boolean is_primary
        integer display_order
        timestamptz created_at
    }

    CONTACT_SUBMISSION {
        bigserial id PK
        varchar name
        varchar email
        text message
        inquiry_type inquiry_type
        submission_status status
        timestamptz created_at
    }

    BLOG_POST {
        bigserial id PK
        varchar title
        varchar slug UK
        text content
        boolean published
        timestamptz published_at
        integer view_count
        timestamptz created_at
    }

    BLOG_CATEGORY {
        bigserial id PK
        varchar name UK
        varchar slug UK
        text description
        timestamptz created_at
    }

    BLOG_TAG {
        bigserial id PK
        varchar name UK
        varchar slug UK
        integer usage_count
        timestamptz created_at
    }

    BLOG_POST_CATEGORY {
        bigint blog_post_id PK,FK
        bigint blog_category_id PK,FK
    }

    BLOG_POST_TAG {
        bigint blog_post_id PK,FK
        bigint blog_tag_id PK,FK
    }

    PROJECT ||--o{ PROJECT_TECHNOLOGY : "uses"
    TECHNOLOGY ||--o{ PROJECT_TECHNOLOGY : "used_in"
    PROJECT ||--o{ PROJECT_IMAGE : "has"
    BLOG_POST ||--o{ BLOG_POST_CATEGORY : "belongs_to"
    BLOG_CATEGORY ||--o{ BLOG_POST_CATEGORY : "contains"
    BLOG_POST ||--o{ BLOG_POST_TAG : "tagged_with"
    BLOG_TAG ||--o{ BLOG_POST_TAG : "tags"
```

## 7. Resume Download Flow

```mermaid
sequenceDiagram
    participant V as Visitor
    participant UI as Frontend
    participant C as ResumeController
    participant S as ResumeService
    participant FS as FileStorage

    V->>UI: Click "Download Resume"
    UI->>C: GET /api/v1/resume/download
    C->>S: getCurrentResume()
    S->>FS: Load latest resume file
    FS-->>S: Return PDF resource
    S-->>C: Return resume resource
    C-->>UI: 200 OK + PDF file
    UI-->>V: Browser downloads file

    Note over V: File saved as "Casey-Quinn-Resume.pdf"
```

## 8. ProjectImage Upload and Management Flow

```mermaid
sequenceDiagram
    participant C as API Client
    participant Ctrl as ProjectController
    participant S as ProjectService
    participant IS as ImageService
    participant FS as FileStorage
    participant DB as Database
    participant Cache as Redis

    C->>Ctrl: POST /api/v1/projects/{id}/images
    Ctrl->>S: uploadProjectImages(projectId, files)

    loop For each image
        S->>IS: processImage(file)
        IS->>IS: Validate (format, size, dimensions)
        IS->>IS: Generate thumbnail if needed
        IS->>FS: Upload original + thumbnail
        FS-->>IS: Return storage URLs
        IS->>DB: Save ProjectImage entity
    end

    S->>S: Update display order
    S->>S: Set primary image (if first)
    S->>Cache: Invalidate project cache
    S-->>Ctrl: Return image metadata
    Ctrl-->>C: 201 Created + image data

    Note over C: Images appear in project gallery
```

## 9. Primary Image Management Flow

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

## 10. Technology Proficiency Tracking

```mermaid
flowchart LR
    A[API Client updates technology] --> B{Change in proficiency?}
    B -->|No| C[Update basic info]
    B -->|Yes| D[Update proficiency level]

    C --> E[Save to database]
    D --> F[Update years experience]
    F --> G[Update featured status]
    G --> E

    E --> H[Update project associations]
    H --> I[Invalidate caches]
    I --> J[Portfolio display updated]

    style A fill:#e3f2fd
    style J fill:#c8e6c9
```

---

**Note**: These diagrams will render automatically when viewing this file in GitHub, GitLab, or any Mermaid-compatible viewer.
