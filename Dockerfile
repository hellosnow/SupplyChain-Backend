# =============================================================================
# Build Stage
# Base image: mcr.microsoft.com/openjdk/jdk:17-ubuntu (required by guardrails.md)
# =============================================================================
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu AS build

# Install Maven (no wrapper present in project)
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy dependency manifest first to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B --quiet

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B --quiet

# =============================================================================
# Runtime Stage
# Base image: mcr.microsoft.com/openjdk/jdk:17-distroless (required by guardrails.md)
# Minimal attack surface: no shell, no package manager, read-only filesystem capable
# Secrets are managed via Azure Key Vault (per guardrails.md) — not baked into image
# TLS 1.2+ enforced at the application/ingress layer (per standards.md)
# =============================================================================
FROM mcr.microsoft.com/openjdk/jdk:17-distroless AS runtime

# OCI-standard image labels
LABEL org.opencontainers.image.title="SupplyChain Backend" \
      org.opencontainers.image.description="Supply Chain Management System – Backend API" \
      org.opencontainers.image.vendor="ACME" \
      org.opencontainers.image.version="1.0.0-LEGACY" \
      org.opencontainers.image.base.name="mcr.microsoft.com/openjdk/jdk:17-distroless" \
      com.acme.target-platform="Azure Kubernetes Service" \
      com.acme.soc2-compliant="true"

WORKDIR /app

# Copy the built JAR from the build stage using a wildcard
# so the filename never needs to be hard-coded here
COPY --from=build /app/target/*.jar app.jar

# Run as non-root user (distroless nonroot, uid=65532)
# This satisfies AKS pod-security standards and SOC 2 least-privilege requirements
USER nonroot

# Expose the application port
EXPOSE 8080

# JVM flags tuned for containerised / AKS workloads:
#   -XX:+UseContainerSupport   – honour cgroup CPU & memory limits instead of host values
#   -XX:MaxRAMPercentage=75.0  – allocate up to 75 % of container memory to the JVM heap,
#                                leaving headroom for OS buffers and non-heap memory
#   -Djava.security.egd=...    – faster SecureRandom seeding (avoids /dev/random blocking)
# Health checks are managed by Kubernetes liveness/readiness probes in the AKS deployment
# manifest; a HEALTHCHECK directive is intentionally omitted from this distroless image.
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "app.jar"]
