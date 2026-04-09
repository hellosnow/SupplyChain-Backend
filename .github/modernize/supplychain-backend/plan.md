# Modernization Execution Plan — SupplyChain Backend

| Field              | Value                                                      |
|--------------------|------------------------------------------------------------|
| **Phase**          | Planning                                                   |
| **Timestamp**      | 2026-04-09T14:00:00Z                                      |
| **Tasks File**     | `.github/modernize/supplychain-backend/tasks.json`         |
| **Assessment File**| `.github/modernize/supplychain-backend/assessment.yaml`    |
| **AppCAT Report**  | `.github/appmod/appcat/result/report.json`                 |
| **Total Tasks**    | 13                                                         |
| **Total Phases**   | 4                                                          |
| **Est. Effort**    | ~140 story points                                          |
| **Playbook**       | None (no `.github/modernize/playbook/` found)              |

---

## Executive Summary

This plan modernizes the **SupplyChain Backend** (a legacy Java 8 / Spring Boot 2.7 application) through 4 sequential phases with 13 tasks. The plan addresses all 26 AppCAT incidents across 10 rules, covering 14 mandatory, 8 optional, and 4 potential findings.

**Current State:**
- Java 8, Spring Boot 2.7.18 (both EOL)
- MySQL with cleartext passwords
- RabbitMQ messaging (3 queues)
- javax.persistence imports, deprecated Hibernate dialect
- RestTemplate, @Autowired field injection, hardcoded URLs

**Target State:**
- Java 17 LTS, Spring Boot 3.2+
- Jakarta EE 9+ namespace
- Azure Service Bus (replacing RabbitMQ)
- Azure Key Vault / Managed Identity (replacing hardcoded credentials)
- Modern patterns (constructor injection, RestClient, HTTPS)

---

## Phase 1: Java Version Upgrade (Effort: ~10 SP)

> **Goal:** Establish Java 17 LTS foundation before any framework upgrades.
> **Agent:** `modernize-java-upgrade`

### Task 1.1 — Upgrade Java 8 → 17 in POM
| Field | Value |
|---|---|
| **Task ID** | `java-pom-version-upgrade` |
| **AppCAT Rule** | `azure-java-version-02000` |
| **Severity** | Mandatory |
| **Files** | `pom.xml` |
| **Dependencies** | None |

**Changes:**
- `<java.version>8</java.version>` → `<java.version>17</java.version>`
- `<maven.compiler.source>8</maven.compiler.source>` → `<maven.compiler.source>17</maven.compiler.source>`
- `<maven.compiler.target>8</maven.compiler.target>` → `<maven.compiler.target>17</maven.compiler.target>`

**Success Criteria:** `mvn compile` passes with Java 17 compiler settings.

---

### Task 1.2 — Update Dockerfile Base Images to Java 17
| Field | Value |
|---|---|
| **Task ID** | `dockerfile-java17-update` |
| **AppCAT Rule** | `dockerfile-java8-base-image` (additional finding) |
| **Severity** | Mandatory |
| **Files** | `Dockerfile` |
| **Dependencies** | `java-pom-version-upgrade` |

**Changes:**
- Build stage: `maven:3.8-openjdk-8` → `maven:3-eclipse-temurin-17`
- Runtime stage: `eclipse-temurin:8-jre` → `eclipse-temurin:17-jre`
- Update JAR filename if version changes

**Success Criteria:** `docker build .` succeeds with Java 17 images.

---

## Phase 2: Spring Boot Upgrade (Effort: ~50 SP)

> **Goal:** Upgrade Spring Boot 2.7.18 → 3.2+ and resolve all breaking changes.
> **Agent:** `modernize-java-upgrade`
> **Prerequisite:** Phase 1 completed (Java 17 required for Spring Boot 3.x)

### Task 2.1 — Upgrade Spring Boot 2.7.18 → 3.2+
| Field | Value |
|---|---|
| **Task ID** | `springboot-version-upgrade` |
| **AppCAT Rules** | `spring-boot-to-azure-spring-boot-version-01000`, `spring-framework-version-01000` |
| **Severity** | Mandatory |
| **Files** | `pom.xml` |
| **Dependencies** | `java-pom-version-upgrade` |

**Changes:**
- `spring-boot-starter-parent` version: `2.7.18` → `3.2.x` (latest stable)
- This transitively upgrades Spring Framework 5.3.x → 6.x

**Success Criteria:** POM dependency resolution succeeds. Build may fail until subsequent tasks complete.

---

