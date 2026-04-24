# Playbook Compliance Report

**Plan**: modernization-plan-shizh  
**Project**: supplychain-backend  
**Playbooks evaluated**: charter.md · targets.md · policies.md

---

## Playbook Compliance

| Playbook | Rule | Status | Task |
|----------|------|--------|------|
| charter.md | Java 8, 11, and 17 are end-of-life for internal use; upgrade required | ✅ COVERED | 001 |
| charter.md | Spring Boot 2.x and 3.x must be upgraded | ✅ COVERED | 001 |
| charter.md | Java supply chain services must replatform to Azure Container Apps (ACA) | ✅ COVERED | 010 |
| charter.md | Use ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) for service-to-service communication | ✅ COVERED | 005 |
| charter.md | Use `Result<T>` (`com.acme.commons.Result`) for error handling | ✅ COVERED | 007 |
| charter.md | Use InternalLogger (`com.acme.logging.InternalLogger`) for logging | ✅ COVERED | 006 |
| targets.md | Java target version: 25 (latest LTS) | ✅ COVERED | 001 |
| targets.md | Spring Boot target version: 4.0+ | ✅ COVERED | 001 |
| targets.md | Maven target version: 3.9+ | ✅ COVERED | 001 |
| targets.md | Default compute platform: Azure Container Apps (ACA) | ✅ COVERED | 010 |
| targets.md | ServiceMesh SDK for all service-to-service communication | ✅ COVERED | 005 |
| targets.md | Azure Key Vault for credentials, API keys, and connection strings | ✅ COVERED | 004 |
| targets.md | Azure AD (OAuth 2.0/OIDC) for user-facing authentication | ➖ N/A | - |
| targets.md | Managed Identity for service-to-service authentication | ✅ COVERED | 002, 003, 004 |
| targets.md | Migrate RestTemplate → ServiceMesh SDK | ✅ COVERED | 005 |
| targets.md | Migrate WebClient → ServiceMesh SDK | ➖ N/A | - |
| targets.md | Migrate FeignClient → ServiceMesh SDK | ➖ N/A | - |
| targets.md | Migrate OkHttp → ServiceMesh SDK | ➖ N/A | - |
| targets.md | Migrate Apache HttpClient → ServiceMesh SDK | ➖ N/A | - |
| targets.md | Migrate SLF4J (`@Slf4j`, `LoggerFactory`) → InternalLogger | ✅ COVERED | 006 |
| targets.md | Migrate Log4j → InternalLogger | ✅ COVERED | 006 |
| targets.md | Migrate Logback → InternalLogger | ✅ COVERED | 006 |
| targets.md | Migrate `java.util.logging` → InternalLogger | ✅ COVERED | 006 |
| targets.md | Migrate `System.out.println` / `System.err.println` → InternalLogger | ✅ COVERED | 006 |
| targets.md | Migrate exception-based error handling → `Result<T>` | ✅ COVERED | 007 |
| targets.md | Migrate JAAS authentication → Azure AD | ➖ N/A | - |
| targets.md | Migrate LDAP authentication → Azure AD | ➖ N/A | - |
| targets.md | Container build image: `mcr.microsoft.com/openjdk/jdk:25-ubuntu` | ✅ COVERED | 009 |
| targets.md | Container runtime image: `mcr.microsoft.com/openjdk/jdk:25-distroless` | ✅ COVERED | 009 |
| policies.md | User-facing authentication must use Azure AD (OAuth 2.0/OIDC) | ➖ N/A | - |
| policies.md | Service-to-service authentication must use Managed Identity | ✅ COVERED | 002, 003, 004 |
| policies.md | Legacy JAAS and LDAP authentication must be migrated | ➖ N/A | - |
| policies.md | Sensitive values must be stored in Azure Key Vault | ✅ COVERED | 004 |
| policies.md | Access secrets via Spring Cloud Azure Key Vault starter or Managed Identity | ✅ COVERED | 004 |
| policies.md | All service-to-service communication via ServiceMesh SDK (mTLS, circuit breaking, tracing) | ✅ COVERED | 005 |
| policies.md | All traffic must use TLS 1.2+ | ❌ NOT COVERED | - |
| policies.md | Data at rest must be encrypted (service-managed keys) | ✅ COVERED | 002, 003 |
| policies.md | PCI-DSS compliance (Payments portfolio) | ➖ N/A | - |
| policies.md | SOC 2 compliance for all applications | ❌ NOT COVERED | - |
| policies.md | Restricted data requires customer-managed encryption keys | ➖ N/A | - |
| policies.md | Prohibited: RestTemplate — replace with ServiceMesh SDK | ✅ COVERED | 005 |
| policies.md | Prohibited: WebClient — replace with ServiceMesh SDK | ➖ N/A | - |
| policies.md | Prohibited: FeignClient — replace with ServiceMesh SDK | ➖ N/A | - |
| policies.md | Prohibited: OkHttp — replace with ServiceMesh SDK | ➖ N/A | - |
| policies.md | Prohibited: Apache HttpClient — replace with ServiceMesh SDK | ➖ N/A | - |
| policies.md | Prohibited: SLF4J (`@Slf4j`, `LoggerFactory`) — replace with InternalLogger | ✅ COVERED | 006 |
| policies.md | Prohibited: Log4j (any version) — replace with InternalLogger | ✅ COVERED | 006 |
| policies.md | Prohibited: Logback (direct usage) — replace with InternalLogger | ✅ COVERED | 006 |
| policies.md | Prohibited: `java.util.logging` — replace with InternalLogger | ✅ COVERED | 006 |
| policies.md | Prohibited: `System.out.println` / `System.err.println` — replace with InternalLogger | ✅ COVERED | 006 |
| policies.md | Prohibited: JAAS authentication — migrate to Azure AD | ➖ N/A | - |
| policies.md | Prohibited: LDAP authentication — migrate to Azure AD | ➖ N/A | - |
| policies.md | Prohibited pattern: Throwing exceptions for business logic flow control | ✅ COVERED | 007 |
| policies.md | Prohibited pattern: `try/catch` blocks for flow control | ✅ COVERED | 007 |
| policies.md | Prohibited pattern: `@ControllerAdvice` for business exceptions | ✅ COVERED | 007 |
| policies.md | Prohibited pattern: Hardcoded credentials in config files or source code | ✅ COVERED | 004 |
| policies.md | Required: Azure Key Vault must be provisioned for every modernized application | ✅ COVERED | 010 |
| policies.md | Required: InternalLogger as the sole logging framework | ✅ COVERED | 006 |
| policies.md | Coding standard: Use `Result<T>` for all error handling in application code | ✅ COVERED | 007 |
| policies.md | Coding standard: Use ServiceMesh SDK for all service-to-service communication | ✅ COVERED | 005 |
| policies.md | Coding standard: Use InternalLogger for all logging | ✅ COVERED | 006 |
| policies.md | Coding standard: Externalize configuration; use Azure Key Vault for sensitive values | ✅ COVERED | 004 |

