-- Oracle-compatible SQL script to insert 2 categories and 3 products into the database.
INSERT INTO categories (name) VALUES ('Electronics');

INSERT INTO categories (name) VALUES ('Books');

INSERT INTO products (name, price, stock, average_rating, category_id, created_at, updated_at)
VALUES ('Smartphone', 699.99, 100, 0, (SELECT id FROM categories WHERE name = 'Electronics'), SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO products (name, price, stock, average_rating, category_id, created_at, updated_at)
VALUES ('Laptop', 1299.99, 50, 0, (SELECT id FROM categories WHERE name = 'Electronics'), SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO products (name, price, stock, average_rating, category_id, created_at, updated_at)
VALUES ('Java Programming Book', 39.99, 200, 0, (SELECT id FROM categories WHERE name = 'Books'), SYSTIMESTAMP, SYSTIMESTAMP);

-- Write SQL script to insert data into the database for tables: Users, Customers, Orders, Order_Items.
-- Insert Users (employees who place orders)
INSERT INTO users (name, email) VALUES ('Sales Rep 1', 'sales1@company.com');
INSERT INTO users (name, email) VALUES ('Sales Rep 2', 'sales2@company.com');

-- Insert Customer data
INSERT INTO customers (name, email, password, address, phone, created_at, updated_at)
VALUES ('John Doe', 'john@example.com', '$2a$10$hashedpassword1', '123 Main St, City, State 12345', '555-0123', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO customers (name, email, password, address, phone, created_at, updated_at)
VALUES ('Jane Smith', 'jane@example.com', '$2a$10$hashedpassword2', '456 Oak Ave, City, State 67890', '555-0456', SYSTIMESTAMP, SYSTIMESTAMP);

-- Insert Orders with both user and customer references
INSERT INTO orders (user_id, customer_id, total_amount, status, created_at, updated_at)
VALUES ((SELECT id FROM users WHERE email = 'sales1@company.com'), 
        (SELECT id FROM customers WHERE email = 'john@example.com'), 
        1739.98, 'PENDING', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES ((SELECT id FROM orders WHERE customer_id = (SELECT id FROM customers WHERE email = 'john@example.com') AND status = 'PENDING'), 
        (SELECT id FROM products WHERE name = 'Smartphone'), 1, 699.99);

INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES ((SELECT id FROM orders WHERE customer_id = (SELECT id FROM customers WHERE email = 'john@example.com') AND status = 'PENDING'), 
        (SELECT id FROM products WHERE name = 'Laptop'), 1, 1299.99);

INSERT INTO orders (user_id, customer_id, total_amount, status, created_at, updated_at)
VALUES ((SELECT id FROM users WHERE email = 'sales2@company.com'), 
        (SELECT id FROM customers WHERE email = 'jane@example.com'), 
        39.99, 'COMPLETED', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES ((SELECT id FROM orders WHERE customer_id = (SELECT id FROM customers WHERE email = 'jane@example.com') AND status = 'COMPLETED'), 
        (SELECT id FROM products WHERE name = 'Java Programming Book'), 1, 39.99);

-- Insert Reviews

-- Insert Reviews
INSERT INTO reviews (product_id, customer_id, rating, review_cont, created_at, updated_at)
VALUES ((SELECT id FROM products WHERE name = 'Smartphone'), 
        (SELECT id FROM customers WHERE email = 'john@example.com'), 
        5, 'Excellent smartphone! Great performance and camera quality.', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO reviews (product_id, customer_id, rating, review_cont, created_at, updated_at)
VALUES ((SELECT id FROM products WHERE name = 'Laptop'), 
        (SELECT id FROM customers WHERE email = 'john@example.com'), 
        4, 'Good laptop for work. Fast performance but a bit heavy.', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO reviews (product_id, customer_id, rating, review_cont, created_at, updated_at)
VALUES ((SELECT id FROM products WHERE name = 'Java Programming Book'), 
        (SELECT id FROM customers WHERE email = 'jane@example.com'), 
        5, 'Great book for learning Java. Very comprehensive and well-written.', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO reviews (product_id, customer_id, rating, review_cont, created_at, updated_at)
VALUES ((SELECT id FROM products WHERE name = 'Smartphone'), 
        (SELECT id FROM customers WHERE email = 'jane@example.com'), 
        4, 'Good phone but battery life could be better.', SYSTIMESTAMP, SYSTIMESTAMP);

-- Update product average ratings based on existing reviews
UPDATE products 
SET average_rating = (
    SELECT ROUND(AVG(r.rating), 2)
    FROM reviews r 
    WHERE r.product_id = products.id
)
WHERE id IN (SELECT DISTINCT product_id FROM reviews);

-- Set average_rating to 0 for products with no reviews
UPDATE products 
SET average_rating = 0 
WHERE average_rating IS NULL;



