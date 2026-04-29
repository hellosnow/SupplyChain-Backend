# Modernized: Java 25 base images per organizational targets
# Build stage: mcr.microsoft.com/openjdk/jdk:25-ubuntu
# Runtime stage: mcr.microsoft.com/openjdk/jdk:25-distroless

FROM mcr.microsoft.com/openjdk/jdk:25-ubuntu AS build

WORKDIR /app

# Install Maven (not bundled in the JDK image)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Create stub JARs for internal provided-scope SDKs (supplied at runtime by the platform)
RUN mkdir -p /tmp/stubs/com/acme/logging /tmp/stubs/com/acme/mesh && \
    printf 'package com.acme.logging;\npublic class InternalLogger {\n  public static InternalLogger getLogger(Class<?> c) { return new InternalLogger(); }\n  public void info(String m, Object... a) {}\n  public void warn(String m, Object... a) {}\n  public void error(String m, Object... a) {}\n  public void debug(String m, Object... a) {}\n}\n' > /tmp/stubs/com/acme/logging/InternalLogger.java && \
    printf 'package com.acme.mesh;\npublic class ServiceMesh {\n  public <T> T call(String svc, String path, Class<T> t) { return null; }\n}\n' > /tmp/stubs/com/acme/mesh/ServiceMesh.java && \
    javac /tmp/stubs/com/acme/logging/InternalLogger.java /tmp/stubs/com/acme/mesh/ServiceMesh.java && \
    jar cf /tmp/internal-logger.jar -C /tmp/stubs com/acme/logging && \
    jar cf /tmp/service-mesh.jar -C /tmp/stubs com/acme/mesh && \
    mvn org.apache.maven.plugins:maven-install-plugin:3.1.1:install-file \
      -Dfile=/tmp/internal-logger.jar -DgroupId=com.acme.logging -DartifactId=internal-logger \
      -Dversion=1.0.0 -Dpackaging=jar -DgeneratePom=true && \
    mvn org.apache.maven.plugins:maven-install-plugin:3.1.1:install-file \
      -Dfile=/tmp/service-mesh.jar -DgroupId=com.acme.mesh -DartifactId=service-mesh \
      -Dversion=1.0.0 -Dpackaging=jar -DgeneratePom=true

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM mcr.microsoft.com/openjdk/jdk:25-distroless

WORKDIR /app

COPY --from=build /app/target/supplychain-backend-1.0.0-LEGACY.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
