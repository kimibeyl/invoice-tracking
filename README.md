# InvoiceTrackingInvoice

This project is a full-stack application leveraging Java 17 with Spring Boot for the backend API and Angular 20 for the frontend user interface. PostgreSQL is used as the database, managed with Flyway for migrations.

## Technologies used

| Layer      |   Technology   | Version / Tool | Notes                                                 |
| ---------- | :------------: | :------------: | :---------------------------------------------------- |
| Backend    |      Java      |       17       | JDK version                                           |
| Backend    |  Spring Boot   | Latest Stable  | Framework for the REST API.                           |
| Database   |   PostgreSQL   | Latest Stable  | Relational database.                                  |
| Migrations |     Flyway     | Latest Stable  | Manages database schema evolution.                    |
| Frontend   |    Angular     |       20       | Frontend framework.                                   |
| Components |    PrimeNG     |       20       | UI component library for Angular.                     |
| Code Gen   | ng-openapi-gen |     v0.53      | Generates Angular services from Swagger/OpenAPI spec. |
| Code Style |    Prettier    | Latest Stable  | Enforces consistent code formatting.                  |
| Deployment | Docker Compose | Latest Stable  | Orchestrates the application and database containers. |

## Prerequisites

To run this project locally, you need the following installed:

- Docker and Docker Compose

- Java Development Kit (JDK) 17

- Node.js (LTS version, required for Angular and npm)

- Angular CLI (npm install -g @angular/cli)

## Getting Started

The recommended way to run the entire application (backend, frontend, and database) is using Docker Compose.

### 1. Clone the Repository

```bash
git clone https://github.com/kimibeyl/invoice-tracking.git
cd invoice-tracking
```

### 2. Configure Environment

Ensure your project contains a .env file (or equivalent configuration) for setting up the necessary environment variables, particularly for the database connection (e.g., POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_DB).

### 3. Run with Docker Compose

Execute the following command in the root directory where your docker-compose.yml file is located:

```bash
docker-compose up --build
```

- The first run will build the Docker images for both the Spring Boot and Angular applications, and start the PostgreSQL container.

- The Spring Boot application will automatically run Flyway migrations on startup to set up the database schema.

- Once running, the applications will be accessible at:

- Backend API: `http://localhost:<BACKEND_PORT>` (typically 8080)

- Frontend UI: `http://localhost:<FRONTEND_PORT>` (typically 4200)

### Local Development (Without Docker)

If you prefer to run the components separately for easier debugging:

A. Backend (Spring Boot)

1. Set up Database: Start the PostgreSQL container using Docker Compose only for the database service, or ensure you have a local PostgreSQL instance running.

2. Build and Run: Navigate to the backend directory and execute:

```bash
./gradlew clean build
./gradlew bootRun
```

B. Frontend (Angular)

1.Install Dependencies: Navigate to the frontend directory (/frontend or similar) and run:

```bash
npm install
```

2. Generate Services: If the OpenAPI specification has changed, regenerate the services:

```bash
ng-openapi-gen -c swagger-config (in this cas: FE\swagger\invoice-config.json)
```

3. Run Application: Start the development server:

```bash
ng serve
```

The application will be available at http://localhost:4200/.

## Code Style and Formatting

This project enforces code style using Prettier.

To automatically format the code in the frontend directory:

```bash
prettier --write .
```

## Database Migrations (Flyway)

Database schema changes are managed via Flyway.

Migration scripts are located in the backend project, typically under `src/main/resources/db/migration`.

Scripts are named following the convention: `V<VERSION>.sql` (e.g., `V1.0.0.sql`).

The Spring Boot application automatically runs pending migrations on startup.

## Login details

Username: `test@domain.com`
Password: `tinqa5`

## Diagram
[<img src="docs/thumbnail.png" width="200"/>](Documentation/Blank diagram.pdf)