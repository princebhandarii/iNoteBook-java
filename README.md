# iNotebook

Private notes app: Spring Boot 3 (Java 17) + MySQL + a single-page vanilla JS UI (sage-olive and cream theme).

## Requirements
- JDK 17+
- Maven 3.8+
- MySQL 8+ running locally

## Run
1. Start MySQL. The `inotebook` database is created automatically (`createDatabaseIfNotExist=true`).
2. Set credentials (defaults are `root` / `root`):
   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD=your_password
   # optional: export DB_URL="jdbc:mysql://host:3306/inotebook?useSSL=true&serverTimezone=UTC"
   ```
3. Start the app:
   ```bash
   mvn spring-boot:run
   ```
4. Open http://localhost:8080

Build a runnable jar: `mvn clean package`, then `java -jar target/inotebook-1.0.0.jar`.

## Schema (created by Hibernate)
```sql
users(id BIGINT PK AUTO_INCREMENT, username VARCHAR(50) UNIQUE NOT NULL, password VARCHAR(100) NOT NULL)
notes(id BIGINT PK AUTO_INCREMENT, title VARCHAR(200) NOT NULL, content TEXT NOT NULL,
      owner_username VARCHAR(50) NOT NULL, FOREIGN KEY (owner_username) REFERENCES users(username))
```

## REST API
| Method | Path | Purpose |
|---|---|---|
| POST | /api/auth/register | Create account |
| POST | /api/auth/login | Log in (starts HTTP session) |
| GET | /api/auth/me | Current user |
| POST | /api/auth/logout | End session |
| GET | /api/notes | List my notes |
| POST | /api/notes | Create note |
| PUT | /api/notes/{id} | Update note |
| DELETE | /api/notes/{id} | Delete note |

## Production notes
- Passwords are stored as BCrypt hashes.
- Every note query is scoped to the logged-in user, so users cannot read or edit each other's notes.
- Session cookie is HttpOnly and SameSite=Lax. Serve over HTTPS and set `COOKIE_SECURE=true`.
- For stricter production setups, use `spring.jpa.hibernate.ddl-auto=validate` with Flyway/Liquibase, and add login rate limiting.
