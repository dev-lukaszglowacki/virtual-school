# Virtual School - Frontend

This is the frontend for the Virtual School project. It is a single-page application (SPA) built with React.

## Technologies Used

*   **React**
*   **React Router** (`react-router-dom`)
*   **Keycloak JS** (`keycloak-js`) for authentication
*   **Node.js** and **npm**

## Setup and Running

While the recommended way to run the entire application is using the `docker-compose.yml` file in the project root, you can also run the frontend as a standalone application for development.

### Prerequisites

*   Node.js (version 16 or newer recommended)
*   npm

### Installation

To install the project dependencies, run the following command from this directory:

```bash
npm install
```

### Running in Development Mode

To start the application in development mode, run:

```bash
npm start
```

This will open the application in your default browser at `http://localhost:3000`. The page will automatically reload if you make edits.

**Note:** The frontend development server is configured to proxy API requests to the backend. All fetch requests to relative paths (e.g., `/api/students`) will be forwarded to the `proxy` URL defined in `package.json`. This is handled automatically when using the main Docker Compose setup.

### Building for Production

To create an optimized production build, run:

```bash
npm run build
```

This will create a `build` folder with the static assets for your application.
