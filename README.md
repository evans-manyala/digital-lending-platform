# Digital Lending Platform

## Overview

This project implements a Digital Lending Platform that connects a bank's customers with a loan management system. The platform integrates with the Bank's Core Banking System (CBS) and a third-party Scoring Engine to provide micro loan products to bank customers.

## Architecture

The system consists of two main components:

1. **Loan Management System (LMS)**: Core service that handles loan applications, queries customer information, and interacts with the Scoring Engine.
2. **Transaction Service**: Exposes customer transaction data to the Scoring Engine.

### System Integration Points

- **Bank's Core Banking System (CBS)**:
  - KYC API (SOAP)
  - Transaction Data API (SOAP)
- **Scoring Engine**:
  - Initiate Query Score API (REST)
  - Query Score API (REST)
- **Mobile Application** (Consumer):
  - Subscription API (REST)
  - Loan Request API (REST)
  - Loan Status API (REST)

## Project Structure

```
digital-lending-platform/
├── lms-service/             # Loan Management System
├── transaction-service/     # Transaction Data Service
├── common/                  # Shared components, models, utilities
└── docs/                    # API documentation
```

## Features

- Customer subscription and loan application
- Customer KYC verification via CBS
- Transaction data retrieval and exposure
- Credit scoring via third-party Scoring Engine
- Asynchronous loan processing with retry mechanisms
- Prevention of duplicate loan applications

## Technologies Used

- Java with Spring Boot
- Spring Web for REST APIs
- Spring WS for SOAP integration
- Spring Data JPA with H2/PostgreSQL
- Spring Retry for resilient integration
- Swagger/OpenAPI for API documentation
- Maven for dependency management
- JUnit and Mockito for testing

## API Documentation

### LMS Service APIs (REST)

#### Subscription API

- **Endpoint**: `/api/v1/loans/subscribe`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "customerNumber": "234774784"
  }
  ```
- **Response**: Subscription status and details

#### Loan Request API

- **Endpoint**: `/api/v1/loans/request`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "customerNumber": "234774784",
    "amount": 10000
  }
  ```
- **Response**: Loan application status and reference

#### Loan Status API

- **Endpoint**: `/api/v1/loans/status/{customerNumber}`
- **Method**: GET
- **Response**: Current status of customer's loan application

### Transaction Service API (REST)

#### Transaction Data API

- **Endpoint**: `/api/v1/transactions/{customerNumber}`
- **Method**: GET
- **Authentication**: Basic Auth
- **Response**: Transaction data for the specified customer

## Integration Details

### CBS Integration (SOAP)

- KYC API: `https://kycapitest.credable.io/service/customerWsdl.wsdl`
- Transaction Data API: `https://trxapitest.credable.io/service/transactionWsdl.wsdl`
- Authentication: Basic Auth (username: admin, password: pwd123)

### Scoring Engine Integration (REST)

- Base URL: `https://scoringtest.credable.io/api/v1/scoring`
- Registration: `https://scoringtest.credable.io/api/v1/client/createClient`
- Authentication: Token-based via client-token header

## Setup and Deployment

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker (optional, for containerization)

### Build Instructions

```bash
# Clone the repository
git clone https://github.com/yourusername/digital-lending-platform.git
cd digital-lending-platform

# Build the project
mvn clean install

# Run LMS Service
cd lms-service
mvn spring-boot:run

# Run Transaction Service (in a new terminal)
cd transaction-service
mvn spring-boot:run
```

### Docker Deployment

```bash
# Build Docker images
docker-compose build

# Start services
docker-compose up -d
```

## Testing

The project includes:

- Unit tests for service and controller layers
- Integration tests for external APIs
- End-to-end tests for complete loan application flow

Run tests with:

```bash
mvn test
```

## Test Data

Customer IDs available for testing:

- 234774784
- 318411216
- 340397370
- 366585630
- 397178638

## License

GNU GENERAL PUBLIC LICENSE
