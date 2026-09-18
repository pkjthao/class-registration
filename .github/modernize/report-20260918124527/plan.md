# Modernization plan — report-20260918124527

## Scope and sources

- Assessment report: .github/modernize/assessment/reports/report-20260918124527/report.json
- Selected categories: java-version-upgrade, database-migration, local-credential, local-resource-access, containerization
- Required security remediation: CWE-259, CWE-778, and CWE-798
- Project language: Java 17 / Spring Boot / Maven
- Project name: class-registration

> No [kbId: ...] markers were present in the selected assessment report, so no knowledge-base IDs were added to the tasks. The plan stays aligned to the assessment findings and the required security tasks.

## Objective

Modernize the application to align with Azure-ready deployment guidance while removing hard-coded credentials and other security vulnerabilities flagged by the assessment. The scope is intentionally limited to the issues reported for this project and the mandatory CWE remediation items.

## Planned workstreams

### 1) Java runtime modernization
- Upgrade the project from Java 17 to a supported LTS Java version (target Java 21 or the platform-standard LTS recommended by the Azure modernization target).
- Validate the build and runtime compatibility of the Spring Boot project with the new JDK.
- Update build configuration, CI settings, and deployment runtime settings as needed.

### 2) Database migration from local PostgreSQL to Azure-managed PostgreSQL
- Replace localhost JDBC configuration with Azure Database for PostgreSQL Flexible Server connection settings.
- Move credentials out of source-controlled property files and into environment variables or Azure Key Vault.
- Update configuration files for development, staging, and production and ensure the app can connect using secure configuration.

### 3) Secret and credential remediation
- Remove hard-coded database credentials from application.properties and application-dev.properties.
- Replace plain-text credential usage with environment-backed configuration and Azure Key Vault integration.
- Add secret scanning and configuration validation to prevent regression.

### 4) Local resource access cleanup
- Eliminate the local JDBC dependency from the app configuration so it can run in a cloud-ready environment.
- Reconfigure the application to use managed Azure services instead of embedded or localhost resources.
- Validate startup behavior against the Azure-ready configuration.

### 5) Containerization and deployment readiness
- Add a Dockerfile for the application so it can be deployed to Azure Container Apps or AKS.
- Ensure the container image builds from the repo and does not rely on local dev-only configuration.
- Document container startup requirements and runtime settings for Azure deployment.

### 6) Security remediation for CWE findings
- CWE-259 — Use of Hard-coded Password: remove embedded passwords from application configuration and replace with injected secrets.
- CWE-778 — Insufficient Logging: add audit logging for successful and failed authentication attempts in the authentication flow.
- CWE-798 — Use of Hard-coded Credentials: remove hard-coded database username/password values and protect all operational credentials with secure secret storage.
- Run a secret scan and dependency review that checks the project for the resolved issues before sign-off.

## Execution order

1. Remove hard-coded credentials and secure configuration sources.
2. Upgrade the Java runtime and confirm the project still builds.
3. Rework local database references to cloud-ready managed PostgreSQL connectivity.
4. Add Docker support and cloud deployment readiness.
5. Add logging for authentication events and validate security behavior.
6. Validate the project build, tests, and secret scan for final sign-off.

## Acceptance criteria

- Java version upgrade is implemented and the build passes.
- No plaintext database credentials remain in source-controlled configuration files.
- Local JDBC configuration is replaced with cloud-safe configuration.
- A Dockerfile is present for Azure container deployment.
- Successful and failed login attempts are logged with sufficient audit detail.
- CWE-259, CWE-778, and CWE-798 are addressed and verified by a fresh security scan.

## Selected categories mapped to tasks

- java-version-upgrade → Java runtime modernization
- database-migration → Azure Database for PostgreSQL migration
- local-credential → secret and credential remediation
- local-resource-access → application configuration cleanup for cloud deployment
- containerization → Dockerfile and deployment readiness
- security/CWE-259 → hard-coded password remediation
- security/CWE-778 → logging remediation
- security/CWE-798 → hard-coded credential remediation
