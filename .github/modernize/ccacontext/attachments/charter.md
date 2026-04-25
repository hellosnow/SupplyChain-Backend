# Charter

## Metadata

| Field | Value |
|-------|-------|
| Playbook Name | Acme Corp Modernization Playbook |
| Version | 2025-01-15 |
| Changelog | Initial version from Internal Technology Guidelines |

## Scope

### Covered Applications and Languages

- All Java applications in the supply chain system

### Application Types

**Included:**

- Java services (supply chain system)

**Excluded:**

_(none specified)_

### Custom Libraries

- `com.acme.mesh.ServiceMesh` — Internal ServiceMesh SDK for service-to-service communication
- `com.acme.commons.Result` — Result\<T\> pattern for error handling
- `com.acme.logging.InternalLogger` — Internal logging framework

### Constraints

- Target completion: Q4 2026
- Java 8, 11, and 17 are end-of-life for internal use
- Spring Boot 2.x and 3.x must be upgraded

## Modernization Strategy (6R Guidelines)

| Application Type | Default Strategy | Override Conditions |
|------------------|-----------------|---------------------|
| Java services (supply chain) | Replatform to Azure Container Apps (ACA) | _(none specified)_ |

## Principles

_(none explicitly stated in source)_