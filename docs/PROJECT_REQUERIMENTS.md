# 📋 Complete Project Requirements

## 📌 Overview

This document defines the functional, technical, security, testing, infrastructure, and deployment requirements for the system.

The project is divided into **three main phases**, prioritized as follows:

- 🟢 **MUST HAVE** — Essential functionality.
- 🟡 **SHOULD HAVE** — Advanced functionality and improvements.
- 🔴 **OPTIONAL / BONUS** — Additional recommended functionality.

---

# 🏗️ PHASE 1: SYSTEM FUNDAMENTALS

**Priority:** 🟢 MUST HAVE
**Estimated duration:** 1.5 – 2 weeks

---

## 1.1 User Management and Authentication

| ID       | Requirement              | Description                                                        | Acceptance Criteria                                                                                                                                                  |
| -------- | ------------------------ | ------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R001** | User Registration        | Allow new users to register with basic information.                | - Endpoint `POST /api/auth/register`<br>- Validates unique email<br>- Encrypts password using BCrypt<br>- Stores name, email, phone, password, and registration date |
| **R002** | Login and JWT Generation | Authenticate users and generate an access token.                   | - Endpoint `POST /api/auth/login`<br>- Validates credentials<br>- Returns JWT with claims (`userId`, `roles`)<br>- Expiration time: 24 hours                         |
| **R003** | Refresh Token            | Allow users to renew their access token without re-authenticating. | - Endpoint `POST /api/auth/refresh`<br>- Validates refresh token<br>- Generates a new access token                                                                   |

---

## 1.2 Account Management

| ID       | Requirement         | Description                                             | Acceptance Criteria                                                                                                                                                  |
| -------- | ------------------- | ------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R004** | Create Bank Account | Allow an authenticated user to create a bank account.   | - Endpoint `POST /api/accounts`<br>- Assigns a unique account number (e.g., 10 digits)<br>- Initial balance: 0 or defined amount<br>- Account type: Savings/Checking |
| **R005** | List Accounts       | Allow users to view all their accounts.                 | - Endpoint `GET /api/accounts`<br>- Returns a list of accounts with balances<br>- Only visible to the authenticated user                                             |
| **R006** | Check Balance       | Allow users to check the balance of a specific account. | - Endpoint `GET /api/accounts/{accountId}/balance`<br>- Verifies account ownership<br>- Returns available balance                                                    |

---

## 1.3 Transfer Core

| ID       | Requirement               | Description                                        | Acceptance Criteria                                                                                                                                                                                           |
| -------- | ------------------------- | -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R007** | Internal Account Transfer | Transfer funds between accounts within the system. | - Endpoint `POST /api/transfers`<br>- Validates source and destination accounts<br>- Verifies sufficient balance<br>- Updates both accounts within an ACID transaction<br>- Generates a unique transaction ID |
| **R008** | Transaction History       | Retrieve account transaction history.              | - Endpoint `GET /api/accounts/{accountId}/transactions`<br>- Supports pagination (`page`, `size`)<br>- Filters by date, type, and amount                                                                      |

---

## 1.4 Security and Middleware

| ID       | Requirement               | Description                                    | Acceptance Criteria                                                                                                                               |
| -------- | ------------------------- | ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R009** | JWT Authentication Filter | Validate the token on every protected request. | - Intercepts requests containing `Authorization: Bearer <token>`<br>- Validates signature and expiration<br>- Extracts user and roles             |
| **R010** | Global Exception Handling | Provide consistent error responses.            | - Implemented using `@ControllerAdvice`<br>- Standard JSON response format<br>- Appropriate HTTP status codes (`400`, `401`, `403`, `404`, `500`) |

---

# ⚡ PHASE 2: ADVANCED FUNCTIONALITY

**Priority:** 🟡 SHOULD HAVE
**Estimated duration:** 2 – 2.5 weeks

---

## 2.1 Idempotency and Consistency

| ID       | Requirement          | Description                                        | Acceptance Criteria                                                                                                                                                             |
| -------- | -------------------- | -------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R011** | Transfer Idempotency | Prevent duplicate processing of the same transfer. | - Endpoint accepts the `Idempotency-Key` header<br>- Stores the key for 24 hours<br>- Returns the previous response when the same key is reused<br>- Prevents duplicate charges |

---

## 2.2 Beneficiary Management

