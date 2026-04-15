# Modernization Plan: SupplyChain Backend – Playbook Compliance Modernization

**Project**: SupplyChain Backend

---

## Technical Framework

- **Language**: Java 21
- **Framework**: Spring Boot 3.2.12
- **Build Tool**: Maven
- **Database**: Azure Database for PostgreSQL (Flexible Server)
- **Key Dependencies**: Spring Data JPA, Spring Cloud Azure (Service Bus, Key Vault),
  SLF4J/Logback (logstash-logback-encoder), Micrometer/OpenTelemetry

---

## Overview

This migration modernizes the SupplyChain Backend to align with the organization's
internal playbook policies. The application currently uses a direct HTTP client for
inter-service communication, SLF4J/Logback for logging, and exception-based error
propagation for business logic flow control. The new architecture will:

- Replace all direct HTTP inter-service calls with the ServiceMesh SDK
  (`com.acme.mesh.ServiceMesh`) to enforce standardized, observable, and
  policy-compliant service-to-service communication.
- Adopt `InternalLogger` (`com.acme.logging.InternalLogger`) as the sole logging
  framework, replacing the current SLF4J / Logback implementation across all
  controllers and services.
- Replace exception-based business logic flow control with the `Result<T>` pattern
  (`com.acme.commons.Result`) to eliminate control-flow abuse and improve error
  transparency.

The migration follows a task-by-task transformation approach, where each task is
independently executable and verifiable via build and unit tests. Containerization
for AKS deployment is included as a final packaging step, aligned with the playbook
compute target.

---

## Migration Impact Summary

| Application          | Original Service         | New Service / Pattern      | Authentication    | Comments                                      |
|----------------------|--------------------------|----------------------------|-------------------|-----------------------------------------------|
| SupplyChain Backend  | RestClient (Spring HTTP) | ServiceMesh SDK            | Managed Identity  | Migrate inter-service HTTP calls to mesh      |
| SupplyChain Backend  | SLF4J / Logback          | InternalLogger             | N/A               | Standardize to org logging framework          |
| SupplyChain Backend  | Exception-based errors   | Result\<T\> pattern        | N/A               | Replace exception flow with Result type       |
| SupplyChain Backend  | Local JAR                | Container Image (AKS)      | Managed Identity  | Package for Kubernetes deployment             |

---

## Migration Tasks

### Task 1 – Migrate HTTP Inter-service Calls to ServiceMesh SDK
Migrate all direct HTTP client usage (`RestClient` in `VendorService`) to use the
organization's ServiceMesh SDK (`com.acme.mesh.ServiceMesh`). All service-to-service
communication must go through the mesh per playbook policy.

### Task 2 – Migrate Logging to InternalLogger
Replace all SLF4J `@Slf4j` / `log.*` usages across controllers and services with
`InternalLogger` (`com.acme.logging.InternalLogger`). Remove Logback configuration
and the `logstash-logback-encoder` dependency once migrated.

### Task 3 – Replace Exception-based Flow Control with Result\<T\> Pattern
Refactor `PurchaseOrderService`, `VendorService`, and `InventoryService` to return
`Result<T>` (`com.acme.commons.Result`) instead of throwing `IllegalArgumentException`,
`IllegalStateException`, or `RuntimeException` for business rule violations.
Update controllers to handle `Result<T>` return values accordingly.

### Task 4 – Containerization
Verify and update the existing `Dockerfile` to produce a production-ready container
image suitable for deployment to Azure Kubernetes Service (AKS), aligned with the
playbook compute target.
