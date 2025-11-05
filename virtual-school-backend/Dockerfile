# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean install -DskipTests

# Package stage
FROM openjdk:17-slim
RUN apt-get update && apt-get install -y curl
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
