# QNR Project - REST API with Spring Boot

## Overview
This project is a simple REST API built using Spring Boot. It handles basic user authentication, order management and token blacklisting.

---

## Technologies Used
- **Java 17**
- **Spring Boot**
- **Spring Security** 
- **Hibernate / JPA** 
- **H2 Database**
- **Maven**

---

## Project Structure
- `entity` – JPA entity classes (`User`, `Order`, `BlacklistedToken`)
- `repository` – Spring Data JPA repositories
- `controller` – REST API controllers (`AuthController`, `OrderController`)
- `security` – JWT utilities, filters, and security configuration
- `application.properties` – Project configuration (database, JWT secret, expiration)

---

## Running the Project

### Prerequisites
- JDK 17 installed
- Maven installed (or use IntelliJ’s Maven integration)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/AlexuAgo/qnr-project.git
   ```
2. Build and run. Make sure you install all required dependencies. 
A test user is already created once you run the application in order to test the endpoints on Postman.


3. The API will start at:
    ```bash 
   http://localhost:8080
   ```
---
## API Endpoints

### Authentication

| Method | Endpoint           | Request Body                                      | Description                        |
|--------|------------------|---------------------------------------------------|------------------------------------|
| POST   | `/api/auth/login` | `{ "username": "admin", "password": "admin123" }` | Login and get a JWT token           |
| POST   | `/api/auth/logout` | None                                              | Logout and blacklist the token      |

---

### Orders

| Method | Endpoint                   | Request Body                                                                | Description                                 |
|--------|----------------------------|-----------------------------------------------------------------------------|---------------------------------------------|
| POST   | `/api/orders`              | `{ "description": "New order", "status": "PENDING", "quantity" : 1000.00 }` | Create a new order                          |
| GET    | `/api/orders`              | None                                                                        | Get all orders of logged-in user with pagination only (default page=0, size=10) |
| GET    | `/api/orders?status=PENDING&page=0&size=10` | None                                                                        | Get paginated orders filtered by status (optional query params: `status`, `page`, `size`) |
| GET    | `/api/orders/{id}`         | None                                                                        | Get a specific order by ID                  |
| PUT    | `/api/orders/{id}`         | `{ "description": "Updated desc", "status": "COMPLETED" }`                  | Update a specific order                     |
| DELETE | `/api/orders/{id}`         | None                                                                        | Delete a specific order                     |

---
## Notes
- All order endpoints require a valid token in the Authorization header.

---
## How to test:
1. Login with the request body I provided on the login endpoint and get the JWT token.
2. Use the token in the Authorization header (select Bearer Token on Auth Type) for all protected endpoints. (everything except login).
3. Logout to blacklist the token and test if it can be reused.

### Notes
- H2 database is in-memory and resets on restart (ddl-auto = update).
- Indexes can be added on user and order fields.

---
### Author
#### Alexandros Agko




