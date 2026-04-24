# Modernization Assessment Summary

**Target Azure Services**: N/A

## Overall Statistics

**Total Applications**: 1

**Name: supplychain-backend**
- Mandatory: 5 issues
- Potential: 2 issues
- Optional: 3 issues

> **Severity Levels Explained:**
> - **Mandatory**: The issue has to be resolved for the migration to be successful.
> - **Potential**: This issue may be blocking in some situations but not in others. These issues should be reviewed to determine whether a change is required or not.
> - **Optional**: The issue discovered is real issue fixing which could improve the app after migration, however it is not blocking.

## Applications Profile

### Name: supplychain-backend
- **JDK Version**: 8
- **Frameworks**: Spring Boot, Spring
- **Languages**: Java
- **Build Tools**: Maven

**Key Findings**:
- **Mandatory Issues (14 locations)**:
  - <!--ruleid=spring-framework-version-01000-->Spring Framework Version End of OSS Support (3 locations found)
  - <!--ruleid=azure-java-version-02000-->Legacy Java version (3 locations found)
  - <!--ruleid=spring-boot-to-azure-spring-boot-version-01000-->Spring Boot Version is End of OSS Support (5 locations found)
  - <!--ruleid=localhost-http-00001-->Local HTTP Calls (1 location found)
  - <!--ruleid=unsecure-network-protocol-00000-->Use of unsecured network protocols or URI libraries (2 locations found)
- **Potential Issues (4 locations)**:
  - <!--ruleid=azure-database-mysql-01000-->MySQL database found (2 locations found)
  - <!--ruleid=azure-password-01000-->Password found in configuration file (2 locations found)
- **Optional Issues (8 locations)**:
  - <!--ruleid=azure-message-queue-rabbitmq-01000-->Spring RabbitMQ usage found in code (4 locations found)
  - <!--ruleid=azure-message-queue-amqp-02000-->Spring AMQP dependency found (2 locations found)
  - <!--ruleid=hardcoded-urls-00001-->Avoid using hardcoded URLs (HTTP protocol) in source code (2 locations found)

## Next Steps

For comprehensive migration guidance and best practices, visit:
- [GitHub Copilot modernization](https://aka.ms/ghcp-appmod)

Have questions or suggestions? [Share your feedback](https://aka.ms/ghcp-appmod/feedback)
