# Virtual School - Backend

This is the backend service for the Virtual School project. It is a Java Spring Boot application that provides a RESTful API for the frontend.

## Technologies Used

*   **Java 17**
*   **Spring Boot**
*   **Spring Security** (OAuth 2.0 Resource Server with JWT)
*   **Spring Data JPA** (with Hibernate)
*   **PostgreSQL**
*   **Maven**
*   **Keycloak** (for user identity and access management)

## Building and Running

While the recommended way to run the entire application is using the `docker-compose.yml` file in the project root, you can also run the backend as a standalone application for development or testing.

### Prerequisites

*   Java 17 (or newer)
*   Maven
*   A running PostgreSQL database
*   A running Keycloak instance with the `virtual-school` realm configured

### Configuration

Before running, you may need to update the `src/main/resources/application.properties` file to point to your local database and Keycloak instances, particularly these properties:

*   `spring.datasource.url`
*   `spring.datasource.username`
*   `spring.datasource.password`
*   `spring.security.oauth2.resourceserver.jwt.issuer-uri`
*   `keycloak.auth-server-url`

### Building the Application

To build the project and package it into a JAR file, run the following command from this directory:

```bash
./mvnw clean install
```

### Running the Application

You can run the application using the Spring Boot Maven plugin:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080` by default.
