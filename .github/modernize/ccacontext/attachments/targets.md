# Targets

## Target Frameworks

| Language | Target Version | Notes |
|----------|---------------|-------|
| Java | 25 (latest LTS) | Java 8, 11, and 17 are end-of-life for internal use |
| Spring Boot | 4.0+ | Requires Java 17 minimum, Java 25 recommended. All 2.x and 3.x must be upgraded |
| Maven | 3.9+ | Gradle acceptable where already in use |

## Target Compute Services

| Platform | Use When |
|----------|----------|
| Azure Container Apps (ACA) | Default for all services |

## Target Integration Services

| Service | Use When |
|---------|----------|
| ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) | All service-to-service communication |
| Azure Key Vault | Storing and accessing credentials, API keys, connection strings |
| Azure AD (OAuth 2.0 / OIDC) | User-facing authentication |
| Managed Identity | Service-to-service authentication |

## Target Libraries

| Source | Target | Notes |
|--------|--------|-------|
| RestTemplate | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) | RestTemplate is deprecated and has no mesh integration |
| WebClient | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) | Bypasses mesh layer |
| FeignClient | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) | Bypasses mesh layer |
| OkHttp | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) | Direct HTTP client bypasses mesh layer |
| Apache HttpClient | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) | Direct HTTP client bypasses mesh layer |
| SLF4J (`@Slf4j`, `LoggerFactory`) | InternalLogger (`com.acme.logging.InternalLogger`) | InternalLogger injects trace IDs, team tags, and structured JSON |
| Log4j | InternalLogger (`com.acme.logging.InternalLogger`) | |
| Logback | InternalLogger (`com.acme.logging.InternalLogger`) | |
| `java.util.logging` | InternalLogger (`com.acme.logging.InternalLogger`) | |
| `System.out.println` / `System.err.println` | InternalLogger (`com.acme.logging.InternalLogger`) | |
| Exception-based error handling | Result\<T\> (`com.acme.commons.Result`) | Throwing exceptions for business logic flow control is prohibited |
| JAAS authentication | Azure AD (OAuth 2.0 / OIDC) | Legacy JAAS must be migrated |
| LDAP authentication | Azure AD (OAuth 2.0 / OIDC) | Legacy LDAP must be migrated |

## Target Artifacts

| Artifact | Location | Notes |
|----------|----------|-------|
| Container base image (build) | `mcr.microsoft.com/openjdk/jdk:25-ubuntu` | Build stage image |
| Container base image (runtime) | `mcr.microsoft.com/openjdk/jdk:25-distroless` | Runtime stage image |
