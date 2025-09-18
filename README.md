# OpenAPI Generator Example
[![Java Tests](https://img.shields.io/github/actions/workflow/status/TD99/spring-boot-openapi-generator-example/java-tests.yml?label=Java%20Tests&color=%233b8640)](https://github.com/TD99/spring-boot-openapi-generator-example/actions/workflows/java-tests.yml)
[![Open Issues](https://img.shields.io/github/issues-raw/TD99/spring-boot-openapi-generator-example?label=Open%20Issues)](https://github.com/TD99/spring-boot-openapi-generator-example/issues?q=is%3Aissue%20state%3Aopen)
[![Closed Issues](https://img.shields.io/github/issues-closed-raw/TD99/spring-boot-openapi-generator-example?label=Closed%20Issues&color=%233b8640)](https://github.com/TD99/spring-boot-openapi-generator-example/issues?q=is%3Aissue%20state%3Aclosed)

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=flat&logo=spring&logoColor=white)
![Angular](https://img.shields.io/badge/angular-%23DD0031.svg?style=flat&logo=angular&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-%23C71A36?style=flat&logo=Apache%20Maven&logoColor=white)
![openapi initiative](https://img.shields.io/badge/openapiinitiative-%23000000.svg?style=flat&logo=openapiinitiative&logoColor=white)

This project demonstrates how to use **OpenAPI Generator** with **Spring Boot 3**, to build a simple REST API for managing TODOs.  
It uses the delegate pattern to separate generated code from custom business logic.

## Features
- OpenAPI 3.0 specification-driven development
- Automatic Spring Boot controller & DTO generation
- Delegate pattern for business logic separation
- JPA & H2 in-memory database integration
- Swagger UI for API exploration
- Unit and integration tests with JUnit 5
- Minimal frontend for showcasing API usage

## Getting Started (Backend)
The project comes with a Spring Boot backend to showcase the usage of the OpenAPI Generator. It is located in the `root` and `src` directory.

### Prerequisites
- Java 21+
- Maven 3.9+

### Build & Run
Run the application: 
```bash
# Generate code from OpenAPI spec and build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

### Base URL
The application is available at [http://localhost:8080](http://localhost:8080). 

### API Endpoints
| Method | Path              | Description      |
|--------|-------------------|------------------|
| GET    | `/api/todos`      | List all todos   |
| POST   | `/api/todos`      | Create a todo    |
| GET    | `/api/todos/{id}` | Get a todo by ID |
| PUT    | `/api/todos/{id}` | Update a todo    |
| PATCH  | `/api/todos/{id}` | Patch a todo     |
| DELETE | `/api/todos/{id}` | Delete a todo    |

### Management Endpoints
| Name         | Path                                                 | Description           | Credentials                            |
|--------------|------------------------------------------------------|-----------------------|----------------------------------------|
| Swagger UI   | [/swagger-ui](http://localhost:8080/swagger-ui.html) | API testing UI        | None                                   |
| OpenAPI Spec | [/v3/api-docs](http://localhost:8080/v3/api-docs)    | OpenAPI specification | None                                   |
| H2 Console   | [/h2-console](http://localhost:8080/h2-console)      | In-memory database UI | Username: `sa`<br/>Password: `<empty>` |

## Getting Started (Frontend)
The project comes with a minimal Angular frontend to showcase the API usage. It is located in the frontend directory.  

### Development Mode
If you are actively developing the frontend, you can run it alongside the Spring Boot backend. This setup supports hot reloading when modifying Angular code.

#### Prerequisites
- Frontend
  - Node.js 20+
  - Angular CLI 20+ (optional)
- Backend (if needed)
  - Java 21+
  - Maven 3.9+

#### Install Dependencies
```bash
# Install frontend dependencies
cd frontend
npm install
```

#### Build & Run
Run backend and frontend separately:
```bash
# Generate code from OpenAPI spec and build the application
mvn clean compile

# Run the backend
mvn spring-boot:run
```

```bash
# In another terminal, run the frontend development server
npm --prefix ./frontend start
```

#### Endpoints
| Type          | Mode   | URL                                                    | Notes                                                          |
|---------------|--------|--------------------------------------------------------|----------------------------------------------------------------|
| Frontend      | Direct | [http://localhost:4200](http://localhost:4200)         | Hot reload enabled                                             |
| Backend (API) | Direct | [http://localhost:8080/api](http://localhost:8080/api) | URL root is reserved for [Spring Boot Mode](#spring-boot-mode) |
| Backend (API) | Proxy  | [http://localhost:4200/api](http://localhost:4200/api) | Proxy via [proxy.conf.json](frontend/proxy.conf.json)          |

*The Angular dev server is configured with a [proxy](frontend/proxy.conf.json) to forward `/api` requests to the backend.*

### Spring Boot Mode
If the frontend is not actively being developed, it will be served directly by the Spring Boot backend. This setup does not support hot reloading when modifying Angular code.

#### Prerequisites
- Java 21+
- Maven 3.9+

#### Build & Run
Run backend and frontend together:
```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

#### Endpoints
| Type          | Mode   | URL                                                     | Notes                                                       |
|---------------|--------|---------------------------------------------------------|-------------------------------------------------------------|
| Frontend      | Direct | [http://localhost:8080](http://localhost:8080)          | Hot reload disabled                                         |
| Backend (API) | Direct | [http://localhost:8080/api](http://localhost:8080/api)  | URL is the same as in [Development Mode](#development-mode) |

#### Technical Notes
- [Spring Boot Mode](#spring-boot-mode) is always active when running the application (even if the frontend is in [Development Mode](#development-mode)).
- It uses the [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin) to install Node.js, run Angular builds, and integrate the frontend with the backend.
- During the Maven build (`mvn clean compile`), the Angular app is compiled (`npm run build`) and copied into Spring Boot’s `static/` resources directory:  
`frontend/dist/openapi-generator-example-frontend/browser` → `target/classes/static`
- This allows the packaged Spring Boot application to serve the frontend directly without needing a separate Node.js process in production mode.

## License
This project is licensed under the MIT License.
