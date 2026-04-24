# Modernization Plan: SupplyChain Backend Azure Migration

**Project**: supplychain-backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18 (Spring Framework 5.x)
- **Build Tool**: Maven
- **Database**: MySQL 8.0 (with hardcoded credentials)
- **Messaging**: RabbitMQ via Spring AMQP
- **Key Dependencies**: Spring Data JPA, Spring AMQP,
  Lombok, Spring Validation

---

## Overview

> This migration upgrades the SupplyChain Backend from
> a legacy Java 8 / Spring Boot 2.7.18 stack and migrates
> it to Azure-managed services. The application currently
> uses on-premises MySQL, RabbitMQ messaging, and stores
> credentials in plaintext configuration files.
> The modernized architecture will:
>
> - Upgrade to Spring Boot 3.x with Java 21, including
>   Jakarta EE namespace migration and Spring Framework 6.x
> - Migrate MySQL to Azure Database for MySQL with managed
>   identity for passwordless, secure authentication
> - Replace RabbitMQ with Azure Service Bus for fully
>   managed cloud messaging
> - Move hardcoded credentials to Azure Key Vault for
>   centralized secrets management
> - Remediate all known CVE vulnerabilities in project
>   dependencies
> - Update the container image for the modernized runtime
>
> The migration follows a phased approach: upgrade first,
> then transform services, fix security issues, and finally
> update containerization.

---

## Migration Impact Summary

| Application          | Original Service | New Azure Service                  | Authentication   | Comments                        |
|----------------------|------------------|------------------------------------|------------------|---------------------------------|
| supplychain-backend  | MySQL 8.0        | Azure Database for MySQL           | Managed Identity | Passwordless connection         |
| supplychain-backend  | RabbitMQ (AMQP)  | Azure Service Bus                  | Managed Identity | Spring AMQP to Service Bus      |
| supplychain-backend  | Plaintext creds  | Azure Key Vault                    | Managed Identity | Secrets externalized            |
| supplychain-backend  | Java 8 / SB 2.7  | Java 21 / Spring Boot 3.x         | N/A              | Framework upgrade               |

---

## Task 1: Upgrade Spring Boot 3.x

**Description**: Upgrade the application from Spring Boot
2.7.18 / Java 8 to Spring Boot 3.x / Java 21, including
Spring Framework 6.x and Jakarta EE namespace migration
(javax.* → jakarta.*).

**App Scope**: Entire project (pom.xml, all source files)

---

## Task 2: Migrate MySQL to Azure Database for MySQL

**Description**: Migrate from on-premises MySQL to Azure
Database for MySQL with managed identity for secure,
credential-free authentication.

**Skills**:
  - Skill Name: migration-mi-mysql-azure-sdk-public-cloud
    - Skill Location: builtin

**App Scope**: Database configuration and data access layer

---

## Task 3: Migrate RabbitMQ to Azure Service Bus

**Description**: Migrate from RabbitMQ with Spring AMQP to
Azure Service Bus for fully managed cloud messaging.

**Skills**:
  - Skill Name: migration-amqp-rabbitmq-servicebus
    - Skill Location: builtin

**App Scope**: Messaging configuration and service classes

---

## Task 4: Migrate Credentials to Azure Key Vault

**Description**: Migrate hardcoded credentials from
application.yml to Azure Key Vault for centralized
and secure secrets management.

**Skills**:
  - Skill Name: migration-plaintext-credential-to-azure-keyvault
    - Skill Location: builtin

**App Scope**: Configuration files (application.yml)

---

## Security Compliance

**Description**: Validate all project dependencies for
known CVE vulnerabilities and apply fixes to ensure a
clean security posture.

**Requirements**:
  Fix all CVE issues in project dependencies as requested.

**Environment Configuration**:
  Runtime: Java 21 (established by upgrade task).
  Build tool: Maven.

**App Scope**: Entire project (pom.xml, all dependencies)

**Skills**:
  - Skill Name: validate-cves-and-fix
    - Skill Location: builtin

---

## Task 6: Update Containerization

**Description**: Update the Dockerfile to use Java 21 base
images appropriate for the modernized application runtime
and Azure Container Apps deployment target.

**App Scope**: Dockerfile

---
