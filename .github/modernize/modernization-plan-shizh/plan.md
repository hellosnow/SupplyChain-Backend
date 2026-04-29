# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven
- **Database**: MySQL (hardcoded credentials in application.yml)
- **Key Dependencies**: Spring Data JPA (javax.persistence), Spring AMQP (RabbitMQ / RabbitTemplate), Lombok, RestTemplate, SLF4J (@Slf4j)

---

## Overview

> This migration upgrades and replatforms the SupplyChain Backend from a legacy
> Java 8 / Spring Boot 2.7.18 application to a cloud-native Java 25 / Spring
> Boot 4.x application running on Azure Container Apps. The application currently
> uses RabbitMQ for messaging, MySQL with hardcoded credentials, RestTemplate for
> HTTP calls, and SLF4J for logging — all of which violate current policy
> guardrails. The new architecture will:
>
> - Upgrade the runtime to Java 25 / Spring Boot 4.x, fulfilling the org-wide
>   end-of-life mandate for Java 8 and Spring Boot 2.x
> - Replace RabbitMQ messaging with Azure Service Bus using Managed Identity for
>   secure, passwordless cloud messaging
> - Replace MySQL with Azure Database for MySQL using Managed Identity to
>   eliminate hardcoded database credentials
> - Move all hardcoded secrets and credentials to Azure Key Vault for centralized,
>   policy-compliant secrets management
> - Replace RestTemplate with the ServiceMesh SDK for all service-to-service
>   communication, gaining automatic circuit breaking, mTLS, and distributed
>   tracing
> - Replace SLF4J / Logback with InternalLogger for unified, trace-context-aware
>   logging
> - Adopt the Result\<T\> pattern to replace exception-based business logic flow
>   control per mandate P0-2024-0847
> - Containerize and deploy to Azure Container Apps via Bicep IaC
>
> The migration follows a phased approach: first upgrading the runtime, then
> migrating each integration and applying policy-required code patterns, then
> containerizing and deploying to Azure.

---

## Migration Impact Summary

| Application          | Original Service           | New Azure Service             | Authentication     | Comments                         |
|----------------------|----------------------------|-------------------------------|--------------------|----------------------------------|
| SupplyChain Backend  | Java 8 / Spring Boot 2.7.18 | Java 25 / Spring Boot 4.x    | N/A                | EOL upgrade per policy           |
| SupplyChain Backend  | RabbitMQ (AMQP)            | Azure Service Bus             | Managed Identity   | 3 queues: order, inventory, approval |
| SupplyChain Backend  | MySQL (hardcoded creds)    | Azure Database for MySQL      | Managed Identity   | Passwordless auth                |
| SupplyChain Backend  | Hardcoded secrets          | Azure Key Vault               | Managed Identity   | All credentials in app config    |
| SupplyChain Backend  | RestTemplate               | ServiceMesh SDK               | N/A                | Policy: mesh layer required      |
| SupplyChain Backend  | SLF4J (@Slf4j / Logback)   | InternalLogger                | N/A                | Policy: trace context required   |
| SupplyChain Backend  | Exception flow control     | Result\<T\> pattern           | N/A                | Policy mandate P0-2024-0847      |
| SupplyChain Backend  | Local / Docker Compose     | Azure Container Apps          | Managed Identity   | Bicep IaC deployment             |

---

## Migration Tasks

### Task 1: Spring Boot 4.x Upgrade

Upgrade the application runtime from Java 8 / Spring Boot 2.7.18 to Java 25 /
Spring Boot 4.x. This includes migrating all `javax.*` packages to `jakarta.*`
(Jakarta EE), upgrading Spring Framework to 7.x, and updating Maven compiler
settings.

**Why**: Java 8 and Spring Boot 2.x are end-of-life for internal use per the
charter. Java 25 and Spring Boot 4.x are the mandated targets.

---

### Task 2: RabbitMQ to Azure Service Bus Migration

Migrate all AMQP messaging from RabbitMQ to Azure Service Bus using Managed
Identity for passwordless authentication. The three queues (`order.created`,
`inventory.alert`, `approval.pending`) must be migrated along with all
producer/consumer code in `PurchaseOrderService` and `InventoryService`.

**Why**: RabbitMQ is an on-premises broker that does not meet cloud-native and
policy requirements. Azure Service Bus with Managed Identity is the approved
messaging service.

---

### Task 3: MySQL to Azure Database for MySQL with Managed Identity

Replace the MySQL data source with Azure Database for MySQL using Managed
Identity for passwordless, credential-free authentication. Remove all hardcoded
database credentials from application configuration.

**Why**: Hardcoded database credentials violate security policy. Managed Identity
is the approved authentication method for service-to-service connectivity.

---

### Task 4: Plaintext Credentials to Azure Key Vault

Migrate all hardcoded credentials and sensitive values from `application.yml`
(database passwords, RabbitMQ credentials, connection strings) to Azure Key
Vault. Access secrets via Spring Cloud Azure Key Vault starter and Managed
Identity.

**Why**: Storing credentials in application configuration files is a prohibited
pattern per security policy. Azure Key Vault is the required secrets store for
all modernized applications.

---

### Task 5: RestTemplate to ServiceMesh SDK

Replace all `RestTemplate` usage (bean definition in `AppConfig`, injection and
call in `VendorService`) with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`)
for service-to-service communication.

**Why**: `RestTemplate` is a prohibited technology — it bypasses the mesh layer
and provides no circuit breaking, mTLS, or distributed tracing. The ServiceMesh
SDK is the only approved HTTP client per policy.

---

### Task 6: SLF4J to InternalLogger

Replace all SLF4J / Lombok `@Slf4j` logging declarations and log calls with
`com.acme.logging.InternalLogger` across all services
(`PurchaseOrderService`, `InventoryService`, `VendorService`) and controllers.

**Why**: SLF4J and Logback are prohibited because they lack trace-context
injection. InternalLogger is the sole approved logging framework.

---

### Task 7: Exception Flow to Result\<T\> Pattern

Replace exception-based business logic flow control (e.g.,
`throw new IllegalArgumentException`, `throw new IllegalStateException`,
`throw new RuntimeException` used as control flow) with the `Result<T>` pattern
(`com.acme.commons.Result`) across all services and controllers.

**Why**: Throwing exceptions for business logic control flow is prohibited per
mandate P0-2024-0847. The `Result<T>` pattern is the approved approach for
error propagation and business validation.

---

### Containerization

Create a multi-stage Dockerfile using
`mcr.microsoft.com/openjdk/jdk:25-ubuntu` for the Maven build stage and
`mcr.microsoft.com/openjdk/jdk:25-distroless` for the production runtime stage
to produce a minimal, secure container image.

---

### Deployment to Azure Container Apps

Deploy the containerized SupplyChain Backend to Azure Container Apps using
Bicep IaC. The deployment must wire the application to Azure Service Bus,
Azure Database for MySQL, and Azure Key Vault using Managed Identity.
