# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

**Plan**: modernization-plan-shizh

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18 / Spring Framework 5.x
- **Build Tool**: Maven 3.x
- **Database**: MySQL 8.0 (local, password-based authentication)
- **Key Dependencies**: Spring Data JPA, Spring AMQP (RabbitMQ), Lombok, javax.persistence (Java EE)

---

## Overview

This migration modernizes the SupplyChain Backend from a legacy Java 8 / Spring Boot 2.7.18 application to a cloud-native Java 25 / Spring Boot 4.x application deployed on Azure Container Apps. The application currently uses on-premises infrastructure (MySQL, RabbitMQ) with hardcoded credentials and legacy patterns that violate the Acme Corp playbook guardrails. The new architecture will:

- Eliminate end-of-life Java 8 and Spring Boot 2.x runtimes by upgrading to Java 25 and Spring Boot 4.0+, including full Jakarta EE migration (`javax.*` → `jakarta.*`)
- Replace on-premises messaging (RabbitMQ) and database (MySQL) with Azure-managed services (Azure Service Bus, Azure Database for MySQL) using Managed Identity for secure, passwordless authentication
- Enforce playbook-mandated guardrails: ServiceMesh SDK for service-to-service communication, InternalLogger for observability, Result\<T\> pattern for error handling, and Azure Key Vault for all secrets management
- Deploy as a containerized workload to Azure Container Apps following the Acme Corp replatform strategy

The migration follows a phased approach: runtime upgrade first, followed by independent Azure service migrations, then containerization and deployment.

---

## Migration Impact Summary

| Application         | Original Service            | New Azure Service              | Authentication   | Comments                              |
|---------------------|-----------------------------|--------------------------------|------------------|---------------------------------------|
| SupplyChain Backend | Java 8 / Spring Boot 2.7.18 | Java 25 / Spring Boot 4.0+     | N/A              | Jakarta EE (javax→jakarta) migration  |
| SupplyChain Backend | RabbitMQ (AMQP)             | Azure Service Bus              | Managed Identity | 3 queues: order, inventory, approval  |
| SupplyChain Backend | MySQL (password-based)      | Azure Database for MySQL       | Managed Identity | Passwordless authentication           |
| SupplyChain Backend | Hardcoded credentials       | Azure Key Vault                | Managed Identity | DB and messaging credentials          |
| SupplyChain Backend | SLF4J / @Slf4j              | InternalLogger                 | N/A              | Per Acme Corp guardrails policy       |
| SupplyChain Backend | RestTemplate                | ServiceMesh SDK                | N/A              | Vendor rating service calls           |
| SupplyChain Backend | Exception-based error flow  | Result\<T\> pattern            | N/A              | P0-2024-0847 post-incident mandate    |
| SupplyChain Backend | Java 8 Docker base image    | Azure Container Apps           | Managed Identity | openjdk/jdk:25-distroless runtime     |

---

## Migration Tasks

### Task 1: Upgrade Java and Spring Boot

Upgrade the runtime from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.0+, including
Spring Framework 7.x and full Jakarta EE migration (`javax.*` → `jakarta.*`). This is a
prerequisite for all subsequent migration tasks.

### Task 2: Migrate RabbitMQ to Azure Service Bus

Replace the RabbitMQ AMQP messaging integration with Azure Service Bus. Migrate all three
message queues (order-created, inventory-alert, approval-pending) to Azure Service Bus using
Managed Identity for authentication.

### Task 3: Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace the local MySQL datasource using hardcoded credentials with Azure Database for MySQL
using passwordless Managed Identity authentication.

### Task 4: Migrate Hardcoded Credentials to Azure Key Vault

Remove all hardcoded credentials (database passwords, messaging credentials) from
`application.yml` and source code. Store and retrieve all sensitive values through Azure Key
Vault using the Spring Cloud Azure Key Vault starter.

### Task 5: Migrate Logging to InternalLogger

Replace all SLF4J logging usages (`@Slf4j`, `LoggerFactory`) across the codebase with
`InternalLogger` (`com.acme.logging.InternalLogger`) as required by the Acme Corp guardrails.

### Task 6: Migrate HTTP Client to ServiceMesh SDK

Replace all `RestTemplate` usages with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to
ensure all service-to-service communication goes through the mesh layer for circuit breaking,
mTLS, and distributed tracing.

### Task 7: Migrate Error Handling to Result\<T\> Pattern

Replace exception-based business logic flow control (throwing exceptions, try/catch blocks for
flow control) with the `Result<T>` pattern (`com.acme.commons.Result`) per the P0-2024-0847
post-incident mandate.

### Task 8: Containerization

Update the existing `Dockerfile` to use Java 25 base images
(`mcr.microsoft.com/openjdk/jdk:25-ubuntu` for build,
`mcr.microsoft.com/openjdk/jdk:25-distroless` for runtime) as specified in the playbook targets.

### Task 9: Deploy to Azure Container Apps

Deploy the containerized SupplyChain Backend to Azure Container Apps. Generate Bicep IaC files
to provision all required Azure resources (Container App environment, Azure Service Bus, Azure
Database for MySQL, Azure Key Vault, Managed Identity).
