# Modernization Plan: Java Version Upgrade

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3
- **Database**: MySQL 8.0
- **Key Dependencies**: Spring Data JPA, Spring AMQP (RabbitMQ), Lombok, Spring Validation

---

## Overview

This migration upgrades the SupplyChain Backend runtime from Java 8 and Spring Boot 2.7.18 to Java 25 and Spring Boot 4.0+. The application currently runs on Java 8, which is end-of-life for internal use, and Spring Boot 2.x, which is a prohibited version per corporate policy. The new configuration will:

- Eliminate the use of end-of-life Java 8 and prohibited Spring Boot 2.x versions
- Upgrade to Java 25 and Spring Boot 4.0+, the organization-mandated runtime targets
- Migrate from Java EE (`javax.*`) namespaces to Jakarta EE (`jakarta.*`) namespaces as required by Spring Boot 4.0+

The migration follows a single-phase upgrade approach: upgrade the runtime and framework together to reach the compliant baseline required by corporate policy.

---

## Migration Impact Summary

| Application          | Original Service     | New Service          | Authentication | Comments                          |
|----------------------|----------------------|----------------------|----------------|-----------------------------------|
| supplychain-backend  | Java 8 / SB 2.7.18   | Java 25 / SB 4.0+    | N/A            | Org-mandated Java 25 + SB 4.0+    |

---

## Tasks

### Task 001 — Upgrade Java and Spring Boot

Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+, including migration from `javax.*` to `jakarta.*` namespaces and all breaking-change remediations required for the framework upgrade.

---
