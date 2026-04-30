# 🔐 Security & Authentication

> **SmartTourism** — Stateless JWT-based authentication with Google OAuth2 support.

---

## Table of Contents

- [Security Model Overview](#security-model-overview)
- [Authentication Providers](#authentication-providers)
- [Public vs Protected Endpoints](#public-vs-protected-endpoints)
- [Roles & Authorization](#roles--authorization)
- [Authentication Endpoints](#authentication-endpoints)
- [JWT Structure](#jwt-structure)
- [Request Authorization Flow](#request-authorization-flow)
- [Using Protected Endpoints](#using-protected-endpoints)
- [Example Workflows](#example-workflows)
- [Security Notes](#security-notes)
- [Environment Configuration](#environment-configuration)
- [File References](#file-references)

---

## Security Model Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                     SmartTourism Security Model                     │
├─────────────────────────┬───────────────────────────────────────────┤
│  Auth Method            │  Stateless JWT  (Bearer <token>)          │
│  Session Management     │  STATELESS — no server-side sessions      │
│  CSRF Protection        │  Disabled  (appropriate for token APIs)   │
│  Password Hashing       │  BCrypt                                   │
│  Auth Providers         │  PASSWORD  |  GOOGLE OAuth2               │
└─────────────────────────┴───────────────────────────────────────────┘
```

---

## Authentication Providers

```
                    ┌───────────────────────────────┐
                    │         Auth Providers         │
                    └───────────────────────────────┘
                           /                \
              ┌────────────────┐    ┌──────────────────┐
              │   PASSWORD     │    │   GOOGLE OAuth2   │
              │  (Email/Pass)  │    │   (Social Login)  │
              └────────────────┘    └──────────────────┘
                     │                       │
              BCrypt hashing          OAuth2 Token Exchange
              Email verification      Auto-enabled on signup
              Manual enable           USER role assigned
```

---

## Public vs Protected Endpoints

### ✅ Public (No Auth Required)

| Endpoint | Description |
|---|---|
| `/api/auth/**` | All authentication endpoints |
| `/swagger-ui.html` | Swagger UI |
| `/swagger-ui/**` | Swagger resources |
| `/v3/api-docs/**` | OpenAPI specification |
| `/swagger-resources/**` | Swagger resources |
| `/webjars/**` | Webjars |

### 🔒 Protected (Valid JWT Required)

| Endpoint | Required Role |
|---|---|
| `/api/destinations/**` | `ADMIN` or `AGENCY` |
| `/api/agency/**` | `ADMIN` or `AGENCY` |
| `/api/admin/**` | `ADMIN` only |
| All other routes | Any authenticated user |

---

## Roles & Authorization

```
┌──────────────────────────────────────────────────────────┐
│                    Role Hierarchy                         │
│                                                          │
│   ADMIN ──────────────────────────────────────────────── │
│   │  Full system access                                  │
│   │  Manage users, agencies, content                     │
│   │                                                      │
│   AGENCY ──────────────────────────────────────────────  │
│   │  Manage own agency and tours                         │
│   │  Must be APPROVED by ADMIN to login                  │
│   │                                                      │
│   USER ─────────────────────────────────────────────── │
│      Browse tours and make bookings                      │
│      Default role for new registrations                  │
└──────────────────────────────────────────────────────────┘
```

> **Note:** New users registering via `PASSWORD` or `GOOGLE` provider receive **USER** role by default.

---

## Authentication Endpoints

### 1️⃣ Register — New User (PASSWORD Provider)

```
POST /api/auth/register
```

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123!",
  "role": "USER"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "USER"
}
```

**Flow:**
```
Register Request
      │
      ▼
BCrypt password hash
      │
      ▼
Save user (enabled = false)
      │
      ▼
Send verification email
      │
      ▼
Return JWT token
```

> ⚠️ Account starts **disabled** until email is verified.

---

### 2️⃣ Register — Agency

```
POST /api/auth/register
```

**Request Body:**
```json
{
  "fullName": "Agency Owner",
  "email": "agency@example.com",
  "password": "SecurePassword123!",
  "role": "AGENCY",
  "agencyName": "Amazing Tours Inc.",
  "agencyAddress": "123 Main St, City",
  "agencyPhone": "+1234567890",
  "description": "We offer the best tours"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "AGENCY"
}
```

> ⚠️ Agency starts with **PENDING** status and must be **approved by ADMIN** before login is allowed.

---

### 3️⃣ Login (PASSWORD Provider)

```
POST /api/auth/login
```

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "USER"
}
```

**Login Flow:**
```
Login Request
      │
      ▼
AuthenticationManager validates credentials
      │
      ├── AGENCY? → Check status = APPROVED
      │
      ▼
Revoke all previous tokens for user
      │
      ▼
Issue new JWT token
      │
      ▼
Save token to database
      │
      ▼
Return token + role
```

**Error Cases:**

| Scenario | Result |
|---|---|
| Invalid credentials | `401 Unauthorized` |
| User not found | `RuntimeException` |
| Agency not approved | `"Agency isn't approved yet"` |

---

### 4️⃣ Login via Google OAuth2

```
GET /login/oauth2/authorization/google
Redirect: http://localhost:8080/login/oauth2/code/google
```

**OAuth2 Flow:**
```
User clicks "Sign in with Google"
      │
      ▼
Redirect → Google Login Page
      │
      ▼
User authenticates with Google
      │
      ▼
Google sends OAuth2 token
      │
      ▼
CustomOAuth2UserService checks if user exists
      ├── New user? → Create account (USER role, enabled = true)
      └── Existing?  → Load existing user
      │
      ▼
OAuth2SuccessHandler generates JWT
      │
      ▼
Return JWT token (same format as PASSWORD provider)
```

---

### 5️⃣ Email Verification

```
GET /api/auth/verify-email?token={token}
```

**Response:**
```json
{
  "message": "Email verified successfully!"
}
```

---

### 6️⃣ Resend Verification Email

```
POST /api/auth/resend-verification?email={email}
```

**Response:**
```json
{
  "message": "Verification email was resent"
}
```

---

### 7️⃣ Logout

```
POST /api/auth/logout
```

**Process:**
1. Marks current token as `expired = true` and `revoked = true` in database
2. Clears Spring Security context
3. Returns `200 OK`

> ✅ After logout, using the old token will return `401 Unauthorized`.

---

## JWT Structure

```
┌─────────────────────────────────────────────────────┐
│                    JWT Token                        │
├──────────────┬──────────────────────────────────────┤
│   Header     │  { "alg": "HS256", "typ": "JWT" }    │
├──────────────┼──────────────────────────────────────┤
│   Payload    │  sub  → User email                   │
│              │  iat  → Issued timestamp              │
│              │  exp  → Expiry (issued + 24h)         │
├──────────────┼──────────────────────────────────────┤
│   Signature  │  HMACSHA256(header + payload, secret) │
└──────────────┴──────────────────────────────────────┘

Token TTL:   24 hours (86,400 seconds)
Algorithm:   HS256 (HMAC SHA-256)
Secret Key:  application.security.jwt.secret-key property
```

---

## Request Authorization Flow

```
Client Request
      │
      ▼
┌─────────────────────────┐
│  JwtAuthenticationFilter │
└─────────────────────────┘
      │
      ├── Path = /api/auth/** ?
      │         └── YES → Skip filter ──────────────────────► Endpoint
      │
      ├── Authorization header missing or not "Bearer"?
      │         └── YES → Continue unauthenticated ──────────► 401
      │
      └── Has valid "Bearer <token>" header
                │
                ▼
        ┌──────────────────┐
        │    JwtService     │
        │  validate token  │
        └──────────────────┘
                │
                ├── Extract email from JWT
                ├── Verify signature & expiration
                ├── Load UserDetails from DB
                └── Check token not revoked in DB
                │
                ├── VALID → Set auth in SecurityContext
                │               │
                │               ▼
                │         Protected Endpoint
                │         └── Process request ✅
                │
                └── INVALID → 401 Unauthorized ❌
```

---

## Using Protected Endpoints

Include the JWT token in the `Authorization` header on every request:

```http
GET /api/destinations HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Unauthorized Response (401):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is expired or invalid.",
  "timestamp": "2026-04-30T10:30:00"
}
```

---

## Example Workflows

### 🚀 Complete Registration & Protected Endpoint Flow

**Step 1 — Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "SecurePassword123!",
    "role": "USER"
  }'
```

**Step 2 — Verify Email:**  
_(User clicks link sent to their email)_
```bash
curl http://localhost:8080/api/auth/verify-email?token={verification-token}
```

**Step 3 — Login & Get Token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'
```

**Step 4 — Call Protected Endpoint:**
```bash
curl -X GET http://localhost:8080/api/tours \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Step 5 — Logout:**
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### 🌐 Google OAuth2 Flow

```
1. User visits:  /login/oauth2/authorization/google
                         │
                         ▼
2. Redirected to Google login
                         │
                         ▼
3. User authenticates → Google sends OAuth2 response
                         │
                         ▼
4. App creates/loads user → Generates JWT
                         │
                         ▼
5. Use JWT token exactly like PASSWORD provider tokens
```

---

## Security Notes

| Feature | Detail |
|---|---|
| Stateless Design | No session storage — all user info encoded in JWT |
| Token Revocation | Tokens tracked in DB; can be revoked on logout |
| Password Security | All passwords hashed with BCrypt before storage |
| CORS | Enabled for development (`cors: {}`) |
| Multiple Login Prevention | Each new login revokes all previous tokens |
| Email Verification | PASSWORD accounts disabled until email verified |
| Agency Approval | Agencies must be ADMIN-approved before login |

---

## Environment Configuration

Add these properties to `application.properties`:

```properties
# ─── JWT Configuration ───────────────────────────────────
application.security.jwt.secret-key=<64-character-random-secret>

# ─── Google OAuth2 ───────────────────────────────────────
spring.security.oauth2.client.registration.google.client-id=<your-client-id>
spring.security.oauth2.client.registration.google.client-secret=<your-client-secret>
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google

# ─── Base URL ─────────────────────────────────────────────
app.base-url=http://localhost:8080
```

> 🔑 **Secret Key:** Generate a secure 64-character random string. Never commit this to source control — use environment variables or a secrets manager in production.

---

## File References

| File | Purpose |
|---|---|
| [`SecurityConfig.java`](./src/main/java/com/ironhack/smarttourism/config/SecurityConfig.java) | Security configuration, endpoint rules |
| [`JwtService.java`](./src/main/java/com/ironhack/smarttourism/service/JwtService.java) | JWT generation and validation |
| [`JwtAuthenticationFilter.java`](./src/main/java/com/ironhack/smarttourism/filter/JwtAuthenticationFilter.java) | Per-request token validation filter |
| [`AuthenticationController.java`](./src/main/java/com/ironhack/smarttourism/controller/AuthenticationController.java) | Auth REST endpoints |
| [`AuthenticationService.java`](./src/main/java/com/ironhack/smarttourism/service/AuthenticationService.java) | Business logic for auth operations |
| [`OAuth2SuccessHandler.java`](./src/main/java/com/ironhack/smarttourism/config/OAuth2SuccessHandler.java) | Handles post-OAuth2 JWT generation |
| [`CustomOAuth2UserService.java`](./src/main/java/com/ironhack/smarttourism/service/CustomOAuth2UserService.java) | Loads/creates users from Google OAuth2 |
| [`User.java`](./src/main/java/com/ironhack/smarttourism/entity/User.java) | User entity model |
| [`RoleName.java`](./src/main/java/com/ironhack/smarttourism/entity/enums/RoleName.java) | Role enum (ADMIN, AGENCY, USER) |

---

## Quick Testing Checklist

```
[ ] Register new user             → Receive JWT token
[ ] Verify email                  → Account enabled
[ ] Login with credentials        → Fresh JWT issued
[ ] Call protected endpoint       → 200 OK ✅
[ ] Call endpoint without token   → 401 Unauthorized ❌
[ ] Logout                        → Token revoked
[ ] Use revoked token             → 401 Unauthorized ❌
[ ] Login via Google              → JWT token returned
[ ] Register as agency            → Status = PENDING
[ ] Admin approves agency         → Status = APPROVED
[ ] Agency login after approval   → Success ✅
```