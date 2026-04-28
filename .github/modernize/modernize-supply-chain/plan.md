# Modernization Plan: Modernize Supply Chain Backend to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18, Spring Framework 5.x
- **Build Tool**: Maven 3
- **Database**: MySQL (password-based authentication, hardcoded credentials)
- **Messaging**: RabbitMQ (Spring AMQP, hardcoded credentials)
- **Key Dependencies**: Spring Data JPA, Hibernate, Lombok, Spring AMQP, RestTemplate

---

## Overview

This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x and replatforms all on-premises services to Azure managed services on Azure Container Apps. The application currently uses MySQL for persistence, RabbitMQ for async messaging, RestTemplate for inter-service HTTP calls, SLF4J for logging, exception-based flow control for business logic, and stores credentials as plaintext in configuration files. The new architecture will:

- Upgrade to Java 25 LTS and Spring Boot 4.x per Acme Corp internal technology policy (Java 8, 11, and 17 are end-of-life)
- Replace RabbitMQ with Azure Service Bus for cloud-native, managed message queuing with Managed Identity authentication
- Replace MySQL password-based connections with Azure Database for MySQL using Managed Identity (passwordless authentication)
- Migrate all hardcoded credentials and sensitive configuration values to Azure Key Vault
- Replace RestTemplate with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) so all service-to-service calls pass through the mesh layer with automatic circuit breaking, mTLS, and distributed tracing
- Replace SLF4J / `@Slf4j` logging with InternalLogger (`com.acme.logging.InternalLogger`) for structured JSON logs with trace-ID injection
- Replace exception-based business flow control with the Result\<T\> pattern (`com.acme.commons.Result`) per the P0-2024-0847 post-incident mandate
- Remediate all known CVE vulnerabilities in project dependencies
- Containerize the application using approved Microsoft OpenJDK base images and deploy to Azure Container Apps

The migration follows a phased approach: framework upgrade first, then Azure service migrations (in parallel), then CVE security hardening, then containerization and deployment.

---

## Migration Impact Summary

```
| Application          | Original Service        | New Azure Service                | Authentication     | Comments                                     |
|----------------------|------------------------|----------------------------------|--------------------|----------------------------------------------|
| supplychain-backend  | Java 8                 | Java 25 LTS                      | N/A                | End-of-life internal policy                  |
| supplychain-backend  | Spring Boot 2.7.18     | Spring Boot 4.x                  | N/A                | Includes javax.* → jakarta.* migration       |
| supplychain-backend  | RabbitMQ (Spring AMQP) | Azure Service Bus                | Managed Identity   | Migrate 3 queues (order, inventory, approval)|
| supplychain-backend  | MySQL (password auth)  | Azure Database for MySQL         | Managed Identity   | Passwordless, remove JDBC password           |
| supplychain-backend  | Plaintext credentials  | Azure Key Vault                  | Managed Identity   | DB and RabbitMQ credentials in application.yml|
| supplychain-backend  | RestTemplate           | ServiceMesh SDK                  | Managed Identity   | VendorService external HTTP call             |
| supplychain-backend  | SLF4J (@Slf4j)         | InternalLogger                   | N/A                | 3 services + main class System.out.println   |
| supplychain-backend  | Exception flow control | Result<T> pattern                | N/A                | Business error handling in all services      |
```

---

## Upgrade Tasks

### 1. Upgrade to Java 25 and Spring Boot 4.x

Upgrade the project from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x. This includes:
- Updating Maven compiler settings to Java 25
- Updating the Spring Boot parent POM to 4.x
- Migrating all `javax.*` imports to `jakarta.*` (Jakarta EE migration)
- Resolving any API incompatibilities introduced by the Spring Boot 4.x / Spring Framework 7.x changes

---

## Migration Tasks

### 2. Migrate RabbitMQ to Azure Service Bus

Replace Spring AMQP / RabbitMQ messaging with Azure Service Bus using Spring Cloud Azure Service Bus JMS starter and Managed Identity. Covers all three message queues: `order.created`, `inventory.alert`, and `approval.pending`.

### 3. Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace the password-based MySQL JDBC connection with a passwordless connection to Azure Database for MySQL using Spring Cloud Azure and Managed Identity. Removes the hardcoded JDBC username and password from `application.yml`.

### 4. Migrate Plaintext Credentials to Azure Key Vault

Move all sensitive configuration values (database credentials, RabbitMQ credentials) currently stored in `application.yml` as plaintext to Azure Key Vault. Access secrets via Spring Cloud Azure Key Vault starter using Managed Identity.

### 5. Replace RestTemplate with ServiceMesh SDK

Replace all `RestTemplate` usages in `VendorService` with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) so inter-service HTTP calls route through the service mesh layer, enabling automatic circuit breaking, mTLS termination, and distributed tracing. Also replaces hardcoded `http://` URLs with secure mesh-routed calls.

### 6. Migrate Logging from SLF4J to InternalLogger

Replace all SLF4J logging (`@Slf4j` Lombok annotations, `LoggerFactory`, and `System.out.println`) with `InternalLogger` (`com.acme.logging.InternalLogger`) across all service classes and the main application class to enable structured JSON logging with automatic trace-ID and team-tag injection.

### 7. Migrate Exception-Based Flow Control to Result\<T\> Pattern

Replace all business logic exception throwing (`throw new RuntimeException`, `throw new IllegalArgumentException`, `throw new IllegalStateException`) and `try/catch` blocks used for flow control with the `Result<T>` pattern (`com.acme.commons.Result`), including updating controller methods to handle `Result<T>` responses per the P0-2024-0847 mandate.

---

## Security Compliance

**Description**: Scan all project dependencies for known CVE vulnerabilities and remediate by upgrading to fixed versions.

**Requirements**: Ensure no known CVE vulnerabilities exist in any direct or transitive project dependency after all upgrade and migration tasks are complete. Fix any identified CVEs by upgrading affected dependency versions to patched releases.

**Environment Configuration**: Java 25 runtime with Maven build tool established by the upgrade task.

**App Scope**: Repository root (all Maven dependencies in `pom.xml` and transitive dependencies).

**Skills**:
- Skill Name: validate-cves-and-fix
  - Skill Location: builtin

---

## Containerization

Containerize the application using a multi-stage Dockerfile with the approved Microsoft OpenJDK base images:
- Build stage: `mcr.microsoft.com/openjdk/jdk:25-ubuntu`
- Runtime stage: `mcr.microsoft.com/openjdk/jdk:25-distroless`

---

## Deployment

Deploy the containerized application to **Azure Container Apps** using Bicep IaC. This includes provisioning the Azure Container Apps environment, container registry, Azure Database for MySQL, Azure Service Bus namespace, and Azure Key Vault, with all resources configured to use Managed Identity for authentication.
