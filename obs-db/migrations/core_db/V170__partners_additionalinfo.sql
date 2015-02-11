-- INSERT IGNORE INTO c_paymentgateway_conf VALUES(null,'neteller',1,'');
INSERT IGNORE INTO m_code VALUES(null,'Partner Type',0,'Partner are created and mapped to type');
SET @id=(select id from m_code where code_name='Partner Type');

INSERT IGNORE INTO m_code_value VALUES(null,@id,'Partner',2);
INSERT IGNORE INTO m_code_value VALUES(null,@id,'Reseller',1);
INSERT IGNORE INTO m_role VALUES(null, 'Partner', 'partners only');

INSERT IGNORE INTO m_code VALUES(null ,'Agreement Type', '0','Describe the agreement status');
SET @id=(select id from m_code where code_name='Agreement Type');

INSERT IGNORE INTO m_code_value VALUES(null, @id, 'Signed', '0');
INSERT IGNORE INTO m_code_value VALUES(null, @id, 'Pending', '0');


INSERT IGNORE INTO m_code VALUES(null ,'Source Category', '0','Describe the different sources');
SET @id=(select id from m_code where code_name='Source Category');

INSERT IGNORE INTO m_code_value VALUES(null, @id, 'Subscriptions', '0');
INSERT IGNORE INTO m_code_value VALUES(null, @id, 'Hardware', '1');
INSERT IGNORE INTO m_code_value VALUES(null, @id, 'On-demand', '2');

INSERT IGNORE INTO  m_permission VALUES (null,'organization', 'CREATE_PARTNER', 'PARTNER', 'CREATE', 0);
INSERT IGNORE INTO  m_permission VALUES (null, 'organisation', 'READ_PARTNER', 'PARTNER', 'READ', 0);
INSERT IGNORE INTO  m_permission VALUES (null, 'organisation', 'UPDATE_PARTNER', 'PARTNER', 'UPDATE', 0);
INSERT IGNORE INTO  m_permission VALUES (null,'organization', 'CREATE_PARTNERAGREEMENT', 'PARTNERAGREEMENT', 'CREATE', 0);
INSERT IGNORE INTO  m_permission VALUES (null,'organization', 'UPDATE_PARTNERAGREEMENT', 'PARTNERAGREEMENT', 'UPDATE', 0);
INSERT IGNORE INTO  m_permission VALUES(null, 'organisation', 'DELETE_PARTNERAGREEMENT', 'PARTNERAGREEMENT', 'DELETE', 0);
INSERT IGNORE INTO  m_permission VALUES(null, 'organisation', 'READ_PARTNERDISBURSEMENT', 'PARTNERDISBURSEMENT', 'READ', 0);


CREATE TABLE IF NOT EXISTS `m_office_additional_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `contact_name` varchar(200) DEFAULT NULL,
  `credit_limit` decimal(6,2) DEFAULT NULL,
  `partner_currency` varchar(50) DEFAULT NULL,
  `is_collective` char(2) DEFAULT 'N',
  PRIMARY KEY (`id`),
  KEY `partner_office_key` (`office_id`),
  CONSTRAINT `partner_office_key` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)  ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `m_controlaccount_balance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `account_type` varchar(50) NOT NULL,
  `balance_amount`  decimal(6,2) NOT NULL,
  `createdby_id` int(5) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `m_office_balance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `balance_amount`  decimal(6,2) NOT NULL,
  `createdby_id` int(5) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

SET SQL_SAFE_UPDATES=0;
SET FOREIGN_KEY_CHECKS=0;

Drop procedure if exists officeAddress;
DELIMITER //
create procedure officeAddress()
BEGIN
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='office_id' 
AND TABLE_NAME='b_office_address'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE b_office_address add column `office_id` bigint(20) not null,
 ADD CONSTRAINT `partner_officeaddress_key`
 FOREIGN KEY(`office_id`)
 REFERENCES `m_office` (`id`);
END IF;
END // 
DELIMITER ;
call officeAddress();

Drop procedure if exists officeAddress;


Drop procedure if exists officeAddr1;
DELIMITER //
create procedure officeAddr1()
BEGIN
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='office_number' 
AND TABLE_NAME='b_office_address'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE `b_office_address` ADD COLUMN `office_number` VARCHAR(100) NULL DEFAULT NULL  AFTER `phone_number`;
END IF;
END // 
DELIMITER ;
call officeAddr1();
Drop procedure if exists officeInfo;

Drop procedure if exists officeInfo;
DELIMITER //
create procedure officeInfo()
BEGIN
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='contact_name' 
AND TABLE_NAME='m_office_additional_info'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE `m_office_additional_info` ADD COLUMN `contact_name` VARCHAR(200) NULL DEFAULT NULL  AFTER `office_id` ;
END IF;
END // 
DELIMITER ;
call officeInfo();
Drop procedure if exists officeInfo;

SET SQL_SAFE_UPDATES=1;
SET FOREIGN_KEY_CHECKS=1;