### Task 2.2 — Migrate javax.persistence → jakarta.persistence
| Field | Value |
|---|---|
| **Task ID** | `javax-to-jakarta-migration` |
| **AppCAT Rule** | `javax-to-jakarta-namespace` (additional finding) |
| **Severity** | Mandatory |
| **Files** | `src/main/java/com/acme/scm/model/Vendor.java`, `src/main/java/com/acme/scm/model/PurchaseOrder.java`, `src/main/java/com/acme/scm/model/Inventory.java` |
| **Dependencies** | `springboot-version-upgrade` |

**Changes:**
- In all 3 entity classes: `import javax.persistence.*;` → `import jakarta.persistence.*;`
- Verify all JPA annotations (`@Entity`, `@Table`, `@Id`, `@Column`, `@GeneratedValue`, `@Enumerated`, `@PrePersist`, `@PreUpdate`) resolve from jakarta namespace.

**Success Criteria:** All entity classes compile with jakarta.persistence imports.

---

### Task 2.3 — Rename MySQL Connector Dependency
| Field | Value |
|---|---|
| **Task ID** | `mysql-connector-rename` |
| **AppCAT Rule** | Part of `spring-boot-to-azure-spring-boot-version-01000` |
| **Severity** | Mandatory |
| **Files** | `pom.xml` |
| **Dependencies** | `springboot-version-upgrade` |

**Changes:**
- Group ID: `mysql` → `com.mysql`
- Artifact ID: `mysql-connector-java` → `mysql-connector-j`
- Version: `8.0.33` → managed by Spring Boot BOM (remove explicit version) or update to `8.2.0+`

**Success Criteria:** MySQL driver resolves correctly, JPA connects to MySQL.

---

### Task 2.4 — Update Hibernate Dialect
| Field | Value |
|---|---|
| **Task ID** | `hibernate-dialect-update` |
| **AppCAT Rule** | `deprecated-mysql-dialect` (additional finding) |
| **Severity** | Optional |
| **Files** | `src/main/resources/application.yml` |
| **Dependencies** | `springboot-version-upgrade` |

**Changes:**
- `org.hibernate.dialect.MySQL5InnoDBDialect` → `org.hibernate.dialect.MySQLDialect`
- Hibernate 6 (bundled with Spring Boot 3.x) auto-detects MySQL storage engine.

**Success Criteria:** Application starts and JPA operations work with updated dialect.

---

### Task 2.5 — Build Verification for Spring Boot 3.x
| Field | Value |
|---|---|
| **Task ID** | `springboot-build-verification` |
| **Severity** | Mandatory |
| **Dependencies** | `javax-to-jakarta-migration`, `mysql-connector-rename`, `hibernate-dialect-update` |

**Verification Steps:**
1. `mvn clean compile` — no compilation errors
2. `mvn test` — all existing tests pass (if any)
3. `mvn package -DskipTests` — JAR builds successfully
4. Verify application starts: `java -jar target/*.jar` (may need MySQL/RabbitMQ running)

**Success Criteria:** Full build pipeline succeeds with zero errors.

---

## Phase 3: Code Quality & Cloud Readiness (Effort: ~30 SP)

> **Goal:** Fix code quality issues and make the app cloud-ready.
> **Agent:** `general-purpose` (manual code changes)
> **Prerequisite:** Phase 2 completed (stable Spring Boot 3.x build)

### Task 3.1 — Fix HTTP → HTTPS URLs and Externalize Config
| Field | Value |
|---|---|
| **Task ID** | `fix-http-urls-and-externalize` |
| **AppCAT Rules** | `localhost-http-00001`, `unsecure-network-protocol-00000`, `hardcoded-urls-00001` |
| **Severity** | Mandatory |
| **Files** | `src/main/java/com/acme/scm/SupplyChainBackendApplication.java`, `src/main/java/com/acme/scm/service/VendorService.java`, `src/main/resources/application.yml` |
| **Dependencies** | `springboot-build-verification` |

**Changes:**
- `SupplyChainBackendApplication.java` line 25: Remove or externalize `http://localhost:8080/api`
- `VendorService.java` line 49: Move `http://vendor-rating-service/api/ratings/` to `application.yml` config
- Add new properties in `application.yml`:
  ```yaml
  app:
    vendor-rating-service:
      base-url: https://vendor-rating-service/api/ratings
  ```
- Replace all `http://` with `https://` in service URLs

**Success Criteria:** No hardcoded HTTP URLs remain in source code. All external URLs are HTTPS and configurable.

---

### Task 3.2 — Convert @Autowired Field Injection → Constructor Injection
| Field | Value |
|---|---|
| **Task ID** | `field-to-constructor-injection` |
| **AppCAT Rule** | `field-injection-pattern` (additional finding) |
| **Severity** | Optional |
| **Files** | 6 classes (3 services + 3 controllers) |
| **Dependencies** | `springboot-build-verification` |

