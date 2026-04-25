# Modernization Plan: SupplyChain Backend — Legacy Modernization to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3
- **Database**: MySQL 8.0 (password-based, hardcoded credentials)
- **Messaging**: RabbitMQ (AMQP, hardcoded credentials)
- **Key Dependencies**: Spring Data JPA (javax.persistence), Spring AMQP (RabbitMQ), Lombok, RestTemplate, SLF4J (Lombok @Slf4j)

---

## Overview

This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 and re-platforms it to Azure. The application currently runs on an end-of-life Java 8 runtime with a prohibited Spring Boot 2.x version, communicates via on-premises RabbitMQ messaging, connects to a plain-password MySQL database, and embeds hardcoded credentials throughout its configuration. Logging is done via SLF4J, HTTP calls use RestTemplate, and business error handling relies on exception-based flow control — all of which violate the corporate guardrails. The new architecture will:

- Upgrade the runtime to Java 25 and Spring Boot 4.0+ (organization-mandated targets), including migration from `javax.*` to `jakarta.*` namespaces
- Replace RabbitMQ AMQP messaging with Azure Service Bus for cloud-native, managed message brokering
- Replace password-based MySQL connectivity with Azure Database for MySQL using Managed Identity for secure, credential-free database access
- Remove all hardcoded credentials from configuration and source code and store them in Azure Key Vault
- Replace RestTemplate with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) for all service-to-service communication in compliance with mesh guardrails
- Replace SLF4J (`@Slf4j`) logging with InternalLogger (`com.acme.logging.InternalLogger`) across all services and controllers
- Replace exception-based flow control with the `Result<T>` pattern (`com.acme.commons.Result`) in all service methods
- Scan and remediate all CVE vulnerabilities found in project dependencies
- Containerize the application using the approved Microsoft OpenJDK 25 base images for deployment to Azure Container Apps
- Generate Bicep infrastructure-as-code to provision all required Azure resources (Container Apps environment, Azure Service Bus, Azure Database for MySQL, Azure Key Vault, Azure Container Registry)

The migration follows a phased approach: runtime upgrade first, then Azure service integrations, followed by security hardening and infrastructure provisioning.

---

## Migration Impact Summary

| Application         | Original Service              | New Azure Service                         | Authentication    | Comments                                         |
|---------------------|-------------------------------|-------------------------------------------|-------------------|--------------------------------------------------|
| supplychain-backend | Java 8 / Spring Boot 2.7.18   | Java 25 / Spring Boot 4.0+                | N/A               | Org-mandated targets; javax.* → jakarta.*        |
| supplychain-backend | RabbitMQ (AMQP)               | Azure Service Bus                         | Managed Identity  | Replace spring-boot-starter-amqp + RabbitTemplate |
| supplychain-backend | MySQL (password-based)        | Azure Database for MySQL                  | Managed Identity  | Passwordless auth via Spring Cloud Azure         |
| supplychain-backend | Hardcoded credentials (yml)   | Azure Key Vault                           | Managed Identity  | DB password, RabbitMQ password → Key Vault       |
| supplychain-backend | RestTemplate                  | ServiceMesh SDK (com.acme.mesh)           | ServiceMesh mTLS  | Guardrail: RestTemplate bypasses mesh layer      |
| supplychain-backend | SLF4J / @Slf4j                | InternalLogger (com.acme.logging)         | N/A               | Guardrail: SLF4J has no trace context integration |
| supplychain-backend | Exception-based flow control  | Result\<T\> (com.acme.commons.Result)     | N/A               | Guardrail: P0-2024-0847 post-incident mandate    |
| supplychain-backend | Dependency CVEs               | Remediated dependencies                   | N/A               | Scan and fix all CVE issues in pom.xml           |
| supplychain-backend | Local Docker (Java 8 image)   | Azure Container Apps (ACA)                | Managed Identity  | Update Dockerfile to use OpenJDK 25 images       |
| supplychain-backend | Manual provisioning           | Azure Bicep IaC                           | N/A               | Provision ACA, Service Bus, MySQL, Key Vault, ACR |

