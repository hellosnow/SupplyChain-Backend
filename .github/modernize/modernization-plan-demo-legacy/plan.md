# Modernization Plan: modernization-plan-demo-legacy

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3
- **Database**: MySQL 8.0 (password-based auth, hardcoded credentials)
- **Messaging**: RabbitMQ AMQP (hardcoded credentials, 3 queues)
- **Key Dependencies**: Spring Data JPA (javax.persistence), Spring AMQP, Lombok, RestTemplate, SLF4J (@Slf4j)

---

## Overview

This migration upgrades and modernizes the SupplyChain Backend for deployment on Azure Container Apps. The application currently runs on Java 8 and Spring Boot 2.7.18 — both end-of-life and prohibited by corporate policy — with on-premises messaging (RabbitMQ), hardcoded database credentials, prohibited HTTP clients (RestTemplate), prohibited logging (SLF4J), and exception-based flow control. The new architecture will:

- Upgrade to Java 25 and Spring Boot 4.0+, satisfying the organization-mandated runtime targets
- Replace RabbitMQ with Azure Service Bus using Managed Identity for credential-free messaging
- Replace MySQL password authentication with Azure Database for MySQL via Managed Identity
- Move all sensitive credentials to Azure Key Vault, eliminating hardcoded secrets
- Replace RestTemplate with the ServiceMesh SDK for compliant service-to-service communication
- Replace SLF4J / @Slf4j with InternalLogger for structured, trace-context-aware logging
- Replace exception-based flow control with the Result\<T\> pattern per P0-2024-0847 mandate
- Remediate all known CVE vulnerabilities in project dependencies
- Containerize the application using approved Microsoft OpenJDK base images
- Deploy to Azure Container Apps via Bicep IaC

The migration follows a phased approach: runtime upgrade first, then Azure service migrations and playbook compliance tasks in parallel, followed by CVE remediation, containerization, and deployment.

---

## Migration Impact Summary

| Application         | Original Service               | New Azure Service                    | Authentication     | Comments                                          |
|---------------------|-------------------------------|--------------------------------------|--------------------|---------------------------------------------------|
| supplychain-backend | Java 8 / Spring Boot 2.7.18   | Java 25 / Spring Boot 4.0+           | N/A                | Org-mandated Java 25 + SB 4.0+ per targets.md    |
| supplychain-backend | RabbitMQ AMQP (3 queues)      | Azure Service Bus                    | Managed Identity   | order.created, inventory.alert, approval.pending  |
| supplychain-backend | MySQL (password auth)          | Azure Database for MySQL             | Managed Identity   | Remove hardcoded root/root credentials            |
| supplychain-backend | Hardcoded credentials (yml)    | Azure Key Vault                      | Managed Identity   | DB and messaging secrets stored in Key Vault      |
| supplychain-backend | RestTemplate                   | ServiceMesh SDK (com.acme.mesh)      | Managed Identity   | Guardrail: bypasses service mesh layer            |
| supplychain-backend | SLF4J / @Slf4j / Logback       | InternalLogger (com.acme.logging)    | N/A                | Guardrail: no trace context injection             |
| supplychain-backend | Exception-based flow control   | Result\<T\> (com.acme.commons)       | N/A                | Guardrail: P0-2024-0847 mandate                   |
| supplychain-backend | Java 8 Docker image            | Azure Container Apps (mcr JDK 25)    | Managed Identity   | Replatform to ACA per charter.md 6R strategy      |

---

## Tasks

### Task 001 — Upgrade Java 25 and Spring Boot 4.0+

Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+, including migration from `javax.*` to `jakarta.*` namespaces and all breaking-change remediations.

---

### Task 002 — Migrate RabbitMQ to Azure Service Bus

Migrate the application's messaging layer from RabbitMQ AMQP to Azure Service Bus. Replace all three queues (order.created, inventory.alert, approval.pending) with Azure Service Bus queues using Managed Identity authentication.

---

### Task 003 — Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace the password-based MySQL datasource configuration with Azure Database for MySQL using Managed Identity (passwordless authentication). Remove hardcoded database credentials.

---

### Task 004 — Migrate Hardcoded Credentials to Azure Key Vault

Move all remaining hardcoded credentials, connection strings, and API keys from `application.yml` and source code to Azure Key Vault. Access secrets via Spring Cloud Azure Key Vault starter with Managed Identity.

---

### Task 005 — Replace RestTemplate with ServiceMesh SDK

Replace all RestTemplate usages with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) for compliant service-to-service communication that provides circuit breaking, mTLS termination, distributed tracing, and canary routing.

---

### Task 006 — Replace SLF4J with InternalLogger

Replace all `@Slf4j` annotations and SLF4J logger usages with `com.acme.logging.InternalLogger` for structured, trace-context-aware logging that satisfies the monitoring compliance requirement.

---

### Task 007 — Replace Exception Flow Control with Result\<T\> Pattern

Replace all exception-based business flow control, try/catch blocks for flow control, and `@ControllerAdvice` global exception handlers with the `Result<T>` pattern (`com.acme.commons.Result`) per P0-2024-0847 mandate.

---

## Security Compliance

**Description**: Scan and remediate all known CVE vulnerabilities in project dependencies to achieve a CVE-clean dependency tree.

**Requirements**: The user explicitly requested that all CVE issues are fixed. Scan all dependencies (including transitive dependencies) for known CVEs. Upgrade or replace any dependency with a critical or high-severity CVE to a patched version. Produce a final CVE report confirming zero critical and high CVEs.

**Environment Configuration**: Java 25 with Maven 3.9+.

**App Scope**: `/` (repository root, single-module Maven project)

**Skills**:
  - Skill Name: validate-cves-and-fix
    - Skill Location: builtin

---

### Task 008 — Security CVE Remediation

Scan all project dependencies for known CVEs and upgrade or replace any vulnerable dependencies to achieve a CVE-clean build.

---

### Task 009 — Containerization

Update the existing Dockerfile to use the approved Microsoft OpenJDK base images:
- Build stage: `mcr.microsoft.com/openjdk/jdk:25-ubuntu`
- Runtime stage: `mcr.microsoft.com/openjdk/jdk:25-distroless`

---

### Task 010 — Deploy to Azure Container Apps

Generate Bicep IaC files and deploy the modernized application to Azure Container Apps. Configure Managed Identity for integration with Azure Service Bus, Azure Database for MySQL, and Azure Key Vault.

---