**Affected Classes:**
| Class | @Autowired Fields |
|---|---|
| `VendorService` | `vendorRepository`, `restTemplate` |
| `PurchaseOrderService` | `orderRepository`, `rabbitTemplate`, `vendorService` |
| `InventoryService` | `inventoryRepository`, `rabbitTemplate` |
| `VendorController` | `vendorService` |
| `PurchaseOrderController` | `orderService` |
| `InventoryController` | `inventoryService` |

**Changes (per class):**
1. Remove `@Autowired` annotations from fields
2. Make fields `private final`
3. Add `@RequiredArgsConstructor` from Lombok (already a dependency)
4. Remove `@Value` fields from constructor (use `@RequiredArgsConstructor` with `final` fields only; keep `@Value` fields non-final)

**Success Criteria:** All 6 classes use constructor injection. Build compiles. Tests pass.

---

### Task 3.3 — Replace System.out.println with SLF4J Logging
| Field | Value |
|---|---|
| **Task ID** | `system-out-to-slf4j` |
| **AppCAT Rule** | `system-out-println` (additional finding) |
| **Severity** | Optional |
| **Files** | `src/main/java/com/acme/scm/SupplyChainBackendApplication.java` |
| **Dependencies** | `springboot-build-verification` |

**Changes:**
- Add `@Slf4j` annotation to `SupplyChainBackendApplication`
- Replace `System.out.println(...)` calls (lines 23-26) with `log.info(...)`
- Use structured format: `log.info("Supply Chain Backend API Started — API Docs: {}", apiDocsUrl)`

**Success Criteria:** No `System.out.println` calls in codebase. Startup messages use SLF4J.

---

### Task 3.4 — Migrate RestTemplate → RestClient (Optional)
| Field | Value |
|---|---|
| **Task ID** | `resttemplate-to-restclient` |
| **AppCAT Rule** | `rest-template-usage` (additional finding) |
| **Severity** | Optional |
| **Files** | `src/main/java/com/acme/scm/config/AppConfig.java`, `src/main/java/com/acme/scm/service/VendorService.java` |
| **Dependencies** | `springboot-build-verification`, `fix-http-urls-and-externalize` |

**Changes:**
- `AppConfig.java`: Replace `RestTemplate` bean with `RestClient` bean
  ```java
  @Bean
  public RestClient restClient() {
      return RestClient.create();
  }
  ```
- `VendorService.java`: Update HTTP call from `restTemplate.getForObject(...)` to `restClient.get().uri(...).retrieve().body(Double.class)`

**Success Criteria:** `VendorService.getVendorRatingFromExternalService()` works with RestClient. No RestTemplate references remain.

---

## Phase 4: Azure Service Migrations (Effort: ~50 SP)

> **Goal:** Replace on-premises middleware with Azure managed services.
> **Agent:** `modernize-azure-java-cli` (via `appmod-run-task`)
> **Prerequisite:** Phase 2 completed (stable Spring Boot 3.x build)

### Task 4.1 — Migrate RabbitMQ → Azure Service Bus
| Field | Value |
|---|---|
| **Task ID** | `rabbitmq-to-azure-servicebus` |
| **AppCAT Rules** | `azure-message-queue-rabbitmq-01000`, `azure-message-queue-amqp-02000` |
| **Severity** | Optional (recommended for Azure) |
| **Files** | `pom.xml`, `src/main/resources/application.yml`, `src/main/java/com/acme/scm/config/AppConfig.java`, `src/main/java/com/acme/scm/service/PurchaseOrderService.java`, `src/main/java/com/acme/scm/service/InventoryService.java` |
| **Dependencies** | `springboot-build-verification` |

**Changes:**
- **pom.xml:** Replace `spring-boot-starter-amqp` with `azure-spring-cloud-starter-servicebus` or `azure-messaging-servicebus`
- **application.yml:** Replace `spring.rabbitmq.*` config with Azure Service Bus connection:
  ```yaml
  spring:
    cloud:
      azure:
        servicebus:
          connection-string: ${AZURE_SERVICEBUS_CONNECTION_STRING}
          # Or use Managed Identity
  ```
- **AppConfig.java:** Remove RabbitMQ beans (`Queue`, `RabbitTemplate`, `Jackson2JsonMessageConverter`). Add Service Bus processor/sender beans.
- **PurchaseOrderService.java:** Replace `rabbitTemplate.convertAndSend(...)` with Azure Service Bus sender
- **InventoryService.java:** Replace `rabbitTemplate.convertAndSend(...)` with Azure Service Bus sender
- Migrate 3 queues: `order.created`, `inventory.alert`, `approval.pending`

**Success Criteria:** All 3 message queues functional on Azure Service Bus. No RabbitMQ references in code.

---

