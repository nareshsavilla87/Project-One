Drop procedure IF EXISTS paymentLongText; 
DELIMITER //
create procedure paymentLongText() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'Remarks'
     and TABLE_NAME = 'b_paymentgateway'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_paymentgateway CHANGE COLUMN `Remarks` `Remarks` LONGTEXT  CHARACTER SET utf8 DEFAULT NULL;
END IF;
END //
DELIMITER ;
call paymentLongText();
Drop procedure IF EXISTS paymentLongText;

Drop procedure IF EXISTS paymentGatewayReprocess; 
DELIMITER //
create procedure paymentGatewayReprocess() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'reprocess_detail'
     and TABLE_NAME = 'b_paymentgateway'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_paymentgateway ADD COLUMN reprocess_detail LONGTEXT  CHARACTER SET utf8 DEFAULT NULL;
END IF;
END //
DELIMITER ;
call paymentGatewayReprocess();
Drop procedure IF EXISTS paymentGatewayReprocess;
