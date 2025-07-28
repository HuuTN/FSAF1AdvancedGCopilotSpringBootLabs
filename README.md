# E-Commerce Spring Boot Application

## Description
A Spring Boot application for E-Commerce, products, and orders. Supports CRUD operations, multi-product order creation, order history, automated testing, and coverage reporting.

## System Requirements
- Java 17
- PostgreSQL
- Gradle

## Setup & Run Guide
1. **Clone the project**
   ```bash
   git clone <repo-url>
   ```
2. **Configure the database**
   - Edit `src/main/resources/application.properties`:
     ```
     spring.datasource.url=jdbc:postgresql://localhost:5432/userdb
     spring.datasource.username=postgres
     spring.datasource.password=postgres
     ```
   - Create the `userdb` database in PostgreSQL if it does not exist.

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```
   The app will be available at: [http://localhost:8080](http://localhost:8080)

4. **API Documentation**
   - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - OpenAPI docs: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

5. **Run tests & view coverage report**
   ```bash
   ./gradlew test jacocoTestReport
   open build/reports/jacoco/test/html/index.html
   ```

## Main APIs

### User API
- `GET /api/v1/users` — Get user list (with pagination)
- `GET /api/v1/users/{id}` — Get user details
- `POST /api/v1/users` — Create a new user
- `PUT /api/v1/users/{id}` — Update user
- `DELETE /api/v1/users/{id}` — Delete user
- `GET /api/v1/users/{userId}/orders` — Get a user's order history

### Product API
- `GET /api/v1/products` — Search products (by keyword, price, category, with pagination)
- `GET /api/v1/products/{id}` — Get product details
- `POST /api/v1/products` — Create a new product
- `PUT /api/v1/products/{id}` — Update product
- `DELETE /api/v1/products/{id}` — Delete product
- `GET /api/v1/products/{id}/count-by-category` — Count products by category

### Order API
- `POST /api/v1/orders` — Create a new order (supports multiple products)
  - **Sample request:**
    ```json
    {
      "userId": 1,
      "items": [
        { "productId": 2, "quantity": 1 },
        { "productId": 3, "quantity": 2 }
      ]
    }
    ```
- `POST /api/v1/orders/{id}/cancel` — Cancel an order

## Notes
- All APIs return JSON data.
- You can use Swagger UI to try out the APIs interactively.
- The project includes automated tests (JUnit, Mockito, Testcontainers) and coverage reporting (Jacoco).

---