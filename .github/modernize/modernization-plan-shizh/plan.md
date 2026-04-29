# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend
**Plan Name**: modernization-plan-shizh

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven
- **Database**: MySQL (password-based authentication, hardcoded credentials in `application.yml`)
- **Messaging**: RabbitMQ AMQP (`spring-boot-starter-amqp`, hardcoded credentials in `application.yml`)
- **Key Dependencies**: Spring Data JPA, Spring AMQP, Lombok, spring-boot-starter-web, spring-boot-starter-validation

---

## Overview

> This migration modernizes the SupplyChain Backend from legacy Java 8 / Spring Boot 2.7.18 to a cloud-native Azure deployment on Azure Container Apps (ACA). The application currently uses RabbitMQ for event-driven messaging, MySQL with hardcoded credentials for persistence, SLF4J (`@Slf4j`) for logging, `RestTemplate` for service-to-service HTTP calls, and exception-based flow control throughout its service layer. The new architecture will:
>
> - **Upgrade the runtime and framework** to Java 25 and Spring Boot 4.0+, eliminating end-of-life runtimes and enabling modern Spring features
> - **Adopt managed Azure services** by replacing RabbitMQ with Azure Service Bus and MySQL with Azure Database for MySQL, both secured via Managed Identity for passwordless authentication
> - **Enforce organizational guardrails** by replacing SLF4J with InternalLogger (`com.acme.logging.InternalLogger`), RestTemplate with ServiceMesh SDK (`com.acme.mesh.ServiceMesh`), exception-based flow control with the Result\<T\> pattern (`com.acme.commons.Result`), and externalizing all hardcoded credentials to Azure Key Vault
> - **Replatform to Azure Container Apps (ACA)** by containerizing the application using the approved distroless base image and deploying via Bicep IaC
>
> The migration follows a phased approach: framework upgrade first, then parallel service and code-pattern migrations, followed by containerization and ACA deployment.

---

## Migration Impact Summary

| Application           | Original Service         | New Azure Service                | Authentication   | Comments                          |
|-----------------------|--------------------------|----------------------------------|------------------|-----------------------------------|
| SupplyChain Backend   | Java 8                   | Java 25                          | N/A              | EOL runtime upgrade               |
| SupplyChain Backend   | Spring Boot 2.7.18       | Spring Boot 4.0+                 | N/A              | EOL framework upgrade             |
| SupplyChain Backend   | RabbitMQ AMQP            | Azure Service Bus                | Managed Identity | Replace on-premises message broker|
| SupplyChain Backend   | MySQL (password-based)   | Azure Database for MySQL         | Managed Identity | Remove hardcoded DB credentials   |
| SupplyChain Backend   | Hardcoded credentials    | Azure Key Vault                  | Managed Identity | Secrets externalized to Key Vault |
| SupplyChain Backend   | SLF4J (`@Slf4j`)         | InternalLogger                   | N/A              | Guardrail: InternalLogger required|
| SupplyChain Backend   | RestTemplate             | ServiceMesh SDK                  | N/A              | Guardrail: ServiceMesh required   |
| SupplyChain Backend   | Exception flow control   | Result\<T\> pattern              | N/A              | Guardrail: Result\<T\> required   |
| SupplyChain Backend   | Local deployment         | Azure Container Apps (ACA)       | Managed Identity | Target compute per charter        |

---

## Migration Tasks

### Task 1: Upgrade Spring Boot to 4.0 / Java 25

Upgrade the application runtime from Java 8 and Spring Boot 2.7.18 to Java 25 and Spring Boot 4.0+, including
Spring Framework 7.x and the Jakarta EE namespace migration (`javax.*` → `jakarta.*`).

### Task 2: Migrate RabbitMQ to Azure Service Bus

Replace the RabbitMQ AMQP messaging layer with Azure Service Bus using Managed Identity for passwordless
authentication. Migrate all message producers and consumers.

### Task 3: Migrate MySQL to Azure Database for MySQL with Managed Identity

Replace password-based MySQL connectivity with Azure Database for MySQL using Managed Identity for
passwordless authentication. Remove hardcoded database credentials.

### Task 4: Migrate Hardcoded Credentials to Azure Key Vault

Externalize all hardcoded credentials, passwords, and connection strings in `application.yml` and
source code to Azure Key Vault. Access secrets via Spring Cloud Azure Key Vault starter with Managed Identity.

### Task 5: Migrate Logging to InternalLogger

Replace all SLF4J usage (`@Slf4j`, `LoggerFactory`) across all service and controller classes with
InternalLogger (`com.acme.logging.InternalLogger`) to comply with organizational guardrails.

### Task 6: Migrate HTTP Client to ServiceMesh SDK

Replace all `RestTemplate` usage with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to route
service-to-service communication through the mesh layer (circuit breaking, mTLS, tracing).

### Task 7: Migrate Error Handling to Result\<T\> Pattern

Replace exception-based business logic flow control (`throw`, `try/catch` for flow control,
`@ControllerAdvice` for business exceptions) with the Result\<T\> pattern (`com.acme.commons.Result`)
per P0-2024-0847 mandate.

### Task 8: Containerize Application

Containerize the application using the approved base images (`mcr.microsoft.com/openjdk/jdk:25-ubuntu`
for the build stage, `mcr.microsoft.com/openjdk/jdk:25-distroless` for the runtime stage).

### Task 9: Deploy to Azure Container Apps

Deploy the containerized application to Azure Container Apps (ACA) using Bicep IaC. Provision all
required Azure resources (ACA environment, Azure Service Bus namespace, Azure Database for MySQL,
Azure Key Vault, Managed Identity assignments).
