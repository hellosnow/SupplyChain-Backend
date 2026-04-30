# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3.x
- **Database**: MySQL (password-based, hardcoded credentials)
- **Key Dependencies**: Spring Data JPA (javax.persistence), Spring AMQP (RabbitMQ), RestTemplate, Lombok, SLF4J

---

## Overview

This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x and replatforms the application to run on Azure Container Apps. The application currently uses RabbitMQ for messaging, a MySQL database with hardcoded credentials, RestTemplate for HTTP calls, SLF4J for logging, and exception-based flow control. The new architecture will:

- Replace RabbitMQ AMQP messaging with Azure Service Bus, providing a fully managed, cloud-native messaging service with Managed Identity authentication
- Replace password-based MySQL connections with Azure Database for MySQL using Managed Identity for credential-free, secure database access
- Move all hardcoded credentials and secrets into Azure Key Vault, accessed via Spring Cloud Azure and Managed Identity
- Replace RestTemplate with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to enforce service mesh policies (mTLS, circuit breaking, distributed tracing, canary routing)
- Replace SLF4J logging with InternalLogger (`com.acme.logging.InternalLogger`) to ensure trace context injection and structured JSON output
- Replace exception-based error handling with the Result\<T\> pattern (`com.acme.commons.Result`) per the P0-2024-0847 post-incident mandate

The migration follows a phased approach: first upgrading the runtime (Spring Boot 4.x / Java 25 / Jakarta EE), then applying Azure service integrations and coding standard remediations, and finally containerizing and deploying the application to Azure Container Apps.

---

## Migration Impact Summary

| Application           | Original Service                              | New Azure Service                         | Authentication     | Comments                                      |
|-----------------------|-----------------------------------------------|-------------------------------------------|--------------------|-----------------------------------------------|
| SupplyChain Backend   | Spring Boot 2.7.18 / Java 8                   | Spring Boot 4.x / Java 25                 | N/A                | Upgrade runtime; javax.* → jakarta.*          |
| SupplyChain Backend   | RabbitMQ (Spring AMQP)                        | Azure Service Bus                         | Managed Identity   | Migrate AMQP messaging to Azure Service Bus   |
| SupplyChain Backend   | MySQL (password-based, hardcoded)             | Azure Database for MySQL                  | Managed Identity   | Passwordless auth via Spring Cloud Azure      |
| SupplyChain Backend   | Hardcoded credentials in application.yml      | Azure Key Vault                           | Managed Identity   | Spring Cloud Azure Key Vault starter          |
| SupplyChain Backend   | RestTemplate                                  | ServiceMesh SDK (com.acme.mesh)           | N/A (mesh mTLS)    | Enforce mesh layer for all HTTP calls         |
| SupplyChain Backend   | SLF4J / Lombok @Slf4j                         | InternalLogger (com.acme.logging)         | N/A                | Structured JSON logging with trace context    |
| SupplyChain Backend   | Exception-based error handling                | Result\<T\> (com.acme.commons.Result)     | N/A                | P0-2024-0847 mandate; no throw for biz logic  |
| SupplyChain Backend   | Local Docker build                            | Azure Container Apps (ACA)                | Managed Identity   | Replatform to ACA per charter strategy        |

---

## Migration Tasks

### Task 1 — Spring Boot 4.x Upgrade (Java 25 / Jakarta EE)

Upgrade the project from Spring Boot 2.7.18 / Java 8 to Spring Boot 4.x / Java 25. This includes migrating all `javax.*` namespaces to `jakarta.*`, updating the Spring Framework to 7.x, and ensuring the Maven compiler targets Java 25. All subsequent migration tasks depend on this upgrade.

---

### Task 2 — RabbitMQ AMQP to Azure Service Bus

Migrate the application's AMQP messaging layer from RabbitMQ (Spring AMQP / RabbitTemplate) to Azure Service Bus. Replace Spring AMQP dependencies with Spring Cloud Azure Service Bus, migrate message producers and consumers, and update connection configuration to use Managed Identity authentication.

---

### Task 3 — MySQL to Azure Database for MySQL (Managed Identity)

Migrate the data source from a password-based MySQL connection to Azure Database for MySQL using Managed Identity. Remove hardcoded database credentials, add Spring Cloud Azure MySQL dependencies, and configure passwordless authentication.

---

### Task 4 — Hardcoded Credentials to Azure Key Vault

Migrate all remaining plaintext credentials (connection strings, passwords, API keys) from `application.yml` and source code to Azure Key Vault. Configure the Spring Cloud Azure Key Vault starter to retrieve secrets at runtime via Managed Identity.

---

### Task 5 — RestTemplate to ServiceMesh SDK

Replace all usages of `RestTemplate` with the internal ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to enforce service mesh policies including mTLS termination, circuit breaking, distributed tracing, and canary routing. Remove the `RestTemplate` bean from `AppConfig`.

---

### Task 6 — SLF4J to InternalLogger

Replace all SLF4J usages (Lombok `@Slf4j`, `LoggerFactory`) with `InternalLogger` (`com.acme.logging.InternalLogger`) to ensure trace context injection, team tags, and structured JSON log output compliant with the logging guardrail.

---

### Task 7 — Exception-Based Error Handling to Result\<T\>

Replace exception-based business logic flow control (`throw new RuntimeException`, `throw new IllegalArgumentException`, `throw new IllegalStateException`, `try/catch` for flow control) with the `Result<T>` pattern (`com.acme.commons.Result`) per the P0-2024-0847 post-incident mandate. Update service and controller signatures accordingly.

---

### Task 8 — Containerization

Generate a multi-stage Dockerfile for the SupplyChain Backend using `mcr.microsoft.com/openjdk/jdk:25-ubuntu` as the build stage image and `mcr.microsoft.com/openjdk/jdk:25-distroless` as the runtime stage image, per the approved container base images in targets.md.

---

### Task 9 — Deploy to Azure Container Apps

Deploy the containerized SupplyChain Backend to Azure Container Apps using Bicep IaC. Provision required Azure resources (Azure Container Apps environment, Azure Service Bus namespace, Azure Database for MySQL, Azure Key Vault, Managed Identity) and deploy the application image.
