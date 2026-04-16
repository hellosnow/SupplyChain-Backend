# Modernization Plan: Java Upgrade

**Project**: supplychain-backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven
- **Database**: MySQL
- **Key Dependencies**: Spring Data JPA, Spring AMQP
  (RabbitMQ), Lombok, Spring Validation

---

## Overview

This migration upgrades the SupplyChain Backend application
from Java 8 and Spring Boot 2.7.18 to Java 21 and
Spring Boot 3.x. The application currently runs on an
outdated Java runtime and framework version that are
approaching end of support. The upgrade will:

- Modernize the runtime to Java 21 for improved
  performance, security, and long-term support
- Upgrade Spring Boot from 2.x to 3.x for access to
  the latest framework features and security patches
- Migrate from javax.* to jakarta.* namespace as
  required by Spring Boot 3.x and Jakarta EE

The migration follows a single-phase upgrade approach
addressing the runtime, framework, and namespace changes
together.

---

## Migration Impact Summary

| Application         | Original          | Target            | Comments            |
|---------------------|-------------------|-------------------|---------------------|
| supplychain-backend | Java 8            | Java 21           | JDK upgrade         |
| supplychain-backend | Spring Boot 2.7.18| Spring Boot 3.x   | Framework upgrade   |
| supplychain-backend | javax.* namespace | jakarta.*         | Namespace migration |

---

## Tasks

### Task 1: Upgrade Spring Boot to 3.x with Java 21

Upgrade the application from Spring Boot 2.7.18 to
Spring Boot 3.x with Java 21. This includes migrating
from javax.* to jakarta.* namespace and upgrading
Spring Framework from 5.x to 6.x. Update Dockerfile
base images to use Java 21.
