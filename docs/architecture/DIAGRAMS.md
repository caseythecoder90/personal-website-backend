# System Architecture Diagrams

This file contains all Mermaid diagrams for the Personal Website Portfolio system. These will render automatically in GitHub.

## 1. Project Creation Flow

```mermaid
sequenceDiagram
    participant A as Admin
    participant C as Controller
    participant S as Service
    participant D as DAO
    participant R as Repository
    participant DB as Database
    participant Cache as Redis

    A->>C: POST /api/v1/projects
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
    S->>Cache: Invalidate project cache
    S-->>C: Return created project
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
    end
    
    subgraph "Business Logic Layer"
        PS[Project Service]
        TS[Technology Service]  
        CS[Contact Service]
        BS[Blog Service]
        AS[Analytics Service]
    end
    
    subgraph "Data Access Layer"
        PD[Project DAO]
        TD[Technology DAO]
        CD[Contact DAO]
        BD[Blog DAO]
        AD[Analytics DAO]
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
    Auth --> PC & TC & CC & BC & AC
    Rate --> PC & TC & CC & BC & AC
    
    PC --> PS
    TC --> TS
    CC --> CS
    BC --> BS
    AC --> AS
    
    PS --> PD
    TS --> TD
    CS --> CD
    BS --> BD
    AS --> AD
    
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

## 8. Admin Dashboard Data Flow

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