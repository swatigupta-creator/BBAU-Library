# BBAU Digital Library — Java + Spring Boot + MongoDB

Responsive library management system for Babasaheb Bhimrao Ambedkar University. The backend uses Java 17, Spring Boot, Spring Data MongoDB, and MongoDB; the existing browser interface is served by the Spring Boot app.

## Folder structure

```text
LibraryManagementSystem/
├── backend/
│   ├── src/main/java/com/library/
│   │   ├── config/        Mongo seed data, password encoder
│   │   ├── controller/   REST APIs
│   │   ├── service/      Authentication and library rules
│   │   ├── repository/   Spring Data MongoDB repositories
│   │   ├── model/        Book, User, Member, Loan, Reservation, Activity
│   │   ├── dto/          API request and response objects
│   │   ├── exception/    API errors and validation responses
│   │   └── LibraryApplication.java
│   ├── src/main/resources/application.properties
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── pages/            login, register, dashboard, books, issue-return entry points
│   ├── css/styles.css
│   ├── js/app.js
│   ├── assets/
│   └── index.html
├── docs/API-documentation.md
├── .env.example
└── README.md
```

The named HTML files in `frontend/pages/` lead into the existing single-page application, where login, registration, dashboard, catalog, and circulation views are rendered.

## Requirements

- Java 17 or newer
- Maven 3.6.3 or newer
- A MongoDB deployment: MongoDB Atlas or local MongoDB Community Server

## Connect MongoDB Atlas

1. In MongoDB Atlas, create a cluster and a database user. In **Network Access**, allow the IP address of the machine running the app.
2. Choose **Connect → Drivers → Java** and copy the connection URI. Replace the password placeholder; URL-encode special characters in the password.
3. Copy `.env.example` to `.env` in the project root. Set `MONGODB_URI` to the private connection URI, choose a strong `ADMIN_PASSWORD`, and keep `.env` out of Git.
4. Set `MONGODB_DATABASE=bbau_library`. MongoDB creates the database and collections automatically when the app first writes data; sample catalog and circulation data are seeded on first startup.

Never paste your MongoDB URI or database password into a public repository or chat.

## Run locally

From PowerShell at the project root:

```powershell
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
```

Edit `.env` with your Atlas URI and a strong admin password. Then:

```powershell
cd backend
mvn spring-boot:run
```

Keep the terminal running and open http://localhost:8080. Admin email is set by `ADMIN_EMAIL` (default `admin@bbau.ac.in`); admin password is `ADMIN_PASSWORD`. Students create separate accounts from the student registration screen.

## Backend

- Spring MVC serves the frontend and JSON API from one origin.
- MongoDB collections hold users, books, members, loans, reservations, activities, and support requests.
- Student passwords are BCrypt-hashed; the admin password is also encoded before it is stored.
- Sessions use HTTP-only, same-site cookies.
- Login requests are expected to be deployed behind a rate-limiting proxy before production use.
- Students can borrow available books, reserve unavailable titles, and cancel their own reservations.
- Admin-only state updates back add-book, member, issue/return, reservations, and reports in the interface.

## Deploy

`render.yaml` describes a Docker-based Spring Boot service. Before deploying, create MongoDB Atlas and set `MONGODB_URI`, `MONGODB_DATABASE`, `ADMIN_PASSWORD`, and `COOKIE_SECURE=true` in the hosting provider's secret/environment settings. Add the host's outbound addresses to the Atlas IP access list. The Java API and frontend are deployed together; GitHub Pages alone cannot run this backend.

## Demo-data notice

The seeded books, members, and circulation records are fictional demo data. This project is not connected to BBAU identity systems or official university records. Confirm university policies and image permissions before official use.
