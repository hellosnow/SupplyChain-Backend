# Modernization Plan: SupplyChain Backend — Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3
- **Database**: MySQL 8.0 (local, hardcoded credentials)
- **Key Dependencies**: Spring Data JPA, Spring AMQP (RabbitMQ), Lombok,
  Spring Validation, mysql-connector-java 8.0.33

---

## Overview

This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18
to Java 25 / Spring Boot 4.0+ and replatforms it to Azure Container Apps. The
application currently runs on end-of-life Java 8 and prohibited Spring Boot 2.x,
uses RabbitMQ for three message queues, MySQL with hardcoded credentials for data
storage, SLF4J for logging, and RestTemplate for HTTP calls — all in violation of
corporate guardrails. The new architecture will:

- Eliminate end-of-life Java 8 and prohibited Spring Boot 2.x; upgrade to
  Java 25 and Spring Boot 4.0+ as mandated by targets.md
- Replace RabbitMQ with Azure Service Bus using Managed Identity for
  cloud-native, passwordless messaging
- Replace local MySQL with Azure Database for MySQL using Managed Identity,
  removing all hardcoded database credentials
- Move all remaining secrets and credentials to Azure Key Vault (required
  element per policies.md) accessed via Managed Identity
- Replace SLF4J / @Slf4j with InternalLogger (com.acme.logging.InternalLogger)
  to enforce trace ID injection and structured JSON logging
- Replace RestTemplate with the ServiceMesh SDK (com.acme.mesh.ServiceMesh)
  for all service-to-service HTTP communication, enabling circuit breaking and
  mTLS
- Replace exception-based flow control with the Result<T> pattern
  (com.acme.commons.Result) per post-incident mandate P0-2024-0847
- Validate and remediate all CVE vulnerabilities in project dependencies to
  achieve a CVE-clean state
- Containerize the application and deploy it to Azure Container Apps following
  corporate container standards

The migration follows a phased approach: framework upgrade first, then parallel
Azure service migrations, followed by CVE remediation, containerization, and
infrastructure provisioning.

---

## Migration Impact Summary

| Application         | Original Service        | New Azure Service              | Authentication   | Comments                            |
|---------------------|-------------------------|--------------------------------|------------------|-------------------------------------|
| supplychain-backend | Java 8 / Spring Boot 2.7.18 | Java 25 / Spring Boot 4.0+ | N/A          | Org-mandated LTS versions           |
| supplychain-backend | RabbitMQ (3 queues)     | Azure Service Bus              | Managed Identity | order.created, inventory.alert,     |
|                     |                         |                                |                  | approval.pending queues             |
| supplychain-backend | Local MySQL             | Azure Database for MySQL       | Managed Identity | Passwordless authentication         |
| supplychain-backend | Hardcoded credentials   | Azure Key Vault                | Managed Identity | All secrets externalized            |
| supplychain-backend | SLF4J / @Slf4j          | InternalLogger                 | N/A              | Guardrail: trace IDs required       |
| supplychain-backend | RestTemplate            | ServiceMesh SDK                | Managed Identity | Guardrail: mesh layer required      |
| supplychain-backend | Exception-based flow    | Result<T> pattern              | N/A              | Mandate P0-2024-0847                |
| supplychain-backend | Local deployment        | Azure Container Apps           | Managed Identity | Default compute target (charter.md) |

---

## Tasks

### Task 001 — Upgrade Java and Spring Boot

Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring
Boot 4.0+, including migration from `javax.*` to `jakarta.*` namespaces and all
breaking-change remediations required by the framework upgrade.

---

### Task 002 — Migrate RabbitMQ to Azure Service Bus

Replace the Spring AMQP RabbitMQ integration (three queues: order.created,
inventory.alert, approval.pending) with Azure Service Bus using Managed Identity,
removing hardcoded RabbitMQ credentials from configuration.

---

### Task 003 — Migrate MySQL to Azure Database for MySQL with Managed Identity

Migrate the MySQL datasource from password-based authentication with hardcoded
credentials to Azure Database for MySQL using Managed Identity for passwordless
authentication.

---

### Task 004 — Migrate Hardcoded Credentials to Azure Key Vault

Move all remaining hardcoded secrets and credentials from `application.yml` and
source code to Azure Key Vault, accessed via the Spring Cloud Azure Key Vault
starter and Managed Identity.

---

### Task 005 — Migrate SLF4J to InternalLogger

Replace all SLF4J usage (`@Slf4j`, `LoggerFactory`) across all service and
configuration classes with InternalLogger (`com.acme.logging.InternalLogger`) to
ensure trace ID injection and structured JSON logging as required by policies.md.

---

### Task 006 — Replace RestTemplate with ServiceMesh SDK

Remove the RestTemplate bean and replace all HTTP client usages with the
ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to comply with the mesh
communication guardrail in policies.md.

---

### Task 007 — Migrate Exception-Based Error Handling to Result\<T\>

Refactor all exception-based business logic flow control in service classes
to use the Result\<T\> pattern (`com.acme.commons.Result`) per post-incident
mandate P0-2024-0847 defined in policies.md.

---

### Task 008 — Validate and Fix CVE Issues

**Description**: Scan all project dependencies for known CVE vulnerabilities and
upgrade or replace affected packages to achieve a CVE-clean state.

**Requirements**: The user explicitly requires all CVE issues to be fixed:
"make sure all CVE issues are fixed". All project dependencies must be scanned,
any dependency with a known CVE must be upgraded or replaced, and the result
must be a fully CVE-clean dependency set.

**Environment Configuration**: Java 25 runtime established by Task 001.
Maven 3 build tool.

**App Scope**: `/` (project root — single-module Maven project)

**Skills**:
  - Skill Name: validate-cves-and-fix
    - Skill Location: builtin

---

### Task 009 — Containerize Application

Create a multi-stage Dockerfile using `mcr.microsoft.com/openjdk/jdk:25-ubuntu`
as the build image and `mcr.microsoft.com/openjdk/jdk:25-distroless` as the
runtime image, following corporate container standards defined in targets.md.

---

### Task 010 — Generate Azure Infrastructure (Bicep)

Generate Bicep IaC files under `infra/` to provision all required Azure resources:
Azure Container Apps, Azure Service Bus, Azure Database for MySQL, Azure Key
Vault, and Azure Container Registry. Configure Managed Identity bindings for all
service connections.

---
