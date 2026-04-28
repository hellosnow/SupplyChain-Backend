# Policies

## Security Requirements

### Authentication & Authorization

- All user-facing authentication must use Azure AD with OAuth 2.0 / OIDC
- Service-to-service authentication must use Managed Identity
- Legacy JAAS and LDAP authentication must be migrated as part of modernization

### Secrets Management

- Sensitive values (credentials, API keys, connection strings) must be stored in Azure Key Vault
- Access secrets via the Spring Cloud Azure Key Vault starter or Managed Identity

### Network Security

- All service-to-service communication must go through the ServiceMesh SDK (`com.acme.mesh.ServiceMesh`), which provides automatic circuit breaking, mTLS termination, distributed tracing, and canary routing

### Encryption

- All traffic must use TLS 1.2+
- Data at rest must be encrypted (service-managed keys; customer-managed keys for Restricted data)

## Compliance Requirements

### Applicable Frameworks

| Framework | Key Constraints |
|-----------|----------------|
| PCI-DSS | Applies to applications in the Payments portfolio |
| SOC 2 | All applications must comply with SOC 2 controls |

### Data Classification

- Restricted data requires customer-managed encryption keys

## Guardrails (Hard Boundaries)

### Prohibited Technologies

| Technology | Reason | Approved Alternative |
|-----------|--------|---------------------|
| RestTemplate | Deprecated, no mesh integration | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| WebClient | Bypasses mesh layer | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| FeignClient | Bypasses mesh layer | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| OkHttp | Bypasses mesh layer | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| Apache HttpClient | Bypasses mesh layer | ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) |
| SLF4J (`@Slf4j`, `LoggerFactory`) | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| Log4j (any version) | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| Logback (direct usage) | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| `java.util.logging` | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| `System.out.println` / `System.err.println` | No trace context integration | InternalLogger (`com.acme.logging.InternalLogger`) |
| JAAS | Legacy authentication | Azure AD (OAuth 2.0 / OIDC) |
| LDAP | Legacy authentication | Azure AD (OAuth 2.0 / OIDC) |

### Prohibited Patterns

| Pattern | Reason | Approved Alternative |
|---------|--------|---------------------|
| Throwing exceptions for business logic flow control | P0-2024-0847 post-incident mandate | Result\<T\> pattern (`com.acme.commons.Result`) |
| `try/catch` blocks for flow control | P0-2024-0847 post-incident mandate | Result\<T\> pattern (`com.acme.commons.Result`) |
| `@ControllerAdvice` global exception handlers for business exceptions | P0-2024-0847 post-incident mandate | Result\<T\> pattern with standard response wrapper |
| Hardcoded credentials in `application.yml`, `application.properties`, environment variables, or source code | Security requirement | Azure Key Vault with Spring Cloud Azure Key Vault starter or Managed Identity |

### Required Elements

Every modernized application must include:

#### Cloud Resources

- Azure Key Vault for secrets management

#### Monitoring

- InternalLogger (`com.acme.logging.InternalLogger`) as the sole logging framework

#### CI/CD

_(none specified)_

#### Testing

_(none specified)_

### Approved Regions / Residency Constraints

_(none specified)_

## Validation & Quality Gates

### Required Scanners/Tools

_(none specified)_

### Pipeline Gates

_(none specified)_

### Confidence Thresholds

_(none specified)_

## Coding Style Guidelines

### Coding Standards

- Use `Result<T>` pattern (`com.acme.commons.Result`) for all error handling in application code
- Use ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) for all service-to-service communication
- Use InternalLogger (`com.acme.logging.InternalLogger`) for all logging
- Externalize application configuration; use Azure Key Vault for sensitive values

### Frontend Style Guidelines

_(none specified)_
