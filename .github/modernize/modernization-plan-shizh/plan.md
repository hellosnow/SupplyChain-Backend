# Modernization Plan: modernization-plan-shizh

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18 (End of OSS Support)
- **Build Tool**: Maven 3.8
- **Database**: MySQL (hardcoded credentials, no managed identity)
- **Messaging**: RabbitMQ via Spring AMQP (on-premises)
- **Key Dependencies**: Spring Data JPA, Hibernate, Lombok, Spring AMQP / RabbitMQ, RestTemplate

---

## Overview

> This migration upgrades and modernizes the SupplyChain Backend Java application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x, and migrates all Azure-incompatible dependencies to their cloud-native Azure equivalents. The application currently uses RabbitMQ for messaging, a MySQL database with plaintext credentials, RestTemplate for HTTP calls, and SLF4J for logging — all of which violate Acme Corp's modernization playbook guardrails. The new architecture will:
>
> - Run on Java 25 and Spring Boot 4.x (Jakarta EE), eliminating end-of-life runtimes and associated CVE exposure
> - Replace RabbitMQ AMQP messaging with Azure Service Bus using Managed Identity for secure, cloud-native message handling
> - Replace the MySQL password-based datasource with Azure Database for MySQL using Managed Identity for passwordless authentication
> - Move all remaining plaintext secrets and credentials to Azure Key Vault, fulfilling the mandatory secrets management policy
> - Replace RestTemplate HTTP calls with the internal ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) to satisfy the mesh communication guardrail
> - Replace SLF4J / Logback logging with InternalLogger (`com.acme.logging.InternalLogger`) for structured, trace-aware logging
> - Replace exception-based business-logic flow control with the `Result<T>` pattern (`com.acme.commons.Result`) per the post-incident mandate
> - Scan and remediate all CVE vulnerabilities in project dependencies
> - Update the Dockerfile to use the approved `mcr.microsoft.com/openjdk/jdk:25-ubuntu` build image and `mcr.microsoft.com/openjdk/jdk:25-distroless` runtime image
>
> The migration follows the Acme Corp Modernization Playbook (charter.md, targets.md, policies.md) and is sequenced so that the framework upgrade happens first; all transform tasks depend on the upgraded runtime.

---

## Migration Impact Summary

| Application           | Original Service           | New Azure Service                   | Authentication    | Comments                                      |
|-----------------------|---------------------------|-------------------------------------|-------------------|-----------------------------------------------|
| SupplyChain Backend   | Java 8 / Spring Boot 2.7  | Java 25 / Spring Boot 4.x           | N/A               | Upgrade runtime + Jakarta EE migration        |
| SupplyChain Backend   | RabbitMQ (Spring AMQP)    | Azure Service Bus                   | Managed Identity  | Migrate 3 queues to Service Bus queues        |
| SupplyChain Backend   | MySQL (password-based)    | Azure Database for MySQL            | Managed Identity  | Passwordless auth via Spring Cloud Azure      |
| SupplyChain Backend   | Plaintext credentials     | Azure Key Vault                     | Managed Identity  | application.yml secrets → Key Vault           |
| SupplyChain Backend   | RestTemplate              | ServiceMesh SDK (com.acme.mesh)     | N/A               | Vendor rating service HTTP call               |
| SupplyChain Backend   | SLF4J / @Slf4j / Logback  | InternalLogger (com.acme.logging)   | N/A               | All services use SLF4J today                  |
| SupplyChain Backend   | Exception flow control    | Result\<T\> (com.acme.commons)      | N/A               | PO, Inventory, Vendor services                |
| SupplyChain Backend   | Old Java 8 base images    | mcr.microsoft.com/openjdk/jdk:25-*  | N/A               | Build + distroless runtime images             |

---

## Tasks

### Task 1 — Spring Boot 4.x Upgrade (Java 25)

**Type**: Upgrade

**Description**: Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x. This includes Spring Framework 7.x and Jakarta EE namespace migration (`javax.*` → `jakarta.*`).

**Reason**: The assessment report identifies mandatory issues for outdated Java version (`azure-java-version-02000`) and end-of-OSS-support Spring Boot (`spring-boot-to-azure-spring-boot-version-01000`) and Spring Framework (`spring-framework-version-01000`). Per targets.md, Java 25 and Spring Boot 4.x are required.

---

### Task 2 — Migrate RabbitMQ AMQP to Azure Service Bus

**Type**: Transform

**Description**: Replace Spring AMQP RabbitMQ messaging with Azure Service Bus using Managed Identity for passwordless authentication.

