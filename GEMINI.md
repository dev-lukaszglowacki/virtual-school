# Project Overview

This project is a virtual school management system. It consists of a Java Spring Boot backend and a React frontend. Keycloak is used for user authentication and authorization. The backend will expose a REST API for managing students, lecturers, and lesson plans. The frontend will provide a user interface for interacting with the API.

# Building and Running

## Backend

The backend is a Java Spring Boot application. To build and run the backend, you will need to have Java and Maven installed.

To build the backend:

```bash
cd virtual-school-backend
mvn clean install
```

To run the backend:

```bash
cd virtual-school-backend
mvn spring-boot:run
```

## Frontend

The frontend is a React application. To build and run the frontend, you will need to have Node.js and npm installed.

To install dependencies:

```bash
cd virtual-school-ui
npm install
```

To run the frontend in development mode:

```bash
cd virtual-school-ui
npm start
```

To build the frontend for production:

```bash
cd virtual-school-ui
npm run build
```

## Services

The project uses Docker to run Keycloak and PostgreSQL. To start these services, run the following command:

```bash
docker compose -f docker-compose.yml up -d
```

# Development Conventions

## Backend

*   **Language:** Java
*   **Framework:** Spring Boot
*   **Authentication:** Keycloak
*   **Database:** PostgreSQL

## Frontend

*   **Language:** JavaScript
*   **Framework:** React
*   **Authentication:** Keycloak
