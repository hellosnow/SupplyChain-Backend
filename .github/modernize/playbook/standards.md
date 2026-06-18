# Standards

## Resource Naming Conventions

| Resource Type | Pattern | Example |
|--------------|---------|---------|

## Tagging Requirements

| Tag | Required | Description |
|-----|----------|-------------|

## Authentication & Authorization

- User-facing authentication: Azure AD with OAuth 2.0 / OIDC
- Service-to-service authentication: Managed Identity

## Secrets Management

- Sensitive values (credentials, API keys, connection strings) must be stored in Azure Key Vault
- Accessed via Spring Cloud Azure Key Vault starter or Managed Identity

## Network Security

## Encryption

- All traffic must use TLS 1.2+
- Data at rest must be encrypted using service-managed keys
- Customer-managed keys required for Restricted data

## Compliance Frameworks

| Framework | Key Constraints |
|-----------|----------------|
| PCI-DSS | Applies to applications in the Payments portfolio |
| SOC 2 | All applications must comply |
