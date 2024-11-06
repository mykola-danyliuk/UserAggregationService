
# User Aggregation Service

## Overview
A Spring Boot application for aggregating user data from multiple databases.

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
4. To stop the application, use the following command:
   ```bash
   docker-compose -f docker-compose.demo.yml down
   ```   

## Endpoints
- `GET /users`: Fetches aggregated user data.
