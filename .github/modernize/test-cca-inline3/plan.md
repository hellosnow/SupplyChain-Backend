# Modernization Plan: SupplyChain Backend — Migrate to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3.8
- **Database**: MySQL 8 (hardcoded credentials, on-premises)
- **Messaging**: RabbitMQ (AMQP, hardcoded credentials)
- **Key Dependencies**: Spring Data JPA (javax.persistence), Spring AMQP, Lombok, Spring Boot Validation

---

## Overview

This migration moves the SupplyChain Backend from an on-premises Java 8 / Spring Boot 2.7.18 application to a modern Azure-hosted service. The application currently uses a local MySQL database with hardcoded credentials and RabbitMQ for asynchronous messaging. The new architecture will:

- Upgrade the runtime to Java 21 LTS and Spring Boot 3.x to meet organizational targets and unlock Azure-native SDK compatibility.
- Replace RabbitMQ with Azure Service Bus for cloud-native, managed asynchronous messaging with managed identity authentication.
- Replace the local MySQL datasource with Azure Database for MySQL using managed identity for passwordless, secure database access.

The migration follows an incremental approach: runtime upgrade first, then cloud service integrations, to minimize risk and allow independent validation of each phase.

---

## Migration Impact Summary

| Application            | Original Service   | New Azure Service              | Authentication     | Comments                                   |
|------------------------|--------------------|--------------------------------|--------------------|--------------------------------------------|
| supplychain-backend    | RabbitMQ (AMQP)    | Azure Service Bus              | Managed Identity   | 3 queues: order.created, inventory.alert, approval.pending |
| supplychain-backend    | MySQL (local)      | Azure Database for MySQL       | Managed Identity   | Remove hardcoded root credentials          |

---

## Migration Tasks

### Task 1 — Java 21 & Spring Boot 3.x Upgrade

Upgrade the application runtime from Java 8 / Spring Boot 2.7.18 to Java 21 LTS / Spring Boot 3.x, including migration from `javax.*` to `jakarta.*` namespaces required by Jakarta EE.

### Task 2 — RabbitMQ to Azure Service Bus

Migrate asynchronous messaging from RabbitMQ (Spring AMQP) to Azure Service Bus via Spring Cloud Azure. Replace `spring-boot-starter-amqp` and RabbitMQ connection configuration with Azure Service Bus JMS or AMQP integration using managed identity authentication.

### Task 3 — MySQL to Azure Database for MySQL with Managed Identity

Migrate the datasource from a local MySQL instance with hardcoded credentials to Azure Database for MySQL, using Spring Cloud Azure with managed identity for passwordless authentication. Remove all hardcoded database passwords from configuration.

---

## Next Steps

After all migration tasks are completed:

1. Validate the build and all unit tests pass.
2. Run integration tests against Azure services.
3. Update the Dockerfile to use a Java 21 base image.
4. Deploy to Azure.
