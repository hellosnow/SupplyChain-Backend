# Modernization Plan: modernization-plan-shizh

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18 (End of OSS Support), Spring Framework 5.x
- **Build Tool**: Maven 3.8
- **Database**: MySQL (local, password-based authentication)
- **Messaging**: RabbitMQ (Spring AMQP)
- **Key Dependencies**: Spring Data JPA, Spring Boot AMQP, mysql-connector-java 8.0.33, Lombok

---

## Overview

> This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x and replatforms it onto Azure Container Apps. The application currently uses an end-of-life Java runtime, an unsupported Spring Boot version, on-premises MySQL and RabbitMQ, and stores credentials in plaintext configuration files.
>
> The new architecture will:
>
> - Run on Java 25 and Spring Boot 4.x to eliminate end-of-life risks and meet the organization's LTS targets
> - Replace RabbitMQ with Azure Service Bus for fully managed, cloud-native messaging
> - Migrate the MySQL datasource to Azure Database for MySQL using Managed Identity for passwordless, policy-compliant authentication
> - Externalize all sensitive credentials to Azure Key Vault, removing hardcoded passwords from configuration files
> - Validate and remediate known CVE vulnerabilities in all project dependencies
> - Containerize the application using the approved Microsoft OpenJDK 25 base images and deploy to Azure Container Apps (ACA)
>
> The migration follows a phased approach: runtime upgrade first, then Azure service integrations and security hardening, followed by CVE remediation, containerization, and deployment.

---

## Migration Impact Summary

| Application            | Original Service        | New Azure Service                    | Authentication     | Comments                                                     |
|------------------------|-------------------------|--------------------------------------|--------------------|--------------------------------------------------------------|
| supplychain-backend    | Java 8 / Spring Boot 2.7 | Java 25 / Spring Boot 4.x           | N/A                | Upgrade runtime and framework to meet LTS policy targets     |
| supplychain-backend    | RabbitMQ (Spring AMQP)  | Azure Service Bus                    | Managed Identity   | Replace on-premises broker; migrate producers and consumers  |
| supplychain-backend    | MySQL (local, password) | Azure Database for MySQL             | Managed Identity   | Passwordless auth via Spring Cloud Azure                     |
| supplychain-backend    | Plaintext credentials   | Azure Key Vault                      | Managed Identity   | Remove hardcoded passwords from application.yml              |
| supplychain-backend    | Dependency CVEs         | Remediated dependencies              | N/A                | Scan and fix all known CVE vulnerabilities                   |
| supplychain-backend    | Local Docker image      | Azure Container Apps (ACA)           | Managed Identity   | Containerize and deploy to ACA using Bicep IaC               |

---

## Phase 1 — Runtime Upgrade

Upgrade the application from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x. This includes the Spring Framework 7.x upgrade and Jakarta EE namespace migration (`javax.*` → `jakarta.*`). All subsequent migration tasks depend on this step.

**Assessment issues addressed**: `azure-java-version-02000`, `spring-boot-to-azure-spring-boot-version-01000`, `spring-framework-version-01000`

---

## Phase 2 — Azure Service Integrations

### 2.1 — Migrate RabbitMQ to Azure Service Bus

Replace the Spring AMQP RabbitMQ dependencies and configuration with Azure Service Bus messaging. Migrate all message producers and consumers in `InventoryService`, `PurchaseOrderService`, and `AppConfig`.

**Assessment issues addressed**: `azure-message-queue-rabbitmq-01000`, `azure-message-queue-amqp-02000`

### 2.2 — Migrate MySQL to Azure Database for MySQL (Managed Identity)

Replace the password-based MySQL datasource configuration with Azure Database for MySQL using Managed Identity for passwordless authentication via Spring Cloud Azure.

**Assessment issues addressed**: `azure-database-mysql-01000`

### 2.3 — Migrate Plaintext Credentials to Azure Key Vault

Remove all hardcoded credentials from `application.yml` and source code and retrieve them securely from Azure Key Vault using the Spring Cloud Azure Key Vault starter.

**Assessment issues addressed**: `azure-password-01000`

---

## Phase 3 — Security: CVE Remediation

Scan all project dependencies for known CVE vulnerabilities and upgrade affected libraries to patched versions to achieve a clean security posture.

**User requirement**: "make sure all CVE issues are fixed"

---

## Phase 4 — Containerization

Update the multi-stage Dockerfile to use the approved Microsoft OpenJDK 25 base images (`mcr.microsoft.com/openjdk/jdk:25-ubuntu` for the build stage and `mcr.microsoft.com/openjdk/jdk:25-distroless` for the runtime stage) per the organization's container policy.

---

## Phase 5 — Deployment to Azure Container Apps

Generate Bicep infrastructure-as-code and deploy the containerized application to Azure Container Apps with Managed Identity, Azure Key Vault, Azure Service Bus, and Azure Database for MySQL as backing services.

---
