# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven
- **Database**: MySQL (self-hosted, hardcoded credentials)
- **Messaging**: RabbitMQ (self-hosted, hardcoded credentials, 3 queues)
- **Key Dependencies**: Spring Data JPA (javax.persistence), Spring AMQP (RabbitMQ), Lombok, RestTemplate, SLF4J (Logback)

---

## Overview

This migration upgrades the SupplyChain Backend from Java 8 and Spring Boot 2.7.18 to Java 25 and Spring Boot 4.x, and migrates all infrastructure dependencies to Azure managed services. The application currently runs with self-hosted MySQL and RabbitMQ, hardcoded credentials in configuration files, legacy HTTP clients that bypass the service mesh, and non-compliant logging and error-handling patterns. The new architecture will:

- Upgrade the runtime to Java 25 and Spring Boot 4.x, replacing all `javax.*` namespaces with `jakarta.*` for full Jakarta EE 10 compatibility
- Replace self-hosted RabbitMQ with Azure Service Bus for cloud-native managed messaging with Managed Identity authentication
- Replace the self-hosted MySQL database with Azure Database for MySQL using Managed Identity for passwordless, secure database access
- Centralize all secrets and sensitive configuration in Azure Key Vault, eliminating hardcoded credentials from configuration files
- Enforce the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) for all service-to-service HTTP communication, replacing RestTemplate per Acme Corp guardrails
- Replace SLF4J (`@Slf4j`) with InternalLogger (`com.acme.logging.InternalLogger`) across all classes for compliant structured logging with trace context
- Replace all exception-based business flow control with the `Result<T>` pattern (`com.acme.commons.Result`) per the P0-2024-0847 mandate
- Containerize the application using approved Microsoft OpenJDK base images and deploy to Azure Container Apps (default Replatform target per Acme Corp charter)

The migration follows a phased approach: upgrade the runtime first, then migrate each infrastructure service and enforce playbook coding standards, and finally containerize and deploy to Azure Container Apps.

---

## Migration Impact Summary

| Application           | Original Service               | New Azure Service             | Authentication     | Comments                                      |
|-----------------------|-------------------------------|-------------------------------|--------------------|-----------------------------------------------|
| SupplyChain Backend   | Java 8 / Spring Boot 2.7.18   | Java 25 / Spring Boot 4.x    | N/A                | Includes javax.* → jakarta.* migration        |
| SupplyChain Backend   | RabbitMQ (AMQP, self-hosted)  | Azure Service Bus             | Managed Identity   | 3 queues: order.created, inventory.alert, approval.pending |
| SupplyChain Backend   | MySQL (self-hosted)            | Azure Database for MySQL      | Managed Identity   | Remove hardcoded DB credentials               |
| SupplyChain Backend   | Hardcoded credentials          | Azure Key Vault               | Managed Identity   | DB password, RabbitMQ credentials in application.yml |
| SupplyChain Backend   | RestTemplate                   | ServiceMesh SDK               | N/A                | Per Acme Corp guardrails (policies.md)        |
| SupplyChain Backend   | SLF4J (@Slf4j)                | InternalLogger                | N/A                | Per Acme Corp guardrails (policies.md)        |
| SupplyChain Backend   | Exception-based error handling | Result\<T\> pattern           | N/A                | Per P0-2024-0847 mandate (policies.md)        |
| SupplyChain Backend   | N/A (not containerized)        | Azure Container Apps          | Managed Identity   | Default Replatform target per charter.md      |

---

## Migration Tasks

### Task 1: Upgrade Spring Boot to 4.x (Java 25)

Upgrade the application runtime from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x, including migration of all `javax.*` namespaces to `jakarta.*` as required by Spring Boot 4.x and Jakarta EE compatibility. This is a prerequisite for all subsequent tasks.

### Task 2: Migrate RabbitMQ to Azure Service Bus

Replace the Spring AMQP RabbitMQ messaging stack with Azure Service Bus, migrating all three message queues (`order.created`, `inventory.alert`, `approval.pending`) and their producers and consumers to the Azure Service Bus equivalent using Managed Identity authentication.

### Task 3: Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace the self-hosted MySQL database connection with Azure Database for MySQL, switching from hardcoded password-based authentication to Managed Identity for secure, passwordless database access. Remove hardcoded credentials from `application.yml`.

### Task 4: Migrate Hardcoded Credentials to Azure Key Vault

Move all hardcoded credentials and sensitive configuration values from `application.yml` and source code to Azure Key Vault. Configure the application to access secrets via the Spring Cloud Azure Key Vault starter with Managed Identity authentication.

### Task 5: Migrate RestTemplate to ServiceMesh SDK

Replace all usages of `RestTemplate` with the internal ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) for all service-to-service HTTP communication. This enforces the mesh layer for circuit breaking, mTLS termination, distributed tracing, and canary routing per Acme Corp guardrails.

### Task 6: Migrate Logging to InternalLogger

Replace all SLF4J logging (`@Slf4j`, `LoggerFactory`) with InternalLogger (`com.acme.logging.InternalLogger`) across all service and controller classes, in compliance with Acme Corp logging guardrails.

### Task 7: Migrate Exception-Based Error Handling to Result\<T\> Pattern

Replace all exception-based business flow control (`throw` for business validation, `try/catch` for flow control) with the `Result<T>` pattern (`com.acme.commons.Result`) across all service and controller classes, in compliance with the P0-2024-0847 post-incident mandate.

### Task 8: Containerize Application

Create a multi-stage Dockerfile using the approved base images (`mcr.microsoft.com/openjdk/jdk:25-ubuntu` for build stage, `mcr.microsoft.com/openjdk/jdk:25-distroless` for runtime stage) to containerize the application for Azure Container Apps deployment.

### Task 9: Deploy to Azure Container Apps

Generate Bicep IaC files and deploy the containerized SupplyChain Backend to Azure Container Apps, provisioning all required Azure services: Azure Container Apps environment, Azure Container Registry, Azure Database for MySQL, Azure Service Bus, and Azure Key Vault. All services use Managed Identity for authentication.
