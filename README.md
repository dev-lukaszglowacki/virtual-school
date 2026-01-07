# Virtual-School

# Project Overview

This project is a virtual school management system featuring a Java Spring Boot backend and a React frontend. It uses Keycloak for identity and access management and is fully containerized with Docker Compose for easy setup.

Key features include:
*   Role-based access for Admins, Teachers, and Students.
*   A central user creation form for administrators.
*   Personalized timetable views for both students and teachers.

# Technical Overview

## Backend

The backend is a RESTful API built with **Java** and **Spring Boot**.

*   **User Management:** Uses Keycloak as the single source of truth for user identity. The application database stores a `keycloakId` to link local data (like a student's group) to a Keycloak user.
*   **API:** Enriches data on the fly. All endpoints are available under the `/api` path. When user lists are requested, it fetches records from its own database and combines them with fresh user details (name, email) from the Keycloak API.
*   **Security:** Endpoints are secured using **Spring Security** as an OAuth 2.0 Resource Server.
*   **Database:** **PostgreSQL** with **Spring Data JPA**.

## Frontend

The frontend is a single-page application built with **React**.

*   **Authentication:** The **`keycloak-js`** library manages the OpenID Connect authentication flow.
*   **UI:** The UI features a streamlined workflow for admins, allowing them to add students or lecturers via a central form that is pre-filled with the correct role.
*   **Routing:** **`react-router-dom`** is used for client-side routing.

## Services & Authentication

The entire infrastructure is managed via **Docker Compose**, which orchestrates the frontend, backend, databases, and **Keycloak**. A realm is automatically imported on first launch to provide default clients, roles (`admin`, `teacher`, `student`), and users.

The backend uses **Apache Kafka** for asynchronous communication with the following services:

*   **`notification-service`**: Handles and sends notifications to users.
*   **`reporting-service`**: Generates reports based on data from the main application.

# Getting Started

## Prerequisites

*   Docker
*   Docker Compose

## Running the Application

The entire project is designed to be run with a single command from the project root.

1.  **Build and Start All Services**
    ```bash
    docker compose up --build -d
    ```
2.  **Stop All Services**
    ```bash
    docker compose down
    ```
3.  **Stop Services and Remove Data** (for a clean restart)
    ```bash
    docker compose down -v
    ```

**Note:** For more detailed instructions on running the backend or frontend as standalone applications, please see the `README.md` files in the `virtual-school-backend` and `virtual-school-ui` directories.

## Accessing the Application

*   **Frontend:** [http://localhost:3000](http://localhost:3000)
*   **Keycloak Admin Console:** [http://localhost:8082](http://localhost:8082)
*   **Adminer (Database Admin Tool):** [http://localhost:8083](http://localhost:8083)

## Default Credentials

*   **Admin:** `adminuser` / `adminpass`
*   **Teacher:** `teacheruser` / `teacherpass`
*   **Student:** `studentuser` / `studentpass`
*   **Keycloak Console:** `keycloakadmin` / `keycloakadmin`


