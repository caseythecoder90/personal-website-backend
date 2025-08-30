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
2. **Skills Assessment** → Filter projects by "Java", "Spring Boot"
3. **Code Quality Review** → Click GitHub links, review commits
4. **Project Depth** → Read project descriptions, check complexity
5. **Communication Skills** → Read blog posts about technical topics
6. **Contact Decision** → Submit hiring inquiry via contact form

Pain Points:
- Need quick skill verification
- Want to see code quality, not just descriptions
- Looking for recent activity and continuous learning

Solutions:
- Featured technologies on homepage
- Technology filtering on projects
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

This comprehensive documentation provides the foundation for understanding system behavior, data flows, and user interactions. Use these diagrams to guide implementation decisions and communicate system design to stakeholders.