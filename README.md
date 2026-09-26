# Class Registration System

This repository contains a Java Spring Boot application for managing class registrations, instructor accounts, student accounts, and course enrollment workflows.

The application source code lives in the `class-registration/` directory.

## Features

- Student registration and login
- Instructor registration and login
- Course creation and management
- Enrollments and registration tracking
- Role-based access control with Spring Security
- JSP-based UI views
- PostgreSQL-backed data persistence
- Email integration for notifications

## Tech Stack

- Java 17
- Spring Boot 4.0.6
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven
- JSP / JSTL
- HTML / CSS / JavaScript

## Project Structure

```text
.
├── README.md
└── class-registration/
    ├── pom.xml
    ├── mvnw
    ├── mvnw.cmd
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   ├── resources/
    │   │   └── webapp/
    │   └── test/
    └── .mvn/
```

## Getting Started

1. Open a terminal in the `class-registration/` directory.
2. Configure PostgreSQL and update your database credentials in `src/main/resources/application.properties` or `application-dev.properties`.
3. Create the database used by the app, for example:

```sql
CREATE DATABASE class_registration_db;
```

4. Build and run the application:

```bash
./mvnw clean spring-boot:run
```

On Windows:

```bash
mvnw.cmd clean spring-boot:run
```

5. Open the app in a browser at:

```text
http://localhost:8080
```

## Default Configuration

The project includes Spring profiles and database settings for a local PostgreSQL environment. You may need to adjust values such as:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

## Security

The application uses Spring Security with role-based authorization for:

- Student routes
- Instructor routes
- Course APIs
- Registration workflows

## Notes

- JSP views are configured under `src/main/webapp/WEB-INF/views/`.
- Static resources such as CSS and JavaScript are stored in `src/main/resources/static/`.
- This project is intended as a full-stack web application for course and student management.

## License

This project does not currently declare a license in the repository metadata.
