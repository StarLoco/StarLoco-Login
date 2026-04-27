-- Migration: Add index on zaap_token column
-- Date: 2026-04-27
-- Description: Add UNIQUE index on zaap_token for performance and to prevent duplicate tokens

-- Check if index already exists before adding
SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics 
    WHERE table_schema = DATABASE() 
    AND table_name = 'world_accounts' 
    AND index_name = 'idx_zaap_token');

SET @sql = IF(@index_exists > 0, 'SELECT ''Index already exists'' AS result', 
    'ALTER TABLE `world_accounts` ADD UNIQUE INDEX `idx_zaap_token` (`zaap_token`)');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;