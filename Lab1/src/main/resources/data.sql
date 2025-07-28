-- file: src/main/resources/data.sql
INSERT INTO category (name) VALUES ('Electronics'), ('Books'), ('Clothing'), ('Home'), ('Toys');

INSERT INTO product (name, price, stock, category_id) VALUES ('Smart TV', 800.00, 30, 1);
INSERT INTO product (name, price, stock, category_id) VALUES ('Clean Code', 35.00, 150, 2);
INSERT INTO product (name, price, stock, category_id) VALUES ('Wireless Mouse', 25.00, 200, 1);
INSERT INTO product (name, price, stock, category_id) VALUES ('Bluetooth Speaker', 120.00, 50, 1);
INSERT INTO product (name, price, stock, category_id) VALUES ('Java Concurrency in Practice', 45.00, 80, 2);
INSERT INTO product (name, price, stock, category_id) VALUES ('T-shirt', 15.00, 500, 3);
INSERT INTO product (name, price, stock, category_id) VALUES ('Coffee Maker', 60.00, 40, 4);
INSERT INTO product (name, price, stock, category_id) VALUES ('Board Game', 35.00, 60, 5);
INSERT INTO product (name, price, stock, category_id) VALUES ('Laptop', 1200.00, 20, 1);
INSERT INTO product (name, price, stock, category_id) VALUES ('Novel: The Pragmatic Programmer', 30.00, 100, 2);

-- Sample users (need to add name, email, password, role, creation_date)
INSERT INTO user (name, email, password, role, creation_date) VALUES ('Alice Johnson', 'alice@example.com', 'password123', 'CUSTOMER', NOW());
INSERT INTO user (name, email, password, role, creation_date) VALUES ('Bob Smith', 'bob@example.com', 'password123', 'CUSTOMER', NOW());
INSERT INTO user (name, email, password, role, creation_date) VALUES ('Charlie Brown', 'charlie@example.com', 'password123', 'CUSTOMER', NOW());
INSERT INTO user (name, email, password, role, creation_date) VALUES ('David Wilson', 'david@example.com', 'password123', 'CUSTOMER', NOW());
INSERT INTO user (name, email, password, role, creation_date) VALUES ('Eva Garcia', 'eva@example.com', 'password123', 'CUSTOMER', NOW());

-- Sample orders
INSERT INTO orders (user_id, order_date, status) VALUES (1, NOW(), 'DELIVERED');
INSERT INTO orders (user_id, order_date, status) VALUES (2, NOW(), 'DELIVERED');
INSERT INTO orders (user_id, order_date, status) VALUES (3, NOW(), 'PENDING');
INSERT INTO orders (user_id, order_date, status) VALUES (4, NOW(), 'DELIVERED');
INSERT INTO orders (user_id, order_date, status) VALUES (5, NOW(), 'PROCESSING');

-- Sample order items
INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (1, 1, 1, 800.00);
INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (1, 3, 2, 25.00);
INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (2, 2, 1, 35.00);
INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (3, 6, 3, 15.00);
INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (3, 4, 1, 120.00);
INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (4, 5, 2, 45.00);
INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (5, 7, 1, 60.00);