---

## Tasks

### Task 001 — Upgrade Java and Spring Boot

Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+, including migration from `javax.*` to `jakarta.*` namespaces and all breaking-change remediations required for the framework upgrade.

---

### Task 002 — Migrate RabbitMQ to Azure Service Bus

Replace the RabbitMQ AMQP messaging integration (spring-boot-starter-amqp, RabbitTemplate) with Azure Service Bus using Managed Identity for authentication. Migrate all message producers and consumers in PurchaseOrderService and InventoryService, and remove RabbitMQ connection configuration from application.yml.

---

### Task 003 — Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace the password-based MySQL JDBC connection with Azure Database for MySQL using Managed Identity for passwordless, secure database access. Update Spring datasource configuration and replace the mysql-connector-java driver with the Spring Cloud Azure MySQL MI starter.

---

### Task 004 — Migrate Hardcoded Credentials to Azure Key Vault

Remove all hardcoded credentials from application.yml (database password, RabbitMQ password) and source code, and store them securely in Azure Key Vault. Access secrets via the Spring Cloud Azure Key Vault starter with Managed Identity.

---

### Task 005 — Replace RestTemplate with ServiceMesh SDK

Replace all usages of RestTemplate (AppConfig bean, VendorService.getVendorRatingFromExternalService) with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to comply with mesh guardrails and ensure automatic circuit breaking, mTLS termination, distributed tracing, and canary routing.

---

### Task 006 — Migrate Logging to InternalLogger

Replace all SLF4J logging (Lombok @Slf4j annotations, log.info/log.warn/log.error/log.debug calls) in service and controller classes with InternalLogger (`com.acme.logging.InternalLogger`) to comply with corporate logging guardrails and ensure trace context integration.

---

### Task 007 — Migrate Error Handling to Result\<T\> Pattern

Replace all exception-based flow control (throwing IllegalArgumentException, IllegalStateException, RuntimeException for business logic) in PurchaseOrderService, InventoryService, and VendorService, and remove try/catch blocks used for flow control. Adopt the `Result<T>` pattern (`com.acme.commons.Result`) for all service methods.

---

## Security Compliance

**Description**: Scan all project dependencies for known CVE vulnerabilities and remediate them to achieve a CVE-clean dependency set.

**Requirements**:
All CVE issues identified in the project's Maven dependencies (pom.xml) must be fixed. This includes upgrading vulnerable dependency versions, replacing vulnerable libraries with safe alternatives, and verifying the project builds and tests pass after remediation.

**Environment Configuration**:
Java 25 runtime and Maven build tool established by the upgrade task (001-upgrade-java-springboot).

**App Scope**:
The root Maven project at `/` (pom.xml).

**Skills**:
- Skill Name: validate-cves-and-fix
  - Skill Location: builtin

---

### Task 008 — Fix CVE Vulnerabilities

Scan all Maven dependencies in pom.xml for known CVE vulnerabilities and remediate all identified issues. Ensure the project compiles and all unit tests pass after remediation.

---

## Containerization

### Task 009 — Containerize Application for Azure Container Apps

Update the existing Dockerfile to use the approved Microsoft OpenJDK 25 base images (`mcr.microsoft.com/openjdk/jdk:25-ubuntu` for the build stage and `mcr.microsoft.com/openjdk/jdk:25-distroless` for the runtime stage), replacing the legacy Java 8 images. Ensure the containerized application is ready for deployment to Azure Container Apps.

---

## Infrastructure

### Task 010 — Generate Bicep Infrastructure for Azure Container Apps

Generate Bicep infrastructure-as-code files to provision all required Azure resources: Azure Container Apps environment, Azure Container Registry, Azure Service Bus namespace and queues, Azure Database for MySQL Flexible Server, and Azure Key Vault. The IaC should configure all services with Managed Identity for secure, passwordless authentication.

---
