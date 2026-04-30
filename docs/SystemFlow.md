# 🗺️ System Flow

> **SmartTourism** — A three-tiered marketplace connecting tourists, tourism agencies, and platform administrators.

---

## Table of Contents

- [Core Business Rules](#core-business-rules)
- [High-Level Role Map](#high-level-role-map)
- [1. Tourist Flow](#1-tourist-flow)
- [2. Agency Flow](#2-agency-flow)
- [3. Admin Flow](#3-admin-flow)
- [State Transition Rules](#state-transition-rules)
- [Permission Model](#permission-model)
- [Pricing Logic](#pricing-logic)
- [Entity Relationships](#entity-relationships)
- [API Endpoints by Role](#api-endpoints-by-role)
- [End-to-End Journey](#end-to-end-journey)
- [File References](#file-references)

---

## Core Business Rules

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        SmartTourism Business Rules                       │
├─────────────────────────┬────────────────────────────────────────────────┤
│  Agency Registration    │  Starts PENDING → must be APPROVED by Admin    │
│  Tour Lifecycle         │  ACTIVE by default → agency can toggle         │
│  Booking Lifecycle      │  PENDING → CONFIRMED → COMPLETED               │
│  Tourist Cancellation   │  PENDING bookings only                         │
│  Pricing Model          │  Base Price × Persons (fixed at booking time)  │
│  Email Verification     │  Required for PASSWORD provider accounts       │
│  Google OAuth2 Users    │  Auto-enabled on registration                  │
└─────────────────────────┴────────────────────────────────────────────────┘
```

---

## High-Level Role Map

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        Platform Role Overview                            │
│                                                                          │
│  ┌──────────────┐     ┌──────────────────┐     ┌──────────────────────┐ │
│  │   TOURIST    │     │     AGENCY       │     │       ADMIN          │ │
│  │   (USER)     │     │  (AGENCY role)   │     │   (ADMIN role)       │ │
│  └──────┬───────┘     └────────┬─────────┘     └──────────┬───────────┘ │
│         │                      │                           │             │
│  Browse tours            Register agency            Approve agencies     │
│  Book tours              Manage tours               Manage destinations  │
│  Cancel bookings         Confirm bookings           Manage categories    │
│  Write reviews           View analytics             Moderate reviews     │
│  Manage profile          Manage profile             Manage users         │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 1. Tourist Flow

### 1.1 Registration & Onboarding

```
                    ┌─────────────────────────────┐
                    │    Tourist Visits SmartTourism│
                    └──────────────┬──────────────┘
                                   │
                    ┌──────────────▼──────────────┐
                    │       Choose Auth Method      │
                    └──────────────┬──────────────┘
                                   │
              ┌────────────────────┼──────────────────────┐
              │                                           │
   ┌──────────▼───────────┐               ┌──────────────▼──────────┐
   │   PASSWORD Provider   │               │    GOOGLE OAuth2         │
   └──────────┬───────────┘               └──────────────┬──────────┘
              │                                           │
   Enter email + password                    Click "Sign in with Google"
              │                                           │
   Save user (enabled=false)             Create user (enabled=true, USER role)
              │                                           │
   Send verification email                       Auto-logged in ✅
              │
   User clicks link in email
              │
   Set user.enabled = true
              │
   Account activated ✅
```

---

### 1.2 Tour Discovery Flow

```
   Tourist opens app (authenticated)
              │
              ▼
   ┌──────────────────────────────┐
   │  Two Discovery Pathways       │
   └──────────────────────────────┘
              │
     ┌────────┴────────┐
     │                 │
     ▼                 ▼
  PATH A            PATH B
Destination       Category /
  First            Filters
     │                 │
  Click on          Search by
  destination        category
     │                 │
  Get agencies      Get tours
  in destination    with filters
     │                 │
  Browse active     Browse active
  tours             tours
     │                 │
     └────────┬────────┘
              │
              ▼
   View Tour Details + Reviews
              │
              ▼
   Confirm persons count
              │
              ▼
   Proceed to Booking ──────────────────► See Section 1.3
```

---

### 1.3 Booking Creation Flow

```
   Tourist clicks "Book Now"
              │
              ▼
   Enter number of persons
              │
              ▼
   POST /api/bookings
   ┌─────────────────────────────────┐
   │  {                              │
   │    userId: currentUserId,       │
   │    tourPackageId: tourId,       │
   │    personsCount: X              │
   │  }                              │
   └─────────────────────────────────┘
              │
              ▼
   Verify tour is ACTIVE
              │
        ┌─────┴─────┐
        │           │
        ▼           ▼
   Capacity     Capacity
    Sufficient   Exceeded
        │           │
        ▼           ▼
  Calculate      Return error
  totalPrice     to tourist
  = price × X
        │
        ▼
  Create booking
  status = PENDING
        │
        ▼
  Show confirmation
  "Awaiting agency approval"

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Example Booking Object Created:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  {
    "id": 1,
    "userId": 100,
    "tourPackageId": 5,
    "personsCount": 3,
    "totalPrice": 900.00,
    "bookingDate": "2026-04-30T10:30:00",
    "status": "PENDING"
  }
```

---

### 1.4 Booking Management

```
   Tourist visits "My Bookings"
              │
              ▼
   GET /api/bookings/my
              │
              ▼
   ┌──────────────────────────────────────────────────┐
   │              Booking Status Actions               │
   ├───────────────┬──────────────┬───────────────────┤
   │   PENDING     │  CONFIRMED   │    COMPLETED       │
   ├───────────────┼──────────────┼───────────────────┤
   │  Can Cancel   │  Awaiting    │  Can Write Review  │
   │  booking      │  tour date   │  (rating 0-5)      │
   └───────┬───────┴──────────────┴───────────────────┘
           │
           ▼ (if cancelled)
   PATCH /api/bookings/{id}/cancel
           │
           ▼
   status = CANCELLED ❌
```

---

### 1.5 Review Submission

```
   Tourist views completed booking
              │
              ▼
   Click "Write Review"
              │
              ▼
   Enter rating (0–5) + comment
              │
              ▼
   POST /api/reviews
   ┌──────────────────────────────┐
   │  {                           │
   │    tourPackageId: X,         │
   │    userId: Y,                │
   │    rating: 4,                │
   │    comment: "Amazing!"       │
   │  }                           │
   └──────────────────────────────┘
              │
              ▼
   Create review record
              │
              ▼
   Update tour average rating
              │
              ▼
   Review published ✅
```

---

## 2. Agency Flow

### 2.1 Agency Registration & Approval

```
   Business owner clicks "Register as Agency"
              │
              ▼
   POST /api/auth/register (role=AGENCY)
   ┌──────────────────────────────────────┐
   │  {                                   │
   │    fullName, email, password,        │
   │    role: "AGENCY",                   │
   │    agencyName, agencyAddress,        │
   │    agencyPhone, description          │
   │  }                                   │
   └──────────────────────────────────────┘
              │
              ▼
   Create User + Create Agency
   Agency status = PENDING ⏳
              │
              ▼
   Verify email (same as USER flow)
              │
              ▼
   ┌──────────────────────────────────┐
   │         Admin Reviews Agency      │
   └─────────────┬────────────────────┘
                 │
     ┌───────────┼───────────┐
     │           │           │
     ▼           ▼           ▼
  APPROVE      REJECT     SUSPEND
     │           │           │
  status=      status=    status=
  APPROVED   REJECTED   SUSPENDED
     │           │           │
  Send         Send        Send
  approval    rejection  suspension
  email        email       notice
     │
     ▼
  Agency can now login ✅
  and manage tours
```

**Agency Status Rules:**
```
PENDING   → Cannot login, cannot create tours
APPROVED  → Full access: login, create tours, confirm bookings
REJECTED  → Cannot login
SUSPENDED → Cannot login
```

---

### 2.2 Tour Package Lifecycle

```
   Agency creates tour
          │
          ▼
   POST /api/agency/tours
   ┌─────────────────────────────────────┐
   │  Title, description, price,         │
   │  capacity, duration, meeting point, │
   │  included/excluded services,        │
   │  destination, category              │
   └─────────────────────────────────────┘
          │
          ▼
   Verify agency status = APPROVED
          │
          ▼
   Tour created with status = ACTIVE ✅

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Tour Status Lifecycle:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

   ┌─────────┐   Agency toggles   ┌──────────┐
   │  ACTIVE │ ◄─────────────────► │ INACTIVE │
   └─────────┘                    └──────────┘
       │                               │
  Visible to tourists             Hidden from tourists
  Bookings allowed                Existing bookings
                                  unaffected

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Tour Deletion Rules:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   Has confirmed bookings? → Cannot delete ❌
   No active bookings?     → Can delete ✅
```

**Example Tour Object:**
```json
{
  "id": 5,
  "title": "Qabala Mountain Adventure",
  "description": "3-day trekking expedition",
  "price": 300.00,
  "capacity": 15,
  "durationDays": 3,
  "meetingPoint": "Qabala city center",
  "includedServices": "Guides, meals, transport",
  "excludedServices": "Personal equipment",
  "status": "ACTIVE",
  "agencyId": 2,
  "categoryId": 1,
  "destinationId": 3
}
```

---

### 2.3 Booking Management (Agency View)

```
   Agency visits "Bookings" dashboard
              │
              ▼
   GET /api/agency/bookings
   (all bookings for agency's tours)
              │
              ▼
   ┌──────────────────────────────────────────────────┐
   │              Booking Status Actions               │
   ├───────────────┬──────────────┬───────────────────┤
   │   PENDING     │  CONFIRMED   │    COMPLETED       │
   ├───────────────┼──────────────┼───────────────────┤
   │  Can Confirm  │  Awaiting    │  Tour finished     │
   │  booking      │  tour date   │  Review possible   │
   └───────┬───────┴──────────────┴───────────────────┘
           │
           ▼ (if confirmed)
   PATCH /api/agency/bookings/{id}/confirm
           │
           ▼
   status = CONFIRMED ✅
   Tourist notified
```

---

## 3. Admin Flow

### 3.1 Agency Approval Management

```
   Admin opens "Pending Agencies"
              │
              ▼
   GET /api/admin/agencies?status=PENDING
              │
              ▼
   Review agency details + owner info
              │
              ▼
   ┌─────────────────────────────────────┐
   │           Admin Decision             │
   └──────┬──────────────┬───────────────┘
          │              │              │
        APPROVE        REJECT        SUSPEND
          │              │              │
   PATCH approve   PATCH reject   PATCH suspend
          │              │              │
      Send email    Send email     Send notice
          │
          ▼
   Agency owner can now login ✅
```

---

### 3.2 Destination & Category Management

```
   Admin manages platform content:

   DESTINATIONS                    CATEGORIES
   ─────────────                   ──────────
   GET  /api/admin/destinations    GET  /api/admin/categories
   POST /api/admin/destinations    POST /api/admin/categories
   PUT  /api/admin/destinations    PUT  /api/admin/categories
   DEL  /api/admin/destinations    DEL  /api/admin/categories

   These are the building blocks
   that agencies use when creating
   their tour packages.
```

---

### 3.3 Content Moderation

```
   Admin visits "Reviews"
              │
              ▼
   GET /api/admin/reviews
   (all reviews across platform)
              │
              ▼
   Filter by rating / flagged
              │
              ▼
   Review inappropriate content
              │
              ▼
   DELETE /api/reviews/{id}
              │
              ▼
   Review removed from platform ✅
```

---

## State Transition Rules

### Booking States

```
                    Tourist Creates Booking
                             │
                             ▼
                         PENDING ⏳
                        /         \
                       /           \
                      ▼             ▼
              Agency Confirms   Tourist Cancels
                      │
                      ▼
                  CONFIRMED ✅
                      │
                      ▼ (after tour date)
                  COMPLETED 🏁
                             \
                              ▼
                           CANCELLED ❌
                     (only from PENDING)
```

### Agency States

```
   Register → PENDING ⏳
                  │
         ┌────────┼────────┐
         ▼        ▼        ▼
      APPROVED  REJECTED  SUSPENDED
         ✅        ❌        ⛔
```

### User States

```
   PASSWORD Register → enabled=false
                            │
                   Click email link
                            │
                       enabled=true ✅

   GOOGLE Register   → enabled=true ✅ (immediate)
```

---

## Permission Model

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         Permission Matrix                                │
├──────────────────────────────┬──────────┬────────┬───────────────────────┤
│ Action                       │ TOURIST  │ AGENCY │ ADMIN                  │
├──────────────────────────────┼──────────┼────────┼───────────────────────┤
│ Browse tours (ACTIVE)        │    ✅    │   ✅   │   ✅                   │
│ Create booking               │    ✅    │   ❌   │   ❌                   │
│ Cancel PENDING booking       │    ✅    │   ❌   │   ❌                   │
│ Write reviews                │    ✅    │   ❌   │   ❌                   │
│ Create/manage own tours      │    ❌    │   ✅   │   ❌                   │
│ Confirm bookings             │    ❌    │   ✅   │   ❌                   │
│ Toggle tour status           │    ❌    │   ✅   │   ❌                   │
│ Approve/reject agencies      │    ❌    │   ❌   │   ✅                   │
│ Manage destinations          │    ❌    │   ❌   │   ✅                   │
│ Manage categories            │    ❌    │   ❌   │   ✅                   │
│ Moderate reviews             │    ❌    │   ❌   │   ✅                   │
│ View all users               │    ❌    │   ❌   │   ✅                   │
└──────────────────────────────┴──────────┴────────┴───────────────────────┘
```

---

## Pricing Logic

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Total Price = Base Price × Persons
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  Tour: "Qabala Adventure" → $300 / person
  Booking: 3 persons
  Total: $300 × 3 = $900.00

  ⚠️  Price is fixed at booking creation time
      and stored immutably — no retroactive
      changes if tour price is updated later.
```

---

## Entity Relationships

```
  User (1) ──────┬──── (many) Bookings
                 ├──── (many) Reviews
                 └──── (0..1) Agency        ← if role = AGENCY

  Agency (1) ────┬──── (many) Tours
                 └──── (1)    User

  Tour (1) ──────┬──── (many) Bookings
                 ├──── (many) Reviews
                 ├──── (1)    Agency
                 ├──── (1)    Category
                 └──── (1)    Destination

  Booking (many) ┬──── (1) User
                 └──── (1) Tour

  Review (many) ─┬──── (1) User
                 └──── (1) Tour
```

---

## API Endpoints by Role

### 🧳 Tourist Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register as tourist |
| `GET` | `/api/auth/verify-email` | Verify email |
| `POST` | `/api/auth/login` | Login |
| `GET` | `/api/destinations` | Browse destinations |
| `GET` | `/api/tours` | Search & filter tours |
| `POST` | `/api/bookings` | Create booking |
| `GET` | `/api/bookings/my` | View my bookings |
| `PATCH` | `/api/bookings/{id}/cancel` | Cancel booking |
| `POST` | `/api/reviews` | Submit review |

### 🏢 Agency Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register as agency |
| `POST` | `/api/auth/login` | Login |
| `POST` | `/api/agency/tours` | Create tour |
| `PUT` | `/api/agency/tours/{id}` | Update tour |
| `PATCH` | `/api/agency/tours/{id}/status` | Toggle tour status |
| `DELETE` | `/api/agency/tours/{id}` | Delete tour |
| `GET` | `/api/agency/bookings` | View bookings |
| `PATCH` | `/api/agency/bookings/{id}/confirm` | Confirm booking |

### 🛡️ Admin Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/admin/agencies` | List agencies |
| `PATCH` | `/api/admin/agencies/{id}/approve` | Approve agency |
| `PATCH` | `/api/admin/agencies/{id}/reject` | Reject agency |
| `PATCH` | `/api/admin/agencies/{id}/suspend` | Suspend agency |
| `GET` | `/api/admin/users` | List all users |
| `GET/POST/PUT/DEL` | `/api/admin/destinations` | Manage destinations |
| `GET/POST/PUT/DEL` | `/api/admin/categories` | Manage categories |
| `GET/DEL` | `/api/admin/reviews` | Moderate reviews |

---

## End-to-End Journey

```
  Step 1 ── Tourist Registration
            Register → Verify email → Login
                │
  Step 2 ── Discovery
            Browse by destination or category
                │
  Step 3 ── Booking
            Select tour → Set persons count → Create booking (PENDING)
                │
  Step 4 ── Agency Review
            Agency sees booking → Reviews details
                │
  Step 5 ── Confirmation
            Agency confirms booking → status = CONFIRMED
                │
  Step 6 ── Tour Day
            Tourist attends the tour
                │
  Step 7 ── Completion
            Booking marked COMPLETED
                │
  Step 8 ── Review
            Tourist can leave rating (0–5) + comment
                │
  Step 9 ── Repeat ♻️
            Tourist can book their next adventure
```

---

## File References

| File | Purpose |
|---|---|
| [`Booking.java`](./src/main/java/com/ironhack/smarttourism/entity/Booking.java) | Booking entity |
| [`TourPackage.java`](./src/main/java/com/ironhack/smarttourism/entity/TourPackage.java) | Tour package entity |
| [`Agency.java`](./src/main/java/com/ironhack/smarttourism/entity/Agency.java) | Agency entity |
| [`BookingService.java`](./src/main/java/com/ironhack/smarttourism/service/BookingService.java) | Booking business logic |
| [`TourService.java`](./src/main/java/com/ironhack/smarttourism/service/TourService.java) | Tour business logic |
| [`BookingStatus.java`](./src/main/java/com/ironhack/smarttourism/entity/enums/BookingStatus.java) | Booking status enum |
| [`AgencyStatus.java`](./src/main/java/com/ironhack/smarttourism/entity/enums/AgencyStatus.java) | Agency status enum |
| [`TourStatus.java`](./src/main/java/com/ironhack/smarttourism/entity/enums/TourStatus.java) | Tour status enum |

---

## Summary

SmartTourism implements a three-tiered marketplace ecosystem:

```
  ┌──────────────┐     discover & book     ┌──────────────┐
  │   Tourists   │ ──────────────────────► │    Tours     │
  └──────────────┘                         └──────┬───────┘
                                                  │ created by
  ┌──────────────┐     govern & approve    ┌──────▼───────┐
  │    Admins    │ ──────────────────────► │   Agencies   │
  └──────────────┘                         └──────────────┘

  All operations are:
  ✅ Role-based        — enforced by Spring Security
  ✅ State-driven      — entity status governs what's allowed
  ✅ Email-aware       — key events trigger notifications
```