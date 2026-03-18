# HMCTS Dev Test Backend

This is the backend service for the HMCTS case management system, built as a Spring Boot application. It provides RESTful APIs for managing tasks and users.

## 🛠 Tech Stack

- **Java 21**: The primary programming language.
- **Spring Boot 3.5.11**: Framework for building the application.
- **Spring Data JPA**: For database ORM.
- **PostgreSQL**: The production-level database.
- **H2 Database**: Used for development/testing if configured.
- **Lombok**: To reduce boilerplate code.
- **Mockito & JUnit 5**: For unit and integration testing.
- **Swagger/OpenAPI**: API documentation (available at `/swagger-ui.html`).

## 🚀 Getting Started

### Prerequisites

- JDK 21
- Docker and Docker Compose

### Running the Application

You can run the application using Docker Compose, which sets up both the backend and the PostgreSQL database:

```bash
docker-compose up --build
```

Alternatively, to run locally with Gradle:

```bash
./gradlew bootRun
```

## 📖 API Documentation

Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

### Key Endpoints

#### Task Management (`/api/tasks`)
- `GET /api/tasks`: Retrieve all tasks (paginated).
- `GET /api/tasks/{id}`: Retrieve a specific task by ID.
- `POST /api/tasks`: Create a new task.
- `PATCH /api/tasks/{id}`: Update an existing task.
- `DELETE /api/tasks/{id}`: Delete a task.

#### User Management (`/api/users`)
- `GET /api/users`: Retrieve all users.
- `GET /api/users/{id}`: Retrieve a specific user by ID.

## 🗄 Database Structure

The application uses two main entities:

### Task
- `id` (UUID): Primary key.
- `title` (String): Task title.
- `description` (Text): Detailed description.
- `status` (Enum): `TODO`, `IN_PROGRESS`, `DONE`, `CANCELLED`.
- `priority` (Enum): `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.
- `dueDate` (Instant): When the task is due.
- `assignee` (User): The user assigned to the task.
- `reporter` (User): The user who created the task.

### User
- `id` (UUID): Primary key.
- `name` (String): User's name.
- `email` (String): User's unique email address.

## 🧪 Testing

I have implemented comprehensive unit and integration tests.

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run integration tests
./gradlew integration

# Run all verification
./gradlew check
```

## 🏗 Build Commands

- `./gradlew build`: Build the project and run tests.
- `./gradlew clean`: Remove the build directory.
- `./gradlew bootJar`: Generate an executable JAR file.
