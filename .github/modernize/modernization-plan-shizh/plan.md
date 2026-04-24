# Modernization Plan: SupplyChain Backend — Azure Migration

**Project**: supplychain-backend

**Plan Name**: modernization-plan-shizh

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18 / Spring Framework 5.x
- **Build Tool**: Maven
- **Database**: MySQL (password-based authentication)
- **Messaging**: RabbitMQ via Spring AMQP (password-based authentication)
- **Key Dependencies**: Spring Data JPA, Hibernate, Spring AMQP, mysql-connector-java 8.0.33

---

## Overview

This migration modernizes the SupplyChain Backend from a legacy Java 8 / Spring Boot 2.7.18 stack to Java 21 / Spring Boot 3.x, and migrates cloud services to Azure. The application currently uses MySQL with hardcoded credentials and RabbitMQ for messaging, both of which are replaced by Azure-managed equivalents. The new architecture will:

- **Upgrade the runtime and framework**: Move from Java 8 and end-of-OSS-support Spring Boot 2.7.18 to Java 21 and Spring Boot 3.x, resolving all mandatory framework and Java version violations from the assessment.
- **Adopt Azure Service Bus for messaging**: Replace RabbitMQ/Spring AMQP with Azure Service Bus, eliminating hardcoded RabbitMQ credentials and providing a fully managed, scalable messaging service.
- **Adopt Azure Database for MySQL with Managed Identity**: Replace password-based MySQL connections with Azure Database for MySQL using Managed Identity, eliminating hardcoded database credentials and improving security posture.
- **Remediate all known CVE vulnerabilities**: Scan and resolve all known CVE issues in project dependencies to achieve a clean security baseline.

The migration proceeds in phases: framework upgrade first, followed by parallel service migrations, and finally CVE remediation on the fully migrated codebase.

---

## Migration Impact Summary

| Application           | Original Service          | New Azure Service                  | Authentication   | Comments                                      |
|-----------------------|---------------------------|------------------------------------|------------------|-----------------------------------------------|
| supplychain-backend   | Java 8 / Spring Boot 2.x  | Java 21 / Spring Boot 3.x          | N/A              | Mandatory framework and Java version upgrade  |
| supplychain-backend   | RabbitMQ (Spring AMQP)    | Azure Service Bus                  | Managed Identity | Migrate AMQP messaging to Azure Service Bus   |
| supplychain-backend   | MySQL (password-based)    | Azure Database for MySQL           | Managed Identity | Migrate database to Azure with passwordless   |
| supplychain-backend   | Dependency CVEs           | Remediated dependencies            | N/A              | Fix all known CVE issues in dependencies      |

---

## Upgrade Tasks

### Spring Boot 3.x Upgrade (Java 21 + Spring Framework 6.x)

**Description**: Upgrade the project from Java 8 / Spring Boot 2.7.18 to Java 21 / Spring Boot 3.x, which also includes Spring Framework 6.x and Jakarta EE migration (`javax.*` → `jakarta.*`).

**Addresses Assessment Issues**:
- `azure-java-version-02000` — Legacy Java version (mandatory)
- `spring-boot-to-azure-spring-boot-version-01000` — Spring Boot end of OSS support (mandatory)
- `spring-framework-version-01000` — Spring Framework end of OSS support (mandatory)

---

## Transform Tasks

### Task 1: Migrate RabbitMQ/Spring AMQP to Azure Service Bus

**Description**: Replace RabbitMQ-based Spring AMQP messaging with Azure Service Bus. Remove hardcoded RabbitMQ credentials and reconfigure message producers and consumers to use Azure Service Bus with Managed Identity.

**Addresses Assessment Issues**:
- `azure-message-queue-amqp-02000` — Spring AMQP dependency found
- `azure-message-queue-rabbitmq-01000` — Spring RabbitMQ usage found in code
- `azure-password-01000` — Hardcoded RabbitMQ password in `application.yml`

---

### Task 2: Migrate MySQL to Azure Database for MySQL with Managed Identity

**Description**: Migrate the application's MySQL database connection to Azure Database for MySQL using Managed Identity authentication, removing hardcoded credentials and adopting passwordless access.

**Addresses Assessment Issues**:
- `azure-database-mysql-01000` — MySQL database found
- `azure-password-01000` — Hardcoded MySQL password in `application.yml`

---

## Security Compliance

**Description**: Scan all project dependencies for known CVE vulnerabilities and remediate them by upgrading or replacing affected libraries to achieve a CVE-clean dependency tree.

**Requirements**: Fix all known CVE issues in project dependencies as identified by vulnerability scanning tools.

**Skills**:
  - Skill Name: validate-cves-and-fix
    - Skill Location: builtin

---
