# API documentation

Base URL: `http://localhost:8080`. JSON endpoints use the `JSESSIONID` cookie returned by the server. Requests that change data must send `Content-Type: application/json`.

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| GET | `/api/health` | Public | Health status |
| GET | `/api/auth/session` | Public | Current signed-in user or `null` |
| POST | `/api/auth/register` | Public | Register a student; fields: `name`, `email`, `department`, `password` |
| POST | `/api/auth/login` | Public | Sign in; fields: `email`, `password`, `role` (`student` or `admin`) |
| POST | `/api/auth/logout` | Signed in | End the session |
| GET | `/api/state` | Signed in | Get the caller's library data |
| PUT | `/api/admin/state` | Admin | Save library arrays: `books`, `members`, `loans`, `reservations`, `activity` |
| POST | `/api/student/borrow` | Student | Borrow an available book; field: `bookId` |
| POST | `/api/student/reservations` | Student | Reserve an unavailable book; field: `bookId` |
| DELETE | `/api/student/reservations/{id}` | Reservation owner | Cancel a reservation |
| POST | `/api/support` | Signed in | Save a support message; field: `message` |

Errors return JSON in the shape `{"error":"Readable explanation"}` with an appropriate HTTP status code. Request bodies and response fields are implemented in `backend/src/main/java/com/library/dto/`.
