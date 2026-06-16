FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM mcr.microsoft.com/openjdk/jdk:21-distroless

WORKDIR /app

COPY --from=build /app/target/supplychain-backend-2.0.0.jar app.jar

ENV SPRING_PROFILES_ACTIVE=azure

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
