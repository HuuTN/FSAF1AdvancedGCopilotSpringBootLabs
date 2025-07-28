# Spring Boot CRUD Example

This project is a Spring Boot application that provides CRUD APIs for User, Product, and Category entities using JPA (ORM) and MySQL database.

## Features
- CRUD REST APIs for User, Product, Category
- JPA/Hibernate ORM
- MySQL database configuration

## How to run
1. Cài đặt MySQL và tạo database tên `lab5db`.
2. Cập nhật tài khoản và mật khẩu MySQL trong `src/main/resources/application.properties`.
3. Build project bằng Maven:
   ```
   mvn clean install
   ```
4. Chạy ứng dụng:
   ```
   mvn spring-boot:run
   ```

## API Endpoints
- `/users` CRUD cho User
- `/products` CRUD cho Product
- `/categories` CRUD cho Category

## Thư mục chính
- `entity/` : Các entity JPA
- `repository/` : Repository interface
- `controller/` : REST controller
