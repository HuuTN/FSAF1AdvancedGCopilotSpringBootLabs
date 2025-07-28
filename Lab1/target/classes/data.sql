-- Sample data for testing the e-commerce review system
-- This file can be used to manually insert test data for MySQL

-- Insert sample users (avoid duplicates)
INSERT IGNORE INTO users (username, email, password, first_name, last_name, role, created_at) VALUES
('admin', 'admin@example.com', '$2a$10$DowJonesIndex', 'Admin', 'User', 'ADMIN', NOW()),
('john_doe', 'john@example.com', '$2a$10$DowJonesIndex', 'John', 'Doe', 'USER', NOW()),
('jane_smith', 'jane@example.com', '$2a$10$DowJonesIndex', 'Jane', 'Smith', 'USER', NOW());

-- Insert sample products (avoid duplicates)
INSERT IGNORE INTO products (name, description, price, stock_quantity, category, average_rating, created_at) VALUES
('iPhone 15 Pro', 'Latest Apple smartphone with advanced features', 999.99, 50, 'Electronics', 0.0, NOW()),
('Samsung Galaxy S24', 'High-performance Android smartphone', 899.99, 30, 'Electronics', 0.0, NOW()),
('MacBook Pro M3', 'Professional laptop for developers', 1999.99, 20, 'Computers', 0.0, NOW()),
('Wireless Headphones', 'Noise-cancelling Bluetooth headphones', 199.99, 100, 'Audio', 0.0, NOW());

-- Insert sample orders (avoid duplicates)
INSERT IGNORE INTO orders (id, user_id, total_amount, status, order_date, shipping_address) VALUES
(1, 2, 999.99, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 30 DAY), '123 Main St, City, State'),
(2, 3, 1099.98, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 15 DAY), '456 Oak Ave, City, State'),
(3, 2, 199.99, 'SHIPPED', DATE_SUB(NOW(), INTERVAL 5 DAY), '123 Main St, City, State');

-- Insert sample order items (avoid duplicates)
INSERT IGNORE INTO order_items (id, order_id, product_id, quantity, price) VALUES
(1, 1, 1, 1, 999.99),  -- John bought iPhone
(2, 2, 2, 1, 899.99),  -- Jane bought Samsung
(3, 2, 4, 1, 199.99),  -- Jane bought Headphones
(4, 3, 4, 1, 199.99);  -- John bought Headphones