| ID       | Requirement        | Description                                       | Acceptance Criteria                                                                                                                                               |
| -------- | ------------------ | ------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R012** | Add Beneficiary    | Save third-party accounts for frequent transfers. | - Endpoint `POST /api/beneficiaries`<br>- Associates the beneficiary with the authenticated user<br>- Stores alias, account number, account holder name, and bank |
| **R013** | List Beneficiaries | Allow users to view their saved beneficiaries.    | - Endpoint `GET /api/beneficiaries`<br>- Returns only the authenticated user's beneficiaries                                                                      |
| **R014** | Delete Beneficiary | Remove a beneficiary from the user's list.        | - Endpoint `DELETE /api/beneficiaries/{id}`<br>- Verifies ownership                                                                                               |

---

## 2.3 Business Rules

| ID       | Requirement          | Description                                                         | Acceptance Criteria                                                                                                    |
| -------- | -------------------- | ------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **R015** | Daily Transfer Limit | Configure a maximum transfer limit per account and per day.         | - Configurable limit (e.g., `$5,000,000`)<br>- Rejects transfers that exceed the limit<br>- Resets at midnight         |
| **R016** | Recipient Validation | Verify the destination account exists before performing a transfer. | - Endpoint `GET /api/accounts/validate?accountNumber=xxx`<br>- Returns the account holder's name if the account exists |

---

## 2.4 Asynchronous Notification System — Kafka

| ID       | Requirement               | Description                                                    | Acceptance Criteria                                                                                                                                                                                               |
| -------- | ------------------------- | -------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **R017** | Transfer Event Publishing | The transfer service publishes events to Kafka.                | - Configures the `transaction-events` topic<br>- Publishes an event after completing the transfer<br>- Event includes `transactionId`, `amount`, `sourceAccount`, `destinationAccount`, `timestamp`, and `userId` |
| **R018** | Email Event Consumer      | A notification service consumes events and sends emails.       | - Subscribed to the `transaction-events` topic<br>- Sends an email containing transaction details<br>- Uses JavaMailSender or SendGrid API                                                                        |
| **R019** | SMS Event Consumer        | A notification service consumes events and sends SMS messages. | - Subscribed to the `transaction-events` topic<br>- Sends an SMS containing a transaction summary<br>- Uses Twilio or a local SMS provider API                                                                    |
| **R020** | Failure Retries           | Automatically handle notification failures.                    | - Configures a retry mechanism<br>- Uses a Dead Letter Queue (DLQ) for failed events<br>- Logs failed attempts                                                                                                    |

---

## 2.5 Additional Notifications

| ID       | Requirement          | Description                                      | Acceptance Criteria                                                     |
| -------- | -------------------- | ------------------------------------------------ | ----------------------------------------------------------------------- |
| **R021** | Welcome Notification | Send an email or SMS when a new user registers.  | - Consumes a registration event from Kafka<br>- Sends a welcome message |
| **R022** | Security Alert       | Notify users when they log in from a new device. | - Detects new IP/device<br>- Sends a push notification or email         |

---

# 🔧 PHASE 3: QUALITY, DOCUMENTATION AND DEPLOYMENT

**Priority:** 🟢 MUST HAVE / 🟡 SHOULD HAVE
**Estimated duration:** 1 – 1.5 weeks
**Note:** This phase can be developed in parallel with the previous phases.

---

## 3.1 Documentation

| ID       | Requirement                   | Description                 | Acceptance Criteria                                                                                                                      |
| -------- | ----------------------------- | --------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| **R023** | OpenAPI/Swagger Documentation | Document all API endpoints. | - UI available at `/swagger-ui.html`<br>- Specification available at `/v3/api-docs`<br>- Documents request/response models with examples |

---

## 3.2 Testing

| ID       | Requirement       | Description                                               | Acceptance Criteria                                                                                           |
| -------- | ----------------- | --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| **R024** | Unit Tests        | Cover services and business logic.                        | - JUnit 5 + Mockito<br>- Minimum 70% coverage in the service layer<br>- Tests for success and error scenarios |
| **R025** | Integration Tests | Test interactions with the database and other components. | - Testcontainers for PostgreSQL<br>- JPA repository tests<br>- Kafka integration tests using Embedded Kafka   |
| **R026** | API Tests         | Test complete API endpoints.                              | - Spring Boot Test with `@WebMvcTest`<br>- MockMvc for simulating requests                                    |
| **R027** | Load Testing      | Simulate multiple concurrent transfers.                   | - JMeter or Gatling<br>- Scenario: 100 simultaneous users<br>- **Optional**                                   |

---

