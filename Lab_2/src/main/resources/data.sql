
# Insert categories
INSERT INTO categories (id, name,parent_id) VALUES (1, 'Electronics',null);
INSERT INTO categories (id, name,parent_id) VALUES (2, 'Books',1);

# Insert products with rating columns
INSERT INTO products (id, name, price, stock, category_id, average_rating, review_count) VALUES (1, 'Smartphone', 599.99, 100, 1, 0.0, 0);
INSERT INTO products (id, name, price, stock, category_id, average_rating, review_count) VALUES (2, 'Laptop', 999.99, 100, 1, 0.0, 0);
INSERT INTO products (id, name, price, stock, category_id, average_rating, review_count) VALUES (3, 'Novel', 19.99, 100, 2, 0.0, 0);

# Create product_reviews table
CREATE TABLE IF NOT EXISTS product_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_product_review (user_id, product_id)
);

# Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_product_reviews_product_id ON product_reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_product_reviews_user_id ON product_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_product_reviews_rating ON product_reviews(rating);
CREATE INDEX IF NOT EXISTS idx_product_reviews_review_date ON product_reviews(review_date);

# Add average_rating and review_count columns to products table (if not exists)
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS average_rating DECIMAL(3,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS review_count INTEGER DEFAULT 0;
