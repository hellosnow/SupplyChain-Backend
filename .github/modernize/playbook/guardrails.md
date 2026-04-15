# Guardrails

## Prohibited Technologies

| Technology | Reason | Approved Alternative |
|-----------|--------|---------------------|
| Java 8 | End-of-life for internal use | Java 17 LTS |
| Java 11 | End-of-life for internal use | Java 17 LTS |
| Spring Boot 2.x | Must be upgraded | Spring Boot 3.x |
| RestTemplate | Deprecated, no mesh integration | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| WebClient | Bypasses mesh layer | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| FeignClient | Bypasses mesh layer | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| Direct HTTP client libraries (OkHttp, Apache HttpClient, etc.) | Bypasses mesh layer | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| SLF4J (`@Slf4j`, `LoggerFactory.getLogger(...)`) | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| Log4j (any version) | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| Logback (direct usage) | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| `java.util.logging` | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| `System.out.println` / `System.err.println` | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| JAAS authentication | Legacy | Azure AD (OAuth 2.0 / OIDC) |
| LDAP authentication | Legacy | Azure AD (OAuth 2.0 / OIDC) |

## Prohibited Patterns

| Pattern | Reason | Approved Alternative |
|---------|--------|---------------------|
| Throwing exceptions for business logic flow control | P0-2024-0847 post-incident mandate | Result\<T\> pattern (`com.acme.commons.Result`) |
| `try/catch` blocks used for flow control | P0-2024-0847 post-incident mandate | Result\<T\> pattern (`com.acme.commons.Result`) |
| `@ControllerAdvice` global exception handlers for business exceptions | P0-2024-0847 post-incident mandate | Result\<T\> pattern (`com.acme.commons.Result`) |
| Hardcoded credentials in `application.yml`, `application.properties`, environment variables, or source code | Security policy | Azure Key Vault via Spring Cloud Azure starter or Managed Identity |
| Direct service-to-service HTTP calls without ServiceMesh | 2024 Q2 outage mandate | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |

## Required Elements

Every modernized application must include:

### Cloud Resources

- Container base image (build): `mcr.microsoft.com/openjdk/jdk:17-ubuntu`
- Container base image (runtime): `mcr.microsoft.com/openjdk/jdk:17-distroless`
- Azure Key Vault for secrets storage

### Monitoring

- InternalLogger (`com.acme.logging.InternalLogger`) for all logging with automatic distributed trace ID, team ownership tag, environment/region tags, and structured JSON output

### CI/CD

### Testing
