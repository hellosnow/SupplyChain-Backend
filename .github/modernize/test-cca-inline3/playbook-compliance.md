## Playbook Compliance

| Playbook | Rule | Status | Task |
|----------|------|--------|------|
| targets.md | Target language: Java 21 LTS | ✅ COVERED | 001-upgrade-java21-springboot3 |
| targets.md | Use Azure Blob Storage when replacing AWS S3 | ⬜ N/A | - |
| targets.md | Replace AWS S3 SDK with com.azure:azure-storage-blob | ⬜ N/A | - |
| charter.md | Migration scope: AWS-to-Azure (Replatform strategy for Java applications on AWS) | ⬜ N/A | - |
| policies.md | Use DefaultAzureCredential for authentication (no hardcoded keys) | ✅ COVERED | 002-transform-rabbitmq-to-servicebus, 003-transform-mysql-to-azure-mysql-mi |
| policies.md | Use managed identity in production | ✅ COVERED | 002-transform-rabbitmq-to-servicebus, 003-transform-mysql-to-azure-mysql-mi |
| policies.md | Connection strings must come from environment variables | ✅ COVERED | 002-transform-rabbitmq-to-servicebus, 003-transform-mysql-to-azure-mysql-mi |
| policies.md | No AWS credentials in code after migration | ⬜ N/A | - |
| policies.md | All blob containers must use private access level | ⬜ N/A | - |
| policies.md | Enable Azure Storage encryption at rest | ⬜ N/A | - |
| policies.md | Prohibited: AWS S3 SDK (must be removed post-migration) | ⬜ N/A | - |
| policies.md | Prohibited: Hardcoded AWS credentials | ⬜ N/A | - |
| policies.md | Prohibited pattern: Hardcoded connection strings or credentials | ✅ COVERED | 002-transform-rabbitmq-to-servicebus, 003-transform-mysql-to-azure-mysql-mi |
| policies.md | Unit tests for all migrated storage operations | ⬜ N/A | - |
| policies.md | Minimum 80% code coverage on new code | ❌ NOT COVERED | - |
| policies.md | Follow Azure SDK best practices for retry and error handling | ❌ NOT COVERED | - |
| policies.md | Use async APIs where available | ❌ NOT COVERED | - |

**COVERED: 5/17  ·  NOT COVERED: 3/17  ·  N/A: 9/17**

> **Note**: 9 rules are marked N/A because they pertain to AWS-to-Azure storage migration (AWS S3, Azure Blob Storage, AWS credentials). The SupplyChain Backend project does not use any AWS services; its existing dependencies are on-premises MySQL and RabbitMQ. The three uncovered rules (code coverage ≥ 80%, Azure SDK retry/error handling, async APIs) represent quality-gate requirements that are not explicitly addressed by the current task set. Consider adding a quality-gate task or enriching the `generateNewUnitTests` success criterion in the migration tasks to satisfy the 80% coverage requirement.
