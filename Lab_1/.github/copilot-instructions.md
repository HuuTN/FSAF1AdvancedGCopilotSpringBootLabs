This document contains best-practice prompts to guide GitHub Copilot in generating a complete Spring Boot application.

1. Project Setup
    # pom.xml (Maven configuration)
<!-- Add dependencies for Spring Boot Web, JPA, Validation, Lombok -->
    # application.properties
    # Configure datasource, JPA, and server port
2. Main Application Entry Point
    # Create Spring Boot main application class with @SpringBootApplication
3. Model & DTO
    Entity
    # Create JPA entity for User with id, name, email
    DTO
    # DTO class for User with id, name, email and validation annotations
4. Repository
    # JPA Repository interface for User entity
5. Service Layer
    Interface
    # Service interface for User with create, getAll, update, delete methods
    Implementation
    # ServiceImpl for User with @Service and autowired repository
6. Controller
    # REST Controller CRUD
    # REST Controller for managing User with CRUD endpoints
    # With pagination
    # REST endpoint to get all Users with pagination using Pageable
    # With validation
    # POST endpoint with @Valid for UserDTO input
7. Exception Handling
    # GlobalExceptionHandler using @ControllerAdvice with custom exception
8. Custom Response
    # Return custom error response with message and status code if not found
9. API Versioning
    # REST Controller for User with API versioning (/api/v1/users)
10. Swagger / OpenAPI Docs
    # Add Swagger @Operation and @ApiResponse to each controller method
11. Dockerfile (Optional)
    # Dockerfile for Spring Boot application with openjdk 17
12. JUnit Test
    # JUnit test for UserService create method
