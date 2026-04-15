# Modernization Plan: SupplyChain Backend – Playbook Compliance Modernization

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Spring Boot 2.7.18
- **Build Tool**: Maven
- **Database**: MySQL (via Spring Data JPA / Hibernate)
- **Key Dependencies**: Spring AMQP (RabbitMQ), Spring Data JPA, Lombok, Spring Validation

---

## Overview

This migration modernizes the SupplyChain Backend to align with the organization's
internal playbook policies. The application currently runs on Java 8 and Spring Boot
2.7.x, uses RestTemplate for inter-service HTTP calls, SLF4J/Logback for logging,
exception-based flow control for business rule violations, and stores credentials
in plaintext within the application configuration. The new architecture will:

- Upgrade the runtime to Java 17 LTS and Spring Boot 3.x, replacing end-of-life
  versions mandated by the playbook.
- Replace all direct HTTP inter-service calls with the ServiceMesh SDK
  (`com.acme.mesh.ServiceMesh`) to enforce standardized, observable, and
  policy-compliant service-to-service communication.
- Adopt `InternalLogger` (`com.acme.logging.InternalLogger`) as the sole logging
  framework, replacing the current SLF4J / Logback implementation across all
  controllers and services.
- Replace exception-based business logic flow control with the `Result<T>` pattern
  (`com.acme.commons.Result`) to eliminate control-flow abuse and improve error
  transparency.
- Migrate all hardcoded credentials (database and messaging) to Azure Key Vault
  for secure, managed secret access.

The migration follows a task-by-task transformation approach, where each task is
independently executable and verifiable via build and unit tests. The upgrade task
runs first as a prerequisite for all subsequent transformations. Containerization
for AKS deployment is included as a final packaging step, aligned with the playbook
compute target.

---

## Migration Impact Summary

| Application         | Original Service       | New Service / Pattern     | Authentication   | Comments                                   |
|---------------------|------------------------|---------------------------|------------------|--------------------------------------------|
| SupplyChain Backend | Java 8 / Spring Boot 2 | Java 17 / Spring Boot 3.x | N/A              | End-of-life versions per playbook          |
| SupplyChain Backend | RestTemplate           | ServiceMesh SDK           | Managed Identity | Migrate inter-service HTTP calls to mesh   |
| SupplyChain Backend | SLF4J / Logback        | InternalLogger            | N/A              | Standardize to org logging framework       |
| SupplyChain Backend | Exception-based errors | Result\<T\> pattern       | N/A              | Replace exception flow with Result type    |
| SupplyChain Backend | Hardcoded credentials  | Azure Key Vault           | Managed Identity | Secure credential management per playbook  |
| SupplyChain Backend | Local JAR              | Container Image (AKS)     | Managed Identity | Package for Kubernetes deployment          |

---

## Migration Tasks

### Task 1 – Upgrade Java and Spring Boot
Upgrade the project from Java 8 and Spring Boot 2.7.18 to Java 17 LTS and Spring
Boot 3.x. Both Java 8 and Spring Boot 2.x are end-of-life for internal use per
the playbook. This task is a prerequisite for all subsequent transform tasks.

### Task 2 – Migrate HTTP Inter-service Calls to ServiceMesh SDK
Migrate all direct HTTP client usage (`RestTemplate` in `VendorService`) to use
the organization's ServiceMesh SDK (`com.acme.mesh.ServiceMesh`). All
service-to-service communication must go through the mesh per playbook policy.

### Task 3 – Migrate Logging to InternalLogger
Replace all SLF4J `@Slf4j` / `log.*` usages across controllers and services with
`InternalLogger` (`com.acme.logging.InternalLogger`). This is the sole permitted
logging framework per playbook policy.

### Task 4 – Replace Exception-based Flow Control with Result\<T\> Pattern
Refactor `PurchaseOrderService`, `VendorService`, and `InventoryService` to return
`Result<T>` (`com.acme.commons.Result`) instead of throwing exceptions for business
rule violations. Update controllers to handle `Result<T>` return values accordingly.

### Task 5 – Migrate Hardcoded Credentials to Azure Key Vault
Remove all hardcoded credentials (MySQL username/password and RabbitMQ
username/password) from `application.yml` and migrate secret access to Azure Key
Vault. The playbook prohibits plaintext credentials in application configuration.

### Task 6 – Containerization
Verify and update the existing `Dockerfile` to produce a production-ready container
image suitable for deployment to Azure Kubernetes Service (AKS), aligned with the
playbook compute target.
