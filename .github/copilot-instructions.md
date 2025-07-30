# AI Agent Instructions for Spring Boot E-commerce Project

## Project Overview
This is a Spring Boot e-commerce application with JWT authentication, following a layered architecture pattern. The application manages users, products, categories, orders, and reviews with MySQL persistence.

## Key Architecture Patterns

### Layer Structure
- `controller/` - REST API endpoints with validation
- `service/` - Business logic layer with interface/implementation pattern
- `repository/` - JPA repositories for data access
- `entity/` - JPA entities with relationships
- `dto/` - Data transfer objects with validation annotations
- `exception/` - Custom exceptions and global error handling
- `security/` - JWT authentication components

### Data Flow Patterns
1. Controller receives DTO with validation annotations
2. Service implements business logic and entity mapping
3. Repository handles data persistence
4. Exceptions are caught by GlobalExceptionHandler

Example from ReviewServiceImpl:
```java
@Transactional
public ReviewDTO addReview(ReviewDTO dto, Long userId, Long productId) {
    // 1. Entity validation
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    // 2. Business rule validation
    if (!orderVerificationService.hasUserPurchasedProduct(userId, productId)) {
        throw new UserNotPurchasedProductException("...");
    }
    // 3. Entity creation and persistence
    Review review = new Review();
    // ... set properties
    Review savedReview = reviewRepository.save(review);
    // 4. Update related entities
    updateProductAverageRating(product.getId());
}
```

## Key Development Workflows

### Authentication Flow
1. Register: POST `/api/auth/register` with UserDTO
2. Login: POST `/api/auth/login` with credentials
3. Use JWT token in Authorization header for subsequent requests

### Testing
- Service layer tests use Mockito for repository mocks
- Integration tests use TestContainers for MySQL 
- Example test pattern in `src/test/java/.../service/DashboardServiceTest.java`

### Database
- MySQL with JPA/Hibernate
- Schema managed through JPA entity annotations
- Initialization data in `src/main/resources/data.sql`

## Project-Specific Conventions

### Entity Relationships
- Orders have OrderItems linking to Products
- Reviews require verified purchase (OrderItem) reference
- Users have ADMIN/USER roles with method-level security

### Validation
- DTOs use Jakarta validation annotations
- Services perform additional business rule validation
- Custom exceptions for specific error cases

### Security
- JWT-based stateless authentication
- Role-based method security with @PreAuthorize
- Token configuration in application.yml

## Integration Points
- MySQL database
- JWT token service
- Mail service (if implemented)

## Tips for AI Agents
- Check entity relationships before suggesting changes
- Validate business rules in service layer
- Use custom exceptions for business logic errors
- Follow existing transaction boundaries
- Consider security implications of API changes
