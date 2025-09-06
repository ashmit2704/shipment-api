# Shipment Tracking API

A basic backend service for tracking shipments in a logistics system built with Java and Spring Boot.

## Features

- ✅ Create new shipments with orderId, origin, destination, and status
- ✅ Fetch shipment details by orderId
- ✅ Update shipment status with proper validation
- ✅ List all shipments with optional filtering
- ✅ Status transition validation (pending → dispatched → in-transit → delivered)
- ✅ Comprehensive error handling
- ✅ OpenAPI/Swagger documentation
- ✅ Unit tests for core functionality
- ✅ In-memory storage (no database required)

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web** - REST API endpoints
- **Spring Validation** - Request validation
- **SpringDoc OpenAPI** - API documentation
- **JUnit 5** - Unit testing
- **Maven** - Build and dependency management

## API Endpoints

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/shipments` | Create a new shipment |
| GET | `/api/v1/shipments/{orderId}` | Get shipment by order ID |
| PATCH | `/api/v1/shipments/{orderId}` | Update shipment status |
| GET | `/api/v1/shipments` | Get all shipments (with optional filters) |
| GET | `/api/v1/shipments/stats` | Get shipment statistics |

### Documentation Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/swagger-ui.html` | Swagger UI documentation |
| GET | `/api-docs` | OpenAPI JSON specification |

## Status Flow

The shipment status follows a strict workflow:

```
pending → dispatched → in-transit → delivered
```

- **pending**: Initial status when shipment is created
- **dispatched**: Shipment has been dispatched from origin
- **in-transit**: Shipment is currently in transit
- **delivered**: Shipment has been delivered (final status)

### Status Transition Rules

- Only forward transitions are allowed
- Cannot skip intermediate states
- No transitions allowed from `delivered` status
- Backward transitions (e.g., `dispatched` → `pending`) are prohibited

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation & Running

1. **Clone the repository** (if applicable)
   ```bash
   git clone <repository-url>
   cd shipment-api
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

4. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

   Or build and run the JAR:
   ```bash
   mvn clean package
   java -jar target/shipment-tracking-api-1.0.0.jar
   ```

5. **Access the application**
   - API Base URL: `http://localhost:8080/api/v1`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - Health Check: `http://localhost:8080/actuator/health`

## API Usage Examples

### Create a New Shipment

```bash
curl -X POST http://localhost:8080/api/v1/shipments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORDER-001",
    "origin": "New York",
    "destination": "Los Angeles"
  }'
```

### Get Shipment Details

```bash
curl http://localhost:8080/api/v1/shipments/ORDER-001
```

### Update Shipment Status

```bash
curl -X PATCH http://localhost:8080/api/v1/shipments/ORDER-001 \
  -H "Content-Type: application/json" \
  -d '{"status": "dispatched"}'
```

### Get All Shipments

```bash
# Get all shipments
curl http://localhost:8080/api/v1/shipments

# Filter by status
curl http://localhost:8080/api/v1/shipments?status=pending

# Filter by origin
curl http://localhost:8080/api/v1/shipments?origin=New%20York

# Filter by both status and origin
curl "http://localhost:8080/api/v1/shipments?status=pending&origin=New York"
```

### Get Statistics

```bash
curl http://localhost:8080/api/v1/shipments/stats
```

## Request/Response Examples

### Create Shipment Request

```json
{
  "orderId": "ORDER-001",
  "origin": "New York",
  "destination": "Los Angeles",
  "status": "pending"
}
```

### Shipment Response

```json
{
  "orderId": "ORDER-001",
  "origin": "New York",
  "destination": "Los Angeles",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00.000Z",
  "updatedAt": "2024-01-15T10:30:00.000Z"
}
```

### Status Update Request

```json
{
  "status": "dispatched"
}
```

### Error Response

```json
{
  "status": 400,
  "error": "Invalid Status Transition",
  "message": "Invalid status transition from pending to delivered. Valid next status is: dispatched",
  "timestamp": "2024-01-15T10:35:00.000Z"
}
```

## Testing

The application includes comprehensive unit tests:

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=ShipmentServiceTest
```

### Test Coverage

- ✅ Status transition validation
- ✅ Service layer business logic
- ✅ Controller endpoints
- ✅ Exception handling
- ✅ Validation scenarios

## Configuration

The application can be configured via `application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: shipment-tracking-api

# Logging levels
logging:
  level:
    com.logistics.shipment: INFO
```

## Architecture

```
src/main/java/com/logistics/shipment/
├── ShipmentTrackingApplication.java    # Main application class
├── controller/
│   └── ShipmentController.java         # REST endpoints
├── service/
│   └── ShipmentService.java           # Business logic
├── model/
│   ├── Shipment.java                  # Shipment entity
│   └── ShipmentStatus.java            # Status enum
├── exception/
│   ├── GlobalExceptionHandler.java    # Error handling
│   ├── ShipmentNotFoundException.java
│   ├── DuplicateShipmentException.java
│   └── InvalidStatusTransitionException.java
└── config/
    └── OpenApiConfig.java             # Swagger configuration
```

## Error Handling

The API provides meaningful error responses for various scenarios:

- **404 Not Found**: Shipment with given order ID doesn't exist
- **409 Conflict**: Attempting to create shipment with duplicate order ID
- **400 Bad Request**: Invalid status transitions or malformed requests
- **400 Bad Request**: Validation errors for required fields

## Future Enhancements

- Database persistence (PostgreSQL, MySQL)
- Authentication and authorization
- Shipment tracking history/audit log
- Real-time notifications
- Batch operations
- Advanced filtering and pagination
- Integration with external logistics providers

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License.