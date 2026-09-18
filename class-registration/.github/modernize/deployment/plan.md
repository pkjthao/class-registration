# Deployment modernization plan

## Project scope
- Spring Boot application packaged with Maven
- Java 17 runtime
- JSP-based web UI with PostgreSQL-backed persistence
- Target: Azure deployment readiness with a container build that does not depend on localhost-only configuration

## Current findings
- The default application profile is `dev`, which loads `application-dev.properties`.
- That dev profile points to `localhost:5432` and local database credentials.
- This configuration is not valid inside a container or Azure-hosted environment and would fail at runtime when the app is started in a container.

## Planned changes
1. Externalize database and runtime settings via environment variables so the application reads configuration from the container environment instead of local-only files.
2. Remove the default dev-profile dependency from the base configuration so the container uses production-style env-driven config.
3. Add a Dockerfile using a multi-stage Maven build and a slim Java runtime image.
4. Add `.dockerignore` to keep the build context small and avoid unnecessary artifacts.
5. Verify the project still builds with Maven and the Docker image can be built successfully from the repo.

## Files to create or update
- `Dockerfile`
- `.dockerignore`
- `src/main/resources/application.properties`
- `src/main/resources/application-prod.properties` (if needed for consistency)

## Validation steps
- `./mvnw -DskipTests package`
- `docker build -t class-registration:local .`

## Deployment notes
- This app requires a PostgreSQL service in Azure or another managed database.
- The runtime should set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and optionally `SPRING_PROFILES_ACTIVE` when deployed.
- Container port should be exposed as `8080` unless the target platform overrides it.