**COVERED: 43/45  ·  NOT COVERED: 2/45  ·  N/A: 17 (technologies not present in project)**

---

## Not Covered — Remediation Notes

| Rule | Reason Not Covered | Recommended Action |
|------|--------------------|--------------------|
| All traffic must use TLS 1.2+ | No explicit TLS configuration task in the plan. | Add a task or deployment configuration to enforce TLS 1.2+ on Azure Container Apps ingress and any outbound connections. ACA enforces TLS on ingress by default; verify and document the policy. |
| SOC 2 compliance | SOC 2 is a broad audit/certification requirement. No dedicated compliance task exists in the plan. | Ensure SOC 2 controls (access logs, audit trails, change management) are documented. Consider adding a compliance verification task post-deployment. |

---

## N/A Rules — Justification

| Rule | Justification |
|------|--------------|
| Azure AD for user-facing authentication | The application has no user-facing authentication surface. No JAAS/LDAP/user-login mechanism is present. |
| WebClient → ServiceMesh SDK | WebClient is not used in the project. |
| FeignClient → ServiceMesh SDK | FeignClient is not used in the project. |
| OkHttp → ServiceMesh SDK | OkHttp is not used in the project. |
| Apache HttpClient → ServiceMesh SDK | Apache HttpClient is not used in the project. |
| JAAS / LDAP migration → Azure AD | Neither JAAS nor LDAP authentication is present in the codebase. |
| PCI-DSS compliance | This application is not in the Payments portfolio; PCI-DSS does not apply. |
| Restricted data — customer-managed encryption keys | No Restricted data classification has been identified for this application. |
