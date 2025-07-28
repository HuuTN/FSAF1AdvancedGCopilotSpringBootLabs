INSERT INTO categories (id, name) VALUES (1, 'Electronics');
INSERT INTO categories (id, name, parent_id) VALUES (2, 'Laptops', 1);

INSERT INTO products (id, name, price, stock, category_id) VALUES (1, 'Smartphone', 599.99, 50, 1);
INSERT INTO products (id, name, price, stock, category_id) VALUES (2, 'Gaming Laptop', 1299.99, 20, 2);
INSERT INTO products (id, name, price, stock, category_id) VALUES (3, 'Ultrabook', 999.99, 15, 2);

INSERT INTO users (id, name, email, password, role) VALUES (1, 'Alice', 'alice@example.com', 'password1', 'USER');
INSERT INTO users (id, name, email, password, role) VALUES (2, 'Bob', 'bob@example.com', 'password2', 'ADMIN');
INSERT INTO users (id, name, email, password, role) VALUES (3, 'Charlie', 'charlie@example.com', 'password3', 'USER');
