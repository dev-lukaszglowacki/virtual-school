# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean install -DskipTests

# Package stage
FROM eclipse-temurin:21-jre-jammy
RUN apt-get update && apt-get install -y curl
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
