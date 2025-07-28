# User Management

Build a RESTful backend for user management system.

## Features
- Create, get list user
- Update, delete user

## Tech stack
- Java 17
- Spring Boot 3.x
- MySQL
- Maven
- Lombok
- Swagger (OpenAPI 3)

## How to run
1. Cài đặt MySQL, tạo database tên `userdb`.
2. Sửa `application.properties` với user/password MySQL của bạn.
3. Chạy lệnh: `mvn spring-boot:run`.
4. Truy cập Swagger UI tại: http://localhost:8080/swagger-ui.html

## API
- GET    `/api/users`         : Lấy danh sách user
- GET    `/api/users/{id}`    : Lấy user theo id
- POST   `/api/users`         : Tạo user mới
- PUT    `/api/users/{id}`    : Cập nhật user
- DELETE `/api/users/{id}`    : Xóa user
