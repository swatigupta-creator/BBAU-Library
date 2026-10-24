# BBAU Digital Library

A responsive library management website for Babasaheb Bhimrao Ambedkar University. It includes a Node.js/Express API, SQLite persistence, server-side student/admin accounts, and role-checked borrowing and library management actions.

## Requirements

- Node.js 22.5 or newer
- npm

## Run locally

1. Open this folder in VS Code.
2. Install dependencies: npm install
3. Run npm run setup. It creates a private .env file and prints a one-time administrator password; save that password.
4. Start the app: npm start
5. Open http://localhost:3000 and sign in as admin@bbau.ac.in using the generated password. New students can register from the Student login screen.

The .env file is ignored by Git. To change the administrator password later, edit ADMIN_PASSWORD in .env and restart the server.

The SQLite database is created at data/bbau-library.sqlite the first time the server runs. Sample book and circulation records are inserted for a first walkthrough. Student accounts are created from the Student registration flow. The administrator email and password come from .env.

## Backend

- Express serves the frontend and JSON API from one origin.
- SQLite stores the library state and user records persistently on disk.
- Passwords are hashed with bcrypt; sessions use HTTP-only, same-site cookies.
- Login requests are rate-limited.
- Student endpoints only allow a student to borrow available books, reserve unavailable titles, and cancel their own reservations.
- Admin-only state updates back the add-book, add-member, issue/return, reservation queue, and report features.
- Support requests are saved to SQLite.

## Important deployment note

For production, use HTTPS, set a strong persistent SESSION_SECRET, configure an administrator password through the hosting provider's secret settings, and attach persistent disk storage for the SQLite database. Sessions and app data are stored in SQLite on the attached disk. The single-file database is intended for a single server instance; use a managed database before horizontal scaling. Never commit .env or the SQLite database.

## Demo data and limitations

Seeded catalogue, members, and circulation entries are fictional demo records. Confirm university policies, contact details, and image permissions before using this as an official BBAU service. This is a functional single-server project; it is not yet integrated with university identity systems, email, or a managed PostgreSQL service.
