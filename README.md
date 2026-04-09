# 🌍 Smart Tourism Booking Platform

## 📌 Overview

Smart Tourism Booking Platform is a **tourism marketplace system** where users can explore, book, and review travel experiences. Agencies can register and manage their tour packages, while administrators control and approve agencies to ensure platform quality. The system is built with a strong focus on **secure backend architecture, relational database design, and role-based access control**.

---

## 🚀 Features

### 👤 Tourist

* Register & Login
* Browse tour packages
* Book tours
* Write reviews
* Use AI chatbot for travel recommendations

### 🏢 Agency

* Register as business account
* Manage agency profile
* Create, update, delete tour packages (after approval)
* View bookings for their tours

### 🛠️ Admin

* Approve / Reject agencies
* Manage destinations & categories
* View all users, bookings, and tours
* Control tour visibility (status)

---

## 🔐 Security

* JWT-based Authentication
* Role-Based Authorization (ADMIN, TOURIST, AGENCY)
* Password hashing with BCrypt
* Protected API endpoints

---

## 🗂️ Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* MySQL

### Frontend

* React (planned)

### AI

* External LLM API (Chatbot integration)

---


## 🗄️ Core Entities

* User
* Role
* Agency
* TourPackage
* Booking
* Review
* Destination
* Category
* Attraction (optional)

---

## ✅ Approval Logic

* Tourist → No approval required
* Agency → Requires admin approval
* Tour → No approval required
* Admin → Controls visibility and status

---

## 👥 Team Workflow

* `main` → stable code
* `dev` → development branch
* `feature/*` → individual work

Pull Request-based workflow is used for merging.

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

Update `application.yml` with your PostgreSQL credentials.

---

## 🎯 Key Goal

This project demonstrates:

* Real-world business logic
* Secure backend system design
* Relational database modeling
* Scalable architecture
* AI-powered enhancement

---

## 📌 Note

Backend is the **core priority** of the project. Frontend and AI chatbot are implemented as **enhancements** to improve user experience and presentation quality.

---

## 🏁 Conclusion

This platform provides a complete foundation for a tourism booking system with a clean architecture, strong backend logic, and extensible design for future improvements.

🚀 Ready for development & scaling!