## 3.3 Infrastructure and Deployment

| ID       | Requirement                  | Description                                  | Acceptance Criteria                                                                                                                           |
| -------- | ---------------------------- | -------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| **R028** | Docker Containerization      | Create Docker images for all services.       | - Dockerfile for each service<br>- Optimized images using multi-stage builds                                                                  |
| **R029** | Docker Compose Orchestration | Allow the entire system to run locally.      | - Services: application, PostgreSQL, Kafka, and Zookeeper<br>- Redis optional for caching<br>- Command: `docker-compose up`                   |
| **R030** | Configuration Profiles       | Separate configuration by environment.       | - `application-dev.yml`: local configuration, H2/PostgreSQL<br>- `application-prod.yml`: production configuration using environment variables |
| **R031** | Centralized Logging          | Configure structured and consistent logging. | - Log4j2 or SLF4J with Logback<br>- Levels: `DEBUG`, `INFO`, `WARN`, `ERROR`<br>- Includes Correlation ID for traceability                    |

---

## 3.4 CI/CD

**Priority:** 🟡 BONUS — Optional but recommended.

| ID       | Requirement                     | Description                             | Acceptance Criteria                                                                                            |
| -------- | ------------------------------- | --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **R032** | CI Pipeline with GitHub Actions | Automate testing and the build process. | - Triggered on pushes to `main`<br>- Runs unit and integration tests<br>- Builds the JAR artifact              |
| **R033** | CD Pipeline                     | Automate application deployment.        | - Deploys to AWS, Heroku, Render, or another platform<br>- Uses secure environment variables<br>- **Optional** |

---

# 📊 Priority Matrix

| Priority                       | Phase                  | Requirements |
| ------------------------------ | ---------------------- | ------------ |
| 🟢 **MUST HAVE**               | System Fundamentals    | R001 – R010  |
| 🟡 **SHOULD HAVE**             | Advanced Functionality | R011 – R022  |
| 🟢 **MUST HAVE / SHOULD HAVE** | Quality and Deployment | R023 – R031  |
| 🟡 **BONUS / OPTIONAL**        | CI/CD                  | R032 – R033  |

---

# 🗺️ Recommended Implementation Order

```text
PHASE 1
│
├── R001–R003  → Authentication
├── R004–R006  → Accounts
├── R007–R008  → Transfers
└── R009–R010  → Security
          │
          ▼
PHASE 2
│
├── R011       → Idempotency
├── R012–R014  → Beneficiaries
├── R015–R016  → Business Rules
├── R017–R020  → Kafka + Notifications
└── R021–R022  → Additional Notifications
          │
          ▼
PHASE 3
│
├── R023       → OpenAPI / Swagger
├── R024–R027  → Testing
├── R028–R031  → Docker + Configuration + Logging
└── R032–R033  → CI/CD
```

---

# 🔐 Git and Branching Rules

To maintain a controlled deployment workflow, the recommended branch flow is:

```text
feature/* ────────┐
                  ▼
                 dev
                  │
                  ▼
                main
```

## `main` Branch Protection

The `main` branch should be protected using a **GitHub Ruleset**.

### Ruleset

**Recommended name:**

> `Only dev → main`

**Enforcement:**

> `Active`

### Recommended Rules

- Direct pushes to `main` are not allowed.
- Pull Requests are required.
- CI checks must pass before merging.
- Force pushes are blocked.
- Branch deletion is blocked.
- Changes targeting `main` must originate from `dev`.
- A GitHub Actions check must validate that the Pull Request's `head branch` is `dev`.

Expected flow:

```text
feature/login
      │
      ▼
     dev
      │
      ▼
    main
```

Not allowed:

```text
feature/login ──────► main ❌
hotfix/* ───────────► main ❌
```

---

# 🎯 Final Goal

After completing all requirements, the system should provide:

- JWT-based authentication and authorization.
- User and bank account management.
- Internal transfers using ACID transactions.
- Transaction history and movement tracking.
- Idempotency for financial operations.
- Beneficiary management.
- Transfer limits and business rules.
- Asynchronous communication using Kafka.
- Email and SMS notifications.
- Centralized exception handling.
- OpenAPI/Swagger documentation.
- Unit, integration, and API testing.
- Docker containerization.
- Reproducible local environments using Docker Compose.
- Environment-specific configuration.
- Structured logging and traceability using Correlation IDs.
- CI/CD pipelines using GitHub Actions.
- Controlled Git workflow using `dev → main`.
