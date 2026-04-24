# Modernization Plan: SupplyChain Backend Azure Migration

**Project**: supplychain-backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven
- **Database**: MySQL (hardcoded credentials)
- **Messaging**: RabbitMQ via Spring AMQP
- **Key Dependencies**: Spring Data JPA, Hibernate,
  Lombok, Spring Validation

---

## Overview

> This migration upgrades and modernizes the
> SupplyChain Backend application for Azure.
> The application currently runs on Java 8 with
> Spring Boot 2.7.18, uses MySQL with hardcoded
> credentials, and RabbitMQ for messaging.
> The new architecture will:
>
> - Upgrade to Spring Boot 3.x with Java 21 for
>   long-term support and modern language features
> - Migrate MySQL to Azure Database for MySQL with
>   managed identity for secure, credential-free
>   authentication
> - Migrate RabbitMQ messaging to Azure Service Bus
>   for a fully managed cloud messaging service
> - Validate and fix all CVE vulnerabilities to
>   ensure a secure dependency chain
> - Update containerization for the new Java 21
>   runtime
>
> The migration follows a phased approach: upgrade
> first, then transform services, fix security
> issues, and finally update containerization.

---

## Migration Impact Summary

| Application         | Original Service | New Azure Service          | Authentication   | Comments                  |
|---------------------|------------------|----------------------------|------------------|---------------------------|
| supplychain-backend | MySQL 8.0        | Azure Database for MySQL   | Managed Identity | Credential-free auth      |
| supplychain-backend | RabbitMQ (AMQP)  | Azure Service Bus          | Managed Identity | Fully managed messaging   |

---

## Phase 1: Upgrade

### Task 001 — Spring Boot 3.x Upgrade

Upgrade from Spring Boot 2.7.18 / Java 8 to
Spring Boot 3.x / Java 21. This includes Spring
Framework 6.x upgrade and Jakarta EE namespace
migration (javax.* → jakarta.*).

---

## Phase 2: Azure Service Migration

### Task 002 — MySQL to Azure Database for MySQL

Migrate the MySQL database connection to Azure
Database for MySQL with managed identity for
secure, credential-free authentication. Remove
hardcoded database credentials from
application.yml.

### Task 003 — RabbitMQ to Azure Service Bus

Migrate from RabbitMQ with Spring AMQP to Azure
Service Bus for messaging. Replace RabbitMQ
configuration, templates, and queue definitions
with Azure Service Bus equivalents.

---

## Phase 3: Security Compliance

### Task 004 — Validate CVEs and Fix

Validate all CVE vulnerabilities in project
dependencies and fix security issues to ensure a
clean, secure dependency chain.

---

## Phase 4: Containerization

### Task 005 — Update Dockerfile

Update the existing Dockerfile to use Java 21
base images compatible with the upgraded runtime.
