# System Architecture Diagrams

This file contains all Mermaid diagrams for the Personal Website Portfolio system. These will render automatically in GitHub.

## 1. Project Creation Flow (Enhanced with ProjectImage Integration)

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
    C->>C: Validate DTO + multipart files
    C->>S: createProject(request, images)
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
        IS->>IS: Validate image files (format, size)
        IS->>FS: Upload images to storage
        FS-->>IS: Return image URLs
        IS->>D: saveProjectImages(images)
        D->>DB: INSERT project_images
        IS->>IS: Set primary image (first uploaded)
    end
    
    S->>Cache: Invalidate project cache
    S-->>C: Return created project + images
    C-->>A: 201 Created + ProjectResponse
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

## 4. System Architecture Overview

```mermaid
flowchart TB
    subgraph "Frontend Layer"
        UI[React Frontend]
        Admin[Admin Dashboard]
    end
    
    subgraph "API Layer"
        Gateway[API Gateway]
        Auth[Authentication]
        Rate[Rate Limiting]
    end
    
    subgraph "Application Layer"
        PC[Project Controller]
        TC[Technology Controller]
        CC[Contact Controller]
        BC[Blog Controller]
        AC[Analytics Controller]
        RC[Resume Controller]
        IC[Image Controller]
    end
    
    subgraph "Business Logic Layer"
        PS[Project Service]
        TS[Technology Service]  
        CS[Contact Service]
        BS[Blog Service]
        AS[Analytics Service]
        RS[Resume Service]
        IS[Image Service]
    end
    
    subgraph "Data Access Layer"
        PD[Project DAO]
        TD[Technology DAO]
        CD[Contact DAO]
        BD[Blog DAO]
        AD[Analytics DAO]
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
        Analytics[Google Analytics]
    end
    
    UI --> Gateway
    Admin --> Gateway
    
    Gateway --> Auth
    Gateway --> Rate
    Auth --> PC & TC & CC & BC & AC & RC & IC
    Rate --> PC & TC & CC & BC & AC & RC & IC
    
    PC --> PS
    TC --> TS
    CC --> CS
    BC --> BS
    AC --> AS
    RC --> RS
    IC --> IS
    
    PS --> PD
    TS --> TD
    CS --> CD
    BS --> BD
    AS --> AD
    IS --> ID
    
    PD --> DB
    TD --> DB
    CD --> DB
    BD --> DB
    AD --> DB
    
    PS --> Cache
    TS --> Cache
    BS --> Cache
    
    CS --> Email
    UI --> CDN
    AS --> Analytics
    
    style UI fill:#e3f2fd
    style Admin fill:#e8f5e8
    style DB fill:#fff3e0
    style Cache fill:#fce4ec
```

## 5. Blog Post Publishing Workflow

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

## 6. Analytics Data Collection

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

## 7. Database Entity Relationships

```mermaid
erDiagram
    USER {
        bigserial id PK
        varchar username UK
        varchar email UK
        varchar password_hash
        user_role role
        boolean active
        timestamptz created_at
    }
    
    PROJECT {
        bigserial id PK
        varchar name UK
        varchar slug UK
        text description
        varchar github_url
        varchar live_url
        project_type type
        project_status status
        difficulty_level difficulty
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
        proficiency_level proficiency
        decimal years_experience
        boolean featured
        timestamptz created_at
    }
    
    PROJECT_TECHNOLOGY {
        bigint project_id PK,FK
        bigint technology_id PK,FK
        timestamptz created_at
    }
    
    PROJECT_IMAGE {
        bigserial id PK
        bigint project_id FK
        varchar url
        image_type type
        boolean is_primary
        timestamptz created_at
    }
    
    LEARNING_OUTCOME {
        bigserial id PK
        bigint project_id FK
        varchar title
        text description
        skill_category category
        difficulty_level difficulty
        timestamptz created_at
    }
    
    CONTACT_SUBMISSION {
        bigserial id PK
        varchar name
        varchar email
        text message
        inquiry_type type
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
    
    PROJECT ||--o{ PROJECT_TECHNOLOGY : "uses"
    TECHNOLOGY ||--o{ PROJECT_TECHNOLOGY : "used_in"
    PROJECT ||--o{ PROJECT_IMAGE : "has"
    PROJECT ||--o{ LEARNING_OUTCOME : "teaches"
    BLOG_POST }o--o{ BLOG_CATEGORY : "belongs_to"
    BLOG_POST }o--o{ BLOG_TAG : "tagged_with"
```

## 8. Resume Download Flow

```mermaid
sequenceDiagram
    participant V as Visitor
    participant UI as Frontend
    participant C as ResumeController
    participant S as ResumeService
    participant AS as AnalyticsService
    participant FS as FileStorage
    participant Q as Queue
    participant DB as Database

    V->>UI: Click "Download Resume"
    UI->>C: GET /api/v1/resume/download
    C->>AS: trackEvent(RESUME_DOWNLOAD, request)
    AS->>Q: Queue analytics event
    
    par Download File
        C->>S: getCurrentResume()
        S->>FS: Load latest resume file
        FS-->>S: Return PDF resource
        S-->>C: Return resume resource
        C-->>UI: 200 OK + PDF file
        UI-->>V: Browser downloads file
    and Process Analytics
        Q->>DB: Store download event (async)
        DB-->>Q: Event stored
    end
    
    Note over V: File saved as "Casey-Quinn-Resume.pdf"
```

## 9. Admin UI Project Creation Workflow

```mermaid
flowchart TD
    A[Admin Login] --> B{Authentication}
    B -->|Success| C[Admin Dashboard]
    B -->|Failure| A
    
    C --> D[Navigate to Projects]
    D --> E[Click 'Create New Project']
    E --> F[Project Creation Form]
    
    F --> G{Form Mode}
    G -->|Quick Create| H[Basic Info + Primary Image]
    G -->|Full Create| I[Tabbed Interface]
    
    H --> H1[Fill Basic Details]
    H1 --> H2[Upload Primary Image]
    H2 --> H3[Select Technologies]
    H3 --> H4{Validation}
    
    I --> I1[Basic Info Tab]
    I1 --> I2[Media Gallery Tab]
    I2 --> I3[Technologies Tab]
    I3 --> I4[SEO Settings Tab]
    I4 --> I5[Learning Outcomes Tab]
    I5 --> H4
    
    H4 -->|Pass| J{Save Action}
    H4 -->|Fail| K[Show Errors]
    K --> F
    
    J -->|Save Draft| L[Save as Unpublished]
    J -->|Publish| M[Save as Published]
    
    L --> N[Success + Redirect]
    M --> N
    N --> O[Project List Updated]
    
    style A fill:#e3f2fd
    style C fill:#e8f5e8
    style N fill:#c8e6c9
    style K fill:#ffebee
```

## 10. ProjectImage Upload and Management Flow

```mermaid
sequenceDiagram
    participant A as Admin
    participant UI as Admin UI
    participant C as ProjectController
    participant S as ProjectService
    participant IS as ImageService
    participant FS as FileStorage
    participant DB as Database
    participant Cache as Redis

    A->>UI: Upload project images
    UI->>UI: Validate files (client-side)
    UI->>C: POST /api/v1/projects/{id}/images
    C->>S: uploadProjectImages(projectId, files)
    
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
    S-->>C: Return image metadata
    C-->>UI: 201 Created + image data
    UI->>UI: Update gallery display
    
    Note over A: Images appear in project gallery
```

## 11. Admin Dashboard Data Flow

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

---

**Note**: These diagrams will render automatically when viewing this file in GitHub, GitLab, or any Mermaid-compatible viewer.