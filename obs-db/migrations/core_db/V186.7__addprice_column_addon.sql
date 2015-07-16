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

Drop procedure IF EXISTS addaddoninprice;
DELIMITER //
create procedure addaddoninprice() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_addon'
     and TABLE_NAME = 'b_order_price'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table b_order_price add column `is_addon` char(1) default 'N' after `tax_inclusive`;

END IF;
END //
DELIMITER ;
call addaddoninprice();
Drop procedure IF EXISTS addaddoninprice;
update b_order_price set is_addon = 'N';
alter TABLE b_orders_addons modify end_date datetime default null;