# Concert Seat Type Management System

A Spring Boot 3+ application for managing concert seat type patterns with CRUD operations, soft-delete mechanism, and seat booking functionality.

## Technology Stack

- **Java 17** with Lombok and `var` for reduced boilerplate
- **Spring Boot 3.2.1** (Web, Data JPA, Security, Validation)
- **Thymeleaf** for server-side rendering
- **Oracle Database** with JDBC driver
- **Maven** for dependency management
- **TestContainers** for integration testing
- **Docker** for containerization
- **Flyway** for database migrations

## Project Structure

```
src/
├── main/
│   ├── java/com/concert/seatbooking/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # Web controllers
│   │   ├── domain/          # Domain entities
│   │   ├── repository/      # Data repositories
│   │   ├── service/         # Business services
│   │   ├── exception/       # Custom exceptions
│   │   ├── dto/             # Data Transfer Objects
│   │   └── util/            # Utility classes
│   └── resources/
│       ├── templates/       # Thymeleaf templates
│       ├── static/          # CSS, JS, Images
│       └── db/migration/    # Flyway migration scripts
└── test/
    └── java/com/concert/seatbooking/
        ├── integration/     # Integration tests
        ├── unit/            # Unit tests
        └── testcontainers/  # TestContainers setup
```

## Features

### Core Functionality
- **Seat Type Management**: CRUD operations for seat type patterns
- **Auto-generation**: Sequential seat type codes (01-99)
- **Soft Delete**: Logical deletion with business rules validation
- **Duplicate**: Copy existing seat types with new codes
- **Booking**: Seat booking functionality with state management

### Business Rules
- Seat type codes are auto-generated sequentially (01-99)
- Cannot create new seat types if code limit (99) is reached
- Cannot delete seat types that are currently booked
- Only available (not deleted, not booked) seat types can be booked

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Oracle Database (for local development)

### Building the Project

```bash
# Clone the repository
git clone <repository-url>
cd seat-booking-management

# Build the project
mvn clean compile

# Run tests
mvn test

# Package the application
mvn clean package
```

### Running with Docker Compose

```bash
# Start the application with Oracle database
docker-compose up -d

# Stop the application
docker-compose down
```

The application will be available at: http://localhost:8080

### Running Locally

1. **Start Oracle Database** (using Docker):
```bash
docker run -d --name oracle-xe \
  -p 1521:1521 \
  -e ORACLE_PASSWORD=seat_booking \
  gvenzl/oracle-xe:21-slim
```

2. **Create Database User**:
```sql
-- Connect as SYSTEM user
CREATE USER seat_booking IDENTIFIED BY seat_booking;
GRANT CONNECT, RESOURCE, DBA TO seat_booking;
```

3. **Run the Application**:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Running Tests

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest="**/*Test"

# Run only integration tests
mvn test -Dtest="**/*IT"

# Run with specific profile
mvn test -Dspring.profiles.active=test
```

## API Endpoints

### Web UI
- `GET /` - Home page
- `GET /seat-types` - List all seat types
- `GET /seat-types/new` - Create new seat type form
- `POST /seat-types` - Save new seat type
- `GET /seat-types/{id}/edit` - Edit seat type form
- `PUT /seat-types/{id}` - Update seat type
- `POST /seat-types/{id}/duplicate` - Duplicate seat type
- `DELETE /seat-types/{id}` - Soft delete seat type
- `GET /book-seats` - Booking page
- `POST /book-seats/{id}` - Book a seat type

### Authentication (Bonus Feature)
- `GET /login` - Login page
- `POST /login` - Process login
- `POST /logout` - Logout

## Configuration

### Application Profiles

- **dev**: Development profile with Oracle database
- **docker**: Docker profile for containerized deployment
- **test**: Test profile with TestContainers

### Database Configuration

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:XE
    username: seat_booking
    password: seat_booking
    driver-class-name: oracle.jdbc.OracleDriver
```

## Development Guidelines

### Code Style
- Use `var` for local variables where type inference is clear
- Implement immutable objects with Lombok `@Builder` and `@Value`
- Follow SOLID and DRY principles
- Use meaningful variable and method names
- Minimal use of setters (prefer builder pattern)

### Testing Strategy
- Unit tests for services using JUnit 5 + Mockito
- Integration tests with TestContainers
- Web layer tests with MockMvc
- High test coverage (target: >80%)

### Logging
- Use SLF4J with Logback
- Structured logging with MDC for tracing
- Appropriate log levels (DEBUG for development, INFO for production)

## Contributing

1. Follow the established code style and patterns
2. Write tests for new functionality
3. Update documentation as needed
4. Ensure all tests pass before submitting changes

## License

This project is part of a technical interview challenge.