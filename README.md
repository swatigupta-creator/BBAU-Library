# BBAU Digital Library — demo

A responsive library management web app prototype for Babasaheb Bhimrao Ambedkar University. It is designed to help explore the intended product experience and key circulation workflows.

## Run locally

Open `index.html` in a browser, or serve the folder with any static server. For example:

```powershell
npx serve .
```

No build step or runtime dependencies are required.

## Included in this demo

- Role picker on open with separate Student and Admin demo sign-in flows
- Student dashboard with personal loans, due dates, and reservations
- Admin dashboard with sample collection and circulation metrics
- Searchable, filterable book catalog with availability
- Admin can add titles and members; issue and return books with inventory updates
- Reservation queue with cancellation and fulfillment
- Member directory, reports CSV export, public digital resource links, and support form
- Browser localStorage persistence for the demo records

## Demo sign-in

- Student demo account: `student@bbau.ac.in` / `student123`
- New students can create separate browser-local accounts from the Student sign-in screen.
- Admin: `admin@bbau.ac.in` / `admin123`

Student registration and login are stored in this browser only. Passwords are hashed locally for this demonstration. The role gate and permissions remain client-side demo behavior only and are not secure production authentication.

## Demo data and production work

All seeded books, member records, activity, metrics, and policies are fictional demonstration data. The app runs entirely in the browser and does not implement real authentication, server-side authorization, a PostgreSQL database, email notifications, or durable shared storage. Do not use it to manage real library accounts or circulation. The university's current policies, official contact details, and photographs have not been verified; replace placeholders with approved institutional information before publication.

A production implementation should add a backend API, PostgreSQL with migrations, secure server-side authentication and role checks, transactional inventory operations, audit logging, validation, and integration tests. The UI's localStorage model is intended only for a single-browser walkthrough.




## Photos and motion

The login panel and Library gallery use photographs of Gautam Buddha Central Library from Wikimedia Commons. Credits and CC BY-SA 4.0 license links appear in the gallery. The photos load from Wikimedia, so an internet connection is needed to display them. Page transitions and card hover motion respect reduced-motion preferences.

