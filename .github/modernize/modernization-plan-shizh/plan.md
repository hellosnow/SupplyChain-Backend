# Modernization Plan: SupplyChain Backend — Azure Migration

**Plan Name**: modernization-plan-shizh
**Project**: SupplyChain Backend (`supplychain-backend`)

---

## Technical Framework

- **Language**: Java 8 (target: Java 21)
- **Framework**: Spring Boot 2.7.18 / Spring Framework 5.x (target: Spring Boot 3.x / Spring Framework 6.x)
- **Build Tool**: Maven
- **Database**: MySQL (password-based authentication)
- **Messaging**: RabbitMQ via Spring AMQP
- **Key Dependencies**: Spring Data JPA, Hibernate, Lombok, Spring AMQP, Spring Validation

---

## Overview

This migration modernizes the SupplyChain Backend from a legacy Spring Boot 2.x / Java 8 stack to a cloud-ready application on Azure. The application currently uses end-of-support runtimes, locally hosted services with plaintext credentials, and an on-premises RabbitMQ broker. The new architecture will:

- Upgrade to Spring Boot 3.x / Java 21 / Spring Framework 6.x (Jakarta EE), resolving all end-of-OSS-support and Java version compliance issues
- Replace MySQL (password-based) with Azure Database for MySQL using passwordless Managed Identity authentication
- Replace RabbitMQ (Spring AMQP) with Azure Service Bus for fully managed, secure cloud messaging
- Eliminate plaintext credentials in configuration files by storing secrets in Azure Key Vault
- Remediate all known CVE vulnerabilities in project dependencies

The migration follows a phased approach: runtime upgrade first, then service-by-service migration, followed by security remediation.

---

## Migration Impact Summary

```
| Application           | Original Service   | New Azure Service             | Authentication   | Comments                              |
|-----------------------|--------------------|-------------------------------|------------------|---------------------------------------|
| supplychain-backend   | MySQL              | Azure Database for MySQL      | Managed Identity | Passwordless connection               |
| supplychain-backend   | RabbitMQ (AMQP)    | Azure Service Bus             | Managed Identity | Spring AMQP migrated to Service Bus   |
| supplychain-backend   | Plaintext secrets  | Azure Key Vault               | Managed Identity | DB + broker credentials removed       |
```

---

## Upgrade Tasks

### Task 1 — Spring Boot 3.x Upgrade

Upgrade the project from Spring Boot 2.7.18 / Java 8 to Spring Boot 3.x / Java 21. This upgrade encompasses:
- Java runtime upgrade from 8 to 21
- Spring Framework upgrade from 5.x to 6.x
- Jakarta EE migration (`javax.*` → `jakarta.*` namespace)
- Dependency compatibility updates

**Assessment findings addressed**: `azure-java-version-02000`, `spring-boot-to-azure-spring-boot-version-01000`, `spring-framework-version-01000`

---

## Migration Tasks

### Task 2 — Migrate MySQL to Azure Database for MySQL (Managed Identity)

Migrate the application's relational database connection from password-based MySQL to Azure Database for MySQL using Managed Identity for secure, credential-free authentication.

**Assessment findings addressed**: `azure-database-mysql-01000`, `azure-password-01000` (datasource password)

### Task 3 — Migrate RabbitMQ (Spring AMQP) to Azure Service Bus

Migrate all Spring AMQP / RabbitMQ messaging code (configuration, producers, consumers) to Azure Service Bus. This covers `AppConfig`, `InventoryService`, and `PurchaseOrderService`.

**Assessment findings addressed**: `azure-message-queue-rabbitmq-01000`, `azure-message-queue-amqp-02000`

### Task 4 — Migrate Plaintext Credentials to Azure Key Vault

Remove all plaintext credentials from `application.yml` (database passwords, RabbitMQ credentials) and store them in Azure Key Vault, accessed via Managed Identity.

**Assessment findings addressed**: `azure-password-01000`

---

## Security Compliance

**Description**: Scan all project dependencies for known CVE vulnerabilities and apply the necessary dependency upgrades or patches to achieve a CVE-clean state.

**Requirements**: Validate all dependencies for known CVEs and upgrade or replace any vulnerable library versions. All CVE issues must be resolved after applying fixes.

**App Scope**: `.` (project root)

**Skills**:
- Skill Name: `validate-cves-and-fix`
  - Skill Location: builtin

---

## Assessment Summary

| Rule ID | Title | Severity | Category | Status |
|---------|-------|----------|----------|--------|
| azure-java-version-02000 | Legacy Java version | Mandatory | java-version-upgrade | Addressed by Task 1 |
| spring-boot-to-azure-spring-boot-version-01000 | Spring Boot End of OSS Support | Mandatory | framework-upgrade | Addressed by Task 1 |
| spring-framework-version-01000 | Spring Framework End of OSS Support | Mandatory | framework-upgrade | Addressed by Task 1 |
| azure-database-mysql-01000 | MySQL database found | Potential | database-migration | Addressed by Task 2 |
| azure-message-queue-rabbitmq-01000 | Spring RabbitMQ usage found | Optional | messaging-service-migration | Addressed by Task 3 |
| azure-message-queue-amqp-02000 | Spring AMQP dependency found | Optional | messaging-service-migration | Addressed by Task 3 |
| azure-password-01000 | Password found in config file | Potential | local-credential | Addressed by Tasks 2 & 4 |
| localhost-http-00001 | Local HTTP Calls | Mandatory | local-resource-access | Addressed by Task 4 |
| hardcoded-urls-00001 | Hardcoded HTTP URLs | Optional | remote-communication | Addressed by Task 4 |
| unsecure-network-protocol-00000 | Unsecure network protocols | Mandatory | remote-communication | Addressed by Task 4 |
