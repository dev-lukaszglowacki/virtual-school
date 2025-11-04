# virtual-school

# Project Overview

This project is a virtual school management system. It consists of a Java Spring Boot backend and a React frontend. Keycloak is used for user authentication and authorization. The backend exposes a REST API for managing the school's educational structure (groups, subjects, lesson plans), while the frontend provides a user-friendly interface.

Key features include:
*   Role-based access for Admins, Teachers, and Students.
*   A central user creation form for administrators.
*   Timetable views for both students and teachers to see their personalized schedules.

This project follows a modern, containerized, client-server architecture.

# Technical Overview

## Backend

The backend is a RESTful API built with **Java** and the **Spring Boot** framework. Its primary responsibilities include business logic, data persistence, and securing resources.

*   **User Management:** The application uses Keycloak as the single source of truth for user identity. The backend's `Student` and `Lecturer` database tables only store a `keycloakId`, linking the application's data to Keycloak's user record.
*   **API:** A REST API is exposed to manage educational entities. When the frontend requests lists of students or lecturers, the backend fetches records from its database and enriches them with user details (name, email) by calling the Keycloak API via the **Keycloak Admin Client**.
*   **Security:** **Spring Security** protects the API endpoints. It is configured as an **OAuth 2.0 Resource Server**, validating JWTs on every request.
*   **Database:** **PostgreSQL** is used for data storage, with **Spring Data JPA** for object-relational mapping.

## Frontend

The frontend is a single-page application built with **JavaScript** and the **React** library.

*   **Authentication:** The **`keycloak-js`** library handles the OpenID Connect authentication flow.
*   **API Communication:** The JWT from Keycloak is included in the `Authorization` header of every request to the backend.
*   **UI:** The UI is built with reusable React components, including role-specific views. The "Add Student" and "Add Lecturer" buttons redirect to a central user creation form with the correct role pre-selected.
*   **Routing:** **`react-router-dom`** is used for client-side routing.

## Authentication and Authorization

**Keycloak** is used as a centralized identity and access management (IAM) solution.

*   **Realm:** A dedicated realm named `virtual-school` is automatically imported from `realm-export.json`.
*   **Clients:**
    *   `virtual-school-ui`: A public client for the frontend.
    *   `admin-cli`: A confidential client used by the backend to communicate with the Keycloak Admin API.
*   **Roles:** Role-based access control (RBAC) is implemented using roles defined in Keycloak (`admin`, `teacher`, `student`).

## Services

The entire infrastructure is managed using **Docker Compose**.

*   **`docker-compose.yml`:** This file defines all services (`frontend`, `backend`, `keycloak`, `postgres` databases, `adminer`), their configurations, and the network they communicate on.

# Getting Started

## Prerequisites

*   Docker
*   Docker Compose

## Running the Application

The entire project is orchestrated using Docker Compose.

1.  **Build and Start All Services**
    Run the following command from the project's root directory:
    ```bash
    docker compose up --build -d
    ```
    *   `--build` forces a rebuild of the images, which is necessary after code changes.
    *   `-d` runs the containers in detached mode.

2.  **Stop All Services**
    ```bash
    docker compose down
    ```

3.  **Stop Services and Remove Data**
    To perform a clean restart and re-import the Keycloak realm, run:
    ```bash
    docker compose down -v
    ```

## Development Environment

On startup, the backend database is automatically seeded with sample data (groups and subjects) from `import.sql`. The Keycloak realm, clients, and default users are imported from `realm-export.json`.

## Accessing the Application

*   **Frontend:** [http://localhost:3000](http://localhost:3000)
*   **Backend API:** [http://localhost:8080](http://localhost:8080)
*   **Keycloak Admin Console:** [http://localhost:8082](http://localhost:8082)
*   **Adminer (Database Admin Tool):** [http://localhost:8083](http://localhost:8083)

## Default Credentials

You can log in with the following users defined in `realm-export.json`:

*   **Role:** Admin
    *   **Username:** `adminuser`
    *   **Password:** `adminpass`
*   **Role:** Teacher
    *   **Username:** `teacheruser`
    *   **Password:** `teacherpass`
*   **Role:** Student
    *   **Username:** `studentuser`
    *   **Password:** `studentpass`
*   **Keycloak Admin Console:**
    *   **Username:** `keycloakadmin`
    *   **Password:** `keycloakadmin`