### Task 4.2 — Externalize Credentials to Azure Key Vault
| Field | Value |
|---|---|
| **Task ID** | `credentials-to-azure-keyvault` |
| **AppCAT Rules** | `azure-password-01000`, `rabbitmq-hardcoded-credentials` |
| **Severity** | Potential (critical for production) |
| **Files** | `pom.xml`, `src/main/resources/application.yml` |
| **Dependencies** | `springboot-build-verification` |

**Changes:**
- **pom.xml:** Add `azure-spring-cloud-starter-keyvault-secrets` dependency
- **application.yml:** Remove cleartext passwords:
  - Remove `spring.datasource.password: root`
  - Remove `spring.rabbitmq.password: guest` (or Service Bus connection string)
  - Add Key Vault configuration:
    ```yaml
    spring:
      cloud:
        azure:
          keyvault:
            secret:
              endpoint: ${AZURE_KEYVAULT_ENDPOINT}
    ```
- Store secrets in Key Vault: `db-password`, `servicebus-connection-string`

**Success Criteria:** No cleartext passwords in `application.yml`. All secrets resolved from Azure Key Vault at runtime.

---

### Task 4.3 — Configure Azure Database for MySQL Connection
| Field | Value |
|---|---|
| **Task ID** | `mysql-to-azure-mysql` |
| **AppCAT Rules** | `azure-database-mysql-01000` |
| **Severity** | Potential (recommended for Azure) |
| **Files** | `src/main/resources/application.yml`, `pom.xml` |
| **Dependencies** | `credentials-to-azure-keyvault` |

**Changes:**
- **application.yml:** Update datasource URL for Azure MySQL Flexible Server:
  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://${AZURE_MYSQL_HOST}:3306/supplychain?useSSL=true&requireSSL=true&serverTimezone=UTC
      username: ${AZURE_MYSQL_USERNAME}
      # Password from Key Vault or use Managed Identity
  ```
- **pom.xml:** Optionally add `azure-identity` for passwordless authentication with Managed Identity
- Remove `useSSL=false` (enforce SSL for Azure MySQL)

**Success Criteria:** Application connects to Azure Database for MySQL with encrypted connections. No hardcoded connection strings.

---

## Dependency Graph

```
Phase 1 (Foundation)
  ├── java-pom-version-upgrade
  │   └── dockerfile-java17-update
  │
Phase 2 (Framework Upgrade)
  ├── springboot-version-upgrade  ← depends on: java-pom-version-upgrade
  │   ├── javax-to-jakarta-migration
  │   ├── mysql-connector-rename
  │   └── hibernate-dialect-update
  │       └── springboot-build-verification  ← gate: all Phase 2 tasks
  │
Phase 3 (Code Quality)                      ← all depend on: springboot-build-verification
  ├── fix-http-urls-and-externalize
  ├── field-to-constructor-injection
  ├── system-out-to-slf4j
  └── resttemplate-to-restclient  ← also depends on: fix-http-urls-and-externalize
  │
Phase 4 (Azure Migrations)                  ← all depend on: springboot-build-verification
  ├── rabbitmq-to-azure-servicebus
  ├── credentials-to-azure-keyvault
  └── mysql-to-azure-mysql  ← depends on: credentials-to-azure-keyvault
```

---

## Agent Assignment Summary

| Agent | Tasks | Description |
|---|---|---|
| `modernize-java-upgrade` | Tasks 1.1, 1.2, 2.1, 2.2, 2.3, 2.4, 2.5 | Java version & Spring Boot framework upgrades |
| `general-purpose` | Tasks 3.1, 3.2, 3.3, 3.4 | Code quality fixes and refactoring |
| `modernize-azure-java-cli` | Tasks 4.1, 4.2, 4.3 | Azure service migration tasks |

---

## Risk Assessment

| Risk | Impact | Mitigation |
|---|---|---|
| Spring Boot 3.x breaks RabbitMQ config | High | Complete Phase 2 verification before Phase 4 |
| javax→jakarta migration misses imports | Medium | Automated scan + build verification in Task 2.5 |
| Lombok incompatibility with Java 17 | Low | Lombok 1.18.30 supports Java 17; verify at build |
| Azure Service Bus API differences | Medium | Use Spring Cloud Azure abstraction layer |
| MySQL Flexible Server connection issues | Medium | Test with Azure MySQL before production cutover |

---

## Rollback Strategy

Each phase is independently deployable:
- **Phase 1 rollback:** Revert pom.xml properties and Dockerfile to Java 8
- **Phase 2 rollback:** Revert Spring Boot parent version and javax imports (git revert)
- **Phase 3 rollback:** Code quality changes are non-breaking; revert individual commits
- **Phase 4 rollback:** Maintain dual config (RabbitMQ fallback) until Azure Service Bus is validated
