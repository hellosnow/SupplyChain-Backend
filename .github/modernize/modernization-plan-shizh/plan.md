# Modernization Plan: Upgrade and Migrate to Azure

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven 3.x
- **Database**: MySQL 8.0 (password-based authentication)
- **Key Dependencies**: Spring Data JPA, Spring AMQP (RabbitMQ),
  Lombok, Spring Web (RestTemplate)

---

## Overview

This migration upgrades and replatforms the SupplyChain Backend from a
Java 8 / Spring Boot 2.7.18 on-premises architecture to a modern
Azure-hosted platform. The application currently uses RabbitMQ for
messaging, MySQL with hardcoded credentials, RestTemplate for
service-to-service HTTP calls, SLF4J for logging, and exception-based
flow control. The new architecture will:

- Upgrade to Java 25 and Spring Boot 4.x, eliminating all end-of-life
  runtime dependencies as required by the Acme Corp playbook
- Replace on-premises messaging (RabbitMQ) and database (MySQL) with
  Azure Service Bus and Azure Database for MySQL, using Managed Identity
  for passwordless authentication
- Enforce all playbook guardrails: ServiceMesh SDK for inter-service
  communication, InternalLogger for structured observability, Result<T>
  pattern for error handling, and Azure Key Vault for centralized
  secrets management
- Replatform the containerized application to Azure Container Apps as
  the default compute host per the Acme Corp modernization charter

The migration follows a phased approach: runtime upgrade first, then
Azure service integrations and code pattern compliance, followed by
containerization and deployment to Azure Container Apps.

---

## Migration Impact Summary

| Application         | Original Service       | New Azure Service        | Authentication   | Comments                        |
|---------------------|------------------------|--------------------------|------------------|---------------------------------|
| SupplyChain Backend | Java 8                 | Java 25                  | N/A              | EOL; upgrade to playbook target |
| SupplyChain Backend | Spring Boot 2.7.18     | Spring Boot 4.x          | N/A              | EOL; upgrade to playbook target |
| SupplyChain Backend | RabbitMQ (AMQP)        | Azure Service Bus        | Managed Identity | Playbook messaging target       |
| SupplyChain Backend | MySQL (password-auth)  | Azure Database for MySQL | Managed Identity | Playbook database target        |
| SupplyChain Backend | Hardcoded credentials  | Azure Key Vault          | Managed Identity | Playbook secrets requirement    |
| SupplyChain Backend | RestTemplate           | ServiceMesh SDK          | N/A              | Guardrail: prohibited tech      |
| SupplyChain Backend | SLF4J / @Slf4j         | InternalLogger           | N/A              | Guardrail: prohibited tech      |
| SupplyChain Backend | Exception flow control | Result<T> pattern        | N/A              | Guardrail: prohibited pattern   |
| SupplyChain Backend | Local process          | Azure Container Apps     | Managed Identity | Replatform per charter          |
