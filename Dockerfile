# Modernized: Java 25 base images per organizational targets
# Build stage: mcr.microsoft.com/openjdk/jdk:25-ubuntu
# Runtime stage: mcr.microsoft.com/openjdk/jdk:25-distroless

FROM mcr.microsoft.com/openjdk/jdk:25-ubuntu AS build

WORKDIR /app

# Install Maven (not bundled in the JDK image)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM mcr.microsoft.com/openjdk/jdk:25-distroless

WORKDIR /app

COPY --from=build /app/target/supplychain-backend-1.0.0-LEGACY.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
