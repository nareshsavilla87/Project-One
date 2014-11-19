INSERT IGNORE INTO c_configuration VALUES (null,'media-crash-email',1,'shiva@openbillingsystem.com');
SET @id=(select id from m_code_value where code_value='Online Payment');
INSERT IGNORE INTO c_configuration VALUES (null,'online-paymode',1,@id);
INSERT IGNORE INTO m_permission VALUES (null,'Finance', 'CREATE_PAYMENTGATEWAYCONFIG', 'PAYMENTGATEWAYCONFIG', 'CREATE', 0);
INSERT IGNORE INTO m_permission VALUES (null,'Finance', 'UPDATE_PAYMENTGATEWAYCONFIG', 'PAYMENTGATEWAYCONFIG', 'UPDATE', 1);
INSERT IGNORE INTO m_permission VALUES (null,'Finance', 'READ_PAYMENTGATEWAYCONFIG', 'PAYMENTGATEWAYCONFIG', 'READ', 2);

CREATE TABLE IF NOT EXISTS `c_paymentgateway_conf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  `value` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_config` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci';

INSERT IGNORE INTO c_paymentgateway_conf VALUES (null,'korta',0,'{"merchantId":"123...","terminalId":"123..","secretCode":"123.."}');
INSERT IGNORE INTO c_paymentgateway_conf VALUES (null,'dalpay',0,'http://......');
INSERT IGNORE INTO c_paymentgateway_conf VALUES (null,'paypal',0,'{"paypalUrl":"https://...","paypalUrl":"xyz@gmail.com"}');
INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('CREATE USER','OBS User Creation','Dear <PARAM1>','OBS User Account has been successfully created .You can login using the following credentials. \n userName : <PARAM2> , \n password : <PARAM3> .','Thankyou','E');

Drop procedure IF EXISTS alterprospectemail; 
DELIMITER //
create procedure alterprospectemail() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'b_prospect' and COLUMN_NAME ='email' and COLUMN_KEY != 'UNI'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_prospect ADD UNIQUE (email);
END IF;
END //
DELIMITER ;
call alterprospectemail();
Drop procedure IF EXISTS alterprospectemail;


Drop procedure IF EXISTS renameTemp; 
DELIMITER //
create procedure renameTemp() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'temp' 
     and TABLE_SCHEMA = DATABASE())THEN
rename table temp to b_client_register;
END IF;
END //
DELIMITER ;
call renameTemp();
Drop procedure IF EXISTS renameTemp;






