# Targets

## Target Frameworks

| Language | Target Version | Notes |
|----------|---------------|-------|
| Java | 17 LTS | Spring Boot 3.x (latest stable), Maven 3.9+ |

## Target Compute Services

| Platform | Use When |
|----------|----------|
| Azure Kubernetes Service (AKS) | Default for all services |
| Azure App Service | Simple web apps under 5K LOC with no async processing |

## Target Data Services

| Service | Use When |
|---------|----------|

## Target Integration Services

| Service | Use When |
|---------|----------|
| ServiceMesh SDK (`com.acme.mesh.ServiceMesh`) | All service-to-service communication |

## Migration Decisions

| Source | Target | Notes |
|--------|--------|-------|
| Java 8 / Java 11 | Java 17 LTS | Java 8 and 11 are end-of-life for internal use |
| Spring Boot 2.x | Spring Boot 3.x | All Spring Boot 2.x applications must be upgraded |
| RestTemplate / WebClient / FeignClient / direct HTTP clients | ServiceMesh SDK | All inter-service calls must go through the mesh |
| SLF4J / Log4j / Logback / java.util.logging / System.out | InternalLogger (`com.acme.logging.InternalLogger`) | Sole logging framework for all applications |
| Exception-based error handling | Result\<T\> pattern (`com.acme.commons.Result`) | Throwing exceptions for business logic flow control is prohibited |
| JAAS / LDAP authentication | Azure AD (OAuth 2.0 / OIDC) + Managed Identity | Legacy auth must be migrated as part of modernization |
| Hardcoded credentials | Azure Key Vault | Accessed via Spring Cloud Azure Key Vault starter or Managed Identity |
