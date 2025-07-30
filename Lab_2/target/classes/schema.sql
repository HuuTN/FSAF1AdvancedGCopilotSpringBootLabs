-- Add FULLTEXT index for product name search optimization
ALTER TABLE products ADD FULLTEXT INDEX idx_product_name_fulltext(name);
