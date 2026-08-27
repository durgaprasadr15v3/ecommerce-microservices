** E-Commerce Microservices Application

** Project Overview

This project is a production-style E-Commerce backend application developed using Java and Spring Boot Microservices architecture. The application is divided into independent services, each responsible for a specific business functionality. Services communicate using REST APIs and are managed through Spring Cloud components.

The project demonstrates how enterprise applications are built using Microservices, API Gateway, Service Discovery, Centralized Configuration, Security, and Docker.

** Features

- User Registration & Login
- Product Management
- Order Management
- Inventory Management
- Payment Processing
- Notification Service
- API Gateway
- Service Discovery
- Centralized Configuration
- Exception Handling
- RESTful APIs
- Spring Data JPA
- MySQL Database
- Docker Support

** Microservices

| Service | Description |
|----------|-------------|
| Service Registry | Eureka Server for service discovery |
| Config Server | Centralized configuration management |
| API Gateway | Routes client requests to services |
| User Service | User registration and authentication |
| Product Service | Product CRUD operations |
| Order Service | Creates customer orders |
| Inventory Service | Checks product availability |
| Payment Service | Handles payment processing |
| Notification Service | Sends email/SMS notifications |

** Technology Stack

*** Backend
- Java 17
- Spring Boot
- Spring Cloud
- Spring Security
- Spring Data JPA
- Hibernate
- REST APIs
- Maven

** Database
- MySQL

** Tools
- IntelliJ IDEA
- Git
- GitHub
- Postman
- Docker

*** Project Structure
Ecommerce-Microservices
│
├── api-gateway
├── service-registry
├── config-server
├── user-service
├── product-service
├── order-service
├── inventory-service
├── payment-service
├── notification-service
├── docker-compose.yml
└── README.md
** Architecture
                Client
                   │
                   ▼
             API Gateway
                   │
      ┌────────────┼────────────┐
      ▼            ▼            ▼
 User Service  Product Service  Order Service
                     │
                     ▼
             Inventory Service
                     │
                     ▼
             Payment Service
                     │
                     ▼
           Notification Service

            Eureka Server
            Config Server
** Getting Started

*** Clone Repository

git clone https://github.com/your-username/ecommerce-microservices.git

**Move to Project

cd ecommerce-microservices

*** Start MySQL

Configure your MySQL database and update the connection properties in `application.yml`

*** Start Services

Run the services in the following order:

1. Config Server
2. Eureka Server
3. API Gateway
4. User Service
5. Product Service
6. Inventory Service
7. Order Service
8. Payment Service
9. Notification Service
    
** API Testing

Use Postman to test the REST APIs.

Example:
POST /users/register

POST /orders

GET /products

PUT /products/{id}

DELETE /products/{id}

** Microservice Flow

1. Client sends request to API Gateway.
2. Gateway routes request to the appropriate service.
3. Service is discovered using Eureka.
4. Business logic executes.
5. Database operations are performed.
6. Response is returned through API Gateway.
 

** Learning Outcomes

- Designed RESTful APIs using Spring Boot.
- Implemented Microservices Architecture.
- Used Eureka Service Discovery.
- Configured Spring Cloud Config Server.
- Integrated API Gateway.
- Implemented Spring Security.
- Used JPA and Hibernate.
- Built Dockerized services.
- Tested APIs using Postman.
- Followed layered architecture and clean code practices.

** Author

**Prasad**

Java Backend Developer

** Skills

- Java
- Spring Boot
- Microservices
- Spring Security
- Spring Data JPA
- MySQL
- Docker
- Git
- Maven
- REST APIs
