CREATE TABLE IF NOT EXISTS `b_deposit_refund` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(10) NOT NULL,
  `transaction_date` datetime NOT NULL,
  `transaction_type` varchar(50) NOT NULL,
  `item_id` int(10) DEFAULT NULL,
  `credit_amount` decimal(24,4) DEFAULT NULL,
  `debit_amount` decimal(24,4) DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  `payment_id` int(10) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `is_refund` char(1) DEFAULT 'N',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DELIMITER $$
Drop procedure IF EXISTS itemid $$
create procedure itemid() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_id'
     and TABLE_NAME = 'b_fee_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER table b_fee_master add `item_id` int(10) DEFAULT NULL;
end if;

IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_refundable'
     and TABLE_NAME = 'b_fee_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER table b_fee_master add `is_refundable` varchar(1) DEFAULT NULL;
end if;

END $$
delimiter ;
call itemid();
Drop procedure IF EXISTS itemid ;


INSERT IGNORE INTO m_code VALUES (null,'Transaction Type',0,'Define Customer Transaction Type');
SET @id = (select id from m_code where code_name='Transaction Type');
INSERT IGNORE INTO m_code_value VALUES (null,@id,'Deposit',3);

INSERT IGNORE INTO m_permission values(null,'billing','CREATE_REFUND','REFUND','CREATE',0);



