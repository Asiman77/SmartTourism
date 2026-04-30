# 🔌 API Diagram

> **SmartTourism** — Complete REST API reference organized by role, resource, and workflow.

---

## Table of Contents

- [API Overview](#api-overview)
- [Authentication APIs](#authentication-apis)
- [Tourist APIs](#tourist-apis)
- [Agency APIs](#agency-apis)
- [Admin APIs](#admin-apis)
- [Complete API Tree](#complete-api-tree)
- [Request & Response Examples](#request--response-examples)
- [HTTP Status Codes](#http-status-codes)
- [API Statistics](#api-statistics)

---

## API Overview

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                       SmartTourism API Structure                             │
│                                                                              │
│   Base URL:  http://localhost:8080                                           │
│   Docs:      http://localhost:8080/swagger-ui.html                           │
│   OpenAPI:   http://localhost:8080/v3/api-docs                               │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   ┌─────────────────┐  ┌──────────────────┐  ┌──────────────┐  ┌─────────┐ │
│   │  🔓 PUBLIC       │  │  🧳 TOURIST       │  │  🏢 AGENCY   │  │ 🛡️ ADMIN│ │
│   │  /api/auth/**    │  │  /api/bookings/** │  │  /api/agency/│  │ /api/   │ │
│   │  /api/tours/**   │  │  /api/reviews/**  │  │             │  │ admin/  │ │
│   │  /api/dest./**   │  │  /api/tours/**    │  │             │  │        │ │
│   └─────────────────┘  └──────────────────┘  └──────────────┘  └─────────┘ │
│                                                                              │
│   ┌──────────────────────────────────────────────────────────────────────┐  │
│   │  Authorization: Bearer <JWT_TOKEN>   (required for all protected)    │  │
│   └──────────────────────────────────────────────────────────────────────┘  │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Authentication APIs

### 🔓 Public Endpoints — No Token Required

```
POST /api/auth/register
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Used for: USER registration & AGENCY registration
  
  USER Request:                    AGENCY Request:
  ┌─────────────────────────┐      ┌─────────────────────────────┐
  │ {                       │      │ {                           │
  │   "fullName": "...",    │      │   "fullName": "...",        │
  │   "email": "...",       │      │   "email": "...",           │
  │   "password": "...",    │      │   "password": "...",        │
  │   "role": "USER"        │      │   "role": "AGENCY",         │
  │ }                       │      │   "agencyName": "...",      │
  └─────────────────────────┘      │   "agencyAddress": "...",   │
                                   │   "agencyPhone": "...",     │
  Response 201:                    │   "description": "..."      │
  ┌─────────────────────────┐      │ }                           │
  │ {                       │      └─────────────────────────────┘
  │   "token": "eyJ...",    │      
  │   "role": "USER"        │      Note: Agency starts PENDING ⏳
  │ }                       │      Must be approved by ADMIN
  └─────────────────────────┘      before login is allowed.
```

```
POST /api/auth/login
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Request:                         Response 200:
  ┌─────────────────────────┐      ┌─────────────────────────┐
  │ {                       │      │ {                       │
  │   "email": "...",       │ ───► │   "token": "eyJ...",    │
  │   "password": "..."     │      │   "role": "USER"        │
  │ }                       │      │ }                       │
  └─────────────────────────┘      └─────────────────────────┘
```

```
GET  /api/auth/verify-email?token={token}
POST /api/auth/resend-verification?email={email}
GET  /login/oauth2/authorization/google          ← Google OAuth2 login
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  verify-email response:          resend-verification response:
  {                               {
    "message": "Email verified!"    "message": "Verification email was resent"
  }                               }
```

### 🔒 Protected Endpoints — Token Required

```
POST /api/auth/logout
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Header:  Authorization: Bearer {token}
  
  Process:
    1. Marks token as expired=true + revoked=true in database
    2. Clears Spring Security context
    3. Returns 200 OK
  
  After logout: previous token is invalid → 401 on any request
```

---

## Tourist APIs

### 🏖️ Destinations — Public Read

```
GET /api/destinations
GET /api/destinations/{id}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Query Params (GET /):             Response 200:
  ┌─────────────────────────┐       ┌───────────────────────────┐
  │ ?page=0                 │       │ [                         │
  │ ?size=10                │       │   {                       │
  │ ?search=Baku            │       │     "id": 1,              │
  └─────────────────────────┘       │     "name": "Baku",       │
                                    │     "description": "...", │
                                    │     "region": "Absheron", │
                                    │     "latitude": 40.3856,  │
                                    │     "longitude": 49.8831  │
                                    │   }                       │
                                    │ ]                         │
                                    └───────────────────────────┘
```

---

### 🗺️ Tours — Search & Discovery

```
GET /api/tours
GET /api/tours/{id}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Filters (GET /):
  ┌──────────────────────────────────────────────────┐
  │  ?destinationId=3    Filter by destination       │
  │  ?categoryId=1       Filter by category          │
  │  ?minPrice=100       Minimum price               │
  │  ?maxPrice=500       Maximum price               │
  │  ?durationDays=3     Tour duration in days       │
  │  ?search=qabala      Search title/description    │
  │  ?sortBy=price       Sort: price | rating | new  │
  │  ?page=0&size=10     Pagination                  │
  └──────────────────────────────────────────────────┘
  
  ⚠️  Returns ACTIVE tours only — INACTIVE tours are hidden.
  
  Response 200:
  ┌───────────────────────────────────────────────────┐
  │ [                                                 │
  │   {                                               │
  │     "id": 5,                                      │
  │     "title": "Qabala Mountain Adventure",         │
  │     "description": "3-day trek...",               │
  │     "price": 300.00,                              │
  │     "capacity": 15,                               │
  │     "durationDays": 3,                            │
  │     "status": "ACTIVE",                           │
  │     "agencyName": "Amazing Tours Inc.",           │
  │     "categoryName": "Adventure",                  │
  │     "destinationName": "Qabala"                   │
  │   }                                               │
  │ ]                                                 │
  └───────────────────────────────────────────────────┘
```

---

### 📋 Bookings — Tourist

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        BOOKING ENDPOINTS                                 │
├────────────────────────┬────────────────────────────────────────────────┤
│  POST /api/bookings    │  Create a new booking                          │
│  GET  /api/bookings/my │  View all my bookings                          │
│  GET  /api/bookings/{id│  Get single booking details                    │
│  PATCH /api/bookings/  │                                                │
│        {id}/cancel     │  Cancel a PENDING booking only                 │
└────────────────────────┴────────────────────────────────────────────────┘

POST /api/bookings
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Request:                          Response 201:
  ┌──────────────────────────┐      ┌──────────────────────────────┐
  │ {                        │      │ {                            │
  │   "userId": 100,         │ ───► │   "id": 1,                   │
  │   "tourPackageId": 5,    │      │   "userId": 100,             │
  │   "personsCount": 3      │      │   "tourPackageId": 5,        │
  │ }                        │      │   "personsCount": 3,         │
  └──────────────────────────┘      │   "totalPrice": 900.00,      │
                                    │   "bookingDate": "2026-...", │
  Price = 300 × 3 = 900.00 ──────►  │   "status": "PENDING"        │
                                    │ }                            │
                                    └──────────────────────────────┘

PATCH /api/bookings/{id}/cancel
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Success 200:  { ...booking, "status": "CANCELLED" }
  Error   409:  Conflict — booking is already CONFIRMED (cannot cancel)
```

---

### ⭐ Reviews — Tourist

```
POST /api/reviews
GET  /api/reviews/tour/{tourId}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  POST Request:                    Response 201:
  ┌──────────────────────────┐     ┌──────────────────────────────┐
  │ {                        │     │ {                            │
  │   "tourPackageId": 5,    │──►  │   "id": 1,                   │
  │   "rating": 4,           │     │   "tourPackageId": 5,        │
  │   "comment": "Amazing!"  │     │   "userId": 100,             │
  │ }                        │     │   "rating": 4,               │
  └──────────────────────────┘     │   "comment": "Amazing!",     │
                                   │   "createdAt": "2026-..."    │
  Rating scale: 0 ──── 5 ★        │ }                            │
                                   └──────────────────────────────┘
```

---

## Agency APIs

> ⚠️ **All agency endpoints require:** `Authorization: Bearer {token}` + Agency status = `APPROVED`

### 🗂️ Tour Management

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       AGENCY TOUR ENDPOINTS                              │
├──────────────────────────────────┬──────────────────────────────────────┤
│  POST   /api/agency/tours        │  Create new tour (status=ACTIVE)     │
│  GET    /api/agency/tours        │  List all my tours                   │
│  PUT    /api/agency/tours/{id}   │  Update tour details                 │
│  PATCH  /api/agency/tours/{id}/  │                                      │
│         status                   │  Toggle ACTIVE ↔ INACTIVE            │
│  DELETE /api/agency/tours/{id}   │  Delete tour (no active bookings)    │
└──────────────────────────────────┴──────────────────────────────────────┘

POST /api/agency/tours
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Request:
  ┌────────────────────────────────────────────────────┐
  │ {                                                  │
  │   "title": "Qabala Mountain Adventure",            │
  │   "description": "3-day trekking expedition",      │
  │   "price": 300.00,                                 │
  │   "capacity": 15,                                  │
  │   "durationDays": 3,                               │
  │   "meetingPoint": "Qabala city center",            │
  │   "includedServices": "Guides, meals, transport",  │
  │   "excludedServices": "Personal equipment",        │
  │   "categoryId": 1,                                 │
  │   "destinationId": 3                               │
  │ }                                                  │
  └────────────────────────────────────────────────────┘
  Response 201: Tour created with status = ACTIVE ✅

PATCH /api/agency/tours/{id}/status
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Request: { "status": "INACTIVE" }
  
  ACTIVE   → Tour visible to tourists, bookings allowed  ✅
  INACTIVE → Tour hidden from discovery (existing bookings unaffected)  ⛔

DELETE /api/agency/tours/{id}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Success 204: Tour deleted
  Error   409: Conflict — tour has confirmed bookings, cannot delete
  Error   403: Forbidden — not your tour
```

---

### 📦 Booking Management (Agency)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     AGENCY BOOKING ENDPOINTS                             │
├──────────────────────────────────────────┬──────────────────────────────┤
│  GET   /api/agency/bookings              │  View all bookings for my    │
│                                          │  tours (filterable)          │
│  GET   /api/agency/bookings/{id}         │  Get single booking detail   │
│  PATCH /api/agency/bookings/{id}/confirm │  Confirm a PENDING booking   │
└──────────────────────────────────────────┴──────────────────────────────┘

GET /api/agency/bookings
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Query Params:
  ?status=PENDING | CONFIRMED | COMPLETED
  ?page=0

PATCH /api/agency/bookings/{id}/confirm
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Response 200: { ...booking, "status": "CONFIRMED" }
  
  Triggers:
    → Email notification sent to tourist
    → Booking status: PENDING → CONFIRMED
```

---

### 🏢 Agency Profile

```
GET /api/agency/profile
GET /api/agency/status/my
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Profile Response 200:             Status Response 200:
  ┌──────────────────────────┐      ┌─────────────────────┐
  │ {                        │      │ {                   │
  │   "id": 2,               │      │   "data": "APPROVED"│
  │   "name": "Tours Inc.",  │      │ }                   │
  │   "email": "...",        │      └─────────────────────┘
  │   "phone": "...",        │
  │   "address": "...",      │
  │   "description": "...",  │
  │   "status": "APPROVED"   │
  │ }                        │
  └──────────────────────────┘
```

---

## Admin APIs

> ⚠️ **All admin endpoints require:** `Authorization: Bearer {token}` + Role = `ADMIN`

### 🏛️ Agency Approval Management

```
┌──────────────────────────────────────────────────────────────────────────┐
│                    ADMIN → AGENCY ENDPOINTS                               │
├───────────────────────────────────────────────┬──────────────────────────┤
│  GET   /api/admin/agencies                    │  List all agencies        │
│  GET   /api/admin/agencies/{id}               │  Agency details           │
│  PATCH /api/admin/agencies/{id}/approve       │  Approve agency ✅         │
│  PATCH /api/admin/agencies/{id}/reject        │  Reject agency ❌          │
│  PATCH /api/admin/agencies/{id}/suspend       │  Suspend agency ⛔         │
└───────────────────────────────────────────────┴──────────────────────────┘

GET /api/admin/agencies
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Query Params:
  ?status=PENDING | APPROVED | REJECTED | SUSPENDED
  ?search=agency name or email
  ?page=0&size=10

PATCH /api/admin/agencies/{id}/approve
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Response: { "status": "APPROVED" }
  Triggers: Email sent to agency owner
            Agency can now login + create tours

PATCH /api/admin/agencies/{id}/reject
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Response: { "status": "REJECTED" }
  Triggers: Email sent to agency owner
            Agency cannot login

PATCH /api/admin/agencies/{id}/suspend
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Response: { "status": "SUSPENDED" }
  Triggers: Email sent, agency cannot login
            Tours remain but hidden from tourists
```

---

### 📍 Destination & Category Management

```
┌─────────────────────────────────────────────────────────────────────────┐
│              ADMIN → DESTINATIONS & CATEGORIES (same structure)          │
├──────────────────────────────────────────┬──────────────────────────────┤
│  GET    /api/admin/destinations          │  List all destinations       │
│  POST   /api/admin/destinations          │  Create destination          │
│  PUT    /api/admin/destinations/{id}     │  Update destination          │
│  DELETE /api/admin/destinations/{id}     │  Delete destination          │
│  GET    /api/admin/categories            │  List all categories         │
│  POST   /api/admin/categories            │  Create category             │
│  PUT    /api/admin/categories/{id}       │  Update category             │
│  DELETE /api/admin/categories/{id}       │  Delete category             │
└──────────────────────────────────────────┴──────────────────────────────┘

POST /api/admin/destinations
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Request:                          Response 201:
  ┌──────────────────────────┐      ┌──────────────────────────┐
  │ {                        │      │ {                        │
  │   "name": "Qabala",      │ ───► │   "id": 3,               │
  │   "description": "...",  │      │   "name": "Qabala",      │
  │   "region": "Ismayilli", │      │   "region": "Ismayilli", │
  │   "latitude": 40.65,     │      │   ...                    │
  │   "longitude": 48.67     │      │ }                        │
  │ }                        │      └──────────────────────────┘
  └──────────────────────────┘

DELETE /api/admin/destinations/{id}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Success 204: Destination deleted
  Error   409: Conflict — tours reference this destination

POST /api/admin/categories
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Request:
  {
    "name": "Adventure",
    "description": "Active adventure tours",
    "icon": "⛰️"
  }
```

---

### 👥 User Management

```
GET /api/admin/users
GET /api/admin/users/{id}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Query Params (GET /):             Response 200:
  ?role=USER | AGENCY | ADMIN       ┌──────────────────────────────┐
  ?search=email or name             │ [                            │
  ?page=0                           │   {                          │
                                    │     "id": 100,               │
                                    │     "fullName": "John Doe",  │
                                    │     "email": "john@...",     │
                                    │     "role": "USER",          │
                                    │     "enabled": true,         │
                                    │     "createdAt": "2026-..."  │
                                    │   }                          │
                                    │ ]                            │
                                    └──────────────────────────────┘
```

---

### 🗑️ Review Moderation

```
GET    /api/admin/reviews
DELETE /api/admin/reviews/{id}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Query Params (GET /):
  ?minRating=0
  ?maxRating=5
  ?flagged=true
  ?page=0

  DELETE Response 204: Review removed
  Triggers:
    → Review removed from platform
    → Tour average rating recalculated
```

---

## Complete API Tree

```
SmartTourism REST API
│
├── 🔓 PUBLIC (No Auth Required)
│   │
│   ├── POST   /api/auth/register                     User & Agency Registration
│   ├── POST   /api/auth/login                        Login (PASSWORD Provider)
│   ├── GET    /api/auth/verify-email?token=...       Email Verification
│   ├── POST   /api/auth/resend-verification?email=.. Resend Verification
│   ├── GET    /login/oauth2/authorization/google     Google OAuth2 Login
│   ├── GET    /api/destinations                      List Destinations
│   ├── GET    /api/destinations/{id}                 Get Destination
│   ├── GET    /api/tours                             Search Tours (filters)
│   ├── GET    /api/tours/{id}                        Get Tour Details
│   └── GET    /api/reviews/tour/{tourId}             Get Tour Reviews
│
├── 🔒 AUTHENTICATED (Any Valid Token)
│   │
│   └── POST   /api/auth/logout                       Logout / Revoke Token
│
├── 🧳 TOURIST (USER Role)
│   │
│   ├── POST   /api/bookings                          Create Booking
│   ├── GET    /api/bookings/my                       View My Bookings
│   ├── GET    /api/bookings/{id}                     Booking Details
│   ├── PATCH  /api/bookings/{id}/cancel              Cancel Booking (PENDING)
│   └── POST   /api/reviews                           Submit Review
│
├── 🏢 AGENCY (AGENCY Role — Must be APPROVED)
│   │
│   ├── GET    /api/agency/profile                    Agency Profile
│   ├── GET    /api/agency/status/my                  My Approval Status
│   │
│   ├── GET    /api/agency/tours                      List My Tours
│   ├── POST   /api/agency/tours                      Create Tour
│   ├── PUT    /api/agency/tours/{id}                 Update Tour
│   ├── PATCH  /api/agency/tours/{id}/status          Toggle Tour Status
│   ├── DELETE /api/agency/tours/{id}                 Delete Tour
│   │
│   ├── GET    /api/agency/bookings                   View Tour Bookings
│   ├── GET    /api/agency/bookings/{id}              Booking Details
│   └── PATCH  /api/agency/bookings/{id}/confirm      Confirm Booking
│
├── 🛡️ ADMIN (ADMIN Role Only)
│   │
│   ├── AGENCIES
│   │   ├── GET    /api/admin/agencies                List Agencies
│   │   ├── GET    /api/admin/agencies/{id}           Agency Details
│   │   ├── PATCH  /api/admin/agencies/{id}/approve   Approve Agency ✅
│   │   ├── PATCH  /api/admin/agencies/{id}/reject    Reject Agency ❌
│   │   └── PATCH  /api/admin/agencies/{id}/suspend   Suspend Agency ⛔
│   │
│   ├── DESTINATIONS
│   │   ├── GET    /api/admin/destinations            List Destinations
│   │   ├── POST   /api/admin/destinations            Create Destination
│   │   ├── PUT    /api/admin/destinations/{id}       Update Destination
│   │   └── DELETE /api/admin/destinations/{id}       Delete Destination
│   │
│   ├── CATEGORIES
│   │   ├── GET    /api/admin/categories              List Categories
│   │   ├── POST   /api/admin/categories              Create Category
│   │   ├── PUT    /api/admin/categories/{id}         Update Category
│   │   └── DELETE /api/admin/categories/{id}         Delete Category
│   │
│   ├── USERS
│   │   ├── GET    /api/admin/users                   List Users
│   │   └── GET    /api/admin/users/{id}              User Details
│   │
│   └── REVIEWS
│       ├── GET    /api/admin/reviews                 List Reviews
│       └── DELETE /api/admin/reviews/{id}            Delete Review
│
└── 📖 DOCS (Public)
    ├── GET  /swagger-ui.html                         Swagger UI
    ├── GET  /v3/api-docs                             OpenAPI JSON
    └── GET  /swagger-resources/**                   Swagger Resources
```

---

## Request & Response Examples

### 🧳 Tourist: Full Registration → Book → Cancel Flow

```bash
# ── Step 1: Register ─────────────────────────────────────────────────────
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "SecurePassword123!",
    "role": "USER"
  }'

# Response: 201 Created
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "USER"
}

# ── Step 2: Verify Email ──────────────────────────────────────────────────
curl "http://localhost:8080/api/auth/verify-email?token=abc123"

# Response: { "message": "Email verified successfully!" }

# ── Step 3: Login ─────────────────────────────────────────────────────────
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "email": "john@example.com", "password": "SecurePassword123!" }'

# Response: { "token": "eyJhbGc...", "role": "USER" }

# ── Step 4: Browse Tours ──────────────────────────────────────────────────
curl "http://localhost:8080/api/tours?destinationId=3&maxPrice=500" \
  -H "Authorization: Bearer eyJhbGc..."

# ── Step 5: Book a Tour ───────────────────────────────────────────────────
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{ "userId": 100, "tourPackageId": 5, "personsCount": 3 }'

# Response: 201 Created
{
  "id": 1,
  "userId": 100,
  "tourPackageId": 5,
  "personsCount": 3,
  "totalPrice": 900.00,
  "bookingDate": "2026-04-30T10:30:00",
  "status": "PENDING"
}

# ── Step 6: Cancel Booking (if PENDING) ───────────────────────────────────
curl -X PATCH http://localhost:8080/api/bookings/1/cancel \
  -H "Authorization: Bearer eyJhbGc..."

# Response: { ...booking, "status": "CANCELLED" }
```

---

### 🏢 Agency: Register → Create Tour → Confirm Booking

```bash
# ── Step 1: Register as Agency ────────────────────────────────────────────
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Agency Owner",
    "email": "agency@example.com",
    "password": "SecurePassword123!",
    "role": "AGENCY",
    "agencyName": "Amazing Tours Inc.",
    "agencyAddress": "123 Main St, Baku",
    "agencyPhone": "+994501234567",
    "description": "Best tours in Azerbaijan"
  }'

# ── Step 2: Verify Email + Wait for Admin Approval ────────────────────────

# ── Step 3: Login (after APPROVED) ────────────────────────────────────────
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "email": "agency@example.com", "password": "SecurePassword123!" }'

# ── Step 4: Create a Tour ─────────────────────────────────────────────────
curl -X POST http://localhost:8080/api/agency/tours \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{
    "title": "Qabala Mountain Adventure",
    "description": "3-day trekking expedition",
    "price": 300.00,
    "capacity": 15,
    "durationDays": 3,
    "meetingPoint": "Qabala city center",
    "includedServices": "Guides, meals, transport",
    "excludedServices": "Personal equipment",
    "categoryId": 1,
    "destinationId": 3
  }'

# ── Step 5: View Pending Bookings ─────────────────────────────────────────
curl "http://localhost:8080/api/agency/bookings?status=PENDING" \
  -H "Authorization: Bearer eyJhbGc..."

# ── Step 6: Confirm a Booking ─────────────────────────────────────────────
curl -X PATCH http://localhost:8080/api/agency/bookings/1/confirm \
  -H "Authorization: Bearer eyJhbGc..."

# Response: { ...booking, "status": "CONFIRMED" }

# ── Step 7: Toggle Tour to Inactive ───────────────────────────────────────
curl -X PATCH http://localhost:8080/api/agency/tours/5/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{ "status": "INACTIVE" }'
```

---

### 🛡️ Admin: Login → Approve Agency → Add Destination

```bash
# ── Step 1: Login as Admin ────────────────────────────────────────────────
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "email": "admin@smarttourism.com", "password": "AdminPass123!" }'

# ── Step 2: List Pending Agencies ─────────────────────────────────────────
curl "http://localhost:8080/api/admin/agencies?status=PENDING" \
  -H "Authorization: Bearer admin_token"

# ── Step 3: Approve Agency ────────────────────────────────────────────────
curl -X PATCH http://localhost:8080/api/admin/agencies/2/approve \
  -H "Authorization: Bearer admin_token"

# Response: { "status": "APPROVED" }

# ── Step 4: Create a Destination ──────────────────────────────────────────
curl -X POST http://localhost:8080/api/admin/destinations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer admin_token" \
  -d '{
    "name": "Qabala",
    "description": "Mountain region in northern Azerbaijan",
    "region": "Ismayilli",
    "latitude": 40.65,
    "longitude": 48.67
  }'

# ── Step 5: Delete an Inappropriate Review ────────────────────────────────
curl -X DELETE http://localhost:8080/api/admin/reviews/99 \
  -H "Authorization: Bearer admin_token"

# Response: 204 No Content
```

---

## HTTP Status Codes

```
┌──────────────────────────────────────────────────────────────────────────┐
│                       Response Code Reference                            │
├──────────┬──────────────────────────────────────────────────────────────┤
│  200 OK  │  Request succeeded. Returns data.                            │
│  201     │  Resource created successfully.                              │
│  204     │  Success, no content returned (DELETE).                      │
├──────────┼──────────────────────────────────────────────────────────────┤
│  400     │  Bad Request — invalid input or missing required fields.      │
│  401     │  Unauthorized — missing token, expired, or revoked JWT.       │
│  403     │  Forbidden — authenticated but insufficient permissions.      │
│  404     │  Not Found — resource does not exist.                        │
│  409     │  Conflict — business rule violation (e.g. cancel CONFIRMED).  │
├──────────┼──────────────────────────────────────────────────────────────┤
│  500     │  Internal Server Error — unexpected server-side failure.      │
└──────────┴──────────────────────────────────────────────────────────────┘

Example 401 body:
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is expired or invalid.",
  "timestamp": "2026-04-30T10:30:00"
}
```

---

## API Statistics

```
┌──────────────────────────────────────────────────────────────────────────┐
│                          API at a Glance                                 │
├─────────────────────────────────────┬────────────────────────────────────┤
│  Total Endpoints                    │  45+                               │
│  ├── Public (no auth)               │  10                                │
│  ├── Tourist (USER)                 │  5                                 │
│  ├── Agency (AGENCY, APPROVED)      │  9                                 │
│  └── Admin (ADMIN)                  │  16+                               │
├─────────────────────────────────────┴────────────────────────────────────┤
│                         HTTP Methods                                     │
├──────────────┬──────────────────────────────────────────────────────────┤
│  GET         │  Read resources (list + detail)         35%              │
│  POST        │  Create resources                       30%              │
│  PATCH       │  Partial update (status changes)        20%              │
│  PUT         │  Full update                            10%              │
│  DELETE      │  Remove resources                       5%               │
├──────────────┴──────────────────────────────────────────────────────────┤
│                         Auth Methods                                     │
├──────────────────────────────────────────────────────────────────────────┤
│  PASSWORD + Email Verification    ✅                                     │
│  Google OAuth2                    ✅                                     │
│  JWT Bearer Token (24h TTL)       ✅                                     │
│  CSRF                             ❌ (disabled for stateless API)        │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## Summary

```
SmartTourism provides a role-based REST API with 45+ endpoints:

  🔓 Public    →  Auth, tour discovery, destination browsing
  🧳 Tourist   →  Bookings lifecycle, review submission
  🏢 Agency    →  Tour CRUD, booking confirmation, profile
  🛡️ Admin     →  Agency approvals, content management, moderation

  All protected endpoints require:
    Authorization: Bearer <JWT_TOKEN>

  API docs available at:
    http://localhost:8080/swagger-ui.html
```