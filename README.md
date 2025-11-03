# virtual-school

# Project Overview

This project is a virtual school management system. It consists of a Java Spring Boot backend and a React frontend. Keycloak is used for user authentication and authorization. The backend will expose a REST API for managing students, lecturers, and lesson plans. The frontend will provide a user interface for interacting with the API.

This project follows a modern client-server architecture, composed of a single-page application (SPA) frontend and a resource-based backend.

## Backend

The backend is a RESTful API built with **Java** and the **Spring Boot** framework. Its primary responsibilities include business logic, data persistence, and securing resources.

*   **API:** A REST API is exposed to perform CRUD (Create, Read,
Update, Delete) operations on the core entities of the system: students,
lecturers, lesson plans, etc.
*   **Security:** **Spring Security** is used to protect the API
endpoints. It is configured as an **OAuth 2.0 Resource Server**,
validating JSON Web Tokens (JWTs) on every incoming request. Endpoint
access is restricted based on user roles (e.g., `admin`, `teacher`,
`student`).
*   **Database:** **PostgreSQL** is the relational database used for data
storage. **Spring Data JPA** with Hibernate is used for object-relational
mapping (ORM), simplifying database interactions.
*   **Authentication Integration:** The backend integrates with Keycloak to
get the necessary information for token validation (e.g., the issuer URI
and the JSON Web Key Set URI).

## Frontend

The frontend is a single-page application built with **JavaScript** and the
**React** library. It provides a dynamic and responsive user interface for
interacting with the virtual school system.

*   **Authentication:** The **`keycloak-js`** library is used to handle
the authentication flow. It redirects users to the Keycloak login page
and, upon successful authentication, receives a JWT.
*   **API Communication:** All communication with the backend API is done
via HTTP requests, managed by a library like `axios` or the native `fetch`
API. The JWT is included in the `Authorization` header of each request to
authenticate the user.
*   **Component-Based UI:** The UI is built as a set of reusable React
components, each responsible for a specific part of the user interface
(e.g., displaying a list of students, a form for adding a new lecturer).
*   **Routing:** **`react-router-dom`** is used for client-side routing,
allowing navigation between different views (e.g., `/students`,
`/lecturers`) without a full page reload.

## Authentication and Authorization

**Keycloak** is used as a centralized identity and access management (IAM)
solution.

*   **Protocols:** It uses the standard **OpenID Connect (OIDC)** and
**OAuth 2.0** protocols for authentication and authorization.
*   **Realm:** A dedicated realm named `virtual-school` isolates the
users, roles, and clients for this application.
*   **Clients:** Two clients are configured in Keycloak:
    *   `spring-boot-app`: A confidential client for the backend.
    *   `react-app`: A public client for the frontend.
*   **Roles:** Role-based access control (RBAC) is implemented using roles
defined in Keycloak (`admin`, `teacher`, `student`). The frontend can
dynamically show or hide UI elements based on the user's role, and the
backend secures business logic at the API level.

## Services

The supporting services are managed using **Docker Compose** to ensure a
consistent and easily reproducible development environment.

*   **`docker-compose.yml`:** This file defines the `keycloak` and
`postgres` services, their configurations, and the network they
communicate on. This allows for a one-command setup of the required
infrastructure.


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

