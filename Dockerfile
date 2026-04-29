# TECH DEBT: Uses old Java 8 base image
# Should use mcr.microsoft.com/openjdk/jdk:17-ubuntu (build)
# and mcr.microsoft.com/openjdk/jdk:17-distroless (runtime)
# per guardrails requirements

FROM maven:3-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/supplychain-backend-1.0.0-LEGACY.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
