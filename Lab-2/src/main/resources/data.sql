
# Insert categories
INSERT INTO categories (id, name,parent_id) VALUES (1, 'Electronics',null);
INSERT INTO categories (id, name,parent_id) VALUES (2, 'Books',1);

# Insert products
INSERT INTO products (id, name, price, stock ,category_id) VALUES (1, 'Smartphone', 599.99,100, 1);
INSERT INTO products (id, name, price, stock ,category_id) VALUES (2, 'Laptop', 999.99,100, 1);
INSERT INTO products (id, name, price, stock ,category_id) VALUES (3, 'Novel', 19.99,100, 2);
