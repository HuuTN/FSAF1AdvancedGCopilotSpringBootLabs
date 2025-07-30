-- Fix for MySQL datetime error when adding audit columns to existing tables

-- For users table: Update existing records to have proper created_at values
-- This handles the case where users table exists but doesn't have created_at column yet
SET SQL_SAFE_UPDATES = 0;

-- If the users table exists but doesn't have created_at, add it as nullable first
-- ALTER TABLE users ADD COLUMN created_at DATETIME(6) NULL;
-- ALTER TABLE users ADD COLUMN updated_at DATETIME(6) NULL;

-- Update existing users with current timestamp for created_at if it's null
UPDATE users SET created_at = NOW(6) WHERE created_at IS NULL OR created_at = '0000-00-00 00:00:00';
UPDATE users SET updated_at = NOW(6) WHERE updated_at IS NULL OR updated_at = '0000-00-00 00:00:00';

-- After updating data, you can make the columns NOT NULL if needed
-- ALTER TABLE users MODIFY COLUMN created_at DATETIME(6) NOT NULL;
-- ALTER TABLE users MODIFY COLUMN updated_at DATETIME(6) NOT NULL;

SET SQL_SAFE_UPDATES = 1;

-- Note: This script should be run manually if you encounter the datetime error
-- The application will handle new records correctly with Spring Data JPA auditing
