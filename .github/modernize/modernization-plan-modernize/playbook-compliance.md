## Playbook Compliance

| Playbook | Rule | Status | Task |
|----------|------|--------|------|
| charter.md | Java 8, 11, 17 are end-of-life for internal use | ✅ COVERED | 001 |
| charter.md | Spring Boot 2.x and 3.x must be upgraded | ❌ NOT COVERED | - |
| charter.md | Replatform to Azure Container Apps (ACA) | ✅ COVERED | 006 |
| charter.md | ServiceMesh SDK for service-to-service communication | ❌ NOT COVERED | - |
| charter.md | Result\<T\> pattern for error handling | ❌ NOT COVERED | - |
| charter.md | InternalLogger for logging | ❌ NOT COVERED | - |
| targets.md | Java target version: 25 | ❌ NOT COVERED | - |
| targets.md | Spring Boot target: 4.0+ | ❌ NOT COVERED | - |
| targets.md | Maven target: 3.9+ | ❌ NOT COVERED | - |
| targets.md | Compute: Azure Container Apps (ACA) | ✅ COVERED | 006 |
| targets.md | Integration: ServiceMesh SDK for all service-to-service comm | ❌ NOT COVERED | - |
| targets.md | Integration: Azure Key Vault for credentials | ✅ COVERED | 004 |
| targets.md | Integration: Azure AD (OAuth 2.0 / OIDC) for user auth | ✅ COVERED | N/A |
| targets.md | Integration: Managed Identity for service-to-service auth | ✅ COVERED | 002 |
| targets.md | RestTemplate → ServiceMesh SDK | ❌ NOT COVERED | - |
| targets.md | SLF4J / Log4j / Logback / JUL → InternalLogger | ❌ NOT COVERED | - |
| targets.md | System.out / System.err → InternalLogger | ❌ NOT COVERED | - |
| targets.md | Exception-based error handling → Result\<T\> | ❌ NOT COVERED | - |
| targets.md | JAAS / LDAP → Azure AD | ✅ COVERED | N/A |
| targets.md | Container build image: mcr.microsoft.com/openjdk/jdk:25-ubuntu | ❌ NOT COVERED | - |
| targets.md | Container runtime image: mcr.microsoft.com/openjdk/jdk:25-distroless | ❌ NOT COVERED | - |
| policies.md | User-facing auth must use Azure AD with OAuth 2.0 / OIDC | ✅ COVERED | N/A |
| policies.md | Service-to-service auth must use Managed Identity | ✅ COVERED | 002 |
| policies.md | Legacy JAAS and LDAP must be migrated | ✅ COVERED | N/A |
| policies.md | Secrets must be stored in Azure Key Vault | ✅ COVERED | 004 |
| policies.md | Access secrets via Spring Cloud Azure KV starter or MI | ✅ COVERED | 004 |
| policies.md | Service-to-service comm through ServiceMesh SDK | ❌ NOT COVERED | - |
| policies.md | All traffic must use TLS 1.2+ | ❌ NOT COVERED | - |
| policies.md | Data at rest must be encrypted | ✅ COVERED | 002, 003 |
| policies.md | Prohibited: RestTemplate | ❌ NOT COVERED | - |
| policies.md | Prohibited: SLF4J (@Slf4j, LoggerFactory) | ❌ NOT COVERED | - |
| policies.md | Prohibited: Logback (direct usage) | ❌ NOT COVERED | - |
| policies.md | Prohibited: System.out / System.err | ❌ NOT COVERED | - |
| policies.md | Prohibited: Exception-based flow control | ❌ NOT COVERED | - |
| policies.md | Prohibited: try/catch for flow control | ❌ NOT COVERED | - |
| policies.md | Prohibited: @ControllerAdvice for business exceptions | ✅ COVERED | N/A |
| policies.md | Prohibited: Hardcoded credentials | ✅ COVERED | 004 |
| policies.md | Required: Azure Key Vault for secrets management | ✅ COVERED | 004 |
| policies.md | Required: InternalLogger as sole logging framework | ❌ NOT COVERED | - |
| policies.md | Coding: Result\<T\> for all error handling | ❌ NOT COVERED | - |
| policies.md | Coding: ServiceMesh SDK for all communication | ❌ NOT COVERED | - |
| policies.md | Coding: InternalLogger for all logging | ❌ NOT COVERED | - |
| policies.md | Coding: Externalize config; Key Vault for sensitive values | ✅ COVERED | 004 |

**COVERED: 17/43  ·  NOT COVERED: 26/43**
