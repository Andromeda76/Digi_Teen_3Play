# Authentication Service

Authentication service for the Digiteen backend assessment.

The service is responsible for user registration, credential management, authentication, password hashing, and JWT access-token issuance. Other backend services can authenticate requests independently by validating the JWT issued by this service.

---

## Overview

The authentication service provides the identity boundary for the application.

Its responsibilities are intentionally limited to authentication and user identity:

* User registration
* User persistence
* Secure password storage
* Credential validation
* Spring Security authentication
* JWT access-token generation
* User authority management

Wallet, payment, transfer, and transaction concerns are intentionally kept outside this service.

The architecture allows the Payment Service to validate an authenticated request without requiring a synchronous authentication-service call for every wallet operation.

---

## Architecture

The authentication flow is based on Spring Security and JWT.

```text
                         ┌──────────────────────┐
                         │      Client          │
                         └──────────┬───────────┘
                                    │
                              Register / Login
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │ Authentication       │
                         │ Service               │
                         │                      │
                         │ Spring Security      │
                         │ UserDetailsService   │
                         │ PasswordEncoder      │
                         │ AuthenticationMgr   │
                         │ JWT Generator        │
                         └──────────┬───────────┘
                                    │
                               JWT Access Token
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   Payment Service    │
                         │                      │
                         │ JWT validation       │
                         │ Wallet ownership     │
                         └──────────────────────┘
```

The authentication service therefore acts as the issuer of application identity, while downstream services act as JWT resource servers.

---

## Authentication Flow

### Registration

A new user is registered through the authentication service.

The password is never stored as plaintext.

```text
Client
  │
  │ registration request
  ▼
AuthenticationController
  │
  ▼
PersonService
  │
  ├── validate user data
  ├── encode password
  └── persist Person
       │
       ▼
     Database
```

The stored password is the result of the configured password encoder rather than the original password supplied by the client.

---

### Login

The login flow delegates credential verification to Spring Security rather than manually comparing passwords inside the controller.

```text
Client
  │
  │ email + password
  ▼
AuthenticationController
  │
  ▼
AuthenticationManager
  │
  ▼
CustomUserDetailService
  │
  ▼
PersonRepository
  │
  ▼
PasswordEncoder
  │
  └── credentials valid
          │
          ▼
      JwtGenerator
          │
          ▼
     Access Token
```

This keeps authentication responsibilities inside the Spring Security authentication pipeline.

---

## JWT Authentication

After successful authentication, the service generates a signed JWT access token.

The current implementation uses:

* HMAC-based JWT signing
* HS256
* A shared Base64-encoded secret
* An expiration time
* User identity information
* User authorities

A simplified token payload has the following structure:

```json
{
  "email": "user@example.com",
  "authorities": [
    "ROLE_USER"
  ],
  "iat": 1234567890,
  "exp": 1234569690
}
```

The token is returned to the client after successful authentication.

The client then sends it to protected services using:

```http
Authorization: Bearer <JWT>
```

---

## Why JWT?

The authentication and payment services are separate applications.

Instead of making the Payment Service call the Authentication Service for every protected request, the authentication service issues a signed token that can be independently validated by downstream services.

```text
Login
  │
  ▼
Authentication Service
  │
  └── JWT
       │
       ├──────────────► Payment Service
       │
       └──────────────► Other protected services
```

This keeps authentication synchronous at login while allowing subsequent service requests to remain independent.

---

## Spring Security

Authentication is implemented using Spring Security's standard authentication architecture.

The main components are:

### `UserDetailsService`

`CustomUserDetailService` loads the application's user from the database and converts the domain user into Spring Security's `UserDetails` representation.

The user's email is used as the authentication username.

Conceptually:

```java
return new User(
        person.getPersonInfo().getEmail(),
        person.getPassword(),
        AuthorityUtils.createAuthorityList("ROLE_USER")
);
```

This allows Spring Security to perform credential verification using its normal authentication mechanism.

---

### `AuthenticationManager`

The login operation delegates authentication to Spring Security's `AuthenticationManager`.

This avoids implementing password comparison manually in the controller.

The authentication pipeline is responsible for:

1. Loading the user
2. Obtaining the stored password hash
3. Comparing the supplied password
4. Establishing the authenticated principal
5. Returning the authentication result

Only after successful authentication is the JWT generated.

---

### `PasswordEncoder`

Passwords are encoded before persistence.

The service never needs to store or retrieve the original plaintext password.

The encoder is also used by Spring Security during authentication to compare:

```text
raw password
      │
      ▼
PasswordEncoder
      │
      ▼
stored password hash
```

---

## Domain Model

The authentication service uses a `Person` domain model.

The personal information is separated into `PersonInfo`, which represents the user's identifying information.

The project therefore avoids placing all user information directly into a single large entity.

Conceptually:

```text
Person
 ├── id
 ├── password
 └── PersonInfo
      ├── email
      └── ...
```

The authentication-specific password remains part of the `Person` entity, while personal information is represented separately.

---

## Project Structure

The project follows a layered Spring architecture.

```text
src/main/java/
└── digiteen3play/
    ├── controller/
    │   ├── AuthenticationController
    │   └── PersonController
    │
    ├── model/
    │   ├── AbstractModel
    │   ├── Logging
    │   ├── Person
    │   └── PersonInfo
    │
    ├── pvm/
    │   ├── AuthenticationResponsePVM
    │   ├── LoginPVM
    │   ├── PersonInfoPVM
    │   └── PersonPVM
    │
    ├── repository/
    │   └── PersonIRepository
    │
    ├── service/
    │   ├── PersonService
    │   └── security/
    │       ├── AccessTokenService
    │       └── CustomUserDetailService
    │
    └── setting/
        ├── JwtGenerator
        └── PasswordConfig
```

