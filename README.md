# IPL Auction Management System - Backend

## Overview
This repository contains the backend core for the **IPL Auction Management System**, built using **Java 17**, **Spring Boot 3**, **Spring Data JPA**, **Spring Security + JWT**, and **MySQL**.

## Core Architecture & Responsibilities (Member 1 - Project Lead)
- **Project Infrastructure**: Base packages, Maven setup, global configurations.
- **Team & Player Management APIs**: CRUD operations, categories, base prices.
- **Rules Engine & Constraints**: Team Purse validations, squad size constraints (Min/Max players, Overseas player cap).
- **Global Exception Handling**: Centralized API error response contract.

## Base Package Hierarchy
```text
com.ipl.auction
├── config/              # Configurations (CORS, Security, OpenAPI)
├── controller/          # REST Endpoints
├── dto/                 # Request & Response Payload objects
├── exception/           # Custom Exceptions & Global Exception Controller Advice
├── model/               # JPA Entities / Database Schema Models
├── repository/          # Spring Data JPA Repositories
├── service/             # Business Logic Interfaces & Implementations
└── util/                # Constants and helper utilities
```

## Running the Application Locally
1. Ensure MySQL is installed and running on port `3306`.
2. Create database `ipl_auction_db`.
3. Configure DB credentials in `src/main/resources/application.yml`.
4. Run the Maven build command:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
5. Check app health: `http://localhost:8080/api/v1/health`
