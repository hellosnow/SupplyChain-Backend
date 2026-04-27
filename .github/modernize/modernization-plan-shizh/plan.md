# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

**Plan Name**: modernization-plan-shizh

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3
- **Database**: MySQL 8 (password-based authentication, hardcoded credentials)
- **Messaging**: RabbitMQ (spring-boot-starter-amqp, 3 queues)
- **Key Dependencies**: Spring Data JPA, Hibernate, Lombok, Spring AMQP

---

## Overview

This migration upgrades and replatforms the SupplyChain Backend from a legacy Java 8 / Spring Boot 2.7.18 monolith to a modern, cloud-native Java 25 / Spring Boot 4.x application deployed on Azure Container Apps. The application currently uses RabbitMQ for messaging, MySQL with hardcoded password credentials, RestTemplate for outbound HTTP calls, SLF4J/Logback for logging, and exception-based error handling. The new architecture will:

- Upgrade to Java 25 and Spring Boot 4.x to meet internal target-version policy (Java 8/11/17 are end-of-life for internal use)
- Replace RabbitMQ with Azure Service Bus for cloud-native, managed messaging
- Replace password-based MySQL with Azure Database for MySQL using Managed Identity for credential-free database authentication
- Move all hardcoded credentials and secrets to Azure Key Vault
- Replace RestTemplate with the ServiceMesh SDK for compliant service-to-service communication with mTLS, circuit breaking, and distributed tracing
- Replace SLF4J/Logback with InternalLogger for trace-context-aware structured logging
- Replace exception-based flow control with the Result\<T\> pattern per post-incident mandate (P0-2024-0847)
- Containerize the application and deploy to Azure Container Apps (ACA) as the default replatform target per charter

The migration follows a phased approach: runtime upgrade first, Azure service integrations next, coding-standard remediations, then containerization and deployment to Azure.

---

## Migration Impact Summary

| Application           | Original Service          | New Azure Service                  | Authentication     | Comments                              |
|-----------------------|---------------------------|------------------------------------|--------------------|---------------------------------------|
| supplychain-backend   | Java 8 / Spring Boot 2.7  | Java 25 / Spring Boot 4.x          | N/A                | Internal target-version policy        |
| supplychain-backend   | RabbitMQ (AMQP)           | Azure Service Bus                  | Managed Identity   | 3 queues migrated                     |
| supplychain-backend   | MySQL (password)          | Azure Database for MySQL           | Managed Identity   | Passwordless authentication           |
| supplychain-backend   | Hardcoded credentials     | Azure Key Vault                    | Managed Identity   | application.yml secrets externalized  |
| supplychain-backend   | RestTemplate              | ServiceMesh SDK                    | mTLS (mesh)        | Guardrail compliance                  |
| supplychain-backend   | SLF4J / Logback           | InternalLogger                     | N/A                | Guardrail compliance                  |
| supplychain-backend   | Exception-based flow      | Result\<T\> pattern                | N/A                | P0-2024-0847 post-incident mandate    |
| supplychain-backend   | Local container (none)    | Azure Container Apps (ACA)         | Managed Identity   | Default replatform target             |

---

## Migration Tasks

### Task 1 — Upgrade to Java 25 and Spring Boot 4.x

Upgrade the runtime from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x. This is the prerequisite for all subsequent Azure service migration tasks.

### Task 2 — Migrate RabbitMQ AMQP to Azure Service Bus

Replace the RabbitMQ AMQP messaging integration (spring-boot-starter-amqp) with Azure Service Bus using Spring Messaging. Migrate all three queues (order.created, inventory.alert, approval.pending) and their producers/consumers. Use Managed Identity for authentication.

### Task 3 — Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace password-based MySQL authentication with Azure Managed Identity for Azure Database for MySQL. Remove hardcoded database passwords and enable credential-free datasource authentication.

### Task 4 — Migrate Hardcoded Credentials to Azure Key Vault

Move all sensitive values (database credentials, connection strings) from application.yml to Azure Key Vault. Configure access via Spring Cloud Azure Key Vault starter with Managed Identity.

### Task 5 — Replace RestTemplate with ServiceMesh SDK

Replace all uses of RestTemplate (and the RestTemplate Spring bean) with the internal ServiceMesh SDK (com.acme.mesh.ServiceMesh) for all outbound service-to-service calls. This is required per guardrail policy.

### Task 6 — Replace SLF4J Logging with InternalLogger

Replace all SLF4J / Logback usage (@Slf4j annotations, LoggerFactory, and logging configuration) with InternalLogger (com.acme.logging.InternalLogger) to ensure trace-context integration and structured JSON output.

### Task 7 — Replace Exception-Based Flow Control with Result\<T\> Pattern

Replace all exception-based business logic flow control (throw, try/catch for flow) with the Result\<T\> pattern (com.acme.commons.Result) per post-incident mandate P0-2024-0847.

### Task 8 — Containerize the Application

Create a multi-stage Dockerfile using the approved base images (build: mcr.microsoft.com/openjdk/jdk:25-ubuntu, runtime: mcr.microsoft.com/openjdk/jdk:25-distroless) to produce a production-ready container image.

### Task 9 — Generate Bicep Infrastructure for Azure Container Apps

Generate Bicep IaC files to provision the required Azure infrastructure: Azure Container Apps environment, Azure Service Bus namespace, Azure Database for MySQL, Azure Key Vault, and Managed Identity with appropriate role assignments.

### Task 10 — Deploy to Azure Container Apps

Deploy the containerized application to Azure Container Apps using the generated Bicep infrastructure.
