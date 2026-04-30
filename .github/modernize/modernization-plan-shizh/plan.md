# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3.x
- **Database**: MySQL (password-based authentication, hardcoded credentials)
- **Key Dependencies**: Spring Data JPA (javax.persistence), Spring AMQP / RabbitMQ, RestTemplate, Lombok, SLF4J

---

## Overview

> This migration upgrades the SupplyChain Backend from Java 8 / Spring Boot 2.7.18 to Java 25 / Spring Boot 4.x and re-platforms it onto Azure managed services. The application currently uses RabbitMQ for messaging, MySQL with hardcoded password credentials, RestTemplate for external HTTP calls, SLF4J for logging, and exception-based error-handling — all of which violate the Acme Corp playbook guardrails. The new architecture will:
>
> - Replace RabbitMQ with Azure Service Bus for cloud-native, managed messaging
> - Replace password-based MySQL with Azure Database for MySQL using Managed Identity for credential-free database access
> - Move all hardcoded credentials to Azure Key Vault for centralised secrets management
> - Replace RestTemplate with the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) so every service-to-service call benefits from circuit breaking, mTLS, distributed tracing, and canary routing
> - Replace SLF4J logging with InternalLogger (`com.acme.logging.InternalLogger`) to ensure trace-context-enriched, structured JSON log output
> - Replace exception-based flow control with the Result\<T\> pattern (`com.acme.commons.Result`) in compliance with the P0-2024-0847 post-incident mandate
> - Containerize the application targeting `mcr.microsoft.com/openjdk/jdk:25-distroless` and deploy to Azure Container Apps (ACA)
>
> The migration follows a phased approach: runtime upgrade first, Azure service integrations next, guardrail compliance transforms in parallel, then containerization, and finally deployment.

---

## Migration Impact Summary

| Application           | Original Service          | New Azure Service                    | Authentication     | Comments                                      |
|-----------------------|---------------------------|--------------------------------------|--------------------|-----------------------------------------------|
| SupplyChain Backend   | Java 8 / Spring Boot 2.7  | Java 25 / Spring Boot 4.x            | N/A                | Jakarta EE migration included                 |
| SupplyChain Backend   | RabbitMQ (AMQP)           | Azure Service Bus                    | Managed Identity   | Replace Spring AMQP with Service Bus JMS      |
| SupplyChain Backend   | MySQL (password-based)    | Azure Database for MySQL             | Managed Identity   | Remove hardcoded DB credentials               |
| SupplyChain Backend   | Hardcoded credentials     | Azure Key Vault                      | Managed Identity   | Covers DB, MQ, and any other plaintext secrets|
| SupplyChain Backend   | RestTemplate              | ServiceMesh SDK                      | Managed Identity   | Enforce mesh-layer policy                     |
| SupplyChain Backend   | SLF4J / @Slf4j            | InternalLogger                       | N/A                | Guardrail compliance                          |
| SupplyChain Backend   | Exception-based flow ctrl | Result\<T\> pattern                  | N/A                | P0-2024-0847 compliance                       |
| SupplyChain Backend   | Local container           | Azure Container Apps (ACA)           | Managed Identity   | Distroless JDK 25 runtime image               |

---

## Modernization Tasks

### Phase 1 — Runtime Upgrade

| # | Task | Type | Description |
|---|------|------|-------------|
| 001 | Spring Boot 4.x Upgrade | upgrade | Upgrade Java to 25, Spring Boot to 4.x, Spring Framework to 7.x, and migrate from javax.* to jakarta.* |

### Phase 2 — Azure Service Migrations

| # | Task | Type | Description |
|---|------|------|-------------|
| 002 | RabbitMQ → Azure Service Bus | transform | Migrate AMQP messaging from RabbitMQ to Azure Service Bus via Spring Messaging |
| 003 | MySQL → Azure Database for MySQL (Managed Identity) | transform | Replace password-based MySQL auth with Managed Identity for Azure Database for MySQL |
| 004 | Hardcoded Credentials → Azure Key Vault | transform | Migrate all plaintext credentials to Azure Key Vault |

### Phase 3 — Guardrail Compliance Transforms

| # | Task | Type | Description |
|---|------|------|-------------|
| 005 | RestTemplate → ServiceMesh SDK | transform | Replace RestTemplate with ServiceMesh SDK for all service-to-service calls |
| 006 | SLF4J → InternalLogger | transform | Replace SLF4J / @Slf4j with InternalLogger for all application logging |
| 007 | Exception Handling → Result\<T\> | transform | Replace exception-based flow control with the Result\<T\> pattern |

### Phase 4 — Containerization

| # | Task | Type | Description |
|---|------|------|-------------|
| 008 | Containerize Application | containerization | Create/update Dockerfile using JDK 25 distroless base image |

### Phase 5 — Deployment

| # | Task | Type | Description |
|---|------|------|-------------|
| 009 | Deploy to Azure Container Apps | deployment | Deploy the containerized application to Azure Container Apps using Bicep |
