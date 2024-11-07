
# User Aggregation Service

## Overview
A Spring Boot application for aggregating user data from multiple databases. 
**Currently, the application supports two databases: MySQL and PostgreSQL.**

## Running the Application
1. Build the project using Maven.
   ```bash
   mvn clean package -DskipTests
   ```
2. Use Docker Compose for demo deployment (with preconfigured databases), logs will be visible:
   ```bash
   docker-compose -f docker-compose.demo.yml up
   ```   
3. Access the application at `http://localhost:8080/users`.

4. Access the Swagger UI at `http://localhost:8080/swagger-ui.html`.

5. To stop the application, use the following command:
   ```bash
   docker-compose -f docker-compose.demo.yml down
   ```   

## Endpoints
`GET /users`: Fetches aggregated user data. Parameters:
1.   login (optional): Filter by login.
2.   firstName (optional): Filter by first name.
3.   lastName (optional): Filter by last name.

## Testing
The project uses JUnit 5 and Testcontainers for integration tests.
To run the tests, use the following command:
   ```bash
   mvn test
   ```
