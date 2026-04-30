# Policies

## Naming & Metadata Standards

### Resource Naming Patterns

| Resource Type | Pattern | Example |
|--------------|---------|---------|

### Tagging Requirements

| Tag | Required | Description |
|-----|----------|-------------|

## Security Requirements

### Authentication & Authorization

- Use DefaultAzureCredential for authentication (no hardcoded keys)
- Use managed identity in production

### Secrets Management

- Connection strings must come from environment variables
- No AWS credentials in code after migration

### Network Security

- All blob containers must use private access level

### Encryption

- Enable Azure Storage encryption at rest

## Compliance Requirements

### Applicable Frameworks

| Framework | Key Constraints |
|-----------|----------------|

### Data Classification

## Guardrails (Hard Boundaries)

### Prohibited Technologies

| Technology | Reason | Approved Alternative |
|-----------|--------|---------------------|
| AWS S3 SDK | Post-migration: all AWS storage dependencies must be removed | Azure SDK for Java (com.azure:azure-storage-blob) |
| Hardcoded AWS credentials | Security risk and incompatible with Azure target | DefaultAzureCredential / managed identity |

### Prohibited Patterns

| Pattern | Reason | Approved Alternative |
|---------|--------|---------------------|
| Hardcoded connection strings or credentials | Security policy | Environment variables + DefaultAzureCredential |

### Required Elements

Every modernized application must include:

#### Cloud Resources

#### Monitoring

#### CI/CD

#### Testing

- Unit tests for all migrated storage operations

### Approved Regions / Residency Constraints

## Validation & Quality Gates

### Required Scanners/Tools

### Pipeline Gates

### Confidence Thresholds

- Minimum 80% code coverage on new code

## Coding Style Guidelines

### Coding Standards

- Follow Azure SDK best practices for retry and error handling
- Use async APIs where available

### Frontend Style Guidelines
