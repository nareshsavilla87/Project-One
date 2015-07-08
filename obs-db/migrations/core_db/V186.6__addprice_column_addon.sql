Drop procedure IF EXISTS addpriceinaddon;
DELIMITER //
create procedure addpriceinaddon() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'price_id'
     and TABLE_NAME = 'b_orders_addons'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_orders_addons add column `price_id` bigint(10) default NULL;

END IF;
END //
DELIMITER ;
call addpriceinaddon();
Drop procedure IF EXISTS addpriceinaddon;
