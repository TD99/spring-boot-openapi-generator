# OpenAPI Generator Example

This project demonstrates how to use **OpenAPI Generator** with **Spring Boot 3**, **JPA**, and **Springdoc OpenAPI** to
build a simple REST API for managing TODOs. It uses the delegate pattern to separate generated code from custom business
logic.

## Features

- OpenAPI 3.0 specification-driven development
- Automatic Spring Boot controller & DTO generation
- Delegate pattern for business logic separation
- JPA & H2 in-memory database integration
- Spring Validation support
- Swagger UI for API exploration

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+

### Build & Run

```bash
# Generate code from OpenAPI spec
mvn clean compile

# Run the application
mvn spring-boot:run
```

### API Endpoints

- `GET /todos` → List all todos
- `POST /todos` → Create a new todo
- `GET /todos/{id}` → Get a todo by ID
- `PUT /todos/{id}` → Update a todo

> You may also import the [openapi.yaml](src/main/resources/openapi.yaml) file into your OpenAPI client such as Postman.

### Management Endpoints

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI spec: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

### H2 Credentials
- Username: `sa`
- Password: `<empty>`

## TODOs
- PATCH /todos/{id}
- DELETE /todos/{id}
- Pagination/Sorting/Filtering
- Tests

## License

This project is licensed under the MIT License.

