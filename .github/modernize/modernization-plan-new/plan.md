# Modernization Plan: SupplyChain Backend — Java Upgrade and Azure Migration

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3
- **Database**: MySQL 8.0 (hardcoded credentials in application.yml)
- **Key Dependencies**: Spring Data JPA, Spring AMQP (RabbitMQ), Lombok, Spring Validation, SLF4J

---

## Overview

This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 to
Java 25 / Spring Boot 4.0+ and migrates all on-premises and legacy services to Azure-managed
services. The application currently runs on end-of-life Java 8 and a prohibited Spring Boot
2.x version, uses RabbitMQ for AMQP messaging with hardcoded credentials, and stores all
sensitive configuration values in plaintext. The new architecture will:

- Upgrade to Java 25 and Spring Boot 4.0+, eliminating end-of-life runtimes and prohibited
  framework versions per organizational policy
- Replace RabbitMQ AMQP messaging with Azure Service Bus for cloud-native, managed messaging
- Replace password-based MySQL connectivity with Azure Database for MySQL using
  Managed Identity for passwordless, secure authentication
- Externalize all hardcoded credentials and sensitive values to Azure Key Vault
- Resolve all CVE vulnerabilities in project dependencies
- Containerize the application using approved Microsoft OpenJDK Java 25 base images
- Deploy to Azure Container Apps as the organization-mandated compute platform

The migration follows a phased approach: runtime upgrade first, Azure service migrations in
parallel, security validation, containerization, and finally deployment.

---

## Migration Impact Summary

| Application         | Original Service           | New Azure Service            | Authentication   | Comments                            |
|---------------------|----------------------------|------------------------------|------------------|-------------------------------------|
| supplychain-backend | Java 8 / Spring Boot 2.7.x | Java 25 / Spring Boot 4.0+   | N/A              | Org-mandated upgrade per targets.md |
| supplychain-backend | RabbitMQ (AMQP)            | Azure Service Bus            | Managed Identity | Cloud-native messaging              |
| supplychain-backend | MySQL (password-based)     | Azure Database for MySQL     | Managed Identity | Passwordless auth via MI            |
| supplychain-backend | Hardcoded credentials      | Azure Key Vault              | Managed Identity | All secrets externalized            |
| supplychain-backend | Legacy Java base images    | Azure Container Apps (ACA)   | Managed Identity | mcr.microsoft.com/openjdk/jdk:25-*  |

---

## Tasks

### Task 001 — Upgrade Java and Spring Boot

Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+,
including migration from `javax.*` to `jakarta.*` namespaces and all breaking-change
remediations required by the framework upgrade.

### Task 002 — Migrate RabbitMQ AMQP to Azure Service Bus

Migrate the application's AMQP messaging from RabbitMQ to Azure Service Bus. Replace all
three message queues (order-created, inventory-alert, approval-pending) with Azure Service
Bus queues using Spring Messaging and Managed Identity authentication.

### Task 003 — Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace the password-based MySQL datasource with Azure Database for MySQL, enabling
passwordless Managed Identity authentication and removing hardcoded database credentials
from application configuration.

### Task 004 — Migrate Hardcoded Credentials to Azure Key Vault

Externalize all remaining hardcoded credentials, connection strings, and sensitive
configuration values from `application.yml` and source code to Azure Key Vault,
accessed via Managed Identity.

### Task 005 — Security: Fix CVE Vulnerabilities

Scan all project dependencies for known CVE vulnerabilities and upgrade affected
dependencies to their secure versions. The project must pass a full dependency
vulnerability scan with zero CVE issues before proceeding to containerization.

### Task 006 — Containerize for Azure Container Apps

Update the Dockerfile to use the approved Microsoft OpenJDK Java 25 base images
(`mcr.microsoft.com/openjdk/jdk:25-ubuntu` for build stage,
`mcr.microsoft.com/openjdk/jdk:25-distroless` for runtime stage) and ensure the
container is optimized for deployment to Azure Container Apps.

### Task 007 — Deploy to Azure Container Apps

Generate Bicep IaC files and deploy the containerized application to Azure Container Apps
with all required supporting infrastructure: Azure Container Registry, Azure Database for
MySQL, Azure Service Bus, and Azure Key Vault.

---

## Security Compliance

**Description**: Validate and remediate all CVE vulnerabilities found in project
dependencies prior to containerization and deployment.

**Requirements**: All CVE issues must be resolved. Dependency versions must be upgraded
to non-vulnerable versions. The project must pass a full dependency vulnerability scan
with zero CVE findings before the containerization and deployment phases proceed.

**Environment Configuration**: Java 25 runtime and Maven 3.9+ build tool established
by Task 001 (Java / Spring Boot upgrade task).

**App Scope**: `/` (repository root — single-module Maven project)

**Skills**:
  - Skill Name: validate-cves-and-fix
    - Skill Location: builtin
