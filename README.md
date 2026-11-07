# StorePulse Platform

RESTful backend of **StorePulse**, the IoT platform of VanguardTech for commercial galleries in Lima: safety
monitoring, utility consumption and billing information for gallery administrators and their tenants.

Built with Spring Boot 4, Spring Data JPA and MySQL, following Domain-Driven Design: one package per Bounded
Context, each with the same four layers.

| Repository | Role |
|---|---|
| [storepulse-web-application](https://github.com/VanguardTechIot/storepulse-web-application) | Web application of the gallery administrator (Angular) |
| [storepulse-mobile](https://github.com/VanguardTechIot/storepulse-mobile) | Tenant application (Flutter) |
| [storepulse-edge](https://github.com/VanguardTechIot/storepulse-edge) | Edge API that receives the telemetry of the IoT nodes and forwards it here |
| [storepulse-report](https://github.com/VanguardTechIot/storepulse-report) | Project report (strategic and tactical DDD, architecture) |

## Getting started

Requirements: JDK 21 (the project compiles on JDK 17 with `-Djava.version=17`), MySQL 8 and the Maven wrapper.

```bash
cp .env.example .env            # fill DB_* and JWT_SECRET
./mvnw spring-boot:run          # http://localhost:8080/api/v1
./mvnw test                     # unit, controller and architecture tests (H2, no MySQL needed)
```

| Variable | Purpose |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` (local MySQL, default) or `prod`. |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Database connection. Tables are generated from the aggregates (`ddl-auto=update`). |
| `JWT_SECRET`, `JWT_EXPIRATION_MINUTES` | Signing key (Base64, at least 256 bits) and lifetime of the access tokens. |
| `FIREBASE_CREDENTIALS_PATH`, `FIREBASE_MAIL_COLLECTION` | Delivery of password recovery codes through Firebase. |
| `CORS_ALLOWED_ORIGINS` | Origins allowed to call the API from a browser, separated by commas. |

OpenAPI documentation: `/swagger-ui.html` (JSON at `/v3/api-docs`). Every endpoint lives under `/api/v1`.
Sign-up, sign-in, password recovery and the documentation are public; everything else needs the JWT issued by
Identity and Access Management as `Authorization: Bearer <token>`.

## Architecture

```
com.vanguardtech.storepulse.platform.<context>/
  domain/
    model/aggregates|entities|valueobjects|commands|queries|events
    services/        command and query service contracts
    exceptions/      <Context>Error enum: its lowercase names are the error codes of the API
  application/
    internal/commandservices|queryservices|eventhandlers
    internal/outboundservices/acl   External<Other>Service: anti-corruption layer over another context's facade
    acl/             implementation of this context's facade
  infrastructure/
    persistence/jpa/repositories    Spring Data repositories
  interfaces/
    rest/controllers|resources|transform
    acl/             <Context>ContextFacade: the only entry point other contexts may use
```

| Package | Bounded Context | Responsibility |
|---|---|---|
| `iam` | Identity and Access Management | Accounts, roles, JWT, password recovery. Upstream of every other context. |
| `profilespreferences` | Profiles and Preferences | Profile of each user, created when the account is registered. |
| `propertymanagement` | Property Management | Commercial gallery, its stores and common areas, tenant invitations. |
| `propertycommunication` | Property Communication | Conversations, tenant notifications and announcements, communication logs, tenant assignments. |
| `assets` | Resource and Asset Management | Resources (stands, common areas) and the IoT devices installed in them. |
| `monitoring` | Service Execution and Monitoring | Telemetry and measurements received from the Edge, consumption per resource. |
| `safety` | Service Execution and Monitoring (safety) | Safety rules per sensor, device states, the response to each measurement and the alerts it triggers. |
| `dashboardanalytics` | Dashboard and Analytics | Consolidated summary for the administrator. |
| `utilitybilling` | Utility Billing | Utility bills per unit and billing disputes. |
| `subscriptionspayments` | Subscriptions and Payments | Plans, subscription of the gallery and its payments. |
| `shared` | Shared kernel | `AuditableAbstractAggregateRoot`, `DomainEvent`, business exceptions, the `/api/v1` prefix and the global error handler. |

Rules enforced by `ArchitectureTest` (ArchUnit) and followed by every context:

- The domain depends on nothing outside itself and the shared kernel.
- The application layer uses the domain contracts; controllers never touch repositories.
- A context reaches another one only through its `interfaces.acl` facade, wrapped in an `External<Other>Service`
  of its own; it never imports another context's domain, application or infrastructure.
- Business rules fail with `BusinessRuleException` (409) or `ResourceNotFoundException` (404). The message is the
  lowercase name of the `<Context>Error` value, which the web application uses as i18n key.
- Aggregates publish `DomainEvent`s with `registerEvent(...)`; they are delivered when the aggregate is saved and
  handled by `@EventListener`s in other contexts (for example, `UserRegistered` creates the profile and
  `UtilityBillIssued` notifies the tenant).

## Property Management

| Method and path | Purpose | Errors |
|---|---|---|
| `GET /galleries?administratorId=` | Gallery of an administrator (empty list when none) | |
| `POST /galleries` | Register the gallery of the authenticated administrator | 409 `gallery_already_registered` |
| `PUT /galleries/{id}` | Update name, address and total of units | 404 `gallery_not_found` |
| `GET /galleries/{id}/units` | Stores and common areas of the gallery, removed ones included | |
| `POST /galleries/{id}/units` | Register a store (`STORE`) or a common area (`COMMON_AREA`) | 409 `duplicated_unit_code`, `duplicated_common_area_name` |
| `GET /units/{id}` | A unit | 404 `unit_not_found` |
| `PATCH /units/{id}` | Edit data, change `status` (AVAILABLE, OCCUPIED, MAINTENANCE) or remove with `active=false` | 409 `unit_occupied`, `unit_not_available` |
| `POST /units/{id}/invite` | Send an invitation link to the tenant of a store | 409 `unit_not_available`, `common_area_without_tenant`, `invalid_email` |
| `GET /tenant-invitations?galleryId=` | Invitations of the gallery | |
| `GET /tenant-invitations/by-token/{token}` | Invitation behind a link | 404 `invitation_not_found` |
| `PATCH /tenant-invitations/{id}` | `status=REVOKED` or `status=ACCEPTED` | 409 `invitation_not_pending`, `invitation_expired` |

Rules: one gallery per administrator; unit codes unique within the gallery among active units (stores in upper
case, common areas by name); a store is removed only when nobody occupies it; invitations last 7 days and a new
one revokes the pending one of the same store. The facade `PropertyManagementContextFacade` lets other contexts
read units and flip their occupancy.

## Property Communication

| Method and path | Purpose | Errors |
|---|---|---|
| `GET /conversations/administrators/{id}`, `GET /conversations/tenants/{id}` | Conversations, most recent first | |
| `POST /conversations` | Start a conversation | 409 `invalid_subject` |
| `PATCH /conversations/{id}/resolve` | Resolve a conversation | 409 `conversation_resolved` |
| `GET /conversations/{id}/messages`, `POST /conversations/{id}/messages` | Thread and new message with attachments (PDF, JPEG, PNG, WebP up to 2 MB) | 409 `conversation_resolved`, `invalid_message_content`, `invalid_attachment`, `attachment_too_large` |
| `GET /tenant-notifications?galleryAdministratorId=` or `?tenantId=` | Notifications sent or received | |
| `POST /tenant-notifications` | Notify one tenant | 409 `invalid_notification_title`, `invalid_notification_body` |
| `POST /tenant-notifications/announcements` | Announcement or incident notice to the tenants of some stores or of the whole gallery | 409 `no_recipients` |
| `PATCH /tenant-notifications/{id}` | `status=PENDING` retries a failed one; `read=true` marks it as read | 409 `notification_not_failed` |
| `GET /communication-logs?galleryAdministratorId=`, `POST /communication-logs` | Trace of every communication | |
| `GET /tenant-assignments?galleryAdministratorId=` | Assignments of the gallery | |
| `POST /tenant-assignments` | Assign a tenant to a free store; the store becomes occupied | 404 `tenant_not_found`, `unit_not_found`; 409 `unit_already_assigned`, `unit_not_available` |
| `PATCH /tenant-assignments/{id}/terminate` | End an assignment; the store becomes available | 409 `assignment_not_active` |

Every notification is delivered through `NotificationDispatcher` (in-app by default) and leaves a communication
log. The context also reacts to `UtilityBillIssuedEvent` by notifying the tenant of the billed store, and exposes
`PropertyCommunicationContextFacade` so other contexts (for example Safety) can notify a tenant without knowing
the notification model.

## Conventions

- Branches: `feature/<name>` from `develop`; releases through `release/x.y.z` into `main`.
- Commits: Conventional Commits with the context as scope, for example `feat(property-communication): ...`.
- Code, identifiers and comments in English. Dates are `Instant` (ISO-8601 in JSON); identifiers are numeric.
