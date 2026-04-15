FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM mcr.microsoft.com/openjdk/jdk:17-distroless

WORKDIR /app

COPY --from=build /app/target/supplychain-backend-1.0.0-LEGACY.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]