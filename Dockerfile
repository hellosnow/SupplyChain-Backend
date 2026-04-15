# Build stage: Use guardrails-approved base image with Maven installed
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu AS build

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage: Use guardrails-approved distroless base image
FROM mcr.microsoft.com/openjdk/jdk:17-distroless

WORKDIR /app

# Copy the built JAR (use wildcard to avoid hardcoding version)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# JVM tuning for containers — respect container memory limits
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]