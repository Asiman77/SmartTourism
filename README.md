# 🌍 Smart Tourism Platform

## 📌 Overview

Smart Tourism Booking Platform is a **tourism marketplace system** where users can explore, book, and review travel experiences. Agencies can register and manage their tour packages, while administrators control and approve agencies to ensure platform quality. The system is built with a strong focus on **secure backend architecture, relational database design, and role-based access control**.

---

## 🚀 Features

### 👤 Tourist
- Register & Login
- Browse tour packages
- Book tours
- Write reviews
- Use AI chatbot for travel recommendations

### 🏢 Agency
- Register as business account
- Manage agency profile
- Create, update, delete tour packages (after approval)
- View bookings for their tours

### 🛠️ Admin
- Approve / Reject agencies
- Manage destinations & categories
- View all users, bookings, and tours
- Control tour visibility (status)

---

## 🔐 Security & Authentication

> 📄 Full details: [SecurityAndAuth.md](docs/SecurityAndAuth.md)

The platform uses **JWT-based authentication** with role-based access control across three user roles.

| Feature | Details |
|---|---|
| Authentication | JWT (JSON Web Token) |
| Password Security | BCrypt hashing |
| Roles | `ADMIN`, `TOURIST`, `AGENCY` |

| Protected Endpoints | All non-public routes require valid JWT |

**Authorization flow:**
1. User registers / logs in → receives JWT token
2. Token attached to every request via `Authorization: Bearer <token>`
3. Spring Security validates token and enforces role-based access

---

## 🔄 System Flow

> 📄 Full details: [SystemFlow.md](docs/SystemFlow.md)

The platform follows a structured request lifecycle from user interaction to database response.

```
Client Request
     │
     ▼
Spring Security Filter (JWT Validation)
     │
     ▼
Controller Layer (REST API)
     │
     ▼
Service Layer (Business Logic)
     │
     ▼
Repository Layer (Spring Data JPA)
     │
     ▼
MySQL Database
```

**Key flows:**
- **Tourist booking flow:** Browse → Select package → Book → Confirmation
- **Agency onboarding flow:** Register → Await admin approval → Create packages → Go live
- **Admin approval flow:** Review agency request → Approve/Reject → Notify agency

---

## 📡 API Diagram

> 📄 Full details: [ApiDiagram.md](docs/ApiDiagram.md)

```
┌──────────────────────────────────────────────────────────────┐
│                    SmartTourism API                          │
├──────────────────────────────────────────────────────────────┤
│ Base URL   : http://localhost:8080                           │
│ Swagger UI : http://localhost:8080/swagger-ui.html           │
│ OpenAPI    : http://localhost:8080/v3/api-docs               │
├──────────────────────────────────────────────────────────────┤
│ 🔓 PUBLIC                                                    │
│   /api/auth/**          /api/tours/**                        │
│   /api/destinations/**                                       │
│                                                              │
│ 🧳 TOURIST (Authenticated)                                   │
│   /api/bookings/**      /api/reviews/**                      │
│                                                              │
│ 🏢 AGENCY (Authenticated + Approved)                         │
│   /api/agency/**                                             │
│                                                              │
│ 🛡️ ADMIN (Authenticated)                                     │
│   /api/admin/**                                              │
├──────────────────────────────────────────────────────────────┤
│ Authorization: Bearer <JWT_TOKEN>                            │
└──────────────────────────────────────────────────────────────┘
```

### API Statistics

| Metric | Value |
|---|---|
| Total Endpoints | 45+ |
| Public (no auth) | 10 |
| Tourist (USER) | 5 |
| Agency (AGENCY, APPROVED) | 9 |
| Admin (ADMIN) | 16+ |

## 🗂️ Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL

---

## ✅ Approval Logic

| Entity | Approval Required |
|---|---|
| Tourist registration | ❌ No |
| Agency registration | ✅ Yes — Admin approval |
| Tour package creation | ❌ No (agency must be approved first) |
| Tour visibility | Controlled by Admin |

---

## ⚙️ Setup Instructions

### 1. Clone repository
```bash
git clone https://github.com/your-username/smart-tourism-platform.git
```

### 2. Run backend
```bash
cd smart-tourism-platform
./mvnw spring-boot:run
```

### 3. Configure database
Update `application.yml` with your MySQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tourism_db
    username: your_username
    password: your_password
```

---

## 👥 Team

<table>
  <tr>
    <td align="center" width="33%">
      <b>Asiman Mirzaliyev</b><br/>
      <a href="https://github.com/Asiman77">🐙 GitHub</a> •
      <a href="https://www.linkedin.com/in/asiman-dev/">💼 LinkedIn</a>
    </td>
    <td align="center" width="33%">
      <b>Lala Aliyeva</b><br/>
      <a href="https://github.com/lalocchi">🐙 GitHub</a> •
      <a href="https://www.linkedin.com/in/lala-aliyeva-b0617a21a/">💼 LinkedIn</a>
    </td>
    <td align="center" width="33%">
      <b>Yusif Xankishiyev</b><br/>
      <a href="https://github.com/XankisiyevYusif">🐙 GitHub</a> •
      <a href="https://www.linkedin.com/in/yusif-xankishiyev/">💼 LinkedIn</a>
    </td>
  </tr>
</table>

---

## 📌 Note

Backend is the **core priority** of the project. Frontend and AI chatbot are implemented as **enhancements** to improve user experience and presentation quality.

---

## 🏁 Conclusion

This platform provides a complete foundation for a tourism booking system with a clean architecture, strong backend logic, and extensible design for future improvements.

🚀 Ready for development & scaling!
