# Java Authentication API

A robust Java-based RESTful API for user authentication with JWT tokens.

## Features

- User registration and login
- JWT-based authentication with access and refresh tokens
- Token refresh functionality
- User profile retrieval
- Logout functionality
- OpenAPI/Swagger documentation

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Security
- JSON Web Token (JWT)
- MyBatis
- OpenAPI/Swagger for API documentation

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Install

```bash
mvn clean install
```

### Format

```bash
mvn spotless:apply
```

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

### Generate openapi.yml　（After run application）

```bash
mvn springdoc-openapi:generate
```

The server will start on http://localhost:8080

### API Documentation

After starting the application, you can access the Swagger UI documentation at:

http://localhost:8080/swagger-ui.html

## API Endpoints

### Authentication

- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/login` - Authenticate user and receive JWT tokens
- `POST /api/auth/refresh` - Refresh access token using refresh token
- `POST /api/auth/logout` - Logout user and invalidate refresh token
- `GET /api/auth/me` - Get current user information

## Security

- JWT tokens for stateless authentication
- Refresh token mechanism
- Password encryption using BCrypt
- Role-based authorization (can be extended)

## Database

The application uses PostgreSQL as the database. It runs in a Docker container.

- Host: `localhost`
- Port: `5432`
- Database: `auth_api`
- Username: `postgres`
- Password: `postgres`

## Configuration

Main configuration properties can be found in `application.yml`:

- JWT secret key and token expiration times
- Database connection details
- Server port

## Next Steps

1. Connect to a production database (PostgreSQL, MySQL, etc.)
2. Add role-based authorization
3. Implement email verification
4. Create a frontend application using NextJS
5. Add more API endpoints for user management