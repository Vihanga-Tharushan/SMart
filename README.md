# S-MArt --- Supermarket Chain Management System

A distributed retail management platform designed to support
multi-branch supermarket operations, online customers, delivery
personnel, branch managers, administrators, and point-of-sale (POS)
terminals.

> **Project status:** In active development. The current implementation
> includes an initial Spring Boot `user-service` with CRUD functionality
> and a Supabase PostgreSQL database. Other services and applications
> described below are planned architecture unless explicitly marked as
> implemented.

## Table of Contents

-   [Project Overview](#project-overview)
-   [Goals](#goals)
-   [Applications](#applications)
-   [Planned Architecture](#planned-architecture)
-   [Microservices](#microservices)
-   [Technology Stack](#technology-stack)
-   [Database Architecture](#database-architecture)
-   [Core Data Model](#core-data-model)
-   [Getting Started](#getting-started)
-   [Configuration and Secrets](#configuration-and-secrets)
-   [Development Roadmap](#development-roadmap)
-   [Engineering Guidelines](#engineering-guidelines)
-   [Security Notes](#security-notes)
-   [Contributing](#contributing)
-   [License](#license)

## Project Overview

S-MArt aims to provide a single platform for managing supermarket
operations across multiple branches. It will connect in-store operations
with online shopping and delivery workflows through backend services and
REST APIs, with asynchronous messaging introduced where appropriate.

The project is being developed with Java and Spring Boot to practise and
demonstrate backend engineering, microservice design, database
modelling, API development, security, and distributed-system concepts.

## Goals

-   Manage products, categories, branches, prices, and branch-level
    inventory.
-   Support customer registration, authentication, shopping, and order
    tracking.
-   Enable branch managers and administrators to manage supermarket
    operations.
-   Support delivery personnel with delivery assignment and status
    updates.
-   Support cashier workflows through a POS application.
-   Keep business responsibilities and data ownership separated between
    services.
-   Introduce secure REST APIs and asynchronous service communication.
-   Build toward containerized local development and automated testing.
-   Leave room for future supplier, procurement, and reporting
    capabilities.

## Applications

The platform is intended to support four main user experiences.

### 1. Supermarket Management Web Application

For administrators, branch managers, and authorized staff:

-   Manage branches and branch staff.
-   Manage products, categories, and pricing.
-   Monitor stock levels and low-stock conditions.
-   Review and manage orders.
-   View operational and sales reports.

### 2. Customer Shopping Web Application

A responsive web application for customers to:

-   Browse and search products.
-   View product details and availability.
-   Select a branch where applicable.
-   Manage a shopping cart and place orders.
-   Complete payment through a supported payment provider.
-   Track order and delivery status.

### 3. Delivery Personnel Mobile Application

For delivery personnel to:

-   Authenticate securely.
-   View and manage assigned deliveries.
-   View relevant order and delivery details.
-   Update delivery progress.
-   Record successful or unsuccessful delivery outcomes.

### 4. Point-of-Sale (POS) Application

For in-store cashiers to:

-   Find or scan products.
-   Build a sale and calculate totals.
-   Record supported payment outcomes.
-   Generate receipts.
-   Integrate with inventory and reporting workflows.

These are application experiences, not separate microservices by
themselves. They are expected to use backend APIs according to their
permissions.

## Planned Architecture

The intended architecture separates business capabilities into
independently deployable Spring Boot services.

``` text
Management Web ─────┐
Customer Web ───────┤
Delivery Mobile App ├──── REST APIs ──── API Gateway (planned)
POS Application ────┘                         │
                                             ▼
   ┌─────────────┬──────────────┬─────────────┬─────────────┐
   │ User/Auth   │ Branch       │ Catalog     │ Inventory   │
   │ Service     │ Service      │ Service     │ Service     │
   └─────────────┴──────────────┴─────────────┴─────────────┘
                                             │
   ┌─────────────┬──────────────┬─────────────┴─────────────┐
   │ Order       │ Payment      │ Delivery      │ POS/Sales │
   │ Service     │ Service      │ Service       │ Service   │
   └─────────────┴──────────────┴───────────────┴───────────┘
                                             │
                                  Reporting Service (planned)

     PostgreSQL / Supabase — service-owned data
     RabbitMQ — asynchronous events (planned)
     Redis — caching and short-lived data (planned)
     Docker Compose — local multi-service environment (planned)
```

This diagram describes the target architecture, not a claim that all
components are implemented.

### Communication

-   **REST APIs:** For requests that require an immediate response, such
    as retrieving product details or creating an order.
-   **RabbitMQ (planned):** For asynchronous business events, such as
    order creation, stock reservation outcomes, and delivery updates.
-   **Redis (planned):** For appropriate caching or short-lived data. It
    must not replace PostgreSQL as the authoritative stock record.
-   **API Gateway (planned):** A common entry point for routing and
    selected cross-cutting concerns.

Distributed workflows will need timeouts, retries, idempotency, and
explicit handling of failure states. A database transaction in one
service cannot automatically make changes in another service atomic.

## Microservices

  -----------------------------------------------------------------------
  Service                 Main responsibilities   Status
  ----------------------- ----------------------- -----------------------
  User / Authentication   User profiles,          Initial user CRUD
  Service                 registration,           exists; security is
                          authentication, roles,  planned
                          access control          

  Branch Service          Branch details,         Planned
                          operating status,       
                          branch staff            
                          assignments             

  Catalog Service         Product master data,    Planned
                          categories, SKU and     
                          barcode information     

  Inventory Service       Stock levels, stock     Planned
                          movements,              
                          reservations, low-stock 
                          rules                   

  Order Service           Orders, order items,    Planned
                          order lifecycle and     
                          status history          

  Payment Service         Payment attempts,       Planned
                          provider references,    
                          payment status and      
                          refunds                 

  Delivery Service        Delivery assignment,    Planned
                          delivery status and     
                          tracking                

  POS / Sales Service     In-store sales and      Planned
                          cashier workflows       

  Reporting Service       Cross-branch            Planned
                          operational and sales   
                          reports                 

  Supplier / Procurement  Supplier records,       Future extension
  capability              purchase orders and     
                          receiving stock         
  -----------------------------------------------------------------------

Service boundaries may be refined as the project develops. Each service
should own its business logic and data, expose documented interfaces,
and avoid directly modifying another service's tables.

## Technology Stack

  -----------------------------------------------------------------------
  Technology                          Intended use
  ----------------------------------- -----------------------------------
  Java                                Backend programming language

  Spring Boot                         REST APIs and independently
                                      deployable backend services

  Spring Security                     Authentication and authorization

  JWT                                 Token-based API authentication, if
                                      selected for the implementation

  PostgreSQL                          Relational data storage

  Supabase                            Managed PostgreSQL for the current
                                      development setup

  React                               Web applications

  Mobile framework                    Delivery application; technology to
                                      be selected

  RabbitMQ                            Asynchronous messaging; planned

  Redis                               Caching and short-lived data;
                                      planned

  Docker / Docker Compose             Containerization and local
                                      orchestration; planned

  Git / GitHub                        Version control and collaboration

  Postman or equivalent               API testing
  -----------------------------------------------------------------------

## Database Architecture

### Initial approach

The current user service uses Supabase as its PostgreSQL database.
During early development, the project can use one PostgreSQL instance
with separate schemas and restricted credentials for each service.

Suggested schema ownership:

  ------------------------------------------------------------------------
  Schema                  Owning service          Initial tables
  ----------------------- ----------------------- ------------------------
  `user_service`          User / Authentication   `users`, `user_profiles`
                          Service                 as needed

  `branch_service`        Branch Service          `branches`,
                                                  `branch_staff`

  `catalog_service`       Catalog Service         `categories`, `products`

  `inventory_service`     Inventory Service       `stock_levels`,
                                                  `stock_movements`,
                                                  `stock_reservations`

  `order_service`         Order Service           `orders`, `order_items`,
                                                  `order_status_history`

  `payment_service`       Payment Service         `payments`,
                                                  `payment_attempts`,
                                                  `refunds` when
                                                  implemented
  ------------------------------------------------------------------------

Create schemas and tables only as the corresponding services are
developed.

### Database ownership rules

1.  Each service is the source of truth for its own data.
2.  Use restricted database roles and credentials so a service cannot
    freely modify another service's schema.
3.  Cross-service identifiers such as `product_id`, `branch_id`,
    `customer_id`, and `order_id` are references to records owned by
    other services. Avoid cross-service database foreign keys if
    services may later move to separate databases.
4.  Keep foreign keys and transactions within a service's own database
    boundary where possible.
5.  Use database migrations under version control as the schema evolves.
6.  A single PostgreSQL instance is a practical starting point; move
    services to separate databases when isolation, scaling, security, or
    availability requirements justify it.

### Core data model

#### Catalog Service

**`categories`**

-   `id` --- UUID primary key
-   `name` --- VARCHAR(100), required
-   `slug` --- VARCHAR(120), unique
-   `description` --- TEXT, nullable
-   `parent_category_id` --- UUID, nullable, self-reference
-   `image_url` --- TEXT, nullable
-   `is_active` --- BOOLEAN, default TRUE
-   `created_at` --- TIMESTAMPTZ
-   `updated_at` --- TIMESTAMPTZ

**`products`**

-   `id` --- UUID primary key
-   `sku` --- VARCHAR(50), unique, required
-   `barcode` --- VARCHAR(100), nullable
-   `name` --- VARCHAR(200), required
-   `description` --- TEXT, nullable
-   `category_id` --- UUID, references a category within the Catalog
    Service
-   `brand` --- VARCHAR(100), nullable
-   `unit_of_measure` --- VARCHAR(20), e.g. `kg`, `g`, `litre`, `piece`
-   `image_url` --- TEXT, nullable
-   `is_active` --- BOOLEAN, default TRUE
-   `created_at` --- TIMESTAMPTZ
-   `updated_at` --- TIMESTAMPTZ

Branch-specific prices can be added through a `product_prices` table or
a dedicated pricing capability when needed. Product master data should
not store branch stock quantities.

#### Inventory Service

**`stock_levels`**

-   `id` --- UUID primary key
-   `branch_id` --- UUID reference to the Branch Service
-   `product_id` --- UUID reference to the Catalog Service
-   `quantity_on_hand` --- NUMERIC(12,3), default 0
-   `quantity_reserved` --- NUMERIC(12,3), default 0
-   `reorder_level` --- NUMERIC(12,3), default 0
-   `updated_at` --- TIMESTAMPTZ
-   Unique constraint on `(branch_id, product_id)`

Available stock is calculated as `quantity_on_hand - quantity_reserved`.
Enforce non-negative stock and reservation quantities.

**`stock_movements`**

-   `id` --- UUID primary key
-   `branch_id` --- UUID reference to the Branch Service
-   `product_id` --- UUID reference to the Catalog Service
-   `movement_type` --- VARCHAR(30), e.g. `RECEIPT`, `SALE`,
    `CUSTOMER_RETURN`, `DAMAGE`, `ADJUSTMENT`, `TRANSFER_IN`,
    `TRANSFER_OUT`
-   `quantity_delta` --- NUMERIC(12,3), signed physical stock change
-   `reference_type` --- VARCHAR(30), nullable
-   `reference_id` --- UUID, nullable
-   `reason` --- TEXT, nullable
-   `performed_by` --- UUID, nullable
-   `idempotency_key` --- VARCHAR(150), nullable
-   `created_at` --- TIMESTAMPTZ

**`stock_reservations`**

-   `id` --- UUID primary key
-   `order_id` --- UUID reference to the Order Service
-   `order_item_id` --- UUID reference to an order item
-   `branch_id` --- UUID reference to the Branch Service
-   `product_id` --- UUID reference to the Catalog Service
-   `quantity` --- NUMERIC(12,3), positive
-   `status` --- VARCHAR(20): `ACTIVE`, `CONFIRMED`, `RELEASED`,
    `EXPIRED`
-   `expires_at` --- TIMESTAMPTZ, nullable
-   `created_at` --- TIMESTAMPTZ
-   `updated_at` --- TIMESTAMPTZ

Stock reservations must be concurrency-safe and idempotent. A
reservation changes reserved quantity, not physical stock. Physical
stock should be adjusted when the corresponding business operation
actually changes stock.

#### Order Service

**`orders`**

-   `id` --- UUID primary key
-   `order_number` --- VARCHAR(30), unique
-   `customer_id` --- UUID reference to the User Service
-   `branch_id` --- UUID reference to the Branch Service
-   `order_type` --- VARCHAR(20), e.g. `ONLINE`, `POS`
-   `status` --- VARCHAR(30), e.g. `PENDING`, `CONFIRMED`, `PROCESSING`,
    `READY_FOR_DELIVERY`, `OUT_FOR_DELIVERY`, `COMPLETED`, `CANCELLED`
-   `currency` --- CHAR(3), e.g. `LKR`
-   `subtotal` --- NUMERIC(12,2)
-   `discount_amount` --- NUMERIC(12,2), default 0
-   `tax_amount` --- NUMERIC(12,2), default 0
-   `delivery_fee` --- NUMERIC(12,2), default 0
-   `total_amount` --- NUMERIC(12,2)
-   `shipping_address` --- JSONB, nullable, address snapshot
-   `customer_notes` --- TEXT, nullable
-   `placed_at` --- TIMESTAMPTZ, nullable
-   `cancelled_at` --- TIMESTAMPTZ, nullable
-   `created_at` --- TIMESTAMPTZ
-   `updated_at` --- TIMESTAMPTZ

**`order_items`**

-   `id` --- UUID primary key
-   `order_id` --- UUID foreign key to `orders.id`
-   `product_id` --- UUID reference to the Catalog Service
-   `product_name` --- VARCHAR(200), product name snapshot
-   `product_sku` --- VARCHAR(50), SKU snapshot
-   `unit_of_measure` --- VARCHAR(20)
-   `quantity` --- NUMERIC(12,3), positive
-   `unit_price` --- NUMERIC(12,2)
-   `discount_amount` --- NUMERIC(12,2), default 0
-   `tax_amount` --- NUMERIC(12,2), default 0
-   `line_total` --- NUMERIC(12,2)
-   `created_at` --- TIMESTAMPTZ

**`order_status_history`**

-   `id` --- UUID primary key
-   `order_id` --- UUID foreign key to `orders.id`
-   `from_status` --- VARCHAR(30), nullable for initial status
-   `to_status` --- VARCHAR(30)
-   `changed_by` --- UUID, nullable
-   `actor_type` --- VARCHAR(20), e.g. `CUSTOMER`, `STAFF`, `SYSTEM`
-   `reason` --- TEXT, nullable
-   `created_at` --- TIMESTAMPTZ

Use Java `BigDecimal` and PostgreSQL `NUMERIC` for monetary
calculations. Preserve product name, SKU, and unit price snapshots in
order items. Keep order status separate from payment status. The Payment
Service should own payment records when implemented.

## Getting Started

### Prerequisites

Install the tools needed for the service you are working on:

-   JDK 21
-   Visual Studio Code or another Java IDE
-   Git
-   A Supabase project with PostgreSQL access
-   Postman or an equivalent API client

Docker, RabbitMQ, and Redis are planned additions and are not required
to run the initial user CRUD service unless the current implementation
has subsequently added them.

### Run the current user service

The exact commands depend on the build configuration in `user-service`.
For a Maven project, a typical local workflow is:

1.  Clone the repository.
2.  Open the `user-service` directory in your IDE or terminal.
3.  Configure the database connection through environment variables.
4.  Run the Spring Boot application using the Maven wrapper or IDE.
5.  Test the available CRUD endpoints with Postman.

For example, on Windows:

``` powershell
cd user-service
.\mvnw.cmd spring-boot:run
```

If the project uses a different wrapper or configuration, follow the
files in the current repository. Confirm the server port and API routes
from the application's configuration and controller classes.

### Environment configuration

Use environment variables for database connection settings. The variable
names below are examples; align them with the current Spring Boot
configuration.

``` dotenv
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USERNAME=<database_username>
DB_PASSWORD=<database_password>
```

Do not commit real `.env` files, passwords, database URLs containing
credentials, JWT signing secrets, or provider API keys.

## Development Roadmap

### Phase 1 --- User Service foundation

-   Complete and test user CRUD.
-   Add request DTO validation.
-   Add consistent error handling.
-   Add password hashing and authentication.
-   Implement Spring Security and JWT if using backend-managed
    authentication.
-   Add role-based and resource-level authorization.
-   Document endpoints and test error cases.

### Phase 2 --- Branch Service

-   Create branch CRUD.
-   Add branch status and staff assignment concepts.
-   Establish service-owned database schema and credentials.
-   Test independent deployment and API behavior.

### Phase 3 --- Catalog Service

-   Create categories and products.
-   Add SKU/barcode validation and search.
-   Plan branch-specific pricing.

### Phase 4 --- Inventory Service

-   Implement stock levels and movement history.
-   Add safe stock reservation and release.
-   Handle concurrent requests and duplicate events.
-   Add low-stock rules.

### Phase 5 --- Order Service

-   Implement orders, order items, and status history.
-   Validate prices and calculate totals on the backend.
-   Coordinate stock reservation.
-   Add order cancellation and failure handling.

### Phase 6 --- Payment and Delivery

-   Integrate a payment provider's sandbox.
-   Track payment attempts and outcomes separately from order status.
-   Implement delivery assignments and delivery status updates.

### Phase 7 --- Client applications and POS

-   Build the management and customer web applications.
-   Build the delivery mobile application.
-   Implement cashier workflows and receipts for POS.

### Phase 8 --- Distributed infrastructure and operations

-   Introduce Docker Compose for local multi-service development.
-   Add RabbitMQ for suitable asynchronous events.
-   Add Redis only for well-defined caching or short-lived data use
    cases.
-   Introduce an API Gateway when useful.
-   Add automated tests, CI/CD, monitoring, structured logging, and
    health checks.
-   Add reporting and future supplier/procurement capabilities.

This roadmap is iterative. Implement and test a small working slice
before adding the next layer of complexity.

## Engineering Guidelines

-   Keep controllers thin; put business logic in service classes.
-   Use DTOs for request and response boundaries rather than exposing
    persistence entities directly.
-   Validate all incoming data on the server.
-   Use meaningful HTTP status codes and consistent error responses.
-   Keep configuration outside source code.
-   Use database migrations and version them with the service.
-   Write unit and integration tests for business rules.
-   Document REST APIs and expected error responses.
-   Use idempotency for operations that may be retried.
-   Avoid distributed transactions; design workflows for partial
    failure.
-   Do not let one service directly modify another service's database
    tables.
-   Keep audit records for important stock and order lifecycle changes.

## Security Notes

-   Hash passwords with an appropriate password encoder; never store
    plaintext passwords.
-   Validate authentication tokens and enforce authorization on the
    backend.
-   Restrict branch managers and staff to branches and operations they
    are permitted to access.
-   Keep secrets out of source control and frontend bundles.
-   Never expose privileged Supabase service-role credentials to client
    applications.
-   Validate payment-provider callbacks and avoid storing raw card
    details.
-   Apply least-privilege database permissions.
-   Use HTTPS in deployed environments.
-   Treat logs and audit records carefully; do not log passwords,
    tokens, or sensitive payment data.

## Contributing

This is an educational and portfolio project under active development.

1.  Create a branch for a focused change.
2.  Follow the existing service structure and coding conventions.
3.  Add or update tests.
4.  Update API or database documentation when contracts change.
5.  Open a pull request or merge the change according to the
    repository's workflow.

## License

No license has been specified yet. Add a `LICENSE` file before
presenting the project as open source or permitting reuse by others.