The package structure separates:

* HTTP/API concerns
* Domain models
* Request/response models
* Persistence
* Business logic
* Security-specific components
* JWT/password configuration

---

## API Responsibilities

### Authentication

The authentication controller exposes the login functionality.

```http
POST /login/authentication
```

The client supplies its credentials and receives an access token after successful authentication.

The token is subsequently used when accessing protected downstream services.

---

### Person Registration

User creation is handled separately from authentication itself.

The registration flow creates the `Person` domain object and securely encodes the supplied password before persistence.

---

## Configuration

Security-sensitive configuration is kept outside the normal source-controlled application configuration.

The project uses externalized configuration for sensitive values such as the JWT secret.

The JWT secret is therefore not intended to be hardcoded into Java source code.

A simplified configuration concept is:

```yaml
jwt:
  secret: <BASE64_ENCODED_SECRET>
```

The actual secret should be supplied through the local/environment-specific configuration.

Sensitive local security configuration is excluded from Git.

---

## Security Boundaries

The authentication service owns:

```text
User identity
Credentials
Password hashing
Authentication
JWT issuance
Authorities
```

The Payment Service owns:

```text
Wallets
Balances
Transfers
Transactions
Wallet concurrency
```

This separation prevents authentication logic from becoming coupled to financial operations.

For example, the Payment Service does not need to know the user's password.

It only needs a valid authenticated identity from the JWT.

---

## Service-to-Service Authentication

The intended communication model is:

```text
                    ┌──────────────────────┐
                    │ Authentication       │
                    │ Service              │
                    └──────────┬───────────┘
                               │
                         Issues JWT
                               │
                               ▼
                    ┌──────────────────────┐
                    │ Payment Service      │
                    │                      │
                    │ JWT Resource Server  │
                    └──────────────────────┘
```

The Payment Service validates the JWT locally using the shared signing secret.

This avoids introducing an authentication-service network dependency into every wallet request.

---

## Design Decisions

### Why Spring Security instead of manual password validation?

Authentication should be handled by a dedicated security framework rather than custom controller logic.

Spring Security provides the authentication pipeline, credential validation, principal creation, and authorization infrastructure.

---

### Why use `UserDetailsService`?

The application uses its own `Person` entity, while Spring Security expects a `UserDetails` representation.

`CustomUserDetailService` acts as the adapter between the application's persistence model and Spring Security.

```text
Person
  │
  ▼
CustomUserDetailService
  │
  ▼
UserDetails
  │
  ▼
Spring Security
```

---

### Why use JWT?

JWT allows the authentication service to issue an independently verifiable identity token.

Protected services can validate the token without requesting the user's password or making an authentication request for every operation.

---

### Why HS256?

The assessment requires a practical authentication implementation within a limited development window.

A shared HMAC secret provides a straightforward implementation for the current two-service architecture.

The design intentionally avoids introducing unnecessary infrastructure such as:

* OAuth2 Authorization Server
* Keycloak
* Redis
* Refresh-token infrastructure
* RSA/EC key management
* Social login providers

These can be introduced in a larger production architecture when their operational requirements justify the additional complexity.

---

## Error Handling

Authentication failures should not expose sensitive credential information.

The authentication flow distinguishes between successful authentication and authentication failure without returning the user's stored password or other security-sensitive information.

Passwords are never returned as part of authentication responses.

---

## Running the Service

The authentication service runs independently from the Payment Service.

Default application endpoint:

```text
http://localhost:8080
```

The Payment Service runs separately:

```text
http://localhost:8082
```

This allows both services to be developed, tested, and started independently.

---

## Example Authentication Flow

### 1. Authenticate

```http
POST http://localhost:8080/login/authentication
Content-Type: application/json
```

Request:

```json
{
  "email": "user@example.com",
  "password": "your-password"
}
```

Response contains the JWT access token.

### 2. Use the token

The client sends:

```http
Authorization: Bearer <JWT>
```

to protected endpoints of the Payment Service.

### 3. Payment Service validates the token

The Payment Service validates:

```text
Signature
Expiration
Authentication
Authorities
User identity
```

The authenticated identity is then used to determine which wallet belongs to the current user.

---

## Git Development History

The implementation was developed incrementally rather than being delivered as a single large commit.

The main milestones included:

```text
chore: initialize authentication service
chore: update project configuration
chore: ignore local security configuration
...
Add authentication with Spring Security
refactor: update application package structure
```

This history reflects the progression from project initialization and configuration to persistence, security, and final package organization.

The incremental history also makes the architectural evolution easier to review.

---

## Scope

### Implemented

* User persistence
* Person / PersonInfo domain model
* User registration
* Password hashing
* Spring Security authentication
* `UserDetailsService`
* `AuthenticationManager`
* JWT generation
* JWT-based identity propagation
* User authorities
* Protected authentication flow
* Externalized sensitive security configuration
* Layered project structure

### Intentionally outside this service

* Wallet management
* Wallet balances
* Deposits
* Withdrawals
* Transfers
* Transaction concurrency
* Payment events
* Outbox processing
* Event consumers

These responsibilities belong to the Payment Service and its associated components.

---

## Technology Stack

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* JWT
* Maven
* SQL Server

---

## Architectural Summary

The authentication service establishes a clear security boundary:

```text
                    Authentication Service
                    ───────────────────────
                           │
                    Authenticate user
                           │
                    Verify password
                           │
                      Generate JWT
                           │
                           ▼
                         Client
                           │
                    Authorization header
                           │
                           ▼
                     Payment Service
                           │
                    Validate JWT locally
                           │
                    Identify current user
                           │
                           ▼
                     Wallet operation
```

The result is a small, independently deployable authentication service with a clear responsibility: **authenticate users and provide verifiable identity to the rest of the system.**
