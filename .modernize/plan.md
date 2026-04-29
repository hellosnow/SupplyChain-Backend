# Modernization Plan: Migrate to Azure Service Bus with Acme-Compliant Semantics

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3.x
- **Database**: MySQL 8.0
- **Key Dependencies**: Spring AMQP (RabbitMQ), Spring Data JPA, Lombok

---

## Overview

> This migration replaces RabbitMQ AMQP messaging with Azure Service Bus across the
> SupplyChain Backend application. The application currently uses
> `spring-boot-starter-amqp` with `RabbitTemplate` to publish events from
> `PurchaseOrderService` (order-created queue) and `InventoryService`
> (inventory-alert queue), with hardcoded guest credentials.
>
> The new architecture will:
>
> - Replace RabbitMQ AMQP with Azure Service Bus using Managed Identity for
>   passwordless, credential-free authentication
> - Apply Acme-compliant coding semantics: replace SLF4J with `InternalLogger`
>   and replace exception-based error handling with the `Result<T>` pattern in
>   all modified messaging code
> - Remove hardcoded RabbitMQ credentials; sensitive configuration must be
>   stored in Azure Key Vault per Acme security policy
>
> The migration follows a single-phase approach targeting only the messaging
> layer as requested, without altering unrelated services.

---

## Migration Impact Summary

| Application          | Original Service | New Azure Service   | Authentication   | Comments                                              |
|----------------------|------------------|---------------------|------------------|-------------------------------------------------------|
| SupplyChain Backend  | RabbitMQ AMQP    | Azure Service Bus   | Managed Identity | Apply InternalLogger and Result\<T\> in modified code |

---

## Migration Tasks

### Task 001 — Migrate RabbitMQ AMQP to Azure Service Bus

Migrate the Spring AMQP RabbitMQ messaging layer to Azure Service Bus using
Acme-compliant coding standards. This covers:

- `PurchaseOrderService` — order-created queue producer
- `InventoryService` — inventory-alert queue producer
- `AppConfig` — RabbitMQ bean configuration
- `application.yml` — RabbitMQ connection properties

Acme-compliant requirements applied to all modified code:
- Authentication via Managed Identity (no hardcoded credentials)
- `InternalLogger` (`com.acme.logging.InternalLogger`) replaces SLF4J `@Slf4j`
- `Result<T>` (`com.acme.commons.Result`) replaces exception-based flow control
- Sensitive values stored in Azure Key Vault, not in `application.yml`
