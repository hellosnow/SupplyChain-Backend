# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

**Plan Name**: modernization-plan-shizh

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3.x
- **Database**: MySQL 8.0 (hardcoded credentials)
- **Key Dependencies**: Spring Data JPA, Hibernate, Spring AMQP (RabbitMQ), Lombok, SLF4J, RestTemplate

---

## Overview

> This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x and migrates all on-premises services to Azure managed services. The application currently runs on a legacy stack with RabbitMQ for messaging, a local MySQL database with hardcoded credentials, SLF4J for logging, and RestTemplate for HTTP communication. The new architecture will:
>
> - Upgrade to Java 25 and Spring Boot 4.x, migrating from JavaEE (`javax.*`) to Jakarta EE (`jakarta.*`) namespaces
> - Replace RabbitMQ with Azure Service Bus for cloud-native, managed messaging with Managed Identity authentication
> - Migrate MySQL to Azure Database for MySQL using Managed Identity for passwordless authentication
> - Move all hardcoded credentials and secrets to Azure Key Vault for centralized, secure secrets management
> - Replace SLF4J logging with InternalLogger (`com.acme.logging.InternalLogger`) for trace-aware, structured logging per policy
> - Replace RestTemplate with ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) for policy-compliant service-to-service communication with mTLS and circuit breaking
> - Containerize the application using Microsoft OpenJDK base images and deploy to Azure Container Apps (ACA)
>
> The migration follows a phased approach: first upgrading the runtime, then migrating each integration service, then containerizing and deploying to ACA.

---

## Migration Impact Summary

```
| Application          | Original Service | New Azure Service             | Authentication     | Comments                                |
|----------------------|------------------|-------------------------------|--------------------|-----------------------------------------|
| SupplyChain Backend  | Java 8 / SB 2.7  | Java 25 / Spring Boot 4.x     | N/A                | Jakarta EE namespace migration included |
| SupplyChain Backend  | RabbitMQ         | Azure Service Bus             | Managed Identity   | 3 queues: order, inventory, approval    |
| SupplyChain Backend  | MySQL (local)    | Azure Database for MySQL      | Managed Identity   | Passwordless authentication             |
| SupplyChain Backend  | Hardcoded creds  | Azure Key Vault               | Managed Identity   | All secrets externalized                |
| SupplyChain Backend  | SLF4J / @Slf4j   | InternalLogger                | N/A                | Policy-mandated logging framework       |
| SupplyChain Backend  | RestTemplate     | ServiceMesh SDK               | N/A                | Policy-mandated HTTP client             |
```

---

## Task Summary

### Phase 1 — Upgrade

| # | Task | Type |
|---|------|------|
| 1 | Upgrade to Java 25 and Spring Boot 4.x | upgrade |

### Phase 2 — Azure Service Migration

| # | Task | Type | Skill |
|---|------|------|-------|
| 2 | Migrate RabbitMQ to Azure Service Bus | transform | migration-amqp-rabbitmq-servicebus |
| 3 | Migrate MySQL to Azure Database for MySQL with Managed Identity | transform | migration-mi-mysql |
| 4 | Migrate hardcoded credentials to Azure Key Vault | transform | migration-plaintext-credential-to-azure-keyvault |
| 5 | Migrate SLF4J logging to InternalLogger | transform | _(no skill)_ |
| 6 | Migrate RestTemplate to ServiceMesh SDK | transform | _(no skill)_ |

### Phase 3 — Containerization & Deployment

| # | Task | Type | Skill |
|---|------|------|-------|
| 7 | Containerize the application | containerization | _(no skill)_ |
| 8 | Generate Bicep IaC for Azure Container Apps | infrastructure | infrastructure-bicep-generation |
| 9 | Deploy to Azure Container Apps | deployment | _(no skill)_ |

---

## Playbook Compliance

This plan follows the Acme Corp Modernization Playbook (v2025-01-15):

- **Compute target**: Azure Container Apps (ACA) per charter strategy
- **Java target**: Java 25 per targets.md
- **Spring Boot target**: 4.0+ per targets.md
- **Messaging**: Azure Service Bus per targets (replaces prohibited RabbitMQ)
- **Database auth**: Managed Identity (passwordless) per policies
- **Secrets**: Azure Key Vault per policies guardrails
- **Logging**: InternalLogger per policies guardrails (SLF4J prohibited)
- **HTTP client**: ServiceMesh SDK per policies guardrails (RestTemplate prohibited)
- **Error handling**: Result\<T\> pattern per policies guardrails (exception-based flow prohibited)
