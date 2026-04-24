# Modernization Plan: modernization-plan-shizh

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18 / Spring Framework 5.x
- **Build Tool**: Maven 3
- **Database**: MySQL 8.0 (password-based, hardcoded credentials)
- **Messaging**: RabbitMQ via Spring AMQP (hardcoded credentials)
- **Key Dependencies**: Spring Data JPA, Spring AMQP, Lombok, Spring Validation, RestTemplate

---

## Overview

This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 and migrates it to Azure Container Apps. The application currently uses RabbitMQ for messaging, MySQL with hardcoded credentials, RestTemplate for HTTP calls, SLF4J for logging, and exception-based flow control — all of which violate corporate policy or require Azure migration. The new architecture will:

- Upgrade the runtime from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+ per corporate mandate (targets.md)
- Replace RabbitMQ with Azure Service Bus and MySQL with Azure Database for MySQL using Managed Identity
- Migrate all hardcoded credentials to Azure Key Vault
- Replace RestTemplate with the ServiceMesh SDK, SLF4J with InternalLogger, and exception-based flow control with the Result\<T\> pattern per policy guardrails
- Scan and remediate all CVE vulnerabilities in project dependencies
- Containerize the application using approved base images and deploy to Azure Container Apps

The migration follows a phased approach: runtime upgrade first, then Azure service migrations and policy compliance changes in parallel, followed by security scanning, containerization, and deployment.

---

## Migration Impact Summary

| Application         | Original Service            | New Azure Service              | Authentication   | Comments                                   |
|---------------------|-----------------------------|--------------------------------|------------------|--------------------------------------------|
| supplychain-backend | Java 8 / Spring Boot 2.7.18 | Java 25 / Spring Boot 4.0+     | N/A              | Org-mandated upgrade per targets.md        |
| supplychain-backend | RabbitMQ (Spring AMQP)      | Azure Service Bus              | Managed Identity | Migrate messaging from RabbitMQ            |
| supplychain-backend | MySQL (password-based)      | Azure Database for MySQL       | Managed Identity | Migrate database with passwordless auth    |
| supplychain-backend | Hardcoded credentials (YAML)| Azure Key Vault                | Managed Identity | Secrets management per policy              |
| supplychain-backend | RestTemplate                | ServiceMesh SDK                | N/A              | Policy: all service comms via ServiceMesh  |
| supplychain-backend | SLF4J (@Slf4j)              | InternalLogger                 | N/A              | Policy: InternalLogger for all logging     |
| supplychain-backend | Exception-based flow control| Result\<T\> pattern            | N/A              | Policy: P0-2024-0847 mandate               |

---

## Tasks

### Task 001 — Upgrade Java and Spring Boot

Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+, including migration from `javax.*` to `jakarta.*` namespaces and all breaking-change remediations required by the framework upgrade.

---

### Task 002 — Migrate RabbitMQ to Azure Service Bus

Migrate all Spring AMQP / RabbitMQ messaging code to Azure Service Bus with Managed Identity for secure, credential-free messaging.

---

### Task 003 — Migrate MySQL to Azure Database for MySQL

Migrate the MySQL datasource to Azure Database for MySQL using Managed Identity to eliminate password-based authentication.

---

### Task 004 — Migrate Hardcoded Credentials to Azure Key Vault

Move all hardcoded credentials and sensitive configuration values in `application.yml` to Azure Key Vault, accessed via Managed Identity.

---

### Task 005 — Replace RestTemplate with ServiceMesh SDK

Replace all `RestTemplate` usages with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to ensure all service-to-service communication goes through the mesh layer (circuit breaking, mTLS, distributed tracing, canary routing).

---

### Task 006 — Replace SLF4J Logging with InternalLogger

Replace all SLF4J (`@Slf4j`, `LoggerFactory`) and any other prohibited logging frameworks with `InternalLogger` (`com.acme.logging.InternalLogger`) to enable trace ID injection, team tags, and structured JSON logging.

---

### Task 007 — Replace Exception-Based Flow Control with Result\<T\> Pattern

Replace all exception-based business logic flow control (`throw`, `try/catch` for flow, `@ControllerAdvice` for business exceptions) with the `Result<T>` pattern (`com.acme.commons.Result`) per the P0-2024-0847 mandate.

---

## Security Compliance

**Description**: Scan and fix all CVE vulnerabilities in project dependencies.

**Requirements**: Scan all project dependencies for known CVE vulnerabilities and upgrade affected dependencies to secure versions. Ensure no critical or high CVE vulnerabilities remain after remediation. Build must continue to pass after all dependency updates.

**Environment Configuration**: Java 25, Maven 3.9+, Spring Boot 4.0+ (established by Task 001).

**App Scope**: `.` (root project — `supplychain-backend`)

---

## Tasks (continued)

### Task 008 — Fix CVE Vulnerabilities

Scan all project dependencies for known CVE vulnerabilities and remediate them by upgrading to secure, non-vulnerable versions.

---

### Task 009 — Containerize Application

Update the existing `Dockerfile` to use the approved multi-stage build with `mcr.microsoft.com/openjdk/jdk:25-ubuntu` as the build image and `mcr.microsoft.com/openjdk/jdk:25-distroless` as the runtime image, ensuring the container is production-ready for Azure Container Apps.

---

### Task 010 — Deploy to Azure Container Apps

Deploy the containerized application to Azure Container Apps using Bicep IaC. Provision required Azure resources (Azure Container Apps environment, Azure Service Bus namespace, Azure Database for MySQL, Azure Key Vault) and configure Managed Identity bindings.

---