**Reason**: Assessment identifies `azure-message-queue-rabbitmq-01000` and `azure-message-queue-amqp-02000` issues. Per charter.md, all Java services must replatform to Azure. Hardcoded RabbitMQ credentials (`guest/guest`) violate the secrets management policy.

**Depends on**: Task 1

---

### Task 3 — Migrate MySQL to Azure Database for MySQL (Managed Identity)

**Type**: Transform

**Description**: Replace the password-based MySQL datasource connection with Azure Database for MySQL using Managed Identity for passwordless authentication via Spring Cloud Azure.

**Reason**: Assessment identifies `azure-database-mysql-01000` and `azure-password-01000` issues. Per policies.md, service-to-service auth must use Managed Identity; hardcoded credentials are prohibited.

**Depends on**: Task 1

---

### Task 4 — Migrate Plaintext Credentials to Azure Key Vault

**Type**: Transform

**Description**: Remove all remaining hardcoded plaintext credentials and secrets from `application.yml` and source code, and store them in Azure Key Vault. Access secrets via Spring Cloud Azure Key Vault starter.

**Reason**: Assessment identifies `azure-password-01000` issues. Per policies.md, all sensitive values must be stored in Azure Key Vault. Per policies.md required elements, every modernized application must include Azure Key Vault.

**Depends on**: Task 1

---

### Task 5 — Migrate RestTemplate to ServiceMesh SDK

**Type**: Transform

**Description**: Replace `RestTemplate` usage (in `VendorService` and `AppConfig`) with the internal ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) for all service-to-service HTTP communication.

**Reason**: Per policies.md and targets.md, `RestTemplate` is a prohibited technology that bypasses the service mesh layer. All service-to-service communication must go through the ServiceMesh SDK which provides mTLS, circuit breaking, and distributed tracing. Assessment also identifies `localhost-http-00001`, `hardcoded-urls-00001`, and `unsecure-network-protocol-00000` violations in the same code path.

**Depends on**: Task 1

---

### Task 6 — Migrate SLF4J Logging to InternalLogger

**Type**: Transform

**Description**: Replace all SLF4J / Logback logging (`@Slf4j`, `LoggerFactory`, direct logger usage) in all service classes with InternalLogger (`com.acme.logging.InternalLogger`).

**Reason**: Per policies.md, SLF4J, Log4j, and Logback are prohibited because they lack trace-context integration. InternalLogger is the mandatory logging framework that injects trace IDs, team tags, and structured JSON. Affected files: `VendorService.java`, `InventoryService.java`, `PurchaseOrderService.java`.

**Depends on**: Task 1

---

### Task 7 — Migrate Exception Handling to Result\<T\> Pattern

**Type**: Transform

**Description**: Replace exception-based business-logic flow control with the `Result<T>` pattern (`com.acme.commons.Result`) in all service classes. Remove `try/catch` blocks used for flow control and any `@ControllerAdvice` for business exceptions.

**Reason**: Per policies.md (post-incident mandate P0-2024-0847), throwing exceptions for business logic flow control is prohibited. Affected files: `VendorService.java` (lines 33), `InventoryService.java` (line 38), `PurchaseOrderService.java` (lines 44, 47, 70, 83).

**Depends on**: Task 1

---

### Task 8 — CVE Security Remediation

**Type**: Security

**Description**: Scan all project Maven dependencies for known CVEs (Common Vulnerabilities and Exposures) and upgrade or replace any vulnerable dependencies to achieve a clean CVE status.

**Reason**: User explicitly requests "make sure all CVE issues are fixed". The current pom.xml uses Spring Boot 2.7.18 and `mysql-connector-java:8.0.33` which may carry known CVEs.

**Depends on**: Tasks 1–7

---

### Task 9 — Update Dockerfile for Java 25 / ACA

**Type**: Containerization

**Description**: Update the existing `Dockerfile` to use the approved Microsoft OpenJDK base images for Java 25: `mcr.microsoft.com/openjdk/jdk:25-ubuntu` for the build stage and `mcr.microsoft.com/openjdk/jdk:25-distroless` for the runtime stage.

**Reason**: The existing Dockerfile uses `maven:3.8-openjdk-8` and `eclipse-temurin:8-jre`, which are end-of-life and not on the approved image list. Per targets.md, the approved container base images are `mcr.microsoft.com/openjdk/jdk:25-ubuntu` (build) and `mcr.microsoft.com/openjdk/jdk:25-distroless` (runtime). The application targets Azure Container Apps per charter.md.

**Depends on**: Task 8
