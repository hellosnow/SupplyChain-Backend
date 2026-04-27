# Modernization Plan: modernization-plan-shizh

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3.8
- **Database**: MySQL 8.0 (hardcoded credentials in `application.yml`)
- **Messaging**: RabbitMQ 3 (AMQP, hardcoded credentials, 3 queues)
- **Key Dependencies**: Spring Data JPA, Spring AMQP (RabbitMQ), Lombok, Spring Validation, RestTemplate

---

## Overview

This migration modernizes the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+, migrates all on-premises services to Azure, and remediates all known CVE vulnerabilities. The application currently runs on end-of-life Java 8 with prohibited Spring Boot 2.x, uses on-premises RabbitMQ and MySQL with hardcoded credentials, and violates multiple corporate guardrails (RestTemplate, SLF4J, exception-based error handling). The new architecture will:

- Upgrade to Java 25 and Spring Boot 4.0+ to meet org-mandated runtime targets
- Replace RabbitMQ with Azure Service Bus for cloud-native managed messaging with Managed Identity
- Replace MySQL (password auth) with Azure Database for MySQL using Managed Identity for passwordless authentication
- Centralize all secrets and credentials in Azure Key Vault accessed via Managed Identity
- Replace RestTemplate with ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to comply with the service mesh communication policy
- Replace SLF4J with InternalLogger (`com.acme.logging.InternalLogger`) to enable trace-context-aware structured logging
- Adopt the Result\<T\> pattern (`com.acme.commons.Result`) to replace exception-based business logic flow control per P0-2024-0847
- Remediate all CVE vulnerabilities in project dependencies
- Containerize the application using Java 25 org-approved base images and deploy to Azure Container Apps (ACA)

The migration follows a phased approach: upgrade the runtime first, then transform services to Azure-native dependencies, remediate security issues, then containerize and provision infrastructure.

---

## Migration Impact Summary

| Application         | Original Service             | New Azure Service                  | Authentication   | Comments                                               |
|---------------------|------------------------------|------------------------------------|------------------|--------------------------------------------------------|
| supplychain-backend | Java 8 / Spring Boot 2.7.18  | Java 25 / Spring Boot 4.0+         | N/A              | Org-mandated targets; javax.* → jakarta.*              |
| supplychain-backend | RabbitMQ 3 (AMQP)            | Azure Service Bus                  | Managed Identity | 3 queues: order.created, inventory.alert, approval.pending |
| supplychain-backend | MySQL (password auth)        | Azure Database for MySQL           | Managed Identity | Remove hardcoded root/root credentials                 |
| supplychain-backend | Plaintext credentials        | Azure Key Vault                    | Managed Identity | All secrets externalized from application.yml          |
| supplychain-backend | RestTemplate                 | ServiceMesh SDK (com.acme.mesh)    | N/A              | Policy: all service-to-service communication via mesh  |
| supplychain-backend | SLF4J / @Slf4j               | InternalLogger (com.acme.logging)  | N/A              | Policy: trace-context logging required                 |
| supplychain-backend | Exception-based flow control | Result\<T\> (com.acme.commons)     | N/A              | Policy P0-2024-0847 mandate                            |
| supplychain-backend | Java 8 container base image  | Azure Container Apps (ACA)         | Managed Identity | Bicep-managed infrastructure, Java 25 distroless image |

---

## Tasks

### Task 001 — Upgrade Java and Spring Boot

Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+, including migration from `javax.*` to `jakarta.*` namespaces and all breaking-change remediations required for the framework upgrade.

### Task 002 — Migrate RabbitMQ to Azure Service Bus

Replace RabbitMQ AMQP messaging with Azure Service Bus. Migrate all three queues (order.created, inventory.alert, approval.pending) and update message producers and consumers in `PurchaseOrderService` and `InventoryService` to use Azure Service Bus with Managed Identity.

### Task 003 — Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace the password-based MySQL connection with Azure Database for MySQL using Managed Identity for passwordless authentication. Remove hardcoded `root/root` database credentials from `application.yml`.

### Task 004 — Migrate Hardcoded Credentials to Azure Key Vault

Move all hardcoded credentials from `application.yml` (datasource password, RabbitMQ username/password) to Azure Key Vault. Configure Spring Cloud Azure Key Vault starter to retrieve secrets at runtime using Managed Identity.

### Task 005 — Replace RestTemplate with ServiceMesh SDK

Replace all `RestTemplate` usages in `VendorService` and the `AppConfig` bean with the internal `ServiceMesh SDK` (`com.acme.mesh.ServiceMesh`) per the corporate service mesh communication policy.

### Task 006 — Replace SLF4J with InternalLogger

Replace all `@Slf4j` / SLF4J logging with `InternalLogger` (`com.acme.logging.InternalLogger`) across all service classes (`PurchaseOrderService`, `InventoryService`, `VendorService`).

### Task 007 — Replace Exception-Based Error Handling with Result\<T\>

Replace all exception-based flow control and `try/catch` blocks used for business logic in service classes with the `Result<T>` pattern (`com.acme.commons.Result`) per the P0-2024-0847 post-incident mandate.

---

## Security Compliance

**Description**: Scan and remediate all known CVE vulnerabilities in project dependencies to meet organizational security requirements.

**Requirements**: All project dependencies must be free of known CVE vulnerabilities. Current at-risk dependencies include Spring Boot 2.7.18 and `mysql-connector-java` 8.0.33. Upgrade or replace all vulnerable components and verify a CVE-clean state after remediation.

**Skills**:
- Skill Name: `validate-cves-and-fix`
  - Skill Location: `builtin`

### Task 008 — CVE Remediation

Scan all project dependencies for known CVEs and upgrade or replace all vulnerable components to achieve a CVE-clean dependency tree.

---

## Containerization

### Task 009 — Update Dockerfile for Java 25

Update the `Dockerfile` to use `mcr.microsoft.com/openjdk/jdk:25-ubuntu` as the build stage base image and `mcr.microsoft.com/openjdk/jdk:25-distroless` as the runtime stage base image per org-approved container targets.

---

## Infrastructure

### Task 010 — Generate Bicep Infrastructure for Azure Container Apps

Generate Bicep IaC files to provision Azure Container Apps (ACA), Azure Database for MySQL Flexible Server, Azure Service Bus namespace, Azure Key Vault, and Azure Container Registry for the modernized application.

---
