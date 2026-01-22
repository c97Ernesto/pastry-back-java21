# Malva Pastry Shop - System Architecture

## Overview
This document describes the system architecture, design patterns, and architectural decisions for the Malva Pastry Shop backend.

**Architecture Style:** Layered + Domain-Driven Design (DDD)  
**Deployment:** Monolithic Spring Boot application  
**Future:** Dual-channel (Admin SSR + Public REST API)

---

## Table of Contents
1. [High-Level Architecture](#high-level-architecture)
2. [Domain-Driven Design](#domain-driven-design)
3. [Layer Architecture](#layer-architecture)
4. [Package Organization](#package-organization)
5. [Design Patterns](#design-patterns)
6. [Security Architecture](#security-architecture)
7. [API Strategy](#api-strategy)

---

## High-Level Architecture

```mermaid
graph TB
    subgraph "Frontend Layer"
        A1[Admin Panel<br/>Thymeleaf SSR]
        A2[React Storefront<br/>Future]
    end
    
    subgraph "Backend - Spring Boot"
        B1[Admin Controllers<br/>@Controller]
        B2[API Controllers<br/>@RestController<br/>Future]
        
        C1[Storefront Services]
        C2[Inventory Services]
        C3[User Service]
        
        D1[JPA Repositories]
        
        E1[Domain Entities]
    end
    
    subgraph "Data Layer"
        F1[(PostgreSQL Database)]
    end
    
    A1 -.HTTP.-> B1
    A2 -.REST API.-> B2
    
    B1 --> C1
    B1 --> C2
    B1 --> C3
    B2 --> C1
    B2 --> C3
    
    C1 --> D1
    C2 --> D1
    C3 --> D1
    
    D1 --> E1
    E1 -.JPA/Hibernate.-> F1
    
    style A1 fill:#e1f5ff
    style A2 fill:#fff4e1
    style B1 fill:#d4edda
    style B2 fill:#fff3cd
    style C1 fill:#cfe2ff
    style C2 fill:#f8d7da
    style F1 fill:#e2e3e5
```

---

## Domain-Driven Design

```mermaid
graph LR
    subgraph "Storefront Context (Public)"
        ST1[Product]
        ST2[Category]
        ST3[Tag]
        ST4[ProductTag]
    end
    
    subgraph "Inventory Context (Internal)"
        IN1[Ingredient]
        IN2[ProductIngredient]
        IN3[UnitOfMeasure]
    end
    
    subgraph "Auth Context (Shared)"
        AU1[User]
        AU2[Role]
    end
    
    ST1 -.recipe.-> IN2
    IN2 --> IN1
    ST1 --> ST4
    ST4 --> ST3
    ST1 -.created by.-> AU1
    
    style ST1 fill:#d1ecf1
    style ST2 fill:#d1ecf1
    style ST3 fill:#d1ecf1
    style ST4 fill:#d1ecf1
    style IN1 fill:#f8d7da
    style IN2 fill:#f8d7da
    style IN3 fill:#f8d7da
    style AU1 fill:#fff3cd
    style AU2 fill:#fff3cd
```

### Bounded Contexts

#### 1. Storefront Context (Public-Facing)
**Purpose:** Customer-facing product catalog  
**Entities:** Product, Category, Tag, ProductTag  
**Services:** ProductService, CategoryService, TagService  
**Exposure:** Will be exposed via REST API to React frontend

**Business Rules:**
- Products have categories and tags for organization/filtering
- Products implement soft-delete (can be restored)
- Tags have URL-friendly slugs
- Cannot delete tag if in use by products

#### 2. Inventory Context (Internal Operations)
**Purpose:** Internal production, recipes, and cost management  
**Entities:** Ingredient, ProductIngredient, UnitOfMeasure  
**Services:** IngredientService  
**Exposure:** Internal only, not exposed to public API

**Business Rules:**
- Ingredients track unit costs for profit analysis
- ProductIngredient defines recipes (quantity of each ingredient)
- Soft-delete prevents accidental data loss

#### 3. Auth Context (Shared)
**Purpose:** Authentication and authorization  
**Entities:** User, Role  
**Services:** UserService (implements UserDetailsService)  
**Exposure:** Admin-only, never public

**Business Rules:**
- Role-based access control (ADMIN, EMPLOYEE)
- BCrypt password hashing
- Session-based authentication for admin panel
- Future: JWT for public API

### Cross-Context Relationships

```mermaid
graph LR
    A[Product<br/>Storefront] -->|has recipe| B[ProductIngredient<br/>Inventory]
    B --> C[Ingredient<br/>Inventory]
    A -->|created by| D[User<br/>Auth]
    
    style A fill:#d1ecf1
    style B fill:#f8d7da
    style C fill:#f8d7da
    style D fill:#fff3cd
```

---

## Layer Architecture

```mermaid
graph TD
    subgraph "Presentation Layer"
        P1[Admin Controllers<br/>Thymeleaf Views]
        P2[API Controllers<br/>JSON Responses]
    end
    
    subgraph "Application Layer"
        A1[Services<br/>Business Logic]
        A2[DTOs<br/>Data Transfer]
    end
    
    subgraph "Domain Layer"
        D1[Entities<br/>Business Rules]
        D2[Value Objects<br/>Enums]
    end
    
    subgraph "Infrastructure Layer"
        I1[Repositories<br/>Data Access]
        I2[Security<br/>Authentication]
        I3[Utils<br/>SlugUtil]
    end
    
    P1 --> A1
    P2 --> A1
    A1 --> D1
    A1 --> I1
    A1 --> A2
    I1 --> D1
    P1 -.uses.-> I2
    P2 -.uses.-> I2
    A1 -.uses.-> I3
    
    style P1 fill:#d4edda
    style P2 fill:#fff3cd
    style A1 fill:#cfe2ff
    style D1 fill:#e2e3e5
    style I1 fill:#f8d7da
```

### Layer Responsibilities

#### Presentation Layer
- **Controllers**: Handle HTTP requests/responses
- **Admin Controllers** (`@Controller`): Return Thymeleaf view names
- **API Controllers** (`@RestController`): Return JSON DTOs
- **Validation**: `@Valid` on request DTOs

#### Application Layer
- **Services**: Business logic, orchestration, transactions
- **DTOs**: Control data shape for different audiences
- **Mappers** (future): Entity ↔ DTO conversion

#### Domain Layer
- **Entities**: Business objects with identity
- **Value Objects**: Immutable objects (enums)
- **Business Rules**: Encoded in entity methods
- **No dependencies**: Pure business logic

#### Infrastructure Layer
- **Repositories**: Database queries via Spring Data JPA
- **Security**: Spring Security configuration
- **Utilities**: Helper classes (e.g., SlugUtil)

---

## Package Organization

```
com.malva_pastry_shop.backend/
│
├── controller/                 # Presentation
│   ├── admin/                  # SSR Controllers (Thymeleaf)
│   └── api/                    # REST Controllers (JSON)
│
├── service/                    # Application
│   ├── storefront/             # Public catalog logic
│   ├── inventory/              # Internal operations
│   └── UserService             # Auth service
│
├── dto/                        # Data Transfer
│   ├── request/                # Input validation
│   └── response/
│       ├── public/             # API responses
│       └── admin/              # Internal reports
│
├── domain/                     # Domain
│   ├── storefront/             # Public entities
│   ├── inventory/              # Internal entities
│   ├── auth/                   # User/Role
│   └── common/                 # Base classes
│
├── repository/                 # Infrastructure
├── config/                     # Configuration
│   ├── SecurityConfig
│   └── DataSeeder
│
└── util/                       # Utilities
    └── SlugUtil
```

### Package Naming Conventions

| Package      | Suffix           | Example                              |
| ------------ | ---------------- | ------------------------------------ |
| Entities     | None             | `Product`, `Tag`                     |
| Repositories | `Repository`     | `ProductRepository`                  |
| Services     | `Service`        | `ProductService`                     |
| Controllers  | `Controller`     | `ProductController`                  |
| DTOs         | `DTO`, `Request` | `ProductPublicDTO`, `ProductRequest` |

---


## Security Architecture

```mermaid
graph LR
    A[HTTP Request] --> B{Authenticated?}
    B -->|No| C[Redirect to /login]
    B -->|Yes| D{Authorized?}
    D -->|No| E[403 Forbidden]
    D -->|Yes| F[Controller Method]
    
    F --> G{Has @PreAuthorize?}
    G -->|Yes| H{Role Check}
    G -->|No| I[Execute]
    H -->|Pass| I
    H -->|Fail| E
    
    style C fill:#f8d7da
    style E fill:#f8d7da
    style I fill:#d4edda
```



---

## API Strategy

### Current: Server-Side Rendering (SSR)
- **Technology:** Thymeleaf
- **Audience:** Admin users
- **Authentication:** Session-based
- **URL Pattern:** `/products`, `/categories`, etc.

### Future: REST API
- **Technology:** Spring REST + JSON
- **Audience:** React frontend (customers)
- **Authentication:** JWT (planned)
- **URL Pattern:** `/api/products`, `/api/categories`


## Technology Stack Alignment

```mermaid
graph TB
    A[Spring Boot 4.0.1] --> B[IoC Container]
    A --> C[Auto-Configuration]
    
    D[Spring MVC] --> E[Admin Controllers]
    D --> F[REST Controllers]
    
    G[Spring Data JPA] --> H[Repositories]
    H --> I[Hibernate 7.2]
    I --> J[PostgreSQL Driver]
    
    K[Spring Security 7.x] --> L[Authentication]
    K --> M[Authorization]
    
    N[Thymeleaf 3.x] --> O[Template Engine]
    
    style A fill:#6db33f
    style D fill:#6db33f
    style G fill:#6db33f
    style K fill:#6db33f
```

## Entity Relationship Diagram

```mermaid
erDiagram
    users ||--o{ products : "creates"
    users }o--|| roles : "has"
    users ||--o{ products_deleted : "soft_deletes"
    users ||--o{ tags_deleted : "soft_deletes"
    users ||--o{ categories_deleted : "soft_deletes"
    users ||--o{ ingredients_deleted : "soft_deletes"
    
    categories ||--o{ products : "categorizes"
    products ||--o{ product_ingredients : "contains"
    products ||--o{ product_tags : "tagged_with"
    ingredients ||--o{ product_ingredients : "used_in"
    tags ||--o{ product_tags : "applies_to"
    
    users {
        bigint id PK "Auto-increment"
        varchar_100 name "NOT NULL"
        varchar_100 last_name "NOT NULL"
        varchar_255 email "UNIQUE, NOT NULL"
        varchar_255 password_hash "NOT NULL"
        boolean enabled "DEFAULT true"
        boolean system_admin "DEFAULT false"
        bigint role_id FK "→ roles.id"
        timestamp inserted_at "DEFAULT now()"
        timestamp updated_at "DEFAULT now()"
    }
    
    roles {
        bigint id PK "Auto-increment"
        varchar_50 name "UNIQUE, NOT NULL (ADMIN/EMPLOYEE)"
        varchar_255 description
        timestamp inserted_at "DEFAULT now()"
        timestamp updated_at "DEFAULT now()"
    }
    
    categories {
        bigint id PK "Auto-increment"
        varchar_100 name "NOT NULL"
        text description
        timestamp inserted_at "DEFAULT now()"
        timestamp updated_at "DEFAULT now()"
        timestamp deleted_at "NULL = active"
        bigint deleted_by_id FK "→ users.id"
    }
    
    products {
        bigint id PK "Auto-increment"
        varchar_100 name "NOT NULL"
        text description
        integer preparation_days "≥ 0"
        numeric_10_2 base_price "≥ 0.00"
        bigint user_id FK "→ users.id (creator)"
        bigint category_id FK "→ categories.id"
        timestamp inserted_at "DEFAULT now()"
        timestamp updated_at "DEFAULT now()"
        timestamp deleted_at "NULL = active"
        bigint deleted_by_id FK "→ users.id"
    }
    
    tags {
        bigint id PK "Auto-increment"
        varchar_50 name "NOT NULL"
        varchar_100 slug "UNIQUE, NOT NULL (URL-friendly)"
        varchar_200 description
        timestamp inserted_at "DEFAULT now()"
        timestamp updated_at "DEFAULT now()"
        timestamp deleted_at "NULL = active"
        bigint deleted_by_id FK "→ users.id"
    }
    
    product_tags {
        bigint id PK "Auto-increment"
        bigint product_id FK "→ products.id, NOT NULL"
        bigint tag_id FK "→ tags.id, NOT NULL"
        timestamp inserted_at "DEFAULT now()"
        timestamp updated_at "DEFAULT now()"
    }
    
    ingredients {
        bigint id PK "Auto-increment"
        varchar_100 name "NOT NULL"
        text description
        numeric_10_2 unit_cost "≥ 0.00"
        varchar_50 unit_of_measure "ENUM"
        timestamp inserted_at "DEFAULT now()"
        timestamp updated_at "DEFAULT now()"
        timestamp deleted_at "NULL = active"
        bigint deleted_by_id FK "→ users.id"
    }
    
    product_ingredients {
        bigint id PK "Auto-increment"
        bigint product_id FK "→ products.id, NOT NULL"
        bigint ingredient_id FK "→ ingredients.id, NOT NULL"
        numeric_14_4 quantity "NOT NULL, > 0"
        timestamp inserted_at "DEFAULT now()"
        timestamp updated_at "DEFAULT now()"
    }
```